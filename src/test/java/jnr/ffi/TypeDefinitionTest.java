/*
 * Copyright (C) 2007-2010 Wayne Meissner
 *
 * This file is part of the JNR project.
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

package jnr.ffi;

import jnr.ffi.types.int8_t;
import jnr.ffi.types.size_t;
import jnr.ffi.types.u_int32_t;
import jnr.ffi.types.u_int8_t;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeDefinitionTest {

    public TypeDefinitionTest() {


    }
    public static interface TestLib {
        public @int8_t int add_int8_t(@int8_t int i1, @int8_t int i2);
        public @int8_t int add_int8_t(@int8_t Integer i1, @int8_t int i2);
        public @int8_t Long add_int8_t(@int8_t Long i1, @int8_t Integer i2);
        public int add_uint8_t(@u_int8_t int i1, @u_int8_t int i2);
        public int ret_uint8_t(@u_int8_t int i1);
        public @u_int32_t long ret_uint32_t(@u_int32_t long i1);
        public @u_int32_t short ret_uint32_t(@u_int32_t byte i1);
        public void ret_long(@size_t int i1);
    }

    static TestLib testlib;

    @BeforeAll
    public static void setUpClass() throws Exception {
        testlib = TstUtil.loadTestLib(TestLib.class);
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
        testlib = null;
    }

    @Test public void doNothing() {
    }

    @Test public void returnUnsigned8() {
        int i1 = 0xdead0001;
        // when passed to the native function, only the lowest 8 bits are passed
        assertEquals(1, testlib.ret_uint8_t(i1), "incorrect value returned");
    }
    @Test public void addUnsigned8() {
        int i1 = 0xdead0001;
        int i2 = 0xbeef0002;
        // when passed to the native function, only the lowest 8 bits are passed
        assertEquals(3, testlib.add_uint8_t(i1, i2), "did not add correctly");
    }
}
