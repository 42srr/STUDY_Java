
## 3.5 형식 검사, 형식 추론, 제약
---
람다로 함수형 인터페이스의 인스턴스를 만들 수 있다.
람다 표현식 자체에는 람다가 어떤 함수형 인터페이스를 구현하는지의 정보가 포함되어 있지 않아 람다 표현식을 더 제대로 이해하려면 람다의 실제 형식을 파악해야 한다.

### 3.5.1 형식 검사
---
람다가 사용되는 콘텍스트(context)를 이용해서 람다의 형식(type)을 추론할 수 있다. 
어떤 콘텍스트(람다가 전달될 메서드 파라미터나 람다가 할당되는 변수 등)에서 기대되는 람다 표현식의 형식을 **대상 형식**(target type)이라고 부른다.

```java
List<Apple> heavierThan150g = 
	filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

형식 확인 과정
1. `filter` 메서드의 선언을 확인한다.
2. `filter` 메서드는 두 번째 파라미터로 `Predicate<Apple>` 형식 (대상 형식)을 기대한다.
3. `Predicate<Apple>`은 `test`라는 한 개의 추상 메서드를 정의하는 함수형 인터페이스다.
4. `test`메서드는 Apple을 받아 boolean을 반환하는 함수 디스크립터를 묘사한다.
5. `filter`메서드로 전달된 인수는 이와 같은 요구사항을 만족해야 한다.

![[모던자바인액션35]] 
- 람다 표현식이 예외를 던질 수 있다면 추상 메서드도 같은 예외를 던질 수 있도록 throws로 선언해야 한다.

### 3.5.2 같은 람다, 다른 함수형 인터페이스
---
대상 형식(target typing)이라는 특징 때문에 같은 람다 표현식이더라도 호환되는 추상 메서드를 가진 다른 함수형 인터페이스로 사용될 수 있다.

예를 들어 이전에 살펴본 Callable과 PrivilegedAction 인터페이스는 인수를 받지 않고 제네릭 형식 T를 반환하는 함수를 정의한다.

하단 두 할당문 모두 유효한 코드이다.
```java
Callable<Integer> c = () -> 42;
PrivilegedAction<Integer> p = () -> 42;
```

#### [[다이아몬드 연산자]]

#### 특별한 void 호환 규칙
람다의 바디에 일반 표현식이 있으면 void를 반환하는 함수 디스크립터와 호환된다. (파라미터 리스트도 호환되어야 함.)

void대신  boolean을 반환하지만 유효한 코드다.

```java
Predicate는 불리언 반환값을 갖는다.
Predicate<String> p = s -> list.add(s);

Consumer는 void 반환값을 갖는다.
Consumer<String> b = s -> list.add(s);
```

### 3.5.3 형식 추론
자바 컴파일러는 람다 표현식이 사용된 콘텍스트(대상 형식)을 이용해서 람다 표현식과 관련된 함수형 인터페이스를 추론한다.

즉, 대상 형식을 이용해서 함수 디스크립터를 알 수 있으므로 컴파일러는 람다의 시그니처도 추론할 수 있다.

결과적으로 컴파일러는 람다 표현식의 파라미터 형식에 접근할 수 있으므로 람다 문법에서 이를 생략할 수 있다.

```java
List<Apple> greenApples = filter(inventory, apple -> GREEN.equals(apple.getColor()));

형식을 추론하지 않음
Comparator<Apple> c = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

