---
제목: "자바 8, 9, 10, 11 : 무슨 일이 일어나고 있는가?"
---

## Java의 시작과 변화

자바는 많은 유용한 라이브러리를 포함하는 잘 설계된 객체 지향언어로 시작했다. 

![](./image/Pasted%20image%2020250205203014.png)

Java 는 처음 부터 Lock 과 Thread 를 이용한 동시성을 지원했으며 Java 코드를 JVM 바이트 코드로 컴파일 하는 특징 덕분에 인터넷 에플릿 프로그램의 주요 언어가 되었다.

이후 자바 버전이 올라감에 따라 `Thread pool`, `Concurrent Collection`, `Fork/Join Framework` 이 도입되었다. 

또한 같이 시간이 지나며 프로그래밍 언어 생태계에 큰 변화의 바람이 불었다. 프로그래머는 빅데이터라는 도전에 직면하며 병렬 프로세싱의 활용이 중요시 되었다.

하지만 이때까지의 자바는 일반 개발자들이 직접 병렬 프로세싱에 관련된 저수준 기능을 온전히 사용하는 것은 어려웠다. `Fork/Join Framework` 를 `Java 7` 에서 제공했지만 여전히 개발자가 활용하기는 쉽지 않았다.

![](./image/Pasted%20image%2020250205205001.png)


이러한 변화하는 환경을 Java 가 따라가지 못한다면 Java 는 다른 환경에 적응한 언어보다 도태되어 아무도 사용하지 않는 언어가 되었을 것이다. 하지만 `Java 8` 에서 가장 큰 변화가 생김으로 이러한 환경변화에 대응했다. 

Java 8에서 어떤 기능이 추가 되었는지 알아보기 전 Java 8에서 제공하는 기능의 모태인 3가지 프로그래밍 개념에 대해 알아보자

## Java 8 에 기반이 되는 프로그래밍 개념 3가지

### 1. Stream 처리


먼저 `Stream` 이란 한번에 하나씩 처리되는 데이터 항목의 모임을 의미한다.  

![](./image/Pasted%20image%2020250205205237.png)

이론적으로 프로그램은 입력 스트림으로 부터 데이터를 하나씩 읽은 후 처리한 뒤 출력 스트림에 하나씩 기록한다.

다음 예제를 보자.

```shell
cat file1 file2 | tr "[A-Z]" "[a-z]" | sort | tail -3
```

`unix` 명령어이다. 파일의 단어를 소문자로 바꾼 뒤 정렬한 후 마지막 3개의 단어를 출력하는 프로그램이다. 

> 💡 file1, file2 에는 한 행에 단어 하나를 포함하고 있다.

![](./image/Pasted%20image%2020250205211411.png)

위 그림 처럼 `sort` 는 여러 행의 스트림을 입력 받아 여러 행의 스트림을 출력으로 만들어 낸다. 여기서 중요한 점은 `cat` 또는 `tr` 이 작업이 안끝났더라도 `sort` 가 작업을 처리하기 시작할 수 있다는 것이다.

즉 모든 프로그램 즉 명령어가 `병렬` 으로 실행된다는 점이다.

이러한 과정을 자동차 생산 공장 라인에 비유할 수 있다.

![](./image/Pasted%20image%2020250205212309.png)

각각의 작업장에서는 자동차를 수리한 뒤 다른 작업장으로 해당 차를 보낸다. 이때 조립 라인은 자동차를 물리적인 순서로 한개씩 운반하지만 각각의 작업장에서는 동시에 작업을 수행한다.

`Java 8` 에서는 `java.util.stream` 패키지에 `Stream API` 가 추가되었다. 유닉스 명령어로 파이프 라인을 구성했던 것 처럼 Stream API 는 파이프라인을 만드는데 필요한 메서드를 제공한다.

### 2. 동작 파라미터화

`Java 8` 에 추가된 두번째 프로그래밍 개념은 코드 일부를 API 로 전달하는 기능이다.


아까 예시로 든 `sort` 명령어를 다시 떠올려 보자. sort에 파라미터를 제공해 정렬하는 방법들을 원할때 마다 바꾸고 싶지만 sort로 수행할 수 있는 동작은 미리 정해져 있다. 즉 원하는 정렬 기준을 미리 sort 코드 내부에 미리 작성해야 했다. 

![](./image/Pasted%20image%2020250205213949.png)

하지만`Java 8` 부터는 메서드를 메서드의 파라미터로 전달할 수 있는 기능이 추가되었다. 정렬에 필요한 조건 메서드를 원하는 시점에 정렬하는 메서드에 넣어 동작 시킬 수 있게 된 것이다.

이러한 기능을 `동작 파라미터화` 하고 부른다. 동작 파라미터화가 중요한 이유는 Stream API 가 연산의 동작을 파라미터화할 수 있는 코드를 전달한다는 사상에 기초하기 때문이다.



