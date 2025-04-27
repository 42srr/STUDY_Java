package map;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Objects;

public class Remove {
    public static void main(String[] args) {
        Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "StarWars")
                , entry("Olivia", "James Bond"));

        String key = "Raphael";
        String value = "Jack Reacher 2";

        boolean removeV1 = basicRemoveV1(favouriteMovies, key, value);

        //boolean removeV2 = favouriteMovies.remove(key, value);

        System.out.println("removeV1 = " + removeV1);
        //System.out.println("removeV2 = " + removeV2);
    }

    private static boolean basicRemoveV1(Map<String, String> favouriteMovies, String key, String value) {

        if (favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
            favouriteMovies.remove(key);
            return true;
        } else {
            return false;
        }
    }
}
