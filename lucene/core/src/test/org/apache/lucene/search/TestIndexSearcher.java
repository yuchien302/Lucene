package org.apache.lucene.search;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.MockAnalyzer;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.RandomIndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.search.postingshighlight.PostingsHighlighter;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.NamedThreadFactory;
import org.apache.lucene.util._TestUtil;
import org.junit.Test;

public class TestIndexSearcher extends LuceneTestCase {
  Directory dir;
  IndexReader reader;
  String testString = new String("This is a test where apple is highlighted and should be highlighted. Happy New Year!!! An apple a day keeps the doctor away!!!");
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    dir = newDirectory();
    RandomIndexWriter iw = new RandomIndexWriter(random(), dir);
    for (int i = 0; i < 100; i++) {
      Document doc = new Document();
      doc.add(newStringField("field", Integer.toString(i), Field.Store.NO));
      doc.add(newStringField("field2", Boolean.toString(i % 2 == 0), Field.Store.NO));
      iw.addDocument(doc);
    }
    reader = iw.getReader();
    iw.close();
  }
  
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    reader.close();
    dir.close();
  }
  
  // should not throw exception
  public void testHugeN() throws Exception {
    ExecutorService service = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS,
                                   new LinkedBlockingQueue<Runnable>(),
                                   new NamedThreadFactory("TestIndexSearcher"));
    
    IndexSearcher searchers[] = new IndexSearcher[] {
        new IndexSearcher(reader),
        new IndexSearcher(reader, service)
    };
    Query queries[] = new Query[] {
        new MatchAllDocsQuery(),
        new TermQuery(new Term("field", "1"))
    };
    Sort sorts[] = new Sort[] {
        null,
        new Sort(new SortField("field2", SortField.Type.STRING))
    };
    Filter filters[] = new Filter[] {
        null,
        new QueryWrapperFilter(new TermQuery(new Term("field2", "true")))
    };
    ScoreDoc afters[] = new ScoreDoc[] {
        null,
        new FieldDoc(0, 0f, new Object[] { new BytesRef("boo!") })
    };
    
    for (IndexSearcher searcher : searchers) {
      for (ScoreDoc after : afters) {
        for (Query query : queries) {
          for (Sort sort : sorts) {
            for (Filter filter : filters) {
              searcher.search(query, Integer.MAX_VALUE);
              searcher.searchAfter(after, query, Integer.MAX_VALUE);
              searcher.search(query, filter, Integer.MAX_VALUE);
              searcher.searchAfter(after, query, filter, Integer.MAX_VALUE);
              if (sort != null) {
                searcher.search(query, Integer.MAX_VALUE, sort);
                searcher.search(query, filter, Integer.MAX_VALUE, sort);
                searcher.search(query, filter, Integer.MAX_VALUE, sort, true, true);
                searcher.search(query, filter, Integer.MAX_VALUE, sort, true, false);
                searcher.search(query, filter, Integer.MAX_VALUE, sort, false, true);
                searcher.search(query, filter, Integer.MAX_VALUE, sort, false, false);
                searcher.searchAfter(after, query, filter, Integer.MAX_VALUE, sort);
                searcher.searchAfter(after, query, filter, Integer.MAX_VALUE, sort, true, true);
                searcher.searchAfter(after, query, filter, Integer.MAX_VALUE, sort, true, false);
                searcher.searchAfter(after, query, filter, Integer.MAX_VALUE, sort, false, true);
                searcher.searchAfter(after, query, filter, Integer.MAX_VALUE, sort, false, false);
              }
            }
          }
        }
      }
    }
    
    _TestUtil.shutdownExecutorService(service);
  }
  
  @Test
  public void testSearchAfterPassedMaxDoc() throws Exception {
    // LUCENE-5128: ensure we get a meaningful message if searchAfter exceeds maxDoc
    Directory dir = newDirectory();
    RandomIndexWriter w = new RandomIndexWriter(random(), dir);
    w.addDocument(new Document());
    IndexReader r = w.getReader();
    w.close();
    
    IndexSearcher s = new IndexSearcher(r);
    try {
      s.searchAfter(new ScoreDoc(r.maxDoc(), 0.54f), new MatchAllDocsQuery(), 10);
      fail("should have hit IllegalArgumentException when searchAfter exceeds maxDoc");
    } catch (IllegalArgumentException e) {
      // ok
    } finally {
      IOUtils.close(r, dir);
    }
  }
  
  @Test
  public void testSimpleSummarize()throws Exception{
    IndexWriter writer = new IndexWriter(dir, newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random())));  
    Document doc = new Document();
    FieldType type = new FieldType(TextField.TYPE_STORED);
    Field field = new Field("contents", testString, type);
    doc.add(field);                      
    writer.addDocument(doc);    

    IndexReader reader = DirectoryReader.open(writer, true);
    IndexSearcher searcher = new IndexSearcher(reader);
    Query query = new TermQuery(new Term("contents", "apple"));      

    /*int hitsPerPage=1;
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(query, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
    int docId = hits[0].doc;*/
    
    int docId = 100;

    String matchStr1 = new String("where apple is highlighted");
    String matchStr2 = new String("Year!!! An apple a day keeps");
    String matchString = new String("  In docId=100, matched 2 times.\n  #1 : position=6 : "+matchStr1+"\n  #2 : position=11 : "+matchStr2+"\n");
    
    
    Summary summary = searcher.summarize(query, docId);
    
    System.out.println("SIMPLE");
    System.out.println(summary.trim(20));
    assertEquals(summary.trim(20),matchString);
    writer.close();   
  }
  
  @Test
  public void testSummarizeWithFastVectorHighlighter() throws Exception {
    IndexWriter writer = new IndexWriter(dir, newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random())));
    Document doc = new Document();
    FieldType type = new FieldType(TextField.TYPE_STORED);
    type.setStoreTermVectorOffsets(true);
    type.setStoreTermVectorPositions(true);
    type.setStoreTermVectors(true);
    type.freeze();
    Field field = new Field("contents", testString, type);
    
    doc.add(field);
    writer.addDocument(doc);
    
    IndexReader reader = DirectoryReader.open(writer, true);
    IndexSearcher searcher = new IndexSearcher(reader);

    Query query = new TermQuery(new Term("contents", "apple"));  
    
    int docId = 100;
    
    FVHAdapter highlighter = new FVHAdapter();
    Summary summary = searcher.summarize(query, docId,highlighter);

    
    String matchStr1 = new String("where apple is highlighted");
    String matchStr2 = new String("Year!!! An apple a day keeps");
    String matchString = new String("  In docId=100, matched 2 times.\n  #1 : position=6 : "+matchStr1+"\n  #2 : position=11 : "+matchStr2+"\n");
    
    System.out.println(summary.trim(20));
    assertEquals(summary.trim(20),matchString);
    reader.close();
    writer.close();
    
  }
  
  @Test
  public void testSummarizeWithPostingHighlighter() throws Exception{
    
    IndexWriterConfig iwc = newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random()));
    iwc.setMergePolicy(newLogMergePolicy());
    RandomIndexWriter iw = new RandomIndexWriter(random(), dir, iwc);
    
    FieldType offsetsType = new FieldType(TextField.TYPE_STORED);
    offsetsType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    Field body = new Field("contents", "", offsetsType);
    Document doc = new Document();
    doc.add(body);
    
    body.setStringValue(testString);
    iw.addDocument(doc);
    //body.setStringValue("Highlighting the first term. Hope it works.");
    //iw.addDocument(doc);*/
    
    IndexReader ir = iw.getReader();
    iw.close();
    
    IndexSearcher searcher = newSearcher(ir);
    //PostingsHighlighter highlighter = new PostingsHighlighter();
    Query query = new TermQuery(new Term("contents", "apple"));
    //TopDocs topDocs = searcher.search(query, null, 10, Sort.INDEXORDER);
    
    /*TopDocs topDocs = searcher.search(query, null, 10, Sort.INDEXORDER);
    assertEquals(2, topDocs.totalHits);
    String snippets[] = highlighter.highlight("body", query, searcher, topDocs);
    assertEquals(2, snippets.length);
    assertEquals("Just a test <b>highlighting</b> from postings. ", snippets[0]);
    assertEquals("<b>Highlighting</b> the first term. ", snippets[1]);*/
    
    /*int hitsPerPage=1;
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(query, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
    int docId = hits[0].doc;*/
    
    int docId = 100;
    
    BaseHighlightAdapter highlighter = new PostingsHighlightAdapter();
    Summary summary = searcher.summarize(query, docId, highlighter);
    System.out.println("POSTING");
    System.out.println(summary.trim(20));
    
    String matchString = new String("to be done");
    
    assertEquals(summary.trim(20),matchString);
    ir.close();
    dir.close();
    
  }
  
  /*@Test
  public void testExplain() throws Exception{
    Directory dir = newDirectory();
    IndexWriter writer = new IndexWriter(dir, newIndexWriterConfig(TEST_VERSION_CURRENT, new MockAnalyzer(random())));

    Document doc  = new Document();
    Document doc2 = new Document();
    Document doc3 = new Document();
    Document doc4 = new Document();
    FieldType type = new FieldType(TextField.TYPE_STORED);

    Field field = new Field("contents", "apple other other other boy", type);
    Field field2 = new Field("contents", "apple apple other other other", type);
    Field field3 = new Field("contents", "apple apple apple other other", type);   
    Field field4 = new Field("contents", "apple apple apple apple other", type);
    
    doc.add(field);
    doc2.add(field2);
    doc3.add(field3);
    doc4.add(field4);
    
    writer.addDocument(doc); 
    writer.addDocument(doc2); 
    writer.addDocument(doc3); 
    writer.addDocument(doc4); 

    
    IndexReader reader = DirectoryReader.open(writer, true);
    IndexSearcher searcher = newSearcher(reader);
    
    TermQuery q = new TermQuery(new Term("contents", "apple"));    

    int hitsPerPage = 10;
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
    
    System.out.println(searcher.explain(q,hits[3].doc));
    
    reader.close();
    writer.close();
    dir.close();    
  }*/

  
}
