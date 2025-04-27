package map;

import java.util.Map;

public class ForEach {
    public static void main(String[] args) {
        Map<String, Integer> ageOfFriends = Map.of("Raphael", 23, "Olivia", 24, "Thibaut", 25);

        for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
            String friend = entry.getKey();
            Integer age = entry.getValue();
            System.out.println(friend + " is " + age + " years old.");
        }

        ageOfFriends.forEach((friend, age)
                -> System.out.println(friend + " is " + age + " years old."));

    }
}
