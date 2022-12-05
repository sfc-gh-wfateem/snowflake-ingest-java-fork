package net.snowflake.ingest.streaming.internal;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

public class FileColumnPropertiesTest {

  @Test
  public void testFileColumnPropertiesConstructor() {
    // Test simple construction
    RowBufferStats stats = new RowBufferStats("COL");
    stats.addStrValue("bcd");
    stats.addStrValue("abcde");
    FileColumnProperties props = new FileColumnProperties(stats);
    Assert.assertEquals("6162636465", props.getMinStrValue());
    Assert.assertNull(props.getMinStrNonCollated());
    Assert.assertEquals("626364", props.getMaxStrValue());
    Assert.assertNull(props.getMaxStrNonCollated());

    // Test that truncation is performed
    stats = new RowBufferStats("COL");
    stats.addStrValue("aßßßßßßßßßßßßßßßß");
    Assert.assertEquals(33, stats.getCurrentMinStrValue().length);
    props = new FileColumnProperties(stats);
    Assert.assertNull(props.getMinStrNonCollated());
    Assert.assertNull(props.getMaxStrNonCollated());
    Assert.assertEquals(32 * 2, props.getMinStrValue().length());
    Assert.assertEquals(32 * 2, props.getMaxStrValue().length());
  }

  @Test
  public void testTruncation() throws DecoderException {
    // Test empty input
    Assert.assertEquals("", FileColumnProperties.truncateBytesAsHex(new byte[0], false));
    Assert.assertEquals("", FileColumnProperties.truncateBytesAsHex(new byte[0], true));

    // Test basic case
    Assert.assertEquals("aa", FileColumnProperties.truncateBytesAsHex(Hex.decodeHex("aa"), false));
    Assert.assertEquals("aa", FileColumnProperties.truncateBytesAsHex(Hex.decodeHex("aa"), true));

    // Test exactly 32 bytes
    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            false));
    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            true));

    Assert.assertEquals(
        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
            false));

    Assert.assertEquals(
        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"),
            true));

    // Test 1 truncate up
    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            false));

    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            true));

    // Test one overflow
    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaafffffffffffffffffffffffffffffffaaff",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex("aaaaaaaaaaaaaaaaaaaaaaaaaaaaafffffffffffffffffffffffffffffffaaffffffff"),
            false));
    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaafffffffffffffffffffffffffffffffab00",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex("aaaaaaaaaaaaaaaaaaaaaaaaaaaaafffffffffffffffffffffffffffffffaaffffffff"),
            true));

    // Test many overflow
    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaafffffffffffffffffffffffffffffffffff",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaafffffffffffffffffffffffffffffffffffffffffffffffffffff"),
            false));
    Assert.assertEquals(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaab00000000000000000000000000000000000",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex(
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaafffffffffffffffffffffffffffffffffffffffffffffffffffff"),
            true));

    // Test infinity
    Assert.assertEquals(
        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex(
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffcccccccccccc"),
            false));
    Assert.assertEquals(
        "Z",
        FileColumnProperties.truncateBytesAsHex(
            Hex.decodeHex(
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffcccccccccccc"),
            true));
  }
}
