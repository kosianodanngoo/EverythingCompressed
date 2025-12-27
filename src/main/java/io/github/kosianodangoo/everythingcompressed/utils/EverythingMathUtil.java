package io.github.kosianodangoo.everythingcompressed.utils;

import net.minecraft.util.Mth;

public class EverythingMathUtil {
    public static long SHORT_MASK = 0xffffL;

    public static long combineStort(int num1, int num2, int num3, int num4) {
        return combineStort((short) num1, (short) num2, (short) num3, (short) num4);
    }

    public static long combineStort(short num1, short num2, short num3, short num4) {
        return Short.toUnsignedLong(num1) << 48 | Short.toUnsignedLong(num2) << 32 | Short.toUnsignedLong(num3) << 16 | Short.toUnsignedLong(num4);
    }

    public static long overflowingAdd(long num1, long num2) {
        long overflowedAmount = num1 + num2 - Long.MAX_VALUE;
        boolean overflowed = overflowedAmount > 0;
        return overflowed ? Long.MAX_VALUE : num1 + num2;
    }

    public static int overflowingLongToInt(long num) {
        return (int) Mth.clamp(num, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
