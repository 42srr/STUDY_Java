package map;

import static java.util.Map.entry;

import java.util.HashMap;
import java.util.Map;

public class PutAll {
    public static void main(String[] args) {
        Map<String, String> family = Map.ofEntries(
                entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));

        Map<String, String> friends = Map.ofEntries(
                entry("Raphael", "Star Wars")
        );

        Map<String, String> everyone = new HashMap<>(family);
        everyone.putAll(friends);
        System.out.println(everyone);

        Map<String, String> family2 = Map.ofEntries(
                entry("Teo", "Star Wars"), entry("Cristina", "James Bond")
        );
        Map<String, String> friends2 = Map.ofEntries(
                entry("Raphael", "Star Wars"), entry("Cristina", "Matrix")
        );
        Map<String, String> everyone2 = new HashMap<>(family2);
        everyone2.putAll(friends2);
        System.out.println(everyone2);

        Map<String, String> everyone3 = new HashMap<>(family2);
        friends2.forEach((k, y) ->
                everyone3.merge(k, y, (movie1, movie2) -> movie1 + " & " + movie2));
        System.out.println(everyone3);

        Map<String, Long> moviesToCount = new HashMap<>();
        String movieName = "JamesBond";
        Long count = moviesToCount.get(movieName);
        if (count == null) {
            moviesToCount.put(movieName, 1L);
        } else {
            moviesToCount.put(movieName, count + 1);
        }
        System.out.println(moviesToCount);

        moviesToCount.merge(movieName, 1L, (count1, increcement) -> count1 + 1L);
        System.out.println("moviesToCount = " + moviesToCount);
    }
}
