package org.example.chpater7;

import java.util.function.LongUnaryOperator;

public class ParallelStreamsHarness {

    public static void main(String[] args) {
        System.out.println("ParallelStreams parallel sum done in: " +
                measurePerf(ParallelStreams::sideEffectParallelSum, 10_000_000L) + " msecs");
    }

    public static long measurePerf(LongUnaryOperator f, long n) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            long result = f.applyAsLong(n);
            long duration = System.currentTimeMillis() - start;
            System.out.println("Result : " + result);
            if (duration < fastest) {
                fastest = duration;
            }
        }
        return fastest;
    }
}
