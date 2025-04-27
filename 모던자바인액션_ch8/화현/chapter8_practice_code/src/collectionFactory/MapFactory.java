package collectionFactory;

import java.util.Map;
import static java.util.Map.entry;

public class MapFactory {
    public static void main(String[] args) {
        Map<String, Integer> ageOfFriends = Map.of("Raphael", 23, "Olivia", 24, "Thibaut", 25);
        System.out.println("ageOfFriends = " + ageOfFriends);

        Map<String, Integer> ageOfFriends2 = Map.ofEntries(entry("Raphael", 23), entry("Olivia", 24), entry("Thibaut", 25));
        System.out.println("ageOfFriends2 = " + ageOfFriends2);
    }
}
