package collectionFactory;

import java.util.Set;

public class SetList {
    public static void main(String[] args) {
        Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
        System.out.println("friends = " + friends);

        Set<String> friends2 = Set.of("Raphael", "Olivia", "Olivia");
        System.out.println("friends2 = " + friends2);
    }
}
