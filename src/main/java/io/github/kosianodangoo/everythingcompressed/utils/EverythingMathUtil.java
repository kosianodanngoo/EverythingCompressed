package io.github.kosianodangoo.everythingcompressed.utils;

public class EverythingMathUtil {
    public static long overflowingAdd(long num1, long num2) {
        long overflowedAmount = num1 + num2 - Long.MAX_VALUE;
        boolean overflowed = overflowedAmount > 0;
        return overflowed ? Long.MAX_VALUE : num1 + num2;
    }
}
