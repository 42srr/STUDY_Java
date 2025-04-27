package map;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compute {
    Map<String, byte[]> dataToHash = new HashMap<>();
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

    public Compute() throws NoSuchAlgorithmException {
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {

        Map<String, List<String>> friendsToMovies = new HashMap<>();
        friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
                .add("Star Wars");

        System.out.println(friendsToMovies);

        friendsToMovies.computeIfAbsent("Rapheal", name -> new ArrayList<>())
                .add("Star Wars");
        System.out.println(friendsToMovies);
    }

    private byte[] calculateDigest(String key) {
        return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
    }
}