### 3. 병렬성과 공유 가변 데이터

Stream 매서드로 전달하는 코드는 다른 코드와 동시에 실행 되더라도 안전하게 실행될 수 있어야 한다. 보통 다른 코드와 동시에 실행되더라도 안전하게 실행 되기 위해서는 `공유 가변 데이터` 에 접근해서는 안된다.

이를 `순수 함수`, `부작용 없는 함수`, `무상태 함수` 라고 부른다.

공유된 변수나 객체에 여러 다른 코드가 동시에 접근할 경우 동시성 문제가 발생한다. 이 책 전체에서는 이와 같은 문제를 어떻게 해결하는지 확인할 수 있다.


## 자바 8에 추가된 새로운 기능

### 1. 메서드 참조

`Java 8` 에서는 메서드 참조라는 기능이 도입되었다. 

```java

File[] hiddenFiles = new File(".").listFiles(new Filter() {

	public boolean accept(File file) {
		return file.isHidden();
	}

});
```

디렉터리에서 모든 숨겨진 파일을 필터링 하는 코드이다. 짧은 코드이지만 각 행이 어떤 작업을 하는지 투명하지 않다.

`Java 8` 부터는 위 코드를 다음과 같이 바꿀 수 있다.

```java
File hiddenFiles = new File(".").listFiles(File::isHidden);
```

여기서 사용된 `::` 가 바로 메서드 참조이다. 매서드 참조는 해당 메서드를 값으로 사용하라는 의미이다. 

이 메서드 참조 덕분에 `isHidden` 함수를 `listFiles` 에 값으로 전달 할 수 있다.

#### 예제 

Apple 과 Color 가 다음과 같고

```java
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

```java
public enum Color {  
    RED, GREEN  
}
```

이들을 무게와 색깔로 필터링을 하기 위해서는 다음과 같이 해야 한다.

```java
public class FilterMainV1 {  
  
    public static void main(String[] args) {  
  
        List<Apple> inventory = getApple();  
  
        // 색으로 필터링  
        List<Apple> greenApples = filterGreenApples(inventory);  
        System.out.println(greenApples);  
  
        // 무게로 필터링  
        List<Apple> heavyApples = filterHeavyApples(inventory);  
        System.out.println(heavyApples);  
    }  
  
    private static List<Apple> getApple() {  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 151);  
        Apple apple3 = new Apple(GREEN, 140);  
        Apple apple4 = new Apple(RED, 200);  
  
        List<Apple> result = new ArrayList<>();  
  
        result.add(apple1);  
        result.add(apple2);  
        result.add(apple3);  
        result.add(apple4);  
  
