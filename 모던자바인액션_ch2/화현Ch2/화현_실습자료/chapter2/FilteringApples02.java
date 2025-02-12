package chapter2;

import static chapter2.Color.GREEN;
import static chapter2.Color.RED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteringApples02 {
    public static void main(String[] args) {
        List<Apple> inventory = getApples();

        List<Apple> greenApples = filterGreenApples(inventory, GREEN);
        System.out.println("Green apples: " + greenApples);
        List<Apple> redApples = filterGreenApples(inventory, RED);
        System.out.println("Red apples: " + redApples);
    }

    private static List<Apple> getApples() {
        List<Apple> inventory = Arrays.asList(
                new Apple(80, GREEN),
                new Apple(155, GREEN),
                new Apple(120, Color.RED));
        return inventory;
    }

    public static List<Apple> filterGreenApples(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getColor().equals(color)) {
                result.add(apple);
            }
        }
        return result;
    }
}
