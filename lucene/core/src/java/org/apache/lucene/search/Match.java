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

public class Match {
  // TODO 
  private String context;
  private int position;
  private int qlength;
  private int trimPosition;
  
  public Match(String _context,int _position, int _qlength){
    
    context = _context;
    position = trimPosition = _position;
    qlength = _qlength;
    
  }
  public int getPosition(){
    return trimPosition;
  }
  
  public String getContext(){
    return context;
  }
  
  public int getQueryLength(){
    return qlength;
  }
  
  public String trim(int maxMatchLength){
    //TODO find the required length string from context and output
//    context = "Lucene,sdfv Lucene, qe Lucene,davdsfv Lucene .";
//    maxMatchLength = 20;
//    position = 38;

    int part = (int) Math.ceil((maxMatchLength-qlength)/2);
    
    int end = ((position+qlength+part)>context.length())? context.length() : position+qlength+part;
    int begin = (position-part)<0? 0 : position-part;
    
    end = context.indexOf(" ", end);
    begin = context.lastIndexOf(" ", begin)+1;

    end = (end>context.length()||end==-1)? context.length() : end;
    
    trimPosition = position - begin;

    return context.substring(begin, end);
  }
}
