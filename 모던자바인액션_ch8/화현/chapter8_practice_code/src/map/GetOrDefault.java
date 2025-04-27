package map;

import static java.util.Map.entry;

import java.util.Map;

public class GetOrDefault {
    public static void main(String[] args) {
        Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "StarWars")
                , entry("Olivia", "James Bond"));

        System.out.println(favouriteMovies.getOrDefault("Olivia", "matrix"));
        System.out.println(favouriteMovies.getOrDefault("Thibaut", "matrix"));
    }
}
