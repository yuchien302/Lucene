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
  public static final int maxNumWord = AdapterConstantSet.DEFAULT_MAX_LENGTH;
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
	    String[] bestFragments = highlighter.getBestFragments(analyzer, field.name(), fieldContent,1000);
		for(String bestFragment:bestFragments){
		  String lowerBestFragment = bestFragment.toLowerCase();
		  String keyword = lowerBestFragment.substring(lowerBestFragment.indexOf("<b>",0)+3,lowerBestFragment.indexOf("</b>",0));
		  lowerBestFragment = lowerBestFragment.replace("<b>","");
		  lowerBestFragment = lowerBestFragment.replace("</b>","");
		  bestFragment = bestFragment.replace("<B>","");
		  bestFragment = bestFragment.replace("</B>","");
		  int flag = 0;
		  while(true){
		    int delta = bestFragment.indexOf(keyword,flag);
			String matched = bestFragment.substring(Math.max(0,delta-maxNumWord), Math.min(bestFragment.length(),delta+maxNumWord));  //sanity chec
			delta = matched.indexOf(keyword,flag);

		    if(delta==-1) // if no keyword in text
		      break;
			flag = delta+keyword.length()+1;
		    matchList.add(new Match(matched,delta,keyword.length()));
		  }
	    }
	  }
    }
	catch(InvalidTokenOffsetsException e) {}
    return matchList.toArray(new Match[matchList.size()]);
  }
}
