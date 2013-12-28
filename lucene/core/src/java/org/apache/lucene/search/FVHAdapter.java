package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StorableField;
import org.apache.lucene.index.StoredDocument;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.DefaultEncoder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragmentsBuilder;

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

public class FVHAdapter implements BaseHighlightAdapter{
  //TODO actually define the constant
  public static final int SOME_CONSTANT = 100;                      //max match length
  public static final int SOME_CONSTANT2 = 2147483647;              //max match number
  public static final String SOME_CONSTANT_STRING1 = "RAYXDDDDD";   //pre tag
  public static final String SOME_CONSTANT_STRING2 = "";            //post tag
  
  private FastVectorHighlighter highlighter;
  public FVHAdapter(){
    highlighter = new FastVectorHighlighter();
  }
  
  @Override
  public Match[] highlight(int docID,IndexSearcher searcher,Query query) throws IOException{
    ArrayList<Match> matchList = new ArrayList<Match>(0);
    final IndexReader reader = searcher.getIndexReader();
    FieldQuery fieldQuery = highlighter.getFieldQuery(query,reader);
    //warning field is not define, how to get this to work?
    StoredDocument document = reader.document(docID);
    Iterator<StorableField> iterator = document.iterator();
    
    while(iterator.hasNext()){
      StorableField field = iterator.next();
      String[] bestFragments = highlighter.getBestFragments(fieldQuery,searcher.getIndexReader(),docID,field.name(),SOME_CONSTANT,SOME_CONSTANT2, new SimpleFragListBuilder(), new SimpleFragmentsBuilder(), new String[]{SOME_CONSTANT_STRING1}, new String[]{SOME_CONSTANT_STRING2},  new DefaultEncoder());
      for(String bestFragment:bestFragments){
        int delta = bestFragment.indexOf("<b>",0);
        String[] tmp = bestFragment.split(SOME_CONSTANT_STRING1);
        String matched = String.format("%s%s",tmp[0],tmp[1]);
        matchList.add(new Match(matched,delta));
      }
    }
    Match[] matchs = matchList.toArray(new Match[0]);
    System.out.println(matchs.length);
    return matchList.toArray(new Match[0]);
  }
  private BooleanQuery queryTranslate(Query q){
    String tmp = q.toString("");
    String[] pair =tmp.split(":",2);
    BooleanQuery resultQ = new BooleanQuery();
    resultQ.add(new TermQuery(new Term(pair[0],pair[1])),Occur.MUST);
    return resultQ;
  }
  // TODO for 睿謙
}
