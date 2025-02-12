package chapter1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MethodReference02 {
    public static void main(String[] args) {
        List<Apple> inventory = getApple();
        List<Apple> greenApple = filterApples(inventory, Apple::isGreenApple);
        System.out.println("greenApple = " + greenApple);

        List<Apple> appleWeight = filterApples(inventory, Apple::isHeavyApple);
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

    private static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }
}
