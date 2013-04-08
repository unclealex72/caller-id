/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author alex
 *
 */
package uk.co.unclealex.xtend

import java.util.Comparator
import org.eclipse.xtext.xbase.lib.Functions

/**
 * Extension methods for {@link Comparator}s.
 */
class ComparatorExtensions {
    /**
     * Create a {@link Comparator} that compares two objects by transforming them using the supplied 
     * {@link Functions$Function1} and then comparing the results.
     * @param f The function used to reduce each object.
     */
    def static <R extends Comparable<? super R>, C> Comparator<C> compareBy(Functions$Function1<C, R> f) {
        [c1, c2|f.apply(c1).compareTo(f.apply(c2))]
    }

    /**
     * Concatenate comparators by using the second comparator if the first returns 0.
     * @param c1 The first comparator.
     * @param c2 The second comparator.
     */
    def static <C> Comparator<C> then(Comparator<C> c1, Comparator<C> c2) {
        [ C o1, C o2 |
            val cmp = c1.compare(o1, o2)
            if(cmp == 0) c2.compare(o1, o2) else cmp
        ]
    }
    
    /**
     * Reverse a comparator.
     * @param The comparator to reverse.
     * @param A comparator that orders largest first with respect to the original comparator.
     */
     def static <C> Comparator<C> reversed(Comparator<C> comparator) {
         [ C o1, C o2 |
             comparator.compare(o2, o1)
         ]
     }
}
