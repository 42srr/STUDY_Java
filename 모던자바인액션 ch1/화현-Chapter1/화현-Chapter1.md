# Chapter 1

## : 자바 8, 9, 10, 11 : 무슨 일이 일어나고 있는가?

### 자바의 역사

프로그래밍 언어는 생태계와 비슷합니다. 새로운 언어가 등장하게 되면, 진화하지 않은 기존 언어는 사라지게 됩니다. 즉, 언어도 끊임없이 진화하지 않으면 도태되어 사라지게 됩니다.

자바의 역사에서 가장 획기적인 변화는 `자바 8`에서 이루어졌습니다.

자바 8 이전에는 병렬 처리를 위해 주로 스레드를 직접 사용하는 것이 일반적이었습니다. 하지만 스레드는 관리가 어렵고 예기치 않은 오류가 발생할 가능성이 높다는 단점이 있었습니다.

이러한 문제를 해결하기 위해 자바는 보다 쉽게 병렬 실행을 관리하고, 오류 발생을 줄일 수 있는 방향으로 발전하려 노력했습니다. 그 결과, 자바8은 간결한 코드, 멀티코어 프로세서의 쉬운 활용이라는 두가지 요구사항을 기반으로 만들어졌습니다.

그렇다면, 자바8에 추가된 새로운 개념에 대해 알아보기 앞서, 자바8 설계의 기본이 세 가지 프로그램밍 개념에 대해 알아보도록 합시다.

---

### 자바8 설계의 기본인 세 가지 프로그래밍 개념

1. **스트림 처리**

   [스트림이란]

   스트림이란 데이터를 한 번에 하나씩 처리하는 항목들의 모임입니다. 예를 들어, 물이 흐르듯이 데이터도 연속적으로 전달되며, 입력에서 하나씩 읽고, 출력으로 하나씩 보냅니다.<br />

   [유닉스에서 스트림 활용]

   유닉스나 리눅스에서는 여러 프로그램이 스트림을 활용하여 데이터를 처리합니다. 파일의 내용을 읽고, 모든 대문자 소문자로 변환하고, 정렬한 후 마지막 3개 행을 출력합니다.<br />

    ```jsx
    cat file1 file2 | tr "[A-Z]" "[a-z]" | sort | tail -3
    ```

   이처럼 여러 프로그램이 데이터를 순차적으로 가공하며, 동시에 실행되어 효율적으로 처리됩니다.

   [자바8의 스트림 API]

   기존에는 데이터를 하나씩 직접 처리했다면, 고수준으로 추상화하여, 스트림 API를 사용하여 데이터를 효율적으로 사용할 수 있으며, 여러 CPU코어를 활용해 병렬 처리도 쉽게 할 수 있습니다.

<br />

2. **동작 파라미터화로 메서드에 코드 전달하기**

   [동작 파라미터화의 개념]

   동작 파라미터는 코드 일부를 API로 전달하는 기능입니다. 다시 말해, 메서드를 다른 메서드의 인자로 넘길 수 있는 기능입니다.

   [동작 파라미터화의 중요성]

   스트림 API와 연결되어 있습니다. 스트림 API도 연산의 동작을 인자로 받을 수 있기 때문에, 더 효율적인 데이터 처리가 가능해집니다.

   또한, 코드의 재사용성이 증가됩니다. 동작만 바꿔 코드를 재사용할 수 있습니다.

   즉, 동작 파라미터화를 이용하면 코드의 유연성이 증가하고, 스트림 API와도 쉽게 결합할 수 있습니다.

<br />

3. **병렬성과 공유 가변 데이터**

   [병렬을 공짜로 얻을 수 있다는 의미]

   스트림 API를 사용하면 쉽게 병렬 처리가 가능합니다. 하지만, 세상에 공짜는 없다는 말처럼, 스트림 메서드로전달하는 코드의 동작 방식을 조금 바꿔야 합니다. 공유된 가변 데이터에 접근하지 않아야 합니다.

   [공유된 가변 데이터의 문제점]

   여러개의 스레드가 동시에 하나의 공유된 변수를 변경하려 하면 문제가 발생할 수 있습니다. 이를 해결하기 위에 기존에는 synchronized 키워드를 사용했지만, 이 방식은 성능 저하를 초래할 수 있습니다.

   [자바8에서의 해결책]

   스트림API를 이용하면 쉽게 병렬성을 활용할 수 있습니다. 또한, 공유되지 않은 가변 데이터, 메서드, 함수 코드를 다른 메서드로 전달하는 기능한 함수형 프로그램밍 패러다임을 통해 해결할 수 있습니다.


---

### 자바8에 추가된 새로운 개념
<br />
자바에서 함수형 프로그래밍을 어떻게 지원하는지 살펴보고, 함수형 인터페이스, 람다 표현식, 그리고 메소드 참조와 같은 개념들에 대해 말씀드리겠습니다. 이를 통해 코드를 좀 더 간결하게 작성할 수 있는 방법을 살펴보겠습니다.

자바 함수는 메서드 특히 정적 메서드와 같은 의미로 사용되며, 수학적인 함수처럼 사용되며 부작용을 일으키지 않는 함수를 의미합니다. 자바8에서는 함수를 프로그래밍 언어의 핵심인 값을 바꾸는 일급값(일급시민)으로 다룰 수 있도록 개선했습니다. 즉, 함수를 변수에 저장하거나 메서드의 인자로 전달할 수 있습니다.

1. **메소드 참조**

   자바8의 메서드 참조(::)는 이 메서드를 값으로 사용하라는 의미입니다.

   [람다 표현식]

   람다 표현식을 사용하면, 이용할 수 있는 편리한 클래스나 메서드가 없을 때 더 간결하게 코드를 구현할 수 있습니다.

    ```java
    File[] hiddenFiles = new File(",").listFiles(new FileFilter() {
    	public boolean accept(File file) {
    		return file.isHidden();
    	}
    }
    ```

   이전에는, FileFilter 객체로 isHidden메서드를 감싼 다음에 [File.](http://File.is)listFiles메서드로 전달해야 했습니다.

    ```java
    File[] hiddenFiles = new File(",").listFiles(File::isHidden);)
    ```

   자바8 이후 메서드 참조(::)를 이용하여 직접 isHidden 함수를 listFiles메서드로 전달할 수 있게 되었습니다.

   즉, 함수를 일급값으로 넘겨주는 프로그램을 구현할 수 있게 되었습니다.

   [예제]

   Apple 클래스와 getColor 메서드가 있고, Apples 리스트를 포함하는 변수 inventory가 있다고 가정해봅시다. 이때, 모든 녹색 사과를 선택해서 리스트를 반환(필터)하는 프로그램을 구현해봅시다.

   자바8이 도입되기 이전에는 다음과 같은 코드로 진행하였습니다.

    ```java
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
    
        @Override
        public String toString() {
            return "{" + color + " - " + weight + '}';
        }
    }
    ```

    ```java
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
    ```

    ```java
    // 결과
    
    greenApple = [{green - 40}, {green - 10}]
    appleWeight = [{green - 40}, {red - 30}]
    ```

   filter관련 메서드에 있어서 if 의 조건문만 다른걸 확인할 수 있습니다. 하지만 자바8에서는 Apple클래스에 각각의 조건문을 추가하여 중복되는 로직을 메서드 호출을 통해 동작하도록 수정해봅시다.

    ```java
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
    ```

    ```java
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
    ```

   메서드 참조를 이용하게 되면, 기존 main함수는 비교적 짧아지며 filter 관련 메서드를 중복 구현할 필요가 없어지게 됩니다. 하지만 Apple클래스의 코드에 매번 메서드를 정의하는 것은 번거롭습니다. 그래서 자바8에서 람다라는 개념을 이용해 이를 해결해봅시다.

    ```java
    package chapter1;
    
    import java.util.ArrayList;
    import java.util.List;
    import java.util.function.Predicate;
    
    public class MethodReference03 {
    
        public static void main(String[] args) {
            List<Apple> inventory = getApple();
            List<Apple> greenApple = filterApples(inventory, (Apple a) -> a.getColor().equals("green"));
            System.out.println("greenApple = " + greenApple);
    
            List<Apple> appleWeight = filterApples(inventory, (Apple a) -> a.getWeight() > 25);
            System.out.println("appleWeight = " + appleWeight);
    
            List<Apple> apples = filterApples(inventory, (Apple a) -> a.getColor().equals("red") || a.getWeight() < 30);
            System.out.println("apples = " + apples);
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
    
    ```

   기존 Apple 클래스를 사용하며 더 짧고 간편하게 사용할 수 있게 되었습니다.

    ```java
    // 결과
    
    greenApple = [{green - 40}, {green - 10}]
    appleWeight = [{green - 40}, {red - 30}]
    apples = [{red - 30}, {red - 20}, {green - 10}]
    ```

2. **스트림**

   자바에서 데이터를 다루는 방식은 주로 컬렉션API를 활용하는 것이 일반적입니다. 하지만 컬렉션 방식으로 데이터를 처리하면 코드가 복잡해지고 가독성이 떨어지는 문제가 발생합니다.

   예를 들어, Transaction 리스트에서 1000원이 넘는 금액만 필터링한 후, currency별로 그룹화하는 코드는 스트림이 적용되기 이전의 자바에서는 다음과 같습니다.

    ```java
    Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();
    
    for (Transaction transaction : transactions) {
        if (transaction.getPrice() > 1000) {
            Currency currency = transaction.getCurrency();
            List<Transaction> transactionsForCurrency = transactionsByCurrencies.get(currency);
            
            if (transactionsForCurrency == null) {
                transactionsForCurrency = new ArrayList<>();
                transactionsByCurrencies.put(currency, transactionsForCurrency);
            }
            
            transactionsForCurrency.add(transaction);
        }
    }
    
    ```

   이는 이중반복문이 있어 가독성이 떨어지며, 코드가 길고 복잡합니다.

   하지만 스트림 API를 활용할 경우 다음과 같이 간단한 코드로 작성될 수 있습니다.

    ```java
    import static java.util.stream.Collectors.groupingBy;
    
    Map<Currency, List<Transaction>> transactionsByCurrencies =
        transactions.stream()
            .filter(t -> t.getPrice() > 1000) // 고가의 트랜잭션 필터링
            .collect(groupingBy(Transaction::getCurrency)); // 통화별 그룹화
    
    ```

   코드가 짧아지며 가독성이 좋아지고, 스트림 API를 이용하여 반복문을 신경쓸 필요가 없어지고, 라이브러리 내부에서 모든 데이터가 처리되는 내부 반복 방식으로 작성되었기에 성능 최적화가 쉬워집니다.

3. 디폴트 메서드

   자바8 개발자들이 겪는 어려움 중 하나는 기존 인터페이스의 변경이었습니다. 예를 들어 Collections.sort는 사실 List 인터페이스에 포함되지만 실제로 List에 포함된 적은 없습니다. 이론적으로는 Collections.list(list, comparator)가 아닌 list.sort(comparator)를 수행하는 것이 더 적절해보입니다.

   사소한 문제 처럼 보이지만 인터페이스를 업데이트하려면 해당 인터페이스를 구현하는 모든 클래스도 업데이트 해야하기에 기존 인터페이스 변경은 매우 어려웠습니다. 이는 자바8에서 디폴트 메서드, 자바9에서 모듈 시스템으로 해결할 수 있었습니다.

   자바8에서의 디폴트 메서드란 인터페이스에서 메서드를 구현할 수 있도록 허용하는 기능입니다. default 키워드를 사용해 인터페이스에 직접 메서드를 구현할 수 있습니다. 따라서 기존 인터페이스 코드의 수정 없이 새로운 기능을 추가할 수 있습니다.

   기존 JAR파일 방식은 패키지 간의 명확한 경계가 없어 유지 보수가 어려웠습니다. 자바9의 모듈 시스템을 이용해 패키지를 그룹으로 묶어 관리할 수 있게 되었습니다.