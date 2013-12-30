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

public class Summary {
  // TODO
  private int docId;
  private int matchCount;
  private Match[] matches;
  static private int defaultFragmentLength=20;
 
  
  public Summary(Match[] matches, int docId) {
    this.docId = docId;
    this.matches = matches;
    if(matches!=null)
      this.matchCount = matches.length;
    
  }
  private String getPrefix(){
    String str=("  In docId=" + docId + ", matched " + matchCount);
    if( (matchCount==1) || (matchCount==0) )
      str += " time.\n";
    else
      str += " times.\n";
    return str;
  }
  
  public String toString(){
    
    String str = getPrefix();
    for(int i = 0; i<matchCount; i++){
      str+=("  #" + (i+1) + " : position=" + matches[i].getPosition() + " : " + matches[i].getContext() + "\n");
    }
    return str;
    
  }
  
  public String trim(){
    return trim(defaultFragmentLength);
  }
  
  public String trim(int maxLengthReturned){
    
    String str = getPrefix();    
    for(int i = 0; i<matchCount; i++){
      matches[i].trim(maxLengthReturned); // so that getPosition() will be correct to trim result;
      str+=("  #" + (i+1) + " : position=" + matches[i].getPosition() + " : " + matches[i].trim(maxLengthReturned) + "\n");
    }
    return str;
    
  }
}