형식을 추론함
Comparator<Apple> c = (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
```

상황에 따라 명시적으로 형식을 포함하는 것이 좋을 때도 있고 형식을 배제하는 것이 가독성을 향상시킬 때도 있다.
정해진 규칙은 없다.

### 3.5.4 지역 변수 사용
지금까지 살펴본 모든 람다 표현식은 인수를 자신의 바디 안에서만 사용했다.
하지만 람다 표현식에서는 익명 함수가 하는 것처럼 **자유 변수**(free variable) (파라미터로 넘겨진 변수가 아닌 외부에서 정의된 변수)를 활용할 수 있다.
이와 같은 동작을 **람다 캡처링**(capturing lambda)라고 부른다.

portNumber 변수를 캡처하는 람다 예제
```java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
```

자유 변수의 제약
- 람다는 인스턴스 변수와 정적 변수를 자유롭게 캡처(자신의 바디에서 참조할 수 있도록)할 수 있다.
- 지역 변수는 명시적으로 final로 선언되어 있어야 하거나 실질적으로 final로 선언된 변수와 똑같이 사용되어야 한다.

즉, 람다 표현식은 한 번만 할당할 수 있는 지역 변수를 캡처할 수 있다.
+ 인스턴스 변수 캡처는 final 지역 변수 this를 갭처하는 것과 마찬가지다.

불가능한 람다 예제
```java
int portNumber 1337;
Runnable r = () -> System.out.println(portNumber);
portNumber = 31337;
```
- 값을 두 번 할당하므로 컴파일 할 수 없는 코드다.

#### 지역 변수의 제약
인스턴스 변수 -> 힙에 저장
지역 변수 -> 스택에 위치

람다에서 지역 변수에 바로 접근할 수 있다는 가정 하에 람다가 스레드에서 실행된다면 변수를 할당한 스레드가 사라져 변수 할당이 해제되었는데도 람다를 실행하는 스레드에서는 해당 변수에 접근하려 할 수 있다.

따라서 자바 구현에서는 원래 변수에 접근을 허용하는 것이 아니라 자유 지역 변수의 복사본을 제공한다.
따라서 복사본의 값이 바뀌지 않아야 하므로 지역 변수에는 **한 번만 값을 할당해야 한다는 제약**이 생긴 것이다.

또한 지역 변수의 제약 때문에 외부 변수를 변화시키는 일반적인 명령형 프로그래밍 패턴(병렬화를 방해하는 요소)에 제동을 걸 수 있다.

#### 클로저
람다가 클로저의 정의에 부합하는가?
원칙적으로 클로저란 함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스를 가리킨다.

예를 들어 클로저를 다른 함수의 인수로 전달할 수 있다.
클로저는 클로저 외부에 정의된 변수의 값에 접근하고, 바꿀 수 있다.

자바 8의 람다와 익명 클래스는 클로저와 비슷한 동작을 수행한다.
람다와 익명 클래스 모두 메서드의 인수로 전달될 수 있으며 자신의 외부 영역의 변수에 접근할 수 있다.

다만, 람다와 익명 클래스는 람다가 정의된 메서드의 지역 변수 값은 바꿀 수 없다.
람다가 정의된 메서드의 지역 변수값은 final 변수여야 한다.
덕분에 람다는 변수가 아닌 값에 국한되어 어떤 동작을 수행한다는 사실이 명확해진다.

이전에도 설명한 것처럼 지역 변숫값은 스택에 존재하므로 자신을 정의한 스레드와 생존을 같이 해야 하며 따라서 지역 변수는 final이어야 한다.

가변 지역 변수를 새로운 스레드에서 캡처할 수 있다면 안전하지 않은 동작을 수행할 가능성이 생긴다
(인스턴스 변수는 스레드가 공유하는 힙에 존재하므로 특별한 제약이 없다.)

## 3.6 메서드 참조
메서드 참조를 이용하면 기존의 메서드 정의를 재활용해서 람다처럼 전달할 수 있다.
때로는 람다 표현식보다 메서드 참조를 사용하는 것이 더 가독성이 좋으며 자연스러울 수 있다.

정렬 예제
```java
기존 코드
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));

