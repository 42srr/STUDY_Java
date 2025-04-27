package collectionFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListFactory {
    public static void main(String[] args) {
        List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
        System.out.println("friends = " + friends);
        //friends.add("Chih-Chun"); // UnsupportedOperationException 발생
        friends.set(0, "Richard"); // UnsupportedOperationException 발생

        System.out.println("friends = " + friends);
    }
}
