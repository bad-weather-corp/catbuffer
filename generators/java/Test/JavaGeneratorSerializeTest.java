/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.*;
import java.io.*;
import java.nio.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import catapult.builders.*;
import jdk.jfr.Timestamp;

public class JavaGeneratorSerializeTest {

    @Test
    public static void testMosaicProperty()
    {
        MosaicPropertyBuilder test = new MosaicPropertyBuilder();
        test.setId(MosaicPropertyIdBuilder.DURATION);
        test.setValue(5);
        byte[] ser = test.serialize();
        ByteArrayInputStream bs = new ByteArrayInputStream(ser);
        DataInput di = new DataInputStream(bs);
        MosaicPropertyBuilder test2 = MosaicPropertyBuilder.loadFromBinary(di);
        assertEquals(test.getId(), test2.getId());
        assertEquals(test.getValue(), test2.getValue());
    }

    @Test
    public static void testTransferTx()
    {
        TransferTransactionBuilder test = new TransferTransactionBuilder();
        test.setSize(5);
        java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(64);
        bb.put(new byte[64]);
        test.setSignature(bb);
        bb = java.nio.ByteBuffer.allocate(32);
        bb.put(new byte[32]);
        test.setSigner(bb);
        test.setVersion((short)2);
        test.setType(EntityTypeBuilder.RESERVED);
        test.setFee(10);
        test.setDeadline(100);
        ByteBuffer bb1 = java.nio.ByteBuffer.allocate(25);
        bb1.put(new byte[25]);
        test.setRecipient(bb1);
        ByteBuffer bb2 = java.nio.ByteBuffer.allocate(30);
        bb2.put(new byte[30]);
        test.setMessage(bb2);
        java.util.ArrayList<UnresolvedMosaicBuilder> mosaics = new java.util.ArrayList<UnresolvedMosaicBuilder>(5);
        mosaics.add(new UnresolvedMosaicBuilder());
        test.setMosaics(mosaics);
        byte[] ser = test.serialize();
        ByteArrayInputStream bs = new ByteArrayInputStream(ser);
        DataInput di = new DataInputStream(bs);
        TransferTransactionBuilder test2 = TransferTransactionBuilder.loadFromBinary(di);
        assertEquals(test.getSize(), test2.getSize());
    }
}