java.util.Comparator.comparing을 활용한 코드
inventory.sort(comparing(Apple::getWeight));
```

### 3.6.1 요약
메서드 참조는 특정 메서드만을 호출하는 람다의 축약형이라고 생각할 수 있다.

예를 들어 람다가 '이 메서드를 직접 호출해'라고 명령한다면
메서드를 어떻게 호출해야 하는지 설명을 참조하기보다는 메서드명을 직접 참조하는 것이 편리하다.
실제로 메서드 참조를 이용하면 기존 메서드 구현으로 람다표현식을 만들 수 있다.

이때 명시적으로 메서드명을 참조함으로써 **가독성을 높일 수 있따**

`Apple:getWeight` Apple 클래스에 정의된 getWeight의 메서드 참조다.
실제로 메서드를 호출하는 것이 아니므로 괄호는 필요 없다.

결국 `(Apple a) -> a.getWeight()`을 축약한 것이다.

#### 메서드 참조를 만드는 방법
세 가지 유형으로 구분할 수 있다.

1. 정적 메서드 참조
Integer의 parseInt -> `Integer::parseInt`

2. 다양한 형식의 인스턴스 메서드 참조
String의 length -> `String::length`

3. 기존 객체의 인스턴스 메서드 참조
Transaction 객체를 할당받은 expensiveTransaction 지역 변수가 있고, Transaction 객체에는 getValue 메서드가 있다면 -> expensiveTransaction::getValue

생성자, 배열 생성자, super 호출 등에 사용할 수 있는 특별한 형식의 메서드 참조도 있다.

메서드 참조는 콘텍스트의 형식과 일치해야 한다.

### 3.6.2 생성자 참조
ClassName::new 처럼 클래스명과 new 키워드를 이용해서 기존 생성자의 참조를 만들 수 있따.
이는 정적 메서드의 참조를 만드는 방법과 비슷하다.

예제
```java
Supplier의 get 메서드를 호출해서 새로운 Apple 객체를 만들 수 있다.
Supplier<Apple> c1 = Apple::new;
Apple a1 = c1.get();

람다 표현식은 디폴트 생성자를 가진 Apple을 만든다.
Supplier<Apple> c1 = () -> new Apple();
Apple a1 = c1.get();
```

Apple(Integer weight)이라는 시그니처를 갖는 생성자는 Function 인터페이스의 시그니처와 같다.
```java
Apple(Integer weight) 생성자를 참조한다
Function<Integer, Apple> c2 = Apple::new;
Apple a2 = c2.apply(110);

Function<Integer, Apple> c2 = (weight) -> new Apple(weight);
Apple a2 = c2.apply(110);
```

Integer를 포함하는 리스트의 각 요소를 우리가 정의했던 map 같은 메서드를 이용해서 Apple 생성자로 전달
```java
List<Integer> weights = Arrays.asList(7, 3, 4, 10);
List<Apple> apples = map(weights, Apple::new);

public List<Apple> map(List<Integer> list, Function<Integer, Apple> f) {
	List<Apple> result = new ArrayList<>();
	for(Integer i: list) {
		result.add(f.apply(i));
	}
	retun result;
}
```

## 3.7 람다, 메서드 참조 활용하기

### 3.7.1 1단계: 코드 전달
sort 메서드에 정렬 전략을 전달하는 방법?

sort 메서드 시그니쳐
`void sort(Comparator<? super E> c)`

Comparator 객체를 전달받아 두 인수를 비교한다.
이제 'sort의 **동작은 파라미터화**되었다고 말할 수 있다.'

```java
public class AppleComparator implements Comparator<Apple> {
	public int compare(Apple a2, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
}

inventory.sort(new AppleComparator());
```

### 3.7.2 2단계: 익명 클래스 사용
한 번만 사용할 Comparator는 위 코드처럼 구현보다는 익명 클래스가 더 좋다

```java

inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
});
```

### 3.7.3 3단계: 람다 표현식 사용
람다 표현식이라는 경령화된 문법을 이용해 **코드를 전달**할 수 있다.
**함수형 인터페이스**를 기대하는 곳 어디에서나 람다 표현식을 사용할 수 있음을 배웠따.

함수형 인터페이스란 오직 하나의 추상 메서드만을 정의하는 인터페이스다.
추상 메서드의 시그니처(**함수 디스크립터**)는 람다 표현식의 시그니쳐를 정의한다.

~~~java
inventory.sort((Apple a2, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
~~~

자바 컴파일러는 람다 표현식이 사용된 콘텍스트를 활용해서 람다의 파라미터 **형식**을 추론한다.

Comparator는 Comparable 키를 추출해서 Comparator 객체로 만드는 Function 함수를 인수로 받는 정적 메서드 Comparing을 포함한다.

~~~java
Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());

import static java.util.Comparator.comparing;
inventory.sort(comparing(apple -> apple.getWeight()));
~~~

### 4단계: 메서드 참조 사용
~~~java
inventory.sort(comparing(Apple::getWeight));
~~~

## 3.8 람다 표현식을 조합할 수 있는 유용한 메서드
자바 8 API의 몇몇 함수형 인터페이스는 다양한 유틸리티 메서드를 포함한다.

여러 개의 람다 표현식을 조합해서 복잡한 람다 표현식을 만들 수 있다는 것이다.

예를 들어 두 프레디케이트를 조합해서 두 프레디케이트의 or 연산을 수행하는 커다란 프리디케이트를 만들 수 있다.

**디폴트 메서드**(추상 메서드가 아니므로 함수형 인터페이스의 정의를 벗어나지 않음)다.

### 3.8.1 Comparator 조합
정적 메서드 Comparator.comparing을 이용해서 비교에 사용할 키를 추출하는 Function 기반의 Comparator를 반환할 수 있다.
~~~java
Comparator<Apple> c = Comparator.comparing(Apple::getWeight);
~~~

#### 역정렬
사과의 무게를 내림차순으로 만들려면 다른 Comparator 인스턴스를 만들 필요가 없다.
인터페이스 자체에서 주어진 비교자의 순서를 뒤바꾸는 reverse라는 디폴트 메서드를 제공하기 때문이다.

~~~java
inventory.sort(Comparing(Apple::getWeight).reversed());
~~~

#### Comparator 연결
무게가 같다면 의도된 결과로 더 다듬기 위한 Comparator를 만들 수 있다.
즉, thenComparing을 사용해 두번째 인자를 통해 비교할 수 있다.

~~~java
inventory.sort(comparing(Apple::getWeight)
		.reversed()
		.thenComparing(Apple::getCountry));
~~~

#### Predicate 조합
Predicate 인터페이스는 복잡한 프레디케이트를 만들 수 있도록 negate, and, or 세 가지 메서드를 제공한다.

- 기존 프레디케이트 객체 redApple의 결과를 반전시킨 객체
~~~java
Predicate<Apple> notRedApple = redApple.negate();
~~~

- and 메서드를 사용해 조건을 추가
~~~java
Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.getWeight() > 150);
~~~

- or 메서드를 사용해 더 많은 조건 추가
~~~java
Predicate<Apple> redAndHeavyAppleOrGreen =
	redApple.and(apple -> apple.getWeight() > 150)
			.or(apple -> GREEN.equals(a.getColor()));
~~~


### 3.8.3 Function 조합
Function 인터페이스에서 제공하는 람다 표현식도 조합할 수 있다.
Function 인터페이스는 Function 인터페이스를 반환하는 andThen, compose 두 가지 디폴트 메서드를 제공한다.

#### andThen 메서드
주어진 함수를 먼저 적용한 결과를 다른 함수의 입력으로 전달하는 함수를 반환한다.
~~~java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x + 2;
Function<Integer, Integer> h = f.andThen(g);

int result = h.apply(1);

4반환
~~~

#### compose 메서드
인수로 주어진 함수를 먼저 실행한 다음에 그 결과를 외부 함수의 인수로 제공
f.andThen(g) -> g(f(x))
f.compose(g) -> f(g(x))

## 3.9 비슷한 수학적 개념
패스

## 3.10 마치며
- **람다 표현식**은 익명 함수의 일종
  이름은 없지만, 파라미터 리스트, 바디, 반환 형식을 가지며 예외를 던질 수 있다.
- **함수형 인터페이스**는 하나의 추상 메서드를 정의하는 인터페이스다.
- 함수형 인터페이스를 기대하는 곳에서만 람다 표현식을 사용할 수 있다.
- 람다 표현식을 이용해서 함수형 인터페이스의 추상 메서드를 즉석으로 제공할 수 있으며 **람다 표현식 전체가 함수형 인터페이스의 인스턴스로 취급된다.**
- java.util.function 패키지는 다양한 함수형 인터페이스를 제공한다.
- 자바8은 Predicate<T>, Function<T, R> 같은 제네릭 함수형 인터페이스와 관련한 박싱 동작을 피할 수 있는 IntPredicate, IntToLongFunction 등과 같은 기본형 특화 인터페이스도 제공한다.
- 실행 어라운드 패턴(자원 할당, 자원 정리)를 람다와 활용하면 유연성과 재사용성을 추가로 얻을 수 있다.
- 람다 표현식의 기대 형식을 **대상** 형식이라고 한다.
- 메서드 참조를 이용하면 기존의 메서드 구현을 재사용하고 직접 전달할 수 있다.

