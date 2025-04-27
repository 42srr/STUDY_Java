package map;

import java.util.HashMap;
import java.util.Map;

public class Replace {
    public static void main(String[] args) {
        Map<String , String> favouriteMovies = new HashMap<>();
        favouriteMovies.put("Raphael", "StarWars");
        favouriteMovies.put("Olivia", "James Bond");
        favouriteMovies.put("Thibaut", "Matrix");

        favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println(favouriteMovies);

        favouriteMovies.replace("Raphael", "Star Wars", "Interstellar");
        System.out.println(favouriteMovies);
    }
}
