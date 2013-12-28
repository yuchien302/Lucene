package org.apache.lucene.search;

import java.io.IOException;

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

public interface BaseHighlightAdapter {
  public Match[] highlight(int docID,IndexSearcher searcher,Query query)  throws IOException;
  
}
/*
 * This class is used to define some useful constant when develop adapters.
 * DEFAULT_MAX_LENGTH is the length the default length of match string.
 * DEFAULT_MAX_CATCH is the max number of match we get, this value is set
 * for get as much as we can. 
 */
class AdapterConstantSet{
  public static final int DEFAULT_MAX_LENGTH = 100;
  public static final int DEFAULT_MAX_CATCH = 2147483647;
  //Do not allow to initialize a object of this class
  private AdapterConstantSet(){}
}