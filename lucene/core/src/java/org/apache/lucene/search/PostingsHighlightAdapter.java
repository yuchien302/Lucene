package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.search.postingshighlight.PostingsHighlighter;

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

public class PostingsHighlightAdapter implements BaseHighlightAdapter {
  //TODO for 曉飛
  
  
  private static final int final_length=50;
  public PostingsHighlightAdapter() {};

  @Override
  public Match[] highlight(int docID, IndexSearcher searcher, Query query)  throws IOException{
    PostingsHighlighter highlighter = new PostingsHighlighter();
    TopDocs topDocs;
    ScoreDoc[] scoreDocs = new ScoreDoc[1];
    try {
      int hitsPerPage = 10;
      TopScoreDocCollector collector = TopScoreDocCollector.create(
          hitsPerPage, true);
      searcher.search(query, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      if (hits.length == 0) {
        return null;
      }
      for (int i = 0; i < hits.length; i++) {
        if (hits[i].doc == docID) {
          scoreDocs[0] = hits[i];
          break;
        }
      }
      if (scoreDocs[0] == null) {
        return null;
      }
      topDocs = new TopDocs(1, scoreDocs, scoreDocs[0].score);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      // System.out.println("Oh shit!");
      return null;
    }
    try {
      String result = highlighter.highlight("title", query, searcher,
          topDocs)[0];

      return parse(result, final_length);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }

  }

  Match[] parse(String s, int extra_length)
  {
    if(s==null)
      return (new Match[0]);
    ArrayList<Match> result=new ArrayList<Match>();
    int i=0;
    int j=0;
    int length=s.length();
    while(i<length-2)
    {
      if(s.charAt(i)=='<' && s.charAt(i+1)=='b' && s.charAt(i+2)=='>')
      {
        j=i+3;
        while(j<length-3)
        {
          if(s.charAt(j)=='<' && s.charAt(j+1)=='/' && s.charAt(j+2)=='b' && s.charAt(j+3)=='>')
            break;
          j++;
        }
        String temp=s.substring(i+3, j);
        temp=clean(temp,"null");
        
        int k=i-1;
        int prelen=(final_length-temp.length())/2+1;
        if(k<0) k=0;
        while(k>0 && (i-k)<prelen)
        {
          k--;
        }
        while(k>0 && isChar(s.charAt(k)))
        {
          k--;
        }
        
        int prePosition=k;
        prePosition=0;
        String pre=clean(s.substring(prePosition, i), "pre");
        
        k=j+5;
        int postlen=(final_length-temp.length()+1)/2+1;
        if(k>length) k=length;
        while(k<length && (k-j-4)<postlen)
        {
          k++;
        }
        while(k<length && isChar(s.charAt(k-1)))
        {
          k++;
        }
        int postPosition=k;
        postPosition=length;
        String post=clean(s.substring(j+4,postPosition),"post");
        Match match=new Match(pre+temp+post, pre.length(), 0); // TODO change qlength
        result.add(match);
        i=j+3;
      }
      i++;
    }
    return (Match[]) result.toArray(new Match[0]);
  }
  boolean isChar(char c)
  {
    if(c>='a' && c<='z')
    {
      return true;
    }
    if(c>='A' && c<='Z')
    {
      return true;
    }
    return false;
    
  }
  String clean(String toClean, String p)
  {
    String result="";
    result=toClean.replace("<b>","");
    result=result.replace("</b>","");
    if(result.length()==0) return result;
    if(p=="pre")
    {
      if(!isChar(result.charAt(0)))
      {
        result=result.substring(1);
      }
    }
    if(p=="post")
    {
      if(!isChar(result.charAt(result.length()-1)))
      {
        result=result.substring(0,result.length());
      }
    }
    return result;
  }
}
  
  
//  
//  @Override
//  public Match[] highlight(int docID,IndexSearcher searcher,Query query) throws IOException{
//    
//    return null;
//  
//  }
//}



