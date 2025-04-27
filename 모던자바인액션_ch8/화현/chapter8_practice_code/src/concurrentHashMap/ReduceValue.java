package concurrentHashMap;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReduceValue {
    public static void main(String[] args) {
//        ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
//        map.put("Raphael", 23L);
//        map.put("Raphael1", 21L);
//        map.put("Raphael2", 27L);
//        map.put("Raphael3", 23L);
//        long parallelismThreshold = 1;
//        Optional<Long> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
//        System.out.println("maxValue = " + maxValue);

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("apple", 1);
        map.put("banana", 2);

        Set<String> keys = map.keySet();
        System.out.println(keys);

        keys.remove("apple");
        System.out.println(map);

        Set<String> concurrentSet = ConcurrentHashMap.newKeySet();

        concurrentSet.add("apple");
        concurrentSet.add("banana");

        System.out.println(concurrentSet);

        concurrentSet.remove("apple");
        System.out.println(concurrentSet);


    }
}
