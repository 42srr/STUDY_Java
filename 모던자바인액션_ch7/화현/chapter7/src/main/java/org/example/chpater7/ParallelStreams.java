package org.example.chpater7;

import java.util.stream.LongStream;

public class ParallelStreams {

    public static class Accumulator {
        public long total;

        public void add(long value) {
            total += value;
        }
    }
    public static void main(String[] args) {
        long num = 10_000_000L;
        long sideEffectSum = sideEffectSum(num);
        long sideEffectParallelSum = sideEffectParallelSum(num);
        System.out.println("sideEffectSum = " + sideEffectSum);
        System.out.println("sideEffectParallelSum = " + sideEffectParallelSum);
    }

    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).forEach(accumulator::add);

        return accumulator.total;
    }

    public static long sideEffectParallelSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);

        return accumulator.total;
    }
}
