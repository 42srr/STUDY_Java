지난 장에서는 동작 파라미터화를 이용해서 변화하는 요구사항에 효과적으로 대응하는 코드를 구현해봤다.
익명 클래스로 다양한 동작을 구현할 수 있지만 만족할 만큼 코드가 깔끔하지는 않았다.
-> 람다 표현식을 배우게 될 것이다.

자바 8 API에 추가된 중요한 인터페이스와 형식 추론 등의 기능도 확인한다.

마지막으로 메서드 참조도 배우게 될 것이다.

## 배울 내용
---
- 람다란 무엇인가?
- 어디에, 어떻게 람다를 사용하는가?
- 실행 어라운드 패턴
- 함수형 인터페이스, 형식 추론
- 메서드 참조
- 람다 만들기

## 3.1 람다란 무엇인가?
---
- 메서드로 전달할 수 있는 익명 함수를 단순화 한 것

람다 표현식에는 이름은 없지만, 파라미터 리스트, 바디, 반환 형식, 발생할 수 있는 예외 리스트는 가질 수 있다.

장점
- 람다를 이용해서 간결한 방식으로 코드를 전달 할 수 있따. 
- 동작 파라미터를 이용할 때 익명 클래스 등 판에 박힌 코드를 구현할 필요가 없다.
### 람다의 특징
---
- 익명
	- 보통의 메서드와 달리 이름이 없으므로 익명이라 표현한다. 구현해야 할 코드에 대한 걱정거리가 줄어든다.
- 함수
	- 람다는 메서드처럼 특정 클래스에 종속되지 않으므로 함수라고 부른다. 하지만 메서드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외 리스트를 포함한다.
- 전달
	- 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
- 간결성
	- 익명 클래스처럼 많은 자질구레한 코드를 구현할 필요가 없다.

>람다라는 용어는 람다 비적분학 학계에서 개발한 시스템에서 유래했다.

### 람다
---
람다 표현식은 파라미터, 화살표, 바디로 이루어진다.
`(Apple a1, apple a2) -> a.getWeigh().compareTo(a2.getWeight());`

- 파라미터 리스트
Comparator의 compare 메서드 파라미터
- 화살표
람다의 파라미터 리스트와, 파라미터 바디를 구분한다
- 람다 바디
두 사과의 무게를 비교한다. 람다의 반환값에 해당하는 표현식

### 람다 기본 문법
---
`(parameters) -> expression`
`(parameters) -> { statements }`


### 람다 예제
```java
(String s) -> s.length()

(Apple a) -> a.getWeight() > 150

(int x, int y) -> {
	System.out.println("Result:);
	System.out.println(x + y);	
}

() -> 42

(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())

불리언 표현식
(List<String> list) -> list.isEmpty()

객체 생성
() -> new Apple(10)

객체에서 소비
(Apple a) -> {
	System.out.println(a.getWeight());
}

객체에서 선택/추출
(String s) -> s.length()

두 값을 조합
(int a, int b) -> a * b

두 객체 비교
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
```

## 3.2 어디에, 어떻게 람다를 사용할 것인지
---

>함수형 인터페이스라는 문맥에서 람다 표현식을 사용할 수 있다.

2장에서 구현했던 필터 메서드에도 람다를 활용할 수 있다.
```java
List<Apple> greenApples = 
	filter(inventory, (Apple a) -> GREEN.equals(a.getColor()));
```


### 3.2.1 함수형 인터페이스
---

>함수형 인터페이스는 정확히 하나의 추상 메서드를 지정하는 인터페이스다.
>자바 API의 함수형 인터페이스로, Comparator, Runnable 등이 있다.


2장에서 `Predicate<T>` 인터페이스로 필터 메서드를 파라미터화할 수 있었따.
이 `Predicate<T>` 가 함수형 인터페이스이다.
- 오직 하나의 추상 메서드만 지정하기 때문이다.

```java
public interface Predciate<T> {
	boolean test(T t);
}
```

>9장에서 나올 내용이지만 인터페이스는 *디폴트 메서드*(인터페이스의 메서드를 구현하지 않은 클래스를 고려해서 기본 구현을 제공하는 바디를 포함하는 메서드)를 포함할 수 있다.
>많은 디폴트 메서드가 있더라도 **추상 메서드가 오직 하나면 함수형 인터페이스**다.

#### 함수형 인터페이스로 뭘 할 수 있을까?
람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으므로 **전체 표현식을 함수형 인터페이스의 인스턴스로 취급**(기술적으로 따지면 함수형 인터페이스를 구현한 클래스의 인스턴스)할 수 있다.
+람다문장 자체가 하나의 인스턴스화된 클래스(메모리 상의 포인터 느낌)으로 구현되고 그걸 함수 포인터 처럼 넘긴다는 뜻인듯


