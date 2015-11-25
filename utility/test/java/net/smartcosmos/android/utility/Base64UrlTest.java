package net.smartcosmos.android.utility;

import org.junit.Test;
import org.junit.Assert;

public class Base64UrlTest {

    static final String b64urlEncoded = "YnVua2hvdXNlQGJhbnphaS5jb20";
    static final String b64urlDecoded = "bunkhouse@banzai.com";

    @Test
    public void TestDecode()
    {
        String b64urlCompare = Base64Url.decode(b64urlEncoded);
        Assert.assertEquals(b64urlDecoded, b64urlCompare);
    }

    @Test
    public void TestEncode()
    {
        String b64urlCompare = Base64Url.encode(b64urlDecoded);
        Assert.assertEquals(b64urlEncoded, b64urlCompare);
    }
}
