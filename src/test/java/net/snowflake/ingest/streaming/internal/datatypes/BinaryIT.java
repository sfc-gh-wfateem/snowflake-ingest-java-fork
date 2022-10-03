package net.snowflake.ingest.streaming.internal.datatypes;

import java.util.Arrays;
import java.util.Random;

import net.snowflake.client.jdbc.internal.org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class BinaryIT extends AbstractDataTypeTest {

  @Test
  public void testBinary() throws Exception {
    testJdbcTypeCompatibility("BINARY", new byte[0], new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", new byte[3], new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("a0b0c0d0e0f0"), new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("aaff"), new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("0000000000000000000000000000000000000000000000000000000000000000"), new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("000000000000000000000000000000000000000000000000000000000000000000"), new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("aaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("aaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", Hex.decode("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"), new ByteArrayProvider());

    testJdbcTypeCompatibility(
        "BINARY", new byte[] {Byte.MIN_VALUE, 0, Byte.MAX_VALUE}, new ByteArrayProvider());
    testJdbcTypeCompatibility(
        "BINARY", "212D", new byte[] {33, 45}, new StringProvider(), new ByteArrayProvider());
    testJdbcTypeCompatibility(
        "BINARY",
        "0F00030A057F",
        new byte[] {15, 0, 3, 10, 5, 127},
        new StringProvider(),
        new ByteArrayProvider());
    testJdbcTypeCompatibility("BINARY", new byte[8 * 1024 * 1024], new ByteArrayProvider());
  }

  @Test
  public void testRandomBinaryArraysOfVariousSizes() throws Exception {
    Random random = new Random();
    for (int arraiLength : Arrays.asList(20, 256, 10 * 1024, 1024 * 1024)) {
      for (int iteration = 0; iteration < 10; iteration++) {
        byte[] input = new byte[arraiLength];
        random.nextBytes(input);
        testJdbcTypeCompatibility("BINARY", input, new ByteArrayProvider());
      }
    }
  }
}