함수형 인터페이스보다는 덜 깔끔하지만 익명 내부 클래스로도 같은 기능을 구현할 수 있다.

예제
```java
람다 사용
Runnable r1 = () -> System.out.println("Hello World 1");

익명 클래스 사용
Runnable r2 = new Runnable() {
	public void run() {
		System.out.prinln("Hello World 2");
	}
};

process(r1); -> 1 출력
process(r2); -> 2 출력
process(() -> System.out.println("Hello World 3")); -> 3 출력
```

### 3.2.2 함수 디스크립터
---
함수형 인터페이스의 추상 메서드 [[Signature]]는 람다 표현식의 시그니처를 가리킨다.
람다 표현식의 시그니처를 서술하는 메서드를 **함수 디스크립터**라고 부른다.

ex) Runnable 인터페이스의 유일한 추상 메서드 run은 인수와 반환값이 없으므로 Runnable 인터페이스는 인수와 반환값이 없는 시그니처로 생각할 수 있다.
-> 자바는 내부적으로 함수형 인터페이스에서 제공하는 기존의 형식을 재활용해서 함수 형식으로 매핑한다.
??? 반환값은 시그니처에 해당되지 않는다는데 왜 반환값이 없는 시그니처라고 하는지 모르겠다.

람다 표현식의 형식을 검사하는 방법은 3.5절 '형식 검사, 형식 추론, 제약'에서 컴파일러가 람다 표현식의 유효성을 확인하는 방법을 설명한다.
람다 표현식은 
- 변수에 할당하거나 함수형 인터페이스를 인수로 받는 메서드로 전달할 수 있으며,
- 함수형 인터페이스의 추상 메서드와 같은 시그니처를 갖는다는 사실이다.

+위에서 설명한 것처럼 그냥 메모리를 준다 이런 느낌..?

#### 람다와 메소드 호출
```java
process(() => System.out.println("This is awesome"));
```

정상적인 람다 표현식이다. 중괄호를 사용하지 않아도 괜찮다.
자바 언어 명세에서 void를 반환하는 메소드 호출과 관련된 특별한 규칙을 정하고 있기 때문에 한 개의 void 메소드 호출은 중괄호로 감쌀 필요가 없다.

## 3.3 람다 활용 : 실행 어라운드 패턴
---
자원 처리에 사용하는 순환 패턴(recurrent pattern)은 자원을 열고, 처리한 다음에, 자원을 닫는 순서로 이뤄진다.
설정(setup)과 정리(cleanup) 과정은 대부분 비슷하다.

실제 자원을 처리하는 코드를 설정과 정리 두 과정이 둘러싸는 형태를 갖는다.
이와 같은 형식의 코드를 **실행 어라운드 패턴** execute around pattern이라고 부른다.

![[모던자바인액션3.3]]

실행 어라운드 패턴 예제
- [[try-with-resources]] 구문을 사용했다.
	- 자원을 명시적으로 닫을 필요가 없어 간결한 코드를 구현하는데 도움을 준다.
```java
public string processFile() throws IOException {
	try (BufferedReader br = new BufferedReader(new FileReader("data.txt))) {
		return br.readLine();
	}
}
```


### 3.3.1 1단계 : 동작 파라미터화를 기억하라
---
- 문제점
현재 코드는 파일에서 한 번에 한 줄만 읽을 수 있다. 한 번에 두 줄을 읽거나 가장 자주 사용되는 단어를 반환하려면 어떻게 해야 할까?

- 개선 방법
기존의 설정, 정리 과정은 재사용하고 processFile메서드만 다른 동작을 수행하도록 명령할 수 있다면 좋을 것 같다.
즉, processFile의 동작을 파라미터화하는 것이다.

- 구현 방법
processFile이 BufferedReader을 이용해서 다른 동작을 수행할 수 있도록 람다를 이용해 동작을 전달해야 한다.

```java
String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

### 3.3.2 2단계 : 함수형 인터페이스를 이용해서 동작 전달
---
함수형 인터페이스 자리에 람다를 사용할 수 있다.
따라서, BufferedReader -> String과 IOException을 던질 수 있는 시그니처와 일치하는 함수형 인터페이스를 만들어야 한다.

```
@FunctionalInterface
public interface BufferedReaderProcessor {
	String process(BufferedReader b) throws IOException;
}

public String processFile(BufferedReaderProcessor p) throws IOException {

}
```
![[@FunctionalInterface]]


### 3.3.3 3단계 : 동작 실행
---
BufferedReaderProcessor의 process 메서드의 시그니처 (BufferedReader -> String) 과 일치하는 람다를 전달할 수 있다.

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
	try (BufferedReader br = new BufferedReader(new FileReader("data.txt))) {
		return p.process(br);
	}

}
```

