package kr.kdev.demo.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class ByteBufUtil {

    public static byte[] toBytes(float value) {
        ByteBuf byteBuf = Unpooled.buffer(4).writeFloat(value);
        return io.netty.buffer.ByteBufUtil.getBytes(byteBuf);
    }

    public static float toFloat32(byte[] bytes) {
        return PooledByteBufAllocator.DEFAULT.buffer()
                .writeBytes(bytes)
                .readFloat();
    }

    public static float toFloat32LE(byte[] bytes) {
        return PooledByteBufAllocator.DEFAULT.buffer()
                .writeBytes(bytes)
                .readFloatLE();
    }

    public static float toFloat32MLE(byte[] bytes) {
        return PooledByteBufAllocator.DEFAULT.buffer()
                .writeBytes(new byte[]{bytes[2], bytes[3]})
                .writeBytes(new byte[]{bytes[0], bytes[1]})
                .readFloat();
    }
}
