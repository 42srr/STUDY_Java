package org.example.chpater7;

import java.util.function.LongUnaryOperator;

public class ForkJoinHarness {
    public static void main(String[] args) {
        System.out.println("Forkjoin sum done in: " +
                measurePerf(ForkJoinSumCalculator::forkJoinSum, 10_000_000L) + " msecs");
    }

    public static long measurePerf(LongUnaryOperator f, long n) {
        long fastest = Long.MAX_VALUE;
        long start = System.currentTimeMillis();
        long result = f.applyAsLong(n);
        long end = System.currentTimeMillis();
        long duration = end - start;
        if (duration < fastest) {
            fastest = duration;
        }
        return fastest;
    }
}