### 3.3.4 4단계 : 람다 전달
---
```
String oneLine = processFile((BufferedReader br) -> br.readLine());

String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

## 3.4 함수형 인터페이스 사용
---
함수형 인터페이스는 오직 하나의 추상 메서드를 지정한다.

함수형 인터페이스의 추상 메서드는 람다 표현식의 시그니처를 묘사한다.

함수형 인터페이스의 추상 메서드 시그니처를 함수 디스크립터(function descriptor)라고 한다.

다양한 람다 표현식을 사용하려면 공통의 함수 디스크립터를 기술하는 함수형 인터페이스 집합이 필요하다.
이미 자바 API는 Comparable, Runnable, Callable 등의 다양한 함수형 인터페이스를 포함하고 있다.

자바 8은 java.util.function 패키지로 여러 가지 새로운 함수형 인터페이스를 제공한다.
### 3.4.1 Predicate
---
`java.util.function.Predicate<T>` 인터페이스는 test라는 추상 메서드를 정의하며 test는 제네릭 형식 T 객체를 인수로 받아 불리언을 반환한다.

Predicate 예제
```java
@FunctionalInterface
public interface Predicate<T> {
	boolean test(T t);
}

public <T> List<T> filter(List<T> list, Predicate<T> p) {
	List<T> results = new ArrayList<>();
	for(T t: list) {
		if(p.test(t))
			results.add(t);
	}
	return results;
}

Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();

List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```

Predicate 인터페이스의 자바독 명세를 보면 and나 or 같은 메서드도 있음을 알 수 있따.

### 3.4.2 Consumer
---
`java.util.function.Consumer<T>`인터페이스는 제네릭 형식 T 객체를 받아서 void를 반환하는 accept라는 추상 메서드를 정의한다.

T 형식의 객체를 인수로 받아 어떤 동작을 수행하고 싶을 때 Consumer 인터페이스를 사용할 수 있다.

consumer예제
```java
@FunctionalInterface
public interface Consumer<T> {
	void accept(T t);
}

public <T> void forEach(List<T> list, Consumer<T> c) {
	for (T t: list) {
		c.accept(t);
	}
}

forEach(
	Arrays.asList(1,2,3,4,5),
	(Integer i) -> System.out.println(i);
)
```

### 3.4.3 Function
---
`java.util.function.Function<T, R>`인터페이스는 제네릭 형식 T를 인수로 받아서 제네릭 형식 R 객체를 반환하는 추상 메서드 apply를 정의한다. 

입력을 출력으로 매핑하는 람다를 정의할 때 Function 인터페이스를 활용할 수 있다.

Function 예제
```java
@FunctionalInterface
public interface Function(T, R) {
	R apply(T t);
}

public <T, R> List<R> map(List<T> list, Function<T, R> f) {
	List<R> result = new ArrayList<>();
	for(T t: list)
		result.add(f.apply(t));
	return result;
}

[7, 2, 6]
List<Integer> l = map(Arrays.asList("lambdas", "in", "action"),
						(String s) -> s.length());
```


### 기본형 특화
---
자바의 모든 형식은 참조형 아니면 기본형에 해당한다. 하지만 제네릭 파라미터 T 는 참조형만 사용할 수 있다.
제네릭의 내부 구현 때문에 어쩔 수 없다. 

- 박싱 boxing
자바에서는 기본형을 참조형으로 변환하는 기능

- 언박싱 unboxing
참조형을 기본형으로 변환하는 반대 동작

- 오토박싱 autoboxing
박싱과 언박싱이 자동으로 이루어지는 기능

올바른 예
```java
List<Integer> list = new ArrayList<>();
for (int i = 300; i < 400; i++) {
	list.add(i);
}
```

하지만 이러한 변환 과정은 비용이 소모된다.
- 박싱한 값은 기본형을 감싸는 래퍼며 힙에 저장된다.
	-> 박싱한 값은 메모리를 더 소비하며 기본형을 가져올 때도 메모리를 탐색하는 과정이 필요하다.

자바8에서는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 특별한 버전의 함수형 인터페이스를 제공한다.

```java
public interface IntPredicate {
	boolean test(int t);
}

참, 박싱 없음
IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbers.test(1000);

거짓, 박싱 
Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
oddNumbers.test(1000);
```

일반적으로 특정 형식을 입력으로 받는 함수형 인터페이스의 이름 앞에는 DoublePredicate, IntConsumer, LongBinaryOperator, IntFunction처럼 형식명이 붙는다. 

Function 인터페이스는 ToIntFunction<T>, IntToDoubleFunction 등의 다양한 출력 형식 파라미터를 제공한다.







