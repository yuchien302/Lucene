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
    matches[0] = new Match("2014 Happy New Year!",5);
    matches[1] = new Match("Happy Birthday!",0);
    matches[2] = new Match("Very Happy!!! Hahaha kerkerker =)",5);
    summary = new Summary(matches,docID);
    System.out.println(summary.toString());
    String matchString = new String("In docId=0, matched 3 times.\n#0 : 2014 Happy New Year!at position:5\n#1 : Happy Birthday!at position:0\n#2 : Very Happy!!! Hahaha kerkerker =)at position:5\n");
    assertEquals(matchString,summary.toString());
  }
  /*public void testToStringWithShortInteger(){
    String string1 = new String("It explains the creation of JUnit tests and how to run them in Eclipse or via own code.");
    String string2 = new String("The following code shows a JUnit test method.");
    String string3 = new String("To run your JUnit tests outside Eclipse you need to add the JUnit library jar to the classpath of your program.");
    String matchStr1 = new String("of JUnit tests");
    String matchStr2 = new String("a JUnit test");
    String matchStr3 = new String("your JUnit tests");
    matches[0] = new Match(string1,28);
    matches[1] = new Match(string2,27);
    matches[2] = new Match(string3,12);
    summary = new Summary(matches,docID);
    System.out.println(summary.toString(10));
    String matchString = new String("In docId=0, matched 3 times.\n#0 : "+matchStr1+"at position:28\n#1 : "+matchStr2+"at position:27\n#2 : "+matchStr3+"at position:12\n");
    assertEquals(matchString,summary.toString());
    
  }*/

  /*public void testToStringWithLongInteger(){
  String string1 = new String("It explains the creation of JUnit tests and how to run them in Eclipse or via own code.");
  String string2 = new String("The following code shows a JUnit test method.");
  String string3 = new String("To run your JUnit tests outside Eclipse you need to add the JUnit library jar to the classpath of your program.");
  String matchStr1 = new String("creation of JUnit tests and how");
  String matchStr2 = new String("code shows a JUnit test method.");
  String matchStr3 = new String("To run your JUnit tests outside");
  matches[0] = new Match(string1,28);
  matches[1] = new Match(string2,27);
  matches[2] = new Match(string3,12);
  summary = new Summary(matches,docID);
  System.out.println(summary.toString(30));
  String matchString = new String("In docId=0, matched 3 times.\n#0 : "+matchStr1+"at position:28\n#1 : "+matchStr2+"at position:27\n#2 : "+matchStr3+"at position:12\n");
  assertEquals(matchString,summary.toString());
  
}*/   


}
