package org.apache.lucene.search;

import org.apache.lucene.util.LuceneTestCase;

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
public class TestSummary extends LuceneTestCase {
  
  
  Match[] matches = new Match[3];
  int docID;
  Summary summary;
  
  public void testToString(){
    matches[0] = new Match("2014 Happy New Year!",5,5);
    matches[1] = new Match("Happy Birthday!",0,5);
    matches[2] = new Match("Very Happy!!! Hahaha kerkerker =)",5,5);
    summary = new Summary(matches,docID);
    String matchString = new String("  In docId=0, matched 3 times.\n  #1 : position=5 : 2014 Happy New Year!\n  #2 : position=0 : Happy Birthday!\n  #3 : position=5 : Very Happy!!! Hahaha kerkerker =)\n");
    assertEquals(matchString,summary.toString());
  }
  public void testTrimWithShortInteger(){
    String string1 = new String("It explains the creation of JUnit tests and how to run them in Eclipse or via own code.");
    String string2 = new String("The following code shows a JUnit test method.");
    String string3 = new String("To run your JUnit tests outside Eclipse you need to add the JUnit library jar to the classpath of your program.");
    String matchStr1 = new String("of JUnit tests");
    String matchStr2 = new String("a JUnit test");
    String matchStr3 = new String("your JUnit tests");
    matches[0] = new Match(string1,28,5);
    matches[1] = new Match(string2,27,5);
    matches[2] = new Match(string3,12,5);
    summary = new Summary(matches,docID);
    String matchString = new String("  In docId=0, matched 3 times.\n  #1 : position=3 : "+matchStr1+"\n  #2 : position=2 : "+matchStr2+"\n  #3 : position=5 : "+matchStr3+"\n");
    assertEquals(matchString,summary.trim(10));
    
  }

  public void testToTrimWithLongInteger(){
  String string1 = new String("It explains the creation of JUnit tests and how to run them in Eclipse or via own code.");
  String string2 = new String("The following code shows a JUnit test method.");
  String string3 = new String("To run your JUnit tests outside Eclipse you need to add the JUnit library jar to the classpath of your program.");
  String matchStr1 = new String("creation of JUnit tests and how");
  String matchStr2 = new String("code shows a JUnit test method.");
  String matchStr3 = new String("To run your JUnit tests outside");
  matches[0] = new Match(string1,28,5);
  matches[1] = new Match(string2,27,5);
  matches[2] = new Match(string3,12,5);
  summary = new Summary(matches,docID);
  String matchString = new String("  In docId=0, matched 3 times.\n  #1 : position=12 : "+matchStr1+"\n  #2 : position=13 : "+matchStr2+"\n  #3 : position=12 : "+matchStr3+"\n");
  assertEquals(matchString,summary.trim(30));
  
}


}
