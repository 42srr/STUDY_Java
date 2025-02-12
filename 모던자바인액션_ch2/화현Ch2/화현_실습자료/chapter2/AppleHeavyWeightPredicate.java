package chapter2;

import static chapter2.Color.RED;

public class AppleHeavyWeightPredicate implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return RED.equals(apple.getColor()) && apple.getWeight() > 150;
    }
}
