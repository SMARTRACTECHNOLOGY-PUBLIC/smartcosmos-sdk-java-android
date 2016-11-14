package net.smartcosmos.android.utility;

import org.junit.*;

public class AsciiHexConverterTest {

    @Test
    public void bytesToHexTest() {

        final String expected = "0123456789ABCDEF";
        final byte[] input = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte) 0xEF};

        final String output = AsciiHexConverter.bytesToHex(input);

        Assert.assertEquals(expected, output);
    }

    @Test
    public void bytesToHexReverseTest() {

        final String expected = "EFCDAB8967452301";
        final byte[] input = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte) 0xEF};

        final String output = AsciiHexConverter.bytesToHexReverse(input);

        Assert.assertEquals(expected, output);
    }

    @Test
    public void hexToBytesTest() {

        final String input = "0123456789ABCDEF";
        final byte[] expected = {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte) 0xEF};

        final byte[] output = AsciiHexConverter.hexToBytes(input);

        Assert.assertArrayEquals(expected, output);
    }

    @Test
    public void hexToBytesReverseTest() {

        final String input = "0123456789ABCDEF";
        final byte[] expected = {(byte)0xEF, (byte)0xCD, (byte)0xAB, (byte)0x89, 0x67, 0x45, 0x23, 0x01};

        final byte[] output = AsciiHexConverter.hexToBytesReverse(input);

        Assert.assertArrayEquals(expected, output);
    }
}
