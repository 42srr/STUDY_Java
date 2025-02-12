# Chapter2
## 동작 파라미터화 코드 전달

이번 장에서는 **동작 파라미터화(Behavior Parameterization)** 라는 개념을 설명합니다.

소프트웨어 개발에서 요구사항은 계속 변하기 마련인데, 변하는 요구사항을 유연하게 처리하는 방법 중 하나가 바로 동작 파라미터화입니다.

##### 📌 동작 파라미터화란?

> 실행할 코드를 미리 정의하지 않고, 나중에 실행될 코드 블록을 메서드의 인수로 전달하는 방식

즉, 메서드의 동작을 파라미터로 전달받아 유연하게 처리할 수 있도록 하는 기법입니다.

예를 들어, 특정 조건에 맞는 사과를 필터링하는 기능이 있다고 가정해 봅시다.

- 처음에는 "녹색 사과"만 찾고 싶다고 합니다.
- 다음날에는 "150g 이상"인 사과를 찾고 싶어 합니다.
- 그다음 날에는 "150g 이상이면서 녹색인 사과"를 찾고 싶어 합니다.

이렇게 요구사항이 계속 변경되고, 그때마다 기존 코드를 수정하거나 새로운 메서드를 추가해야 한다면 유지보수가 점점 어려워질 것입니다.
그렇다면,  해결하는 방법 중 하나가 동작 파라미터화를 적용하는 것입니다. 동작 파라미터화를 사용하면 **필터링 기준을 메서드의 인수로 전달**하는 즉, **필요할 때 원하는 동작을 전달**할 수 있는, **유연한 코드 설계**가 가능합니다.

그러면 지금부터 사과를 관리하는 예제를 통해 왜 변화하는 요구사항에 유연하게 대응해야하는지를 알아보도록 합시다.

#### 사과 필터링하는 프로그램 만들기

Color라는 enum이 다음과 같이 존재합니다.

```java
public enum Color {  
    RED, GREEN;  
}
```


##### 녹색 사과 필터링

```java
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
```

```java 
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
```

초록 사과만 필터링하는 코드는 다음과 같습니다. 그런데 만약 농부가 빨간 사과도 필터링하고 싶다면, **filterRedApples** 메서드를 추가하고 **if** 문의 조건을 **RED** 로 변경하는 방법이 있을 것입니다. 하지만 필터링할 색상이 점점 늘어나면, 코드가 복잡해지고 유지보수도 어려워집니다.
이를 해결하기 위해, 반복되는 코드를 추상화하여 더 효율적인 방식으로 개선해 보겠습니다.

##### 빨간 사과 필터링

```java
import static chapter2.Color.GREEN;  
import static chapter2.Color.RED;  
  
import java.util.ArrayList;  
import java.util.Arrays;  
import java.util.List;  
  
public class FilteringApples02 {  
    public static void main(String[] args) {  
        List<Apple> inventory = getApples();  
  
        List<Apple> greenApples = filterApplesColor(inventory, GREEN);  
        System.out.println("Green apples: " + greenApples);  
        List<Apple> redApples = filterApplesColor(inventory, RED);  
        System.out.println("Red apples: " + redApples);  
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
}
```

**filterGreenApples** 메서드에 **color**파라미터를 추가하여, 색상을 추상화한 **filterApplesByColor**메서드로 변경할 수 있었습니다. 하지만 여기에 사과의 무게도 반영할 수 있으면 좋겠다는 새로운 요구사항이 생긴다면, 무게 정보를 추가로 고려하는 메서드를 만들어야 할 것입니다. 이를 반영한 코드의 변화는 다음과 같습니다.

##### 무게 필터링

```java
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
```

