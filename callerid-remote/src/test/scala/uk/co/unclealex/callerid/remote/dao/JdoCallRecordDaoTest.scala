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
 * @author unclealex72
 *
 */
package uk.co.unclealex.callerid.remote.dao

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.List
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.co.unclealex.callerid.remote.model.CallRecord

import static org.junit.Assert.*
import static org.hamcrest.Matchers.*

/**
 * @author alex
 *
 */
class JdoCallRecordDaoTest extends AbstractDaoTest {

    @Autowired
    var CallRecordDao callRecordDao

    extension val DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Test
    def void testStoreAndGet() {
        val firstCallRecord = new CallRecord => [
            callDate = "05/09/1972 09:12:00".parse
            telephoneNumber = "0125698113113"
        ]
        val secondCallRecord = new CallRecord => [
            callDate = "05/09/1972 09:13:00".parse
            telephoneNumber = "0148322114114"
        ]
        callRecordDao.store(firstCallRecord, secondCallRecord)
        val List<CallRecord> persistedCallRecords = callRecordDao.all
        assertThat("The wrong call records were returned.", persistedCallRecords,
            containsInAnyOrder(firstCallRecord, secondCallRecord))
    }
}
