
> 정리된 코드는 [github](https://github.com/joonwan/modern-java-in-action/tree/main/src/part1/chapter2) 에 있습니다.

# 동작 파라미터화

동작 파라미터화란 아직은 어떻게 실행할 것인지 결정하지 않은 코드를 의미하며 메서드를 다른 메서드의 인수로 전달할 수 있는 기능이다. 이를 이용해서 자주 바뀌는 요구사항에 효과적으로 대응할 수 있다. 예제를 점진적으로 발전시켜 변화하는 요구 사항에 대응하여 동작 파라미터를 사용하는 방법을 학습해보자.

## 변화하는 요구 사항에 대응하기

먼저 Apple class 와 Color enum 을 만들자.

**Apple class**
```java
package part1.chapter2;  
  
public class Apple {  
  
    private Color color;  
    private int weight;  
  
    public Apple(Color color, int weight) {  
        this.color = color;  
        this.weight = weight;  
    }  
  
    public Color getColor() {  
        return color;  
    }  
  
    public int getWeight() {  
        return weight;  
    }  
  
    @Override  
    public String toString() {  
        return "Apple{" +  
                "color=" + color +  
                ", weight=" + weight +  
                '}';  
    }  
}
```

**Color enum**
```java
package part1.chapter2;  
  
public enum Color {  
    GREEN, RED;  
}
```

### 녹색 사과 필터링 

**FilterMainV1**

```java
package part1.chapter2;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.*;  
  
public class FilterMainV1 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> apples = filterGreenApples(inventory);  
        System.out.println(apples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 150);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
    }  
  
    public static List<Apple> filterGreenApples(List<Apple> inventory) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (GREEN.equals(apple.getColor())) {  // filter
                result.add(apple);  
            }  
        }  
        return result;  
    }  
}
```

```text
[Apple{color=GREEN, weight=100}, Apple{color=GREEN, weight=150}, Apple{color=GREEN, weight=140}]
```

- `filterGreenApples`
	- 모든 사과를 담은 리스트인 inventory 를 받아 초록사과만 추출해 반환하는 메서드이다.

위 예제는 초록 사과만 추출하는 예제이다. 현재 추출조건은 **초록색인 사과** 이다. 만약 요구사항이 바뀌어 빨간 사과만 추출하고 싶을 경우 다음과 같이 메서드를 추가해야 한다.

```java
package part1.chapter2;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.*;  
  
public class FilterMainV1 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> greenApples = filterGreenApples(inventory);  
        System.out.println("greenApples = " + greenApples);  
  
        List<Apple> redApples = filterRedApple(inventory);  
        System.out.println("redApples = " + redApples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 150);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
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
  
    public static List<Apple> filterRedApple(List<Apple> inventory) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (RED.equals(apple.getColor())) {  
                result.add(apple);  
            }  
        }  
        return result;  
    }  
}
```

**실행결과**

```text
greenApples = [Apple{color=GREEN, weight=100}, Apple{color=GREEN, weight=150}, Apple{color=GREEN, weight=140}]
redApples = [Apple{color=RED, weight=120}, Apple{color=RED, weight=160}]
```


만약 요구사항이 더 추가된다면 매번 새로운 메서드를 만들어야 한다. 

```java
    public static List<Apple> filterGreenApples(List<Apple> inventory) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (GREEN.equals(apple.getColor())) {  // GREEN 고정
                result.add(apple);  
            }  
        }  
        return result;  
    }  
  
    public static List<Apple> filterRedApple(List<Apple> inventory) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (RED.equals(apple.getColor())) {   // RED 고정
                result.add(apple);  
            }  
        }  
        return result;  
    } 
```

위 두 메서드를 비교해보면 필터링의 기준이 되는 색깔만 다르지 나머지 코드는 동일한 것을 확인할 수 있다. 여기서 구체화 되어있는 색을 것을 추상화 시킨다면 코드 중복을 줄일 수 있다.

### 색을 파라미터화

기존 구체화 되어있던 색을 추상화 시켜 코드 중복을 줄여보자. 즉 색을 메서드의 파라미터에 추가해 더 유연하게 대응하는 코드를 만들자.

**FilterMainV2**

```java
package part1.chapter2;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.GREEN;  
import static part1.chapter2.Color.RED;  
  
public class FilterMainV2 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> greenApples = filterApplesByColor(inventory, GREEN);  
        System.out.println("greenApples = " + greenApples);  
  
        List<Apple> redApples = filterApplesByColor(inventory, RED);  
        System.out.println("redApples = " + redApples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 150);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
    }  
  
    private static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (color.equals(apple.getColor())) {  
                result.add(apple);  
            }  
        }  
        return result;  
    }  
}
```

```text
greenApples = [Apple{color=GREEN, weight=100}, Apple{color=GREEN, weight=150}, Apple{color=GREEN, weight=140}]
redApples = [Apple{color=RED, weight=120}, Apple{color=RED, weight=160}]
```
색을 파라미터화 하며 코드의 중복을 줄일수 있었고 결과 또한 동일하게 나온다.

이제 색을 통한 필터링은 매우 유연하게 동작한다. 하지만 색뿐만 아니라 무게를 기준으로 필터링해달라는 요구 조건이 들어오면 어떻게 될까?


```java
private static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {  
    List<Apple> result = new ArrayList<>();  
    for (Apple apple : inventory) {  
        if (color.equals(apple.getColor())) {  
            result.add(apple);  
        }  
    }  
    return result;  
}  
  
private static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {  
    List<Apple> result = new ArrayList<>();  
    for (Apple apple : inventory) {  
        if (apple.getWeight() > weight) {  
            result.add(apple);  
        }  
    }  
    return result;  
}
```

각 기준에 맞게 또한 메서드를 추가해야 한다. 두 메서드를 보면 필터링의 조건 기준과 파라미터를 제외하면 동일한 코드임을 알 수 있다. 즉 중복되는 코드가 존재한다. 이는 소프트웨어 공학의 `DRY` 원칙을 무시하는 것이다.

>🔥 DRY 법칙
>Don't Repeat Yourself 의 약자로 같은 것을 반복하지 말것 이라는 의미이다.

이러한 반복되는 코드를 줄이기 위해서 Apple 의 속성인 색과 무게를 기준으로 필터링 하는 메서드를 합치는 방법이 있다.

### 가능한 모든 속성으로 필터링

**FilterMainV3**
```java
package part1.chapter2;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.GREEN;  
import static part1.chapter2.Color.RED;  
  
public class FilterMainV3 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);  
        System.out.println("greenApples = " + greenApples);  
  
        List<Apple> heavyApples = filterApples(inventory, null, 150, false);  
        System.out.println("heavyApples = " + heavyApples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 150);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
    }  
  
    private static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {  
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
```

**실행결과**

```text
greenApples = [Apple{color=GREEN, weight=100}, Apple{color=GREEN, weight=150}, Apple{color=GREEN, weight=140}]
heavyApples = [Apple{color=RED, weight=160}]
```

`filterApples` 메서드를 사용하기 위해 다음과 같이 코드를 작성해야 한다.

```java
List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
List<Apple> heavyApples = filterApples(inventory, null, 150, false);
```
먼저 마지막 파라미터인 true 와 false 가 무엇을 의미하는지 잘 나타나지 않는다. 또한 지금은 Apple class 의 속성이 두개이기 때문에 `boolean` 타입으로 필터링 조건을 걸 수 있지만 Apple class 의 속성이 추가될 경우 변화하는 요구사항에 잘 대응하지 못한다. 즉 유연하지 못한 코드이다.

문제가 잘 정의되어 있는 상황에서는 위 코드가 잘 동작하지만 `filterApples` 에 **어떤 기준으로 사과를 필터링 할 것인지 효과적으로 전달** 할 수 있다면 좋을 것이다.

동작 파라미터화를 이용해 위 코드에 유연성을 부여해 보자.

## 동작 파라미터화 도입

기존 코드는 컬렉션의 요소를 탐색하는 로직과 컬렉션의 요소를 검증하는 로직이 강결합 되어 있었다. 이 둘을 분리한다면 원하는 시점에 원하는 검증 로직 즉 전략을 선택하여 유연성을 높인 코드를 작성할 수 있다.

**ApplePredicate**
```java
package part1.chapter2.strategy;  
  
import part1.chapter2.Apple;  
  
public interface ApplePredicate {  
    boolean test(Apple apple);  
}
```

> 🔥 Predicate 란?
> 수학에서 인수로 값을 받아 true 나 false 를 반환하는 함수를 Predicate 라고한다.


이 `ApplePredicate` 를 구현할 경우 여러 필터링 조건들을 만들 수 있다.

**무게 기준 필터링** 


```java
package part1.chapter2.strategy;  
  
import part1.chapter2.Apple;  
  
public class AppleHeavyWeightPredicate implements ApplePredicate{  
    @Override  
    public boolean test(Apple apple) {  
        return apple.getWeight() > 150;  
    }  
}
```

**색깔기준 필터링**
```java
package part1.chapter2.strategy;  
  
import part1.chapter2.Apple;  
  
import static part1.chapter2.Color.*;  
  
public class AppleGreenColorPredicate implements ApplePredicate{  
    @Override  
    public boolean test(Apple apple) {  
        return apple.getColor().equals(GREEN);  
    }  
}
```

`ApplePredicate` 를 구현한 여러 필터링 전략들을 필요할 때마다 구현하여 사용하면 어떠한 요구사항이 발생해도 유연하게 대응할 수 있다. 요구사항이 추가될 때 마다 요구사항에 맞는 `ApplePredicate` 구현체를 만든 뒤 필터링 메서드에 집어 넣기만 하면 되기 때문이다.

이를 바로 **전략 디자인 패턴** 이라고 한다. 


> 🔥 전략 디자인 패턴
> 런타임에 알고리즘을 선택할 수 있도록 하는 소프트웨어 설계 패턴이다. 이는 전략이라 불리는 알고리즘을 캡슐화 하는 알고리즘 패밀리를 정의해놓은 다음 런타임에 알고리즘을 선택하는 기법이다.
> 위 예시에서 AlgorithmFamily 는 ApplePredicate 이며 이 구현체 들이 Strategy 이다.


이렇게 검증 로직, 즉 알고리즘 패밀리와 전략을 구현했다. 이제 기존 필터링 메서드가 전략을 주입 받을 수 있게끔 수정해보자.


**AppleRedAndHeavyPredicate**

```java
package part1.chapter2.strategy;  
  
import part1.chapter2.Apple;  
import part1.chapter2.Color;  
  
public class AppleRedAndHeavyPredicate implements ApplePredicate {  
    @Override  
    public boolean test(Apple apple) {  
        return apple.getColor().equals(Color.RED)  
                && apple.getWeight() > 150;  
    }  
}
```


**FilterMainV4**

```java
package part1.chapter2;  
  
import part1.chapter2.strategy.ApplePredicate;  
import part1.chapter2.strategy.AppleRedAndHeavyPredicate;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.GREEN;  
import static part1.chapter2.Color.RED;  
  
public class FilterMainV4 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> redAndHeavyApples = filterApples(inventory, new AppleRedAndHeavyPredicate());  
        System.out.println(redAndHeavyApples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 160);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
    }  
  
    private static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (p.test(apple)) {  
                result.add(apple);  
            }  
        }  
        return result;  
    }  
  
}
```

무게가 150 이상이며 색이 RED 인 사과를 필터링 하는 로직이다. `filterApples` 를 자세히 살펴보자.

```java
    private static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (p.test(apple)) {  
                result.add(apple);  
            }  
        }  
        return result;  
    }  
```

매개변수를 살펴보면 `ApplePredicat` 즉 필터링 전략을 주입 받는 것을 확인할 수 있다. 또한 우리가 주입한 전략에 따라 `filterApples` 메서드는 다르게 동작한다.

즉 동작(전략) 을 파라미터화 한 것이다.

동작을 파라미터화 하므로써 우리는 두가지를 얻을 수 있다.

1. 코드의 간결성
2. 변화하는 요구사항에 쉽게 대응할 수 있는 유연함

## 복잡한 과정 간소화

### 익명 클래스 도입

매번 전략을 구현하는 것은 반복적인 일이기 때문에 매우 귀찮다. 따라서 클래스 선언과 인스턴스화를 동시에 수행할 수 있는 **익명 클래스** 를 도입하자.

**FilterMainV5**

```java
package part1.chapter2;  
  
import part1.chapter2.strategy.ApplePredicate;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.GREEN;  
import static part1.chapter2.Color.RED;  
  
public class FilterMainV5 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> redApples = filterApples(inventory, new ApplePredicate() {  
            @Override  
            public boolean test(Apple apple) {  
                return apple.getColor().equals(RED);  
            }  
        });  
  
        System.out.println(redApples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 160);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
    }  
  
    private static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (p.test(apple)) {  
                result.add(apple);  
            }  
        }  
        return result;  
    }  
  
}
```

익명 클래스를 도입하여 클래스 선언과 구현을 동시에했다.

```java
	List<Apple> redApples = filterApples(inventory, new ApplePredicate() {  
            @Override  
            public boolean test(Apple apple) {  
                return apple.getColor().equals(RED);  
            }  
    });     
```

기존에는 `ApplePredicate` 를 구현하는 클래스를 직접 정의한 뒤 이를 인스턴스화 하였다. 즉 번거로운 작업이었다.

하지만 익명 클래스를 도입하여 정의과 구현을 동시에 할 수 있었다. 즉 코드양과 시간을 줄일 수 있다.

하지만 위 코드에서 볼 수 있듯 익명 클래스는 매우 많은 공간을 차지한다. 람다를 도입해 더 간결하게 표현해보자.

### 람다 도입

```java
package part1.chapter2;  
  
import part1.chapter2.strategy.ApplePredicate;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.GREEN;  
import static part1.chapter2.Color.RED;  
  
public class FilterMainV6 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> redApples = filterApples(inventory, apple -> apple.getColor().equals(RED));  
  
        System.out.println(redApples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 160);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
    }  
  
    private static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {  
        List<Apple> result = new ArrayList<>();  
        for (Apple apple : inventory) {  
            if (p.test(apple)) {  
                result.add(apple);  
            }  
        }  
        return result;  
    }  
  
}
```
람다를 도입할 경우 정말 깔끔하게 코드를 구현할 수 있다. 람다의 자세한 내용은 chapter 3 에 등장한다.

## 타입 유연성 증가시키기

기존 `filterApples` 메서드는 오로지 Apple class 만 필터링 할 수 있었다. 만약 오렌지, 포도 등 다양한 과일들을 필터링해달라는 요구사항이 들어왔을 때 어떻게 해야할까?

이때 리스트에 들어가는 타입을 파라미터화 할 경우 해당 요구사항에 유연하게 대처할 수 있다. 이를 위해서 Generics 를 도입애햐 한다.

### Generics 도입

기존 ApplePredicate 의 타입을 확장 시킨 MyPredicate 라는 interface 를 만들자.

**MyPredicate**
```java
package part1.chapter2.generic;  
  
public interface MyPredicate <T>{  
    boolean test(T t);  
}
```

이 전략을 통해 필터링을 할 수 있는 필터링 메서드를 만들자. 

```java
private static <T> List<T> filter(List<T> inventory, MyPredicate<T> p) {  
    List<T> result = new ArrayList<>();  
  
    for (T t : inventory) {  
        if (p.test(t)) {  
            result.add(t);  
        }  
    }  
  
    return result;  
}
```

전체 코드는 다음과 같다.

**FilterMainV7**
```java
package part1.chapter2;  
  
import part1.chapter2.generic.MyPredicate;  
import part1.chapter2.strategy.ApplePredicate;  
  
import java.util.ArrayList;  
import java.util.List;  
  
import static part1.chapter2.Color.GREEN;  
import static part1.chapter2.Color.RED;  
  
public class FilterMainV7 {  
  
    public static void main(String[] args) {  
        List<Apple> inventory = getInventory();  
  
        List<Apple> redApples = filter(inventory, apple -> apple.getColor().equals(RED));  
  
        System.out.println(redApples);  
    }  
  
    private static List<Apple> getInventory() {  
  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 120);  
        Apple apple3 = new Apple(GREEN, 160);  
        Apple apple4 = new Apple(GREEN, 140);  
        Apple apple5 = new Apple(RED, 160);  
  
        return List.of(apple1, apple2, apple3, apple4, apple5);  
    }  
  
    private static <T> List<T> filter(List<T> inventory, MyPredicate<T> p) {  
        List<T> result = new ArrayList<>();  
  
        for (T t : inventory) {  
            if (p.test(t)) {  
                result.add(t);  
            }  
        }  
  
        return result;  
    }  
  
}
```

지네릭스를 도입했기 때문에 더 다양한 타입에 대해 유연하게 대응할 수 있다.

# 실전 예제

## Comparator 로 정렬하기


> 💡 Comparator 간단 정리
> Comparator 를 직역하면 비교자이다. 즉 같은 타입의 두 객체를 어떻게 비교하는지 기준을 정해주는 역할을 한다고 생각하면 된다.
> Comparator 를 구현하는 구현체는 반드시 `int compare(T o1, T o2)` 라는 메서드를 구현해야 한다.
> [오름 차순 기준]
> - o1 이 o2 보다 작은 경우 음수 반환
> - o1 과 o2 가 같다면 0 반환
> - o1 이 o2 보다 큰 경우 양수 반환

**무게를 기준으로 사과 정렬하기**

```java
package part1.chapter2.example;  
  
import part1.chapter2.Apple;  
import part1.chapter2.Color;  
  
import java.util.ArrayList;  
import java.util.List;  
  
public class ComparatorMain {  
  
    public static void main(String[] args) {  
        List<Apple> apples = getApples();  
  
        // 무게기준 오름차순 정렬  
        apples.sort((a1, a2) -> a1.getWeight() - a2.getWeight());  
  
        System.out.println(apples);  
    }  
  
    private static List<Apple> getApples() {  
        Apple apple1 = new Apple(Color.RED, 150);  
        Apple apple2 = new Apple(Color.GREEN, 140);  
        Apple apple3 = new Apple(Color.RED, 120);  
        Apple apple4 = new Apple(Color.GREEN, 130);  
        Apple apple5 = new Apple(Color.RED, 100);  
  
        return new ArrayList<>(List.of(apple1, apple2, apple3, apple4, apple5));  
    }  
}
```
**살행결과**

```text
[Apple{color=RED, weight=100}, Apple{color=RED, weight=120}, Apple{color=GREEN, weight=130}, Apple{color=GREEN, weight=140}, Apple{color=RED, weight=150}]
```
무게를 기준으로 사과를 오름차순 정렬하는 예제이다. 

만약 요구 사항이 바뀌어 내림차순으로 정렬로 바꿀 경우 단지 Comparator 만 바꾸어 주면 된다.

```java
        // 무게기준 오름차순 정렬  
//        apples.sort((a1, a2) -> a1.getWeight() - a2.getWeight());  
        // 무게기준 내림차순 정렬  
        apples.sort((a1, a2) -> a2.getWeight() - a1.getWeight());
```

```text
[Apple{color=RED, weight=150}, Apple{color=GREEN, weight=140}, Apple{color=GREEN, weight=130}, Apple{color=RED, weight=120}, Apple{color=RED, weight=100}]
```

## Runnable 로 코드블록 실행하기


> 💡 Runnable 간단 정리
> Runnable Interface 의 구현체들은  `public abstract void run()` 메서드를 구현해야 한다.
> Runnable 의 구현체를 Thread class 의 생성자로 전달한 다음 Thread 의 `start()` 메서드를 호출해야 호출 thread 와 다른 별개의 thread 가 해당 작업을 한다. `run()` 메서드를 호출할 경우 호출 thread 가 runnable 내부의 작업을 수행한다.


```java
public class RunnableMain {  
    public static void main(String[] args) throws InterruptedException {  
  
        Thread thread1 = new Thread(() -> System.out.println(Thread.currentThread().getName() + " is running!!"));  
        Thread thread2 = new Thread(() -> System.out.println(Thread.currentThread().getName() + " is running!!!"));  
        Thread thread3 = new Thread(() -> System.out.println(Thread.currentThread().getName() + " is running!!!"));  
  
        thread1.start();  
        thread2.start();  
        thread3.start();  
  
        thread1.join();  
        thread2.join();  
        thread3.join();  
  
        System.out.println(Thread.currentThread().getName() + " thread 종료");  
  
    }  
}
```

## Callable 을 결과로 반환하기

> 💡 Callable 간단 정리
> Runnable 과 다르게 thread 가 하는 작업의 결과를 반환 받을 수 있고 check 예외 또한 발생시킬 수 있다. Callable 을 구현하는 구현체들은 `V call() throws Exception` 를 구현해야 한다.

> 💡 Executor Framework 간단 정리
> Thread 생성및 관리를 해줘 개발자가 Thread 를 보다 쉽게 사용할 수 있게 해주는 Framework 이다.
> 해당 프레임워크에서 주로 사용하는 `ExecutorService` 의 구현체로는 `ThreadPoolExecutor` 이다. ThreadPoolExecutor 내부에는 Thread pool 과 BlockingQueue 가 존재한다.


```java
package part1.chapter2.example;  
  
import java.util.concurrent.*;  
  
public class CallableMain {  
  
    public static void main(String[] args) throws ExecutionException, InterruptedException {  
        ExecutorService es = Executors.newCachedThreadPool();  
  
        long startTime = System.currentTimeMillis();  
  
        Future<String> future1 = es.submit(new MyCallable());  
        Future<String> future2 = es.submit(new MyCallable());  
        Future<String> future3 = es.submit(new MyCallable());  
        Future<String> future4 = es.submit(new MyCallable());  
  
        String name1 = future1.get();  //blocking
        String name2 = future2.get();  //blocking
        String name3 = future3.get();  //blocking
        String name4 = future4.get();  //blocking
  
        System.out.println("name1 = " + name1);  
        System.out.println("name2 = " + name2);  
        System.out.println("name3 = " + name3);  
        System.out.println("name4 = " + name4);  
  
        long endTime = System.currentTimeMillis();  
  
        System.out.println("result = " + (endTime - startTime) + "ms");  
        es.shutdown();
  
    }  
  
    static class MyCallable implements Callable<String> {  
  
        @Override  
        public String call() throws Exception {  
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                throw new RuntimeException(e);  
            }  
            return Thread.currentThread().getName();  
        }  
    }  
}
```
```text
name1 = pool-1-thread-1
name2 = pool-1-thread-2
name3 = pool-1-thread-3
name4 = pool-1-thread-4
result = 1013ms
```

참고로 `submit()` 을 실행할 경우 바로 `Future (FutureTask)` 가 반환된다. 이후 `get()` 메서드를 호출 시 결과를 thread 실행 결과를 받을 때 까지 호출 thread 는 대기한다 (blocking).

따라서 4개의 작업을 제출 후 병렬로 실행되어 약 1초 동안 작업을 마친것을 확인할 수 있다.

```java
package part1.chapter2.example;  
  
import java.util.concurrent.*;  
  
public class CallableMain {  
  
    public static void main(String[] args) throws ExecutionException, InterruptedException {  
        ExecutorService es = Executors.newCachedThreadPool();  
  
        long startTime = System.currentTimeMillis();  
  
        Future<String> future1 = es.submit(new MyCallable());  
        String name1 = future1.get();  
        Future<String> future2 = es.submit(new MyCallable());  
        String name2 = future2.get();  
        Future<String> future3 = es.submit(new MyCallable());  
        String name3 = future3.get();  
        Future<String> future4 = es.submit(new MyCallable());  
        String name4 = future4.get();  
  
        System.out.println("name1 = " + name1);  
        System.out.println("name2 = " + name2);  
        System.out.println("name3 = " + name3);  
        System.out.println("name4 = " + name4);  
  
        long endTime = System.currentTimeMillis();  
  
        System.out.println("result = " + (endTime - startTime) + "ms");  
        es.shutdown();
  
    }  
  
    static class MyCallable implements Callable<String> {  
  
        @Override  
        public String call() throws Exception {  
            try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                throw new RuntimeException(e);  
            }  
            return Thread.currentThread().getName();  
        }  
    }  
}
```
```text
name1 = pool-1-thread-1
name2 = pool-1-thread-2
name3 = pool-1-thread-2
name4 = pool-1-thread-2
result = 4021ms
```
만약 위 처럼 작업을 제출하고 걀과를 바로 받아오려고 할 경우 모든 작업이 병렬적으로 실행이 안되며 직렬로 실행된다. 이런식으로 executor service 를 사용할 경우 매우 비효율적이다.


### newFixedThreadPool vs newCachedThreadPool

#### newFixedThreadPool(int nThreads)

최대 nThreads 개 까지의 Thread 를 Thread Pool 에 만든다. 최초로 작업이 Queue 에 들어올 경우 Thread 가 생성된다.

만약 가용가능한 Thread 가 없는 상태에서 `submit()` 에 의해 작업이 추가된 경우 해당 작업은 Thread 가 작업을 완료할 때 까지 `BlockingQueue` 에서 대기한다.

ExecutorService 의 shutdown() 메서드가 호출될때 까지 Pool 내부의 Thread 들은 살아있다.

#### newCachedThreadPool()

 `newCachedThreadPool()` 은 필요에 따라 새로운 thread 를 생성하지만 가용가능한 thread 가 있을 경우 thread 를 재사용하는 thread pool 을 만든다. 60초 동안 사용되지 않은 thread 는 종료된다.

