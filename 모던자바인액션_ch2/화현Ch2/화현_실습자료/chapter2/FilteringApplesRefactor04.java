package chapter2;

import static chapter2.Color.GREEN;
import static chapter2.Color.RED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteringApplesRefactor04 {
    public static void main(String[] args) {
        List<Apple> inventory = getApples();
        List<Integer> numbers = getNumbers();

        List<Apple> redApples = filter(inventory, apple -> RED.equals(apple.getColor()));
        System.out.println(redApples);
        List<Integer> evenNumbers = filter(numbers, (Integer number) -> number % 2 == 0);
        System.out.println(evenNumbers);
    }

    private static List<Apple> getApples() {
        List<Apple> inventory = Arrays.asList(
                new Apple(80, GREEN),
                new Apple(155, GREEN),
                new Apple(120, RED),
                new Apple(160, RED));
        return inventory;
    }

    private static List<Integer> getNumbers() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return numbers;
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) {
                result.add(e);
            }
        }
        return result;
    }
}
