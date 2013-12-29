package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StorableField;
import org.apache.lucene.index.StoredDocument;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

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

public class HighlightAdapter implements BaseHighlightAdapter {
  private Highlighter highlighter;
  private int maxNumFragments;
  public HighlightAdapter(){
	this(1000);
  }
  public HighlightAdapter(int _maxNumFragments){
    maxNumFragments = _maxNumFragments;
  }
  public Match[] highlight(int docID,IndexSearcher searcher,Query query) throws IOException
  {
    final QueryScorer scorer = new QueryScorer(query);
    final IndexReader reader = searcher.getIndexReader();
    highlighter=new Highlighter(scorer);
  
    ArrayList<Match> matchList = new ArrayList<Match>();
    StoredDocument document = reader.document(docID);
    Iterator<StorableField> iterator = document.iterator();
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
    try{
	  while(iterator.hasNext()) {
	    StorableField field = iterator.next();
	    String fieldContent = searcher.doc(docID).getField(field.name()).stringValue();
	    String[] bestFragments = highlighter.getBestFragments(analyzer, field.name(), fieldContent, maxNumFragments);
		for(String bestFragment:bestFragments){
		  Integer[] prePositions = getPositions(bestFragment,"<B>");
		  Integer[] postPositions = getPositions(bestFragment,"</B>");
		  String matched;
		  matched = bestFragment.replace("<B>","");
		  matched = matched.replace("</B>","");
		
		  for(int i = 0;i < prePositions.length;i++){
		    matchList.add(new Match(matched,prePositions[i] - i * 3 - i * 4,postPositions[i] - prePositions[i] - 3));
		  }
	    }
	  }
    }
	catch(InvalidTokenOffsetsException e) {}
    return matchList.toArray(new Match[matchList.size()]);
  }
  private Integer[] getPositions(String bestFragment,String tag){
    ArrayList<Integer> posList = new ArrayList<Integer>();
    int delta = 0;
    while(true){
      if((delta = bestFragment.indexOf(tag,delta)) == -1)
        break;
      posList.add(new Integer(delta));
      delta += 1;
    }
    return posList.toArray(new Integer[0]);
  }
}
