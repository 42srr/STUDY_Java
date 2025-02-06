package chapter1;

public class Apple {
    private String color;
    private int weight;

    public Apple(String color, int weight) {
        this.color = color;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public String getColor() {
        return color;
    }

    public static boolean isGreenApple(Apple apple) {
        return apple.color.equals("green");
    }

    public static boolean isHeavyApple(Apple apple) {
        return apple.weight >= 25;
    }

    @Override
    public String toString() {
        return "{" + color + " - " + weight + '}';
    }

}
