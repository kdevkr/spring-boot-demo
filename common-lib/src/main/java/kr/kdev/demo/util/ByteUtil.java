package kr.kdev.demo.util;

import com.google.common.primitives.Bytes;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class ByteUtil {

    public static byte[] toBytes(float value) {
        int intBits = Float.floatToIntBits(value);
        return new byte[]{
                (byte) (intBits >> 24),
                (byte) (intBits >> 16),
                (byte) (intBits >> 8),
                (byte) (intBits)
        };
    }

    public static float toFloat32(byte[] bytes) {
        int intBits = bytes[0] << 24
                | (bytes[1] & 0xFF) << 16
                | (bytes[2] & 0xFF) << 8
                | (bytes[3] & 0xFF);
        return Float.intBitsToFloat(intBits);
    }

    public static float toFloat32LE(byte[] bytes) {
        Bytes.reverse(bytes);
        return toFloat32(bytes);
    }

    public static float toFloat32MLE(byte[] bytes) {
        byte[] nBytes = new byte[bytes.length];
        System.arraycopy(bytes, 2, nBytes, 0, 2);
        System.arraycopy(bytes, 0, nBytes, 2, 2);
        return toFloat32(nBytes);
    }
}
