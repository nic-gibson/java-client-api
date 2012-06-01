/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.marklogic.client.config.Tuple;
import com.marklogic.client.config.TypedDistinctValue;
import com.marklogic.client.io.TuplesHandle;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TuplesHandleTest {
	private static final Logger logger = (Logger) LoggerFactory
			.getLogger(QueryOptionsHandleTest.class);
	
    @BeforeClass
    public static void beforeClass() {
        Common.connectAdmin();
    }

    @AfterClass
    public static void afterClass() {
        Common.release();
    }

    @Test
    public void testValuesHandle() throws IOException, ParserConfigurationException, SAXException {
        File f = new File("src/test/resources/tuples.xml");
        FileInputStream is = new FileInputStream(f);

        MyTuplesHandle t = new MyTuplesHandle();
        t.parseTestData(is);

        Tuple[] tuples = t.getTuples();

        assertEquals("Four tuples expected", 4, tuples.length);

        TypedDistinctValue[] dv = tuples[0].getValues();

        assertEquals("Two values per tuple expected", 2, dv.length);
        assertEquals("First is long", "xs:unsignedLong",  dv[0].getType());
        assertEquals("Second is string", "xs:string", dv[1].getType());
        assertEquals("Frequency is 1", 1, tuples[0].getCount());
        assertEquals("First value", (long) 45375, (long) dv[0].get(Long.class));
        assertEquals("Second value", "1/160", dv[1].get(String.class));
    }

    public class MyTuplesHandle extends TuplesHandle {
        public void parseTestData(InputStream stream) {
            receiveContent(stream);
        }
    }
}