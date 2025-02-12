package chapter2;

public class Apple {
    int weight;
    Color color;

    public Apple(int weight, Color color) {
        this.weight = weight;
        this.color = color;
    }

    public int getWeight() {
        return weight;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "{" + color + " - " + weight + '}';
    }
}
