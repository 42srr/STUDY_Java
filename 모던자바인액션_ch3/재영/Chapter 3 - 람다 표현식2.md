
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