위 코드도 좋은 해결방법이 될 수 있습니다. 하지만, 색을 필터링하는 메서드와 무게를 필터링하는 메서드의 코드가 중복되는 걸 확인할 수 있습니다. 이는 스프트웨어 공학의 **DRY(don't repeat yourself)**(같은 것을 반복하지 말 것) 원칙을 어기는 것입니다. 그러면, 실전에서 권하지는 않지만, 플래그를 추가해 중복되는 코드를 합칠 수 있습니다. 플래그를 추가한 코드는 다음과 같습니다.

##### 비슷한 속성들 필터링

```java
package chapter2;  
  
import static chapter2.Color.GREEN;  
import static chapter2.Color.RED;  
  
import java.util.ArrayList;  
import java.util.Arrays;  
import java.util.List;  
  
public class FilteringApples04 {  
    public static void main(String[] args) {  
        List<Apple> inventory = getApples();  
  
        List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);  
        System.out.println("Green apples: " + greenApples);  
        List<Apple> redApples = filterApples(inventory, RED,0, true);  
        System.out.println("Red apples: " + redApples);  
        List<Apple> heavyApples = filterApples(inventory, null, 150, false);  
        System.out.println("Heavy apples: " + heavyApples);  
    }  
  
    private static List<Apple> getApples() {  
        List<Apple> inventory = Arrays.asList(  
                new Apple(80, GREEN),  
                new Apple(155, GREEN),  
                new Apple(120, Color.RED));  
        return inventory;  
    }  
  
    public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {  
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

**fillterApples**메서드를 통해 다양한 속성을 필터링할 수 있도록 개선해 보았습니다. 하지만, 코드만 보고는 `true`와 `false`가 어떤 의미인지 명확하지 않고, 새로운 조건이 추가될 때마다 메서드를 수정해야 하는 문제가 있습니다.

이제 **동작 파라미터화**를 활용해 보다 유연하고 깔끔한 방식으로 코드를 리팩토링해 보겠습니다.

#### 사과 필터링 프로그램 리팩토링하기

기존 방식에서는 사과를 필터링할 때 **특정 조건(예: 녹색 사과, 무거운 사과 등)** 을 코드에 직접 명시해야 했습니다. 하지만, 새로운 필터링 조건이 추가될 때마다 메서드를 수정해야 하는 문제가 있었습니다.

이를 해결하기 위해 **동작 파라미터화(Behavior Parameterization)** 를 적용하면, **필터링 로직과 선택 조건을 분리하여 코드의 유연성을 높일 수 있습니다.**

###### 프레디케이트(Predicate) 인터페이스 도입

프레디케이트는 **참(`true`) 또는 거짓(`false`)을 반환하는 함수**로, 특정 조건을 만족하는지 여부를 판별하는 역할을 합니다. 예를 들어, `ApplePredicate`라는 인터페이스를 만들어 필터링 조건을 추상화할 수 있습니다.

```java
public interface ApplePredicate {
    boolean test(Apple apple);
}
```

이 인터페이스를 구현하여 **다양한 필터링 조건**을 정의할 수 있습니다.

```java
// 무거운 사과만 선택
public class AppleHeavyWeightPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
        return apple.getWeight() > 150;
    }
}

// 녹색 사과만 선택
public class AppleGreenColorPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
        return GREEN.equals(apple.getColor());
    }
}
```

###### 전략 디자인 패턴(Strategy Pattern) 적용

위와 같은 방식은 **전략 디자인 패턴(Strategy Pattern)** 을 적용한 것입니다.  
전략 디자인 패턴이란 여러 알고리즘(전략)을 미리 정의해 두고, 런타임에 적절한 알고리즘을 선택할 수 있도록 하는 기법입니다.

즉, `ApplePredicate`가 **전략(알고리즘) 그룹**을 의미하고, `AppleHeavyWeightPredicate` 및 `AppleGreenColorPredicate`가 **개별 전략**이 됩니다.



##### 리팩토링 01 - 추상적 조건으로 필터링하기

그러면 지금부터는 동작 파리미터화를 적용해 `filterApples`메서드가 `ApplePredicate` 객체를 인수로 받아 들이도록 수정해봅시다. `filterApples`메서드는 다음과 같습니다.

```java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if (p.test(apple)) {  // 프레디케이트에 따라 조건을 검사
            result.add(apple);
        }
    }
    return result;
}
```

이제 **필요한 전략(조건)만 전달** 하여, `ApplePredicate` 메서드만 전달하여, 사과의 색은 빨간색이며, 무게가 150g이 넘는 사과를 필터링하도록 리팩토링해봅시다.

```java
public class FilteringApplesRefactor01 {  
    public static void main(String[] args) {  
        List<Apple> inventory = getApples();  
  
        List<Apple> AppleRedAndHeavyPredicate = filterApples(inventory, new AppleHeavyWeightPredicate());  
        System.out.println(redAndHeavyApples);  
    }  
  
