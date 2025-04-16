동작 파라미터화에는 메서드 내부적으로 다양한 동작을 수행할 수 있도록 코드를 메서드 인수로 전달한다.
이 동작 파라미터화를 이용하면 자주 바뀌는 요구사항에 효과적으로 대응할 수 있다.

## 배울 내용
---
- 변화하는 요구사항에 대응
시각각 변화하는 사용자 요구 사에 **동작 파라미터화**를 이용하면 효과적으로 대응할 수 있다.

- 동작 파라미터화
아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록
나중에 프로그램에서 호출, 코드 블록의 실행은 나중으로 미뤄진다.
강점 : 컬렉션 탐색 로직과 그 항목에 적용할 동작을 분리할 수 있다는 것

- 익명 클래스
- 람다 표현식 미리보기
동작 파라미터화를 추가하려면 쓸데 없는 코드가 늘어나는데, 자바 8은 람다 표현식으로 이 문제를 해결한다.

## 2.1 변화하는 요구사항에 대응하기
---
- 색을 구별하는 코드, 무게를 고려하는 코드, 색을 구별하고 무게를 고려하는 코드는 소프트웨어 공학의 DRY 원칙을 어기는 것

## 2.2 동작 파라미터화
---
- 우리는 선택 조건을 어떤 속성에 기초해서 불리언 값을 반환하는 방법이 있다.
>참 또는 거짓을 반환하는 함수를 프레디케이트라고 한다.

```java
무거운 사과만 선택
public class AppleHeavyWeightPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return apple.getWeight() > 150;
	}
}

녹색 사과만 선택
public class AppleGreenPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return GREEN.equals(apple.getColor());
	}
}
```

![[모던자바인액션ch2]]
- ApplePredicate는 사과 선택 전략을 캡슐화 함
- 위 조건에 따라 filter 메서드가 다르게 동작할 것이라고 예상할 수 있다.
- 이를 전략 디자인 패턴이라고 한다.
	- 각 알고리즘(전략)을 캡슐화하는 알고리즘 패밀리를 정의해둔 다음에 런타임에 알고리즘을 선택하는 기법
	- ApplePredicate -> 알고리즘 패밀리, 각각 Predicate -> 전략

ApplePredicate가 어떻게 다양한 동작을 수행할 수 있을까?
1. filterApples에서 ApplePredicate 객체를 받아 사과의 조건을 검사하도록 메소드를 고쳐야 한다.
2. 이렇게 동작 파라미터화. 메서드가 다양한 동작(전략)을 받아서 내부적으로 다양한 동작을 수행할 수 있다.

### 2.2.1 4번째 시도 : 추상적 조건으로 필터링
---
```java
ApplePredicate를 이용한 필터 메서드

public static List<Apple> filterApples(List<Apple> inv, ApplePredicate p) {
	List<Apple> result = new ArrayList<>();

	for(Apple apple : inv) {
		if(p.test(apple))
			result.add(apple);
	}
	return result;
}
```
if 조건을 프레디케이트 객체로 사과 검사 조건을 캡슐화 했다.

## 2.3 복잡한 과정 간소화
---
문제점
- filterApples 메서드로 새로운 동작을 전달하려면 ApplePredicate 인터페이스를 구현하는 여러 클래스를 정의한 다음 인스턴스화 해야한다.

해결책
- 자바는 클래스 선언과 인스턴스화를 동시에 수행할 수 있도록 익명 클래스라는 기법을 제공한다.
- 익명 클래스가 모든 것을 해결하는 것은 아니기에 람다를 배워 더 가독성 있는 코드를 구현한다.

### 2.3.1 익명 클래스
---
자바의 지역 클래스(블록 내부에 선언된 클래스)와 비슷한 개념이다.
사용 시 클래스 선언과 인스턴스화를 동시에 할 수 있다.
즉, 즉석에서 필요한 구현을 만들어서 사용할 수 있다.

### 2.3.2 다섯 번째 시도 : 익명 클래스
```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
	public boolean test(Apple apple) {
		return RED.equals(apple.getColor());
	}
});
```
- filterApples 메서드의 동작을 직접 파라미터화 했다.

부족한 점
- 익명 클래스는 여전히 많은 공간을 차지한다.(코드가 많다.)

### 2.3.3 여섯 번째 시도 : 람다 표현식
```java
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

### 2.3.4 일곱 번째 시도 : 리스트 형식으로 추상화
```java
public interface Predicate<T> {
	boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
	List<T> result = new ArrayList<>();
	for(T e: list) {
		if(p.test(e))
			result.add(e);
	}
	return result;
}

필터 메서드 사용 예제
List<Integer> evenNumbers = filter(numbers, (Integer i) -> i % 2 == 0);
```
- 유연성과 간결함을 모두 해결할 수 있다.

## 2.4 실전 예제
---
- 2.4.1 Comparator 정렬
- Runnable 코드 블록 실행
- Callable 결과 반환
- GUI 이벤트 처리

### 2.4.1 Comparator로 정렬하기
---
자바 8의 List에는 sort 메서드가 포함되어 있다. (Collections.sort도 존재)

```java
java.util.Comparator

public interface Comparator<T> {
	int compare(T o1, T o2);
}
```

이 Comparator를 구현해서 sort 메서드의 동작을 다양화 할 수 있다.
```java
inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
});

람다
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

### 2.4.2 Runnable로 코드 블록 실행하기
---
자바 스레드를 이용하면 병렬로 코드 블록을 실행할 수 있다.

```java
java.lang.Runnable

public interface Runnable {
	void run();
}

Thread t = new Thread(new Runnable() {
	public void run() {
		System.out.println("Hello world");
	}
});

람다
Thread t = new Thread(() -> System.out.println("Hello world"));
```

### 2.4.3 Callable을 결과로 반환하기
---
자바 5부터 지원하는 ExecutorService 인터페이스는 태스크 제출과 실행 과정의 연관성을 끊어준다.

태스크를 스레드 풀로 보내고 결과를 Future로 저장할 수 있다는 점이 스레드와 Runnable을 이용하는 방식과는 다르다.

```java
java.util.concurrent.Callable

public interface Callable<V> {
	V call();
}

ExecutorService exe = Executors.newCachedThreadPool();

Future<String> threadName = exe.submit(new Callable<String>() {
	@Override
	public String call() throws Exception {
		return Thread.currentThread().getName();
	}
});

람다
Future<String> threadName = exe.submit(() -> Thread.currentThread().getName());
```

## 동작 파라미터화 방법
---
코드 전달 기법을 이용하면 동작을 메서드의 인수로 전달할 수 있다.

익명 클래스로도 어느 정도 코드를 깔끔하게 만들 수 있지만 자바 8에서는 인터페이스를 상속받아 여러 클래스를 구현해야 하는 수고를 없앨 수 있는 방법을 제공한다.

3. 선택 조건을 결정하는 (전략)인터페이스 선언(Predicate)
```java
public interface ApplePredicate {
	boolean test(Apple apple);
}
```

4. 클래스를 통한 동작 파라미터화
```java
public class AppleGrennColorPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return GREEN.equals(apple.getColor());
	}
}

List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate());
```

5. 익명 클래스를 통한 동작 파라미터화
```java
List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate() {
	public boolean test(Apple apple) {
		return GREEN.equals(apple.getColor());
	}
});
```

6. 람다를 통한 동작 파라미터화
```java
List<Apple> greenApples = filterApples(inventory, (Apple apple) -> GREEN.equals(apple.getColor()))
```

![[파라미터화]]

- 자바 API의 많은 메서드는 정렬, 스레드, GUI 처리 등을 포함한 다양한 동작으로 파라미터화 할 수 있다.
```java
//java.util.Comparator
//java.util.Compaator
java.util.Comparator
public interface Comparator<T> {
	int compare(T o1, To2)
}

anonymous class
inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
});

lamda
inventory.ort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

## 예시 코드
7. 익명 클래스 Anonymous Class
```java
import java.util.ArrayList;
import java.util.List;

public class BehaviorParameteriztionExample {

	동작 인터페이스 (전략 인터페이스)
	interface Predicate<T> {
		boolean test(T t);
	}

	리스트 필터링 메서드: 특정 조건에 맞는 요소들만 반환
	public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
		List<T> result = new ArrayList<>();
		for (T t : list) {
			if (predicate.test(t)) {
				result.add(t);
			}
		}
		return result;
	}

	public static void main(String[] args) {
		List<Integer> numbers = List.of(1,2,3,4,5,6);

		짝수 필터링 - 익명 클래스를 사용한 구현
		List<Integer> evens = filter(numbers, new Predicate<Integer>() {
			@Override
			public boolean test(Integer number) {
				return number % 2 == 0;
			}
		});
		System.out.println("짝수: " + evens);
	}
}

```

8. 람다 표현식 Lamda Expression
```java
import java.util.ArrayList;
import java.util.List;

public class LambdaBehaviorParameterization {
    
    함수형 인터페이스
    @FunctionalInterface
    interface Predicate<T> {
        boolean test(T t);
    }
    
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T t : list) {
            if (predicate.test(t)) {
                result.add(t);
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
        
        람다 표현식을 사용한 짝수 필터링
        List<Integer> evens = filter(numbers, number -> number % 2 == 0);
        
        System.out.println("짝수: " + evens);
    }
}

```

9. 메서드 참조 Method Reference
```java
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        
        // java.util.function.Predicate 인터페이스를 사용하여 람다 대신 메서드 참조로 처리 가능
        // 여기서는 예시로 짝수 판별을 별도의 메서드로 분리하여 사용
        List<Integer> evens = numbers.stream()
                                     .filter(MethodReferenceExample::isEven)
                                     .collect(Collectors.toList());
        
        System.out.println("짝수: " + evens);
    }
    
    private static boolean isEven(int number) {
        return number % 2 == 0;
    }
}

```