        return result;  
    }  
  
    private static List<Apple> filterGreenApples(List<Apple> inventory) {  
        List<Apple> result = new ArrayList<>();  
  
        for (Apple apple : inventory) {  
            if (GREEN.equals(apple.getColor())) {  
                result.add(apple);  
            }  
        }  
  
        return result;  
    }  
  
    private static List<Apple> filterHeavyApples(List<Apple> inventory) {  
        List<Apple> result = new ArrayList<>();  
  
        for (Apple apple : inventory) {  
            if (apple.getWeight() > 150) {  
                result.add(apple);  
            }  
        }  
  
        return result;  
    }  
}
```

**결과**
```text
[Apple{color=GREEN, weight=100}, Apple{color=GREEN, weight=140}]
[Apple{color=RED, weight=151}, Apple{color=RED, weight=200}]
```

필터링 할 때 마다 복붙을 한 뒤 조건문만 바꿔치기 해야 한다. 즉 코드 중복이 많아짐과 동시에 어떤 코드에 버그가 있을 경우 복붙한 코드를 모두 수정해야하는 불상사가 생긴다.

하지만 다행히도 `Java 8` 에서 도입된 메서드 참조를 이용하면 함수를 메서드의 매개변수로 넘겨줄 수 있기 때문에 코드의 중복이 많이 줄어든다.

Apple class 에 Predicate 를 추가해보자.

```java
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
  
    public static boolean isHeavyApple(Apple apple) {  
        return apple.getWeight() > 150;  
    }  
  
    public static boolean isGreenApple(Apple apple) {  
        return GREEN.equals(apple.getColor());  
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

변경된 Main 클래스는 다음과 같다.

```java
public class FilterMainV2 {  
  
    public static void main(String[] args) {  
  
        List<Apple> inventory = getApple();  
  
        // 색으로 필터링  
        List<Apple> greenApples = filterApples(inventory, Apple::isGreenApple);  
        System.out.println(greenApples);  
  
        // 무게로 필터링  
        List<Apple> heavyApples = filterApples(inventory, Apple::isHeavyApple);  
        System.out.println(heavyApples);  
    }  
  
    private static List<Apple> getApple() {  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 151);  
        Apple apple3 = new Apple(GREEN, 140);  
        Apple apple4 = new Apple(RED, 200);  
  
        List<Apple> result = new ArrayList<>();  
  
        result.add(apple1);  
        result.add(apple2);  
        result.add(apple3);  
        result.add(apple4);  
  
        return result;  
    }  
  
    private static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {  
        List<Apple> result = new ArrayList<>();  
  
        for (Apple apple : inventory) {  
            if(p.test(apple)) {  
                result.add(apple);  
            }  
        }  
  
        return result;  
    }  
}
```

**결과**
``` text
[Apple{color=GREEN, weight=100}, Apple{color=GREEN, weight=140}]
[Apple{color=RED, weight=151}, Apple{color=RED, weight=200}]
```
### 2. 람다 - 익명 함수

메서드를 값으로 전달하는 기능은 매우 유용하지만 `isHeavyApple()` `isGreenApple()` 을 한 두번만 사용할건데 매번 정의하는 것은 매우 귀찮다. 

`Java 8` 에서는 이러한 문제를 해결하기 위해 람다 즉 익명 함수를 도입하였다.

Apple 클래스를 다시 원상태로 돌려놓자. 그후 Main 문에 람다식을 적용해 보자.

```java
public class FilterMainV3 {  
  
    public static void main(String[] args) {  
  
        List<Apple> inventory = getApple();  
  
        // 색으로 필터링  
        List<Apple> greenApples = filterApples(inventory, (a) -> GREEN == a.getColor());  
        System.out.println(greenApples);  
  
        // 무게로 필터링  
        List<Apple> heavyApples = filterApples(inventory, (a) -> a.getWeight() > 150);  
        System.out.println(heavyApples);  
    }  
  
    private static List<Apple> getApple() {  
        Apple apple1 = new Apple(GREEN, 100);  
        Apple apple2 = new Apple(RED, 151);  
        Apple apple3 = new Apple(GREEN, 140);  
        Apple apple4 = new Apple(RED, 200);  
  
        List<Apple> result = new ArrayList<>();  
  
        result.add(apple1);  
        result.add(apple2);  
        result.add(apple3);  
        result.add(apple4);  
  
        return result;  
    }  
  
    private static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {  
        List<Apple> result = new ArrayList<>();  
  
        for (Apple apple : inventory) {  
            if(p.test(apple)) {  
                result.add(apple);  
            }  
        }  
  
        return result;  
    }  
}
```
**결과**
```text
[Apple{color=GREEN, weight=100}, Apple{color=GREEN, weight=140}]
[Apple{color=RED, weight=151}, Apple{color=RED, weight=200}]
```

### 3. Stream

거의 모든 Java Application 에서 Collection 을 만들고 활용한다. 

예를 들어 리스트에서 고가의 거래만 필터링한 다음 통화로 결과를 그룹화 해야한다고 가정하자.

```java

Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();

for (Transaction transaction : transactions) {

	if (transactions.getPrice() > 1000) {
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

보기 힘들고 가독성이 정말 떨어진다. 이러한 방식을 `외부 반복` 이라고 한다. 하지만 `java 8` 에서 제공하는 Stream API 를 사용하면 다음과 같이 코드를 작성할 수 있다.

```java

Map<Currency, List<Transaction>> transactionsByCurrencies = 
	transactions.stream()
			.filter((Transaction t) -> t.getPrice() > 1000)
			.collect(groupingBy(Transaction::getCurrency));
```

stream api 를 사용할 경우 다음과 같이 코드를 간결하게 작성할 수 있다. stream 라이브러리 내부에서 반복을 처리하기 때문에 이를 `내부 반복`이라고 한다.

또한 stream 은 많은 데이터를 병렬으로 처리할 수 있게끔 지원한다.

### 4. Default 메서드

`java 7` 까지는 List 와 그 구현체 들에게 stream 을 생성하는 `stream()` 이라는 메서드가 존재하지 않았다.

`java 8` 에 들어오면서 모든 list 들은 `stream` 을 생성할 수 있도록 해야하는 요구 사항이 발생했다.

기존에는 메서드 구현을 모두 구현체에서 했기 때문에 모든 리스트를 돌면서 stream 을 생성하는 메서드를 구현해야 했을 것이다.

하지만 `java 8` 에서 interface 에 추상메서드만 작성할 수 있는 것이 아닌 완전한 메서드를 list 에 구현할 수 있게 했는데 바로 `default` 메서드이다.

이로인해 기존 구현 코드를 건들이지 않아도 원래의 인터페이스 설계를 자유롭게 확장할 수 있다.

