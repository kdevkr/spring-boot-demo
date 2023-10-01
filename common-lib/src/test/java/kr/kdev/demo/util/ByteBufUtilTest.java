package kr.kdev.demo.util;

import io.netty.buffer.PooledByteBufAllocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HexFormat;

@DisplayName("ByteBuf Test")
class ByteBufUtilTest {
    @DisplayName("Float32 Mid Little Endian")
    @Test
    void TestFloat32MLE() {
        String hexadecimal = "00004260"; // 00 00 42 60
        byte[] bytes = HexFormat.of().parseHex(hexadecimal);
        float originValue = PooledByteBufAllocator.DEFAULT.buffer()
                .writeBytes(new byte[]{bytes[2], bytes[3]}) // CD (42 60)
                .writeBytes(new byte[]{bytes[0], bytes[1]}) // AB (00 00)
                .readFloat();
        float value = BigDecimal.valueOf(originValue)
                .setScale(1, RoundingMode.HALF_UP)
                .floatValue();
        Assertions.assertEquals(56.0f, value);
    }

    @DisplayName("Float32 Mid Little Endian using Netty ByteBuf")
    @Test
    void TestFloat32MLEUsingByteBufUtil() {
        float value = 56f;
        byte[] bytes = ByteBufUtil.toBytes(value);
        float float32MLE = ByteBufUtil.toFloat32MLE(bytes);
        float float32LE = ByteBufUtil.toFloat32LE(bytes);
        float float32 = ByteBufUtil.toFloat32(bytes);

        Assertions.assertNotEquals(value, float32MLE);
        Assertions.assertNotEquals(value, float32LE);
        Assertions.assertEquals(value, float32);
    }

}
