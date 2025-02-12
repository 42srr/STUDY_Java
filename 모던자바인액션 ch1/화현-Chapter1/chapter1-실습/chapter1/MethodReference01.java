package chapter1;

import java.util.ArrayList;
import java.util.List;

public class MethodReference01 {
    public static void main(String[] args) {
        List<Apple> inventory = getApple();
        List<Apple> greenApple = filterGreenApples(inventory);
        System.out.println("greenApple = " + greenApple);

        List<Apple> appleWeight = filterAppleWeight(inventory);
        System.out.println("appleWeight = " + appleWeight);
    }

    private static List<Apple> getApple() {
        Apple apple1 = new Apple("green", 40);
        Apple apple2 = new Apple("red", 30);
        Apple apple3 = new Apple("red", 20);
        Apple apple4 = new Apple("green", 10);

        List<Apple> inventory = List.of(apple1, apple2, apple3, apple4);
        return inventory;
    }

    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();

        for (Apple apple : inventory) {
            if ("green".equals(apple.getColor())) {
                result.add(apple);
            }
        }
        return result;
    }

    private static List<Apple> filterAppleWeight(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();

        for (Apple apple : inventory) {
            if (apple.getWeight() > 25) {
                result.add(apple);
            }
        }
        return result;
    }

}
