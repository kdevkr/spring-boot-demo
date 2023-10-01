package kr.kdev.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

@Slf4j
@DisplayName("Byte Test")
class ByteUtilTest {

    @BeforeAll
    static void init() {
        log.info("ByteOrder: {}, but java use Big Endian", ByteOrder.nativeOrder());
    }

    @DisplayName("Float32 with Byte Orders")
    @Test
    void TestFloat32() {
        float value = 56.0f;
        byte[] bytes = ByteUtil.toBytes(value);
        float float32 = ByteUtil.toFloat32(bytes);
        float float32LE = ByteUtil.toFloat32LE(bytes);
        float float32MLE = ByteUtil.toFloat32MLE(bytes);

        Assertions.assertEquals(float32, value);
        Assertions.assertNotEquals(float32LE, value);
        Assertions.assertNotEquals(float32MLE, value);
    }
}
