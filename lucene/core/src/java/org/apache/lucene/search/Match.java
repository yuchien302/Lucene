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
  public Match(String _context,int _position){
    context = _context;
    position = _position;
  }

  public String toString(int maxMatchLength){
    //TODO find the required length string from context and output
    
//    context = "A a soft a soft";
//    maxMatchLength = 20;
//    position = 4;
    
    int end = context.indexOf(" ", position);
    int qlength = end-position;
    int part = (int) Math.ceil((maxMatchLength-qlength)/2);
    
    end = ((end+part)>context.length())? context.length() : end+part;
    int begin = (position-part)<0? 0 : position-part;
    
    end = context.indexOf(" ", end);
    begin = context.lastIndexOf(" ", begin);

    begin = begin<0? 0 : begin;
    end = (end>context.length()||end==-1)? context.length() : end;
    
    
//    System.out.println("[important!!]"+context.substring(begin, end).trim());
    return context.substring(begin, end).trim();
  }
}
