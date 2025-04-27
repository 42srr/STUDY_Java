package map;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Map.Entry;

public class Sort {

    public static void main(String[] args) {
        Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James Bond"));

        favouriteMovies.entrySet()
                .stream()
                .sorted(Entry.comparingByKey())
                .forEachOrdered(System.out::println);
    }
}
