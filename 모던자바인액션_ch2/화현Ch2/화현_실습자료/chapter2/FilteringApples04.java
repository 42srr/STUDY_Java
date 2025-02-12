package chapter2;

import static chapter2.Color.GREEN;
import static chapter2.Color.RED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteringApples04 {
    public static void main(String[] args) {
        List<Apple> inventory = getApples();

        List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
        System.out.println("Green apples: " + greenApples);
        List<Apple> redApples = filterApples(inventory, RED,0, true);
        System.out.println("Red apples: " + redApples);
        List<Apple> heavyApples = filterApples(inventory, null, 150, false);
        System.out.println("Heavy apples: " + heavyApples);
    }

    private static List<Apple> getApples() {
        List<Apple> inventory = Arrays.asList(
                new Apple(80, GREEN),
                new Apple(155, GREEN),
                new Apple(120, Color.RED));
        return inventory;
    }

    public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if ((flag && apple.getColor().equals(color)) ||
                    (!flag && apple.getWeight() > weight)) {
                result.add(apple);
            }
        }
        return result;
    }
}
