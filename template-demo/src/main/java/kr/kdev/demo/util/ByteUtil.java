package kr.kdev.demo.util;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Longs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HexFormat;

@Slf4j
@UtilityClass
public class ByteUtil {

    public static HexFormat HEX_FORMAT = HexFormat.of();

    public static BigDecimal toBigDecimal(ByteBuffer bb, int scale) {
        return new BigDecimal(new BigInteger(bb.array()), scale);
    }

    public static String toHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

    public static byte[] toBytes(String hex) {
        return HexFormat.of().parseHex(hex);
    }

    public static byte[] toBytes(ByteBuffer bb) {
        byte[] bytes = new byte[bb.remaining()];
        bb.get(bytes);
        return bytes;
    }

    public static double toFloat(byte[] bytes) {
        if (bytes.length < 4) {
            throw new ArrayIndexOutOfBoundsException("Require 4 bytes and more");
        }

        return Float.intBitsToFloat(toInt(bytes));
    }

    public static double toDouble(byte[] bytes) {
        return Double.longBitsToDouble(toLong(bytes));
    }

    public static int toInt(byte[] bytes) {
        return new BigInteger(bytes).intValue();
    }

    public static long toLong(byte[] bytes) {
        return new BigInteger(bytes).longValue();
    }

    public static byte[] toBytes(float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    public static float toFloatWithByteBuffer(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static byte[] concat(byte[] b1, byte[] b2) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(b1.length + b2.length);
        byteBuffer.put(b1);
        byteBuffer.put(b2);
        return byteBuffer.array();
    }

    public static void main(String[] args) {
        log.info("ByteOrder: {}", ByteOrder.nativeOrder()); // Java use BIG_ENDIAN.

        long l = 1234;
        byte[] byteArray = ByteBuffer.allocate(8).putLong(l).array();
        System.out.println(byteArray);

        String s = HEX_FORMAT.formatHex(byteArray);
        System.out.println(s);

        byte[] bytes1 = Bytes.concat(new byte[]{0, 0}, new byte[]{4, -46});
        System.out.println(ByteBuffer.wrap(bytes1).getFloat());

        float f = 65556;
        byte[] bytes = ByteBuffer.allocate(4).putFloat(f).order(ByteOrder.BIG_ENDIAN).array();
//        f = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
        ByteBuf byteBuf = buffer.writeBytes(bytes);
        float v = byteBuf.readFloat();
        log.info("{} , {}", v, ByteBufUtil.decodeHexDump("10"));
    }
}
