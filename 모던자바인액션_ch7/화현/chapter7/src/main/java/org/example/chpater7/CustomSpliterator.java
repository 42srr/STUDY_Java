package org.example.chpater7;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CustomSpliterator {
    static final String SENTENCE =
            "Nel mezzo del cammin di nostra vita " +
                    "mi ritrovai in una selva oscura" +
                    " ch la dritta via era smarrita " ;
    
    public static void main(String[] args) {
        //System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");

        //Stream<Character> stream = IntStream.range(0, SENTENCE.length())
                //.mapToObj(SENTENCE::charAt);
        //System.out.println("Found " + countWords(stream) + " words");

        // int result = countWords(stream.parallel());
        //System.out.println("Found " + countWords(stream.parallel()) + " words");

        Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
        Stream<Character> stream = StreamSupport.stream(spliterator, true);
        System.out.println("Found " + countWords(stream) + " words.");
    }

    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c: s.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) {
                    counter++;
                }
                lastSpace = false;
            }
        }
        return counter;
    }

    private static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine);

        return wordCounter.getCounter();
    }
}
