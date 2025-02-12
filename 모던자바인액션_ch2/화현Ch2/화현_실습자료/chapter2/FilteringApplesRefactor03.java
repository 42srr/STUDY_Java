package chapter2;

import static chapter2.Color.GREEN;
import static chapter2.Color.RED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteringApplesRefactor03 {
    public static void main(String[] args) {
        List<Apple> inventory = getApples();

        List<Apple> redApples = filterApples(inventory, apple -> RED.equals(apple.getColor()));
        System.out.println(redApples);
    }

    private static List<Apple> getApples() {
        List<Apple> inventory = Arrays.asList(
                new Apple(80, GREEN),
                new Apple(155, GREEN),
                new Apple(120, RED),
                new Apple(160, RED));
        return inventory;
    }

    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }
}