    private static List<Apple> getApples() {  
        List<Apple> inventory = Arrays.asList(  
                new Apple(80, GREEN),  
                new Apple(155, GREEN),  
                new Apple(120, RED),  
                new Apple(160, RED));  
        return inventory;  
    }  
  
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {  
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

```java
public class AppleRedAndHeavyPredicate implements ApplePredicate {  
    @Override  
    public boolean test(Apple apple) {  
        return RED.equals(apple.getColor()) 
	        && apple.getWeight() > 150;  
    }  
}
```

위 코드에서 알 수 있듯이, `filterApples`는 특정한 동작을 직접 정의하지 않고, 필터링 로직을 메서드 인자로 전달받습니다. 즉, `ApplePredicate`를 통해 필터링 조건을 캡슐화하고, 이를 `filterApples`에서 실행합니다.

`ApplePredicate`를 활용하면 필터링 조건이 추가되더라도 기존 코드를 수정할 필요 없이 새로운 `Predicate`클래스를 만들기만 하면 됩니다. 같은 `filterApples` 메서드를 다양한 조건으로 활용할 수 있어 코드의 재사용성이 증가합니다.

현재 방식은 매우 유연하지만, 조건을 추가할 때마다 새로운 클래스를 생성해야 하는 번거로움이 있습니다.이를 해결하기 위해 **익명 클래스**나 **람다 표현식**을 사용하여 더 간결하게 구현할 수 있습니다. 다음 단계에서는 이를 통해 필터링 로직을 더욱 효율적으로 개선해 보겠습니다.

##### 리팩토링02 - 복잡한 과정 간소화, 익명클래스

앞선 코드에서는 `ApplePredicate`인터페이스를 구현하는 새로운 클래스를 정의해 필터링했었습니다. 하지만, 새로운 요구사항이 들어오면 계속해서 새로운 클래스를 만든 다음 인스턴스화해야 했습니다.

클래스 선언과 동시에 인스턴스화를 수행할 수 있도록하는 **익명클래스**라는 기법을 통해 리팩토링해보겠습니다.

###### 익명클래스

익명 클래스는 이름 없이 바로 만들어서 사용할 수 있는 클래스입니다. 즉, 새로운 클래스를 따로 만들지 않고도 필요한 기능을 즉석에서 정의하고 사용할 수 있습니다. 이를 통해 코드가 더 간결해지고, 한 번만 사용할 기능을 쉽게 추가할 수 있습니다.

빨간색 사과만을 필터링하는 코드를 익명 클래스를 이용해 작성해보겠습니다.

```java
package chapter2;  
  
import static chapter2.Color.GREEN;  
import static chapter2.Color.RED;  
  
import java.util.ArrayList;  
import java.util.Arrays;  
import java.util.List;  
  
public class FilteringApplesRefactor02 {  
    public static void main(String[] args) {  
        List<Apple> inventory = getApples();  
  
        List<Apple> redApples = filterApples(inventory, new ApplePredicate() {  
            @Override  
            public boolean test(Apple apple) {  
                return RED.equals(apple.getColor());  
            }  
        });  
          
        System.out.println(redApples);  
    }  
  
    private static List<Apple> getApples() {  
        List<Apple> inventory = Arrays.asList(  
                new Apple(80, GREEN),  
                new Apple(155, GREEN),  
                new Apple(120, RED),  
                new Apple(160, RED));  
        return inventory;  
    }  
  
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {  
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

이 방식은 불필요한 클래스를 줄이는 데 도움이 되지만, 여전히 코드가 장황하고, 가독성이 떨어집니다. 그러면, 이 문제를 해결하기위해 다음 장에서 본격적으로 다룰 내용인 람다 표현식으로 위 코들를 리팩토링해보도록 하겠습니다.

##### 리팩토링03 - 복잡한 과정 간소화, 람다 표현식

자바 8부터는 람다 표현식(Lambda Expression)을 활용하여 더 간결한 방식으로 코드를 작성할 수 있습니다. 익명 클래스를 사용할 필요 없이 간단한 표현식만으로 동작을 정의할 수 있습니다.

빨과 사과만 필터링하도록 람다 표현식을 이용해 코드를 작성해보겠습니다.
```java
  
public class FilteringApplesRefactor03 {  
    public static void main(String[] args) {  
        List<Apple> inventory = getApples();  
  
        List<Apple> redApples = filterApples(inventory, apple -> RED.equals(apple.getColor()));  
        System.out.println(redApples);  
    }  
  
    private static List<Apple> getApples() {  
        List<Apple> inventory = Arrays.asList(  
                new Apple(80, GREEN),  
                new Apple(155, GREEN),  
                new Apple(120, RED),  
                new Apple(160, RED));  
        return inventory;  
    }  
  
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {  
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

위 코드에서 볼 수 있듯이, 익명클래스로 작성된 부분을 람다 표현식으로 바꿔 작성해보았습니다. 람다 표현식을 사용하면, 코드가 한줄로 줄어들며, 가독성이 훨씬 좋아지는 걸 확인할 수 있습니다. 죽, 불필요한 클래스와 메서드 선언을 하지 않고, 코드의 간결성을 높이는데 도움이 됩니다.

##### 리팩토링04 - 복잡한 과정 간소화, 리스트형식으로 추상화

그러면 다음 리팩토링을 통해, 람다 표현식을 활용하여 특정 객체 유형에 국한되지 않고 보다 더 일반적인 필터링 메서드를 작성해보도록 합시다.

일반적인 필터링 메서드를 작성하기 위해, `Predicate<T>` 를 만들어야 합니다.

```java
public interface Predicate<T> {  
    boolean test(T t);  
}
```

`Predicate<T>`는 **제네릭 인터페이스**로, 타입 `T`를 처리하는 조건을 테스트하는 함수형 인터페이스입니다. 이 인터페이스는 주로 "조건을 만족하는지 여부"를 판별하는데 사용됩니다.

`T`는 제네릭 타입으로, 이 인터페이스를 사용할 때 특정 타입으로 구체화됩니다. 예를 들어, `Predicate<String>`은 문자열을 테스트하는 조건을, `Predicate<Integer>`는 정수를 테스트하는 조건을 정의할 수 있게 됩니다.

`test` 메서드는 `Predicate` 인터페이스의 **추상 메서드**입니다. 이 메서드는 하나의 매개변수 `T t`를 받아서, 그 값이 조건을 **만족하는지** 여부를 `boolean`으로 반환합니다.

`T`는 이 메서드가 테스트할 값의 타입이고, `boolean` 값은 테스트한 값이 조건을 만족하면 `true`, 그렇지 않으면 `false`를 반환합니다.

```java  
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
```

위 코드에서 확인할 수 있듯이, filter라는 메서드는 Predicate를 받아들여, 조건에 맞는 요소들로 필터링 되는걸 확인할 수 있습니다. 이렇게 동작을 파라미터로 전달하여, 코드의 간결성, 유연성, 재사용성을 높일 수 있습니다.


그러면, 동작 파라미터화를 실전에서 어떻게 활용할 수 있는지 알아보도록 합시다.

#### 동작 파라미터화와 자바 API 활용

동작 파라미터화는 동작을 하나의 코드 조각으로 캡슐화하고 이를 메서드에 전달함으로써, 코드의 유연성과 재사용성을 극대화할 수 있습니다. 이는 실제 개발에서 매우 유용하며, 다양한 상황에서 적용할 수 있습니다.

예를 들어, **Comparator**를 이용한 컬렉션 정렬, **Runnable**을 사용한 코드 블록 실행, **Callable**을 통한 결과 반환, 그리고 **GUI 이벤트 처리** 등에서 이 방식이 효과적으로 사용됩니다.

##### Comparator를 이용한 컬렉션 정렬

컬렉션 정렬은 자주 필요한 작업이며, 정렬 기준을 쉽게 변경할 수 있는 코드가 필요합니다.

자바 8에서는 `List`에 `sort` 메서드를 제공해 컬렉션을 정렬할 수 있습니다. `Comparator` 인터페이스를 사용하면 정렬 기준을 실행 중에 쉽게 바꿀 수 있다는 점이 매우 유용합니다. 예를 들어, 처음에는 무게로 정렬하고 나중에는 색상으로 정렬하는 등의 변화가 필요할 때, `Comparator`만 바꾸면 간단히 동적으로 정렬 기준을 변경할 수 있습니다.

###### Comparator 인터페이스

```java
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

`Comparator` 인터페이스는 두 객체를 비교하는 `compare` 메서드를 정의합니다. 이 메서드를 구현하여, 정렬 기준을 자유롭게 설정할 수 있습니다.


##### Runnable로 코드 블록 실행

자바에서 스레드를 사용하면 동시에 여러 작업을 실행할 수 있습니다. 스레드에 동적으로 실행할 코드를 지정하기 위해, `Runnable` 인터페이스를 사용합니다. `Runnable`은 실행할 코드가 포함된 `run()` 메서드를 정의하는 인터페이스입니다.

###### Runnable 인터페이스

```java
public interface Runnable {
    void run();
}
```

- `Runnable` 인터페이스는 실행할 코드 블록을 지정하는 역할을 합니다. 
- `run()` 메서드는 반환값이 없고, 그저 실행할 작업을 포함하고 있습니다.

###### MultiThread 환경에서 Runnable 인터페이스 활용

```java
public class MultiThreadEx {  
    public static void main(String[] args) {  
        Thread t1 = new Thread(new Runnable() {  
            public void run() {  
                for (int i = 0; i < 5; i++) {  
                    System.out.println("t1 :" + i);  
                    try {  
                        Thread.sleep(500);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        });  
  
        Thread t2 = new Thread(new Runnable() {  
            public void run() {  
                for (int i = 0; i < 5; i++) {  
                    System.out.println("t2 :" + i);  
                    try {  
                        Thread.sleep(300);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        });  
  
        t1.start();  
        t2.start();  
    }  
}
```

```
[결과]
t1 :0
t2 :0
t2 :1
t1 :1
t2 :2
t2 :3
t1 :2
t2 :4
t1 :3
t1 :4
```

`Runnable`인터페이스는 동작을 객체로 전달하고, 동적으로 변경가능한 것을 위 코드에서 알 수 있습니다.

`Runnable` 인터페이스는 **코드를 객체로 캡슐화**한 형태입니다. 즉, `t1`, `t2`와 같은 객체를 `Thread`에 넘겨주면, 각 스레드가 다른 동작을 수행하게 됩니다.

동작을 변경하기 위새서는 `Runnable`객체만 새로 만들면 되기에, 코드 동작을 쉽게 바꿀 수 있습니다.

##### Callable을 통한 결과 반환

**`Callable`** 인터페이스는 `Runnable`의 업그레이드 버전으로, 결과를 반환할 수 있습니다. 즉, **작업을 실행하고 그 결과를 받을 수 있는** 인터페이스입니다. `Runnable`은 결과를 반환하지 않지만, `Callable`은 결과를 반환하거나 예외를 던질 수 있습니다.

###### Callabe 인터페이스

```java
public interface Callable<V> {
    V call();
}
```
- **`V`**: `Callable`의 제네릭 타입으로, 결과의 타입을 정의합니다. 예를 들어, `Callable<Integer>`는 정수를 반환하는 작업을 의미합니다.
- `call()` 메서드 : 이 메서드는 실제 작업을 실행하고 결과를 반환합니다. 작업이 끝난 후, 반환값을 `Future`를 통해 받아올 수 있습니다. 또한 예외가 발생하면 던질 수 있습니다.

##### GUI 이벤트 처리

GUI 프로그램에서 **이벤트 처리**는 사용자의 상호작용에 따라 프로그램이 반응하는 방식입니다. 예를 들어, 사용자가 버튼을 클릭하거나 마우스를 움직일 때 그에 맞는 동작을 해야 합니다. 이런 동작을 코드에서 유연하게 처리하려면 **동작을 파라미터화**해야 합니다.