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
  public static final String PRE_TAG = "TheMatchPreTagTryToGetThePlaceOfThisTag";   //pre tag
  public static final String POST_TAG = "TheMatchPostTagTryToGetThePlaceOfThisTag";            //post tag
  
  private int maxCatch;
  private FastVectorHighlighter highlighter;
  public FVHAdapter(){
    this(AdapterConstantSet.DEFAULT_MAX_CATCH);
  }
  public FVHAdapter(int _maxCatch){
    maxCatch = _maxCatch;
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
      String[] bestFragments = highlighter.getBestFragments(fieldQuery,searcher.getIndexReader(),docID,field.name(), AdapterConstantSet.DEFAULT_MAX_LENGTH, maxCatch, new SimpleFragListBuilder(), new SimpleFragmentsBuilder(), new String[]{PRE_TAG}, new String[]{POST_TAG},  new DefaultEncoder());
      for(String bestFragment:bestFragments){
        Integer[] prePositions = getPositions(bestFragment,PRE_TAG);
        Integer[] postPositions = getPositions(bestFragment,POST_TAG);
        String matched;
        matched = removeTag(bestFragment,PRE_TAG);
        matched = removeTag(matched,POST_TAG);
        System.out.println(matched);
        
        for(int i = 0;i < prePositions.length;i++){
          matchList.add(new Match(matched,prePositions[i] - i * (PRE_TAG.length()) - i * (POST_TAG.length()),postPositions[i] - prePositions[i]));
        }
      }
    }
    return matchList.toArray(new Match[0]);
  }
  private Integer[] getPositions(String bestFragment,String tag){
    ArrayList<Integer> posList = new ArrayList<Integer>(0);
    int delta = 0;
    while(true){
      if((delta = bestFragment.indexOf(tag,delta)) == -1)
        break;
      posList.add(new Integer(delta));
      delta += 1;
    }
    return posList.toArray(new Integer[0]);
  }
  private String removeTag(String bestFragment,String tag){
    String[] splits = bestFragment.split(tag);
    String match = "";
    for(String s:splits){
      match = match + s;
    }
    return match;
  }
  // TODO for 睿謙
}
