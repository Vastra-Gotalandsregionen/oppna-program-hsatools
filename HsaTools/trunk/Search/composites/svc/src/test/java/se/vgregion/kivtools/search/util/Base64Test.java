/**
 * Copyright 2009 Västa Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
package se.vgregion.kivtools.search.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

public class Base64Test extends TestCase
{
    private static final long SEED = 12345678;
    private static Random s_random = new Random(SEED);
    
    private byte[] createData(int length) throws Exception
    {
        byte[] bytes = new byte[length];
        s_random.nextBytes(bytes);
        return bytes;
    }
    
    private void runStreamTest(int length) throws Exception
    {
        byte[] data = createData(length);
        ByteArrayOutputStream out_bytes = new ByteArrayOutputStream();
        OutputStream out = new Base64.OutputStream(out_bytes);
        out.write(data);
        out.close();
        byte[] encoded = out_bytes.toByteArray();
        byte[] decoded = Base64.decode(encoded, 0, encoded.length, Base64.NO_OPTIONS);
        assertTrue(Arrays.equals(data, decoded));
        
        Base64.InputStream in = new Base64.InputStream(new ByteArrayInputStream(encoded));
        out_bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[3];
        for (int n = in.read(buffer); n > 0; n = in.read(buffer)) {
            out_bytes.write(buffer, 0, n);
        }
        out_bytes.close();
        in.close();
        decoded = out_bytes.toByteArray();
        assertTrue(Arrays.equals(data, decoded));
    }
    
    public void testStreams() throws Exception
    {
        for (int i = 0; i < 100; ++i) {
            runStreamTest(i);
        }
        for (int i = 100; i < 2000; i += 250) {
            runStreamTest(i);
        }
        for (int i = 2000; i < 80000; i += 1000) {
            runStreamTest(i);
        }
    }
}
