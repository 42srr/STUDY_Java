package chapter2;

import static chapter2.Color.GREEN;
import static chapter2.Color.RED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteringApples03 {
    public static void main(String[] args) {
        List<Apple> inventory = getApples();

        List<Apple> greenApples = filterApplesColor(inventory, GREEN);
        System.out.println("Green apples: " + greenApples);
        List<Apple> redApples = filterApplesColor(inventory, RED);
        System.out.println("Red apples: " + redApples);
        List<Apple> heavyApples = filterApplesByWeight(inventory, 150);
        System.out.println("Heavy apples: " + heavyApples);
    }

    private static List<Apple> getApples() {
        List<Apple> inventory = Arrays.asList(
                new Apple(80, GREEN),
                new Apple(155, GREEN),
                new Apple(120, Color.RED));
        return inventory;
    }

    public static List<Apple> filterApplesColor(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getColor().equals(color)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if(apple.getWeight() > weight) {
                result.add(apple);
            }
        }
        return result;
    }
}
