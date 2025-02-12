package chapter2;

import static chapter2.Color.GREEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteringApples01 {
    public static void main(String[] args) {
        List<Apple> inventory = getApples();

        List<Apple> greenApples = filterGreenApples(inventory);
        System.out.println("Green apples: " + greenApples);
    }

    private static List<Apple> getApples() {
        List<Apple> inventory = Arrays.asList(
                new Apple(80, GREEN),
                new Apple(155, GREEN),
                new Apple(120, Color.RED));
        return inventory;
    }

    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (GREEN.equals(apple.getColor())) {
                result.add(apple);
            }
        }
        return result;
    }
}
