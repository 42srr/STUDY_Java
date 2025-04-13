**CHAPTER 5 스트림 활용**

이 장의 내용

- 필터링, 슬라이싱, 매칭
- 검색, 매칭, 리듀싱
- 특정 범위의 숫자와 같은 숫자 스트림 사용하기
- 다중 소스로부터 스트림 만들기
- 무한 스트림

### 5.1 필터링

여기서는 스트림의 요소를 선택하는 방법, 즉 프레디케이트 필터링 방법과 고유 요소만 필터링 하는 방법을 배운다.

#### 5.1.1 프레디케이트로 필터링

프레디케이트: 불리언을 반환하는 함수

```java
List<Dish> vegetarianMenu = menu.stream()
				.filter(Dish::isVegetarian) // 채식 요리인지 확인하는 메서드 참조
				.collect(toList());
```

#### 5.1.2 고유 요소 필터링

distinct 메서드: 고유 요소로 이루어진 스트림을 반환

```java
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream()
	.filter(i -> i % 2 == 0)
	.distinct()
	.forEach(System.out::println); // 2, 4 하나씩 출력

```

### 5.2 스트림 슬라이싱

스트림의 요소를 선택하거나 스킵하는 스트림 슬라이싱을 배운다. 프레디케이트를 이용하는 방법, 스트림의 처음 몇 개의 요소를 무시하는 방법, 특정 크기로 스트림을 줄이는 방법 등 다양한 방법을 이용해 효율적으로 이런 작업을 수행할 수 있다.

#### 5.2.1 프레디케이트를 이용한 슬라이싱

##### TAKEWHILE

takeWhile: 프레디케이트가 거짓이 되면 반복 중단

아래코드: specialMenu 는 칼로리 순으로 정렬되어 있고, 칼로리가 320 이상이 되면 중단된다.

```java
List<Dish> slicedMenu1
	= specialMenu.stream()
			.takeWhile(dish -> dish.getCalories() < 320)
			.collect(toList());
```

##### DROPWHILE

dropWhile: 프레디케이트가 거짓이 되면 drop 을 중단

```java
List<Dish> slicedMenu2
	= specialMenu.stream()
			.dropWhile(dish -> dish.getCalories() < 320)
			.collect(toList());

```

#### 5.2.2 스트림 축소

limit(n): 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환

#### 5.2.3 요소 건너뛰기

skip(n): 처음 n개 요소를 제외한 스트림을 반환

### 5.3 매핑

특정 객체에서 특정 데이터를 선택하는 작업은 데이터 처리 과정에서 자주 수행되는 연산이다. 예를 들어 SQL의 테이블에서 특정 열만 선택할 수 있다. 스트림 API의 map과 flatMap 메서드는 특정 데이터를 선택하는 기능을 제공한다.

#### 5.3.1 스트림의 각 요소에 함수 적용하기

map: 인수로 제공된 함수는 각 요소에 적용되어 함수를 적용한 결과가 새로운 요소로 매핑된다.(매핑은 고친다기 보다는 변환에 가깝다고 볼 수 있다.)

```java
List<String> dishNames = menu.stream()
				.map(Dish::getName)
				.collect(toList());
```

#### 5.3.2 스트림 평면화

##### flatMap 사용

flatMap: 한 차원 낮은 결과값을 반환 ( 3차원->2차원 , 2차원 -> 1차원 )

```java
// 
List<String> uniqueCharacters = 
	words.stream()
		.map(word -> word.split("")) // 각 단어를 개별 문자를 포함하는 배열로 변환
		.flatMap(Arrays::stream) // 생성된 스트림을 하나의 스트림으로 평면화
		.distinct()
		.collect(toList());
```

### 5.4 검색과 매칭

특정 속성이 데이터 집합에 있는지 여부를 검색하는 데이터 처리도 자주 사용된다. 스트림 API는 allMatch, anyMatch, noneMatch, findFirst, findAny 등 다용한 유틸리티 메서드를 제공한다.

#### 5.4.1 (anyMatch) 프레디케이트가 적어도 한 요소와 일치하는지 확인

anyMatch: 프레디케이트가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인할 때 사용한다. (anyMatch 는 불리언을 반환하므로 최종 연산이다.)

아래 코드는 menu에 채식요리가 있는지 확인하는 예제다.

```java
if (menu.stream().anyMatch(Dish::isVegetarian)) {
	System.out.println("the menu is (somewhat) vegetarian friendly!!");
}
```

#### 5.4.2 프레디케이트가 모든 요소와 일치하는지 검사

allMatch: 스트림의 모든 요소가 주어진 프레디케이트와 일치하는지 검사

##### NONEMATCH

noneMatch: allMatch 와 반대 연산을 수행(주어진 프레디케이트와 일치하는 요소가 없으면 참을 반환)

> anyMatch, allMatch, noneMatch 세 메서드는 스트림 **쇼트서킷** 기법, 즉 자바의 &&, || 와 같은 연산을 활용한다.

#### 5.4.3 (findAny) 요소 검색

findAny: 현재 스트림에서 임의의 요소를 반환한다.

##### Optional 이란?

NULL 혹은 T 를 반환하는 메소드의 경우 에러가 일어나지 않도록 하기 위해 사용

- isPresent()는 Optional 이 값을 포함하면 참을 반환하고, 값을 포함하지 않으면 거짓을 반환
- ifPresent(Consumer\<T\>) 은 값이 있으면 주어진 블록을 실행한다.
- T get()은 값이 존재하면 값을 반환, 값이 없으면 NoSuchElementException 을 일으킨다.
- T orElse(T other)는 값이 있으면 값을 반환하고, 값이 없으면 기본값을 반환한다.

#### 5.4.4 (findFirst) 첫번째 요소 찾기

findFirst: 논리적인 아이템 순서가 스트림에 정해져 있을 경우 첫번째 요소를 반환

### 5.5 리듀싱

리듀싱 연산: 모든 스트림 요소를 처리해서 값으로 도출하는 질의

함수형 프로그래밍 언어 용어로는 이 과징이 마치 종이(스트림)를 작은 조각이 될 때 까지 반복해서 접는 것과 비슷하다는 의미로 **폴드(fold)** 라고 부른다.

#### 5.5.1 요소의 합

리듀싱 연산은 다음과 비슷하다.

```java
int sum = 0;
for (int x : numbers) {
	sum += x;
}
```

numbers의 각 요소는 결과에 반복적으로 더해진다. 리스트에서 하나의 숫자가 남을 때까지 redue 과정을 반복한다. 코드에는 파라미터를 두 개 사용했다.

- sum 변수의 초깃값 0
- 리스트의 모든 요소를 조합하는 연산(+)

위의 코드를 reduce 를 이용해서 애플리케이션의 반복된 패턴을 추상화할 수 있다.

```java
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
```

reduce 는 두 개의 인수를 갖는다.

- 초깃값 0
- 두 요소를 조합해서 새로운 값을 만드는 BinaryOperator\<T\>. 예제에서는 람다 표현식 `(a, b) -> a + b` 를 사용했다.

메서드 참조를 이용한 예시(더 간단함)

```java
int sum = numbers.stream().reduce(0, Integer::sum);
```

##### 초깃값 없음

```java
Optional<Integer> sum = numbers.stream().reduce((a, b) -> (a + b));
```

초깃값 없는 reduce 가 Optional 을 반환하는 이유: 스트림에 아무 요소도 없으면 초깃값이 없으므로 reduce 가 합계를 반환할 수 없기 때문에. 따라서 합계가 없음을 가리킬 수 있도록 Optional 객체로 감싼 결과를 반환한다.

### 스트림 연산 메서드 표

**설명:**

* **형식** : 중간 연산(Intermediate) 또는 최종 연산(Terminal)인지 표시
* **반환 형식** : 해당 메서드가 반환하는 값의 유형
* **사용된 함수형 인터페이스** : 해당 메서드에서 사용되는 함수형 인터페이스
* **함수 디스크립터** : 함수형 인터페이스의 메서드 시그니처

| 메서드        | 형식      | 반환 형식                 | 사용된 함수형 인터페이스   | 함수 디스크립터         |
| ------------- | --------- | ------------------------- | -------------------------- | ----------------------- |
| `filter`    | 중간 연산 | `Stream<T>`             | `Predicate<T>`           | `T -> boolean`        |
| `distinct`  | 중간 연산 | `Stream<T>`             | (없음)                     | (없음)                  |
| `takeWhile` | 중간 연산 | `Stream<T>`             | `Predicate<T>`           | `T -> boolean`        |
| `dropWhile` | 중간 연산 | `Stream<T>`             | `Predicate<T>`           | `T -> boolean`        |
| `skip`      | 중간 연산 | `Stream<T>`             | (없음)                     | (없음)                  |
| `limit`     | 중간 연산 | `Stream<T>`             | (없음)                     | (없음)                  |
| `map`       | 중간 연산 | `Stream<R>`             | `Function<T, R>`         | `T -> R`              |
| `flatMap`   | 중간 연산 | `Stream<R>`             | `Function<T, Stream<R>>` | `T -> Stream<R>`      |
| `sorted`    | 중간 연산 | `Stream<T>`             | `Comparator<T>`(옵션)    | `(T, T) -> int`(옵션) |
| `anyMatch`  | 최종 연산 | `boolean`               | `Predicate<T>`           | `T -> boolean`        |
| `noneMatch` | 최종 연산 | `boolean`               | `Predicate<T>`           | `T -> boolean`        |
| `allMatch`  | 최종 연산 | `boolean`               | `Predicate<T>`           | `T -> boolean`        |
| `findAny`   | 최종 연산 | `Optional<T>`           | (없음)                     | (없음)                  |
| `findFirst` | 최종 연산 | `Optional<T>`           | (없음)                     | (없음)                  |
| `forEach`   | 최종 연산 | `void`                  | `Consumer<T>`            | `T -> void`           |
| `collect`   | 최종 연산 | `R`                     | `Collector<T, A, R>`     | (복잡한 구조)           |
| `reduce`    | 최종 연산 | `Optional<T>`또는 `T` | `BinaryOperator<T>`      | `(T, T) -> T`         |
| `count`     | 최종 연산 | `long`                  | (없음)                     | (없음)                  |

---

### 5.6 ~ 5.8 공부하며 떠올린 질문들에 대한 답

- mapToInt() 같은 기본형 특화 스트림은 왜 박싱, 언박싱 비용을 줄이는가?
- joining() 이 뭐였지
- 숫자형 스트림을 사용하는 이유는?
- int max = maxCalories.orElse(1); // 값이 없을 때 기본 최댓값을 명시적으로 설정 여기서 최댓값이 없는 상황은 무엇을 말하는 거지? boxed() 와 mapToObj() 의 차이 (알 것 같기는 한데, 좀 더 정확히)
- Stream<double[]> pythagoreanTriples2 = Instream.rangeClosed(1 ....) 188 페이지 여기서 boxed() 를 했는데 flatMap 에서 lambda 식에서 IntStream 을 사용하는 이유는?
- IntSupplier 와 IntStream 의 차이는?
- 우리는 무한한 크기를 가진 스트림을 처리하고 있으므로 limit 를 이용해서 명시적으로 스트림의 크기를 제한해야 한다. 그렇지 않으면 최종 연산(예제에서는 forEach)을 수행했을 때 아무 결과도 계산되지 않는다. 마찬가지로 무한 스트림의 요소는 무한적으로 계산이 반복되므로 정렬하거나 리듀스 할 수 없다. <- 이게 무슨 말인지 모르겠다.

---

#### ❓ 1. `mapToInt()` 같은 기본형 특화 스트림은 왜 박싱, 언박싱 비용을 줄이는가?

**배경:**

기존 `Stream<Integer>`는 내부적으로 `Integer` 객체를 다루기 때문에  **기본형 → 객체 (박싱)** , **객체 → 기본형 (언박싱)** 과정이 자주 발생함.

```java
Stream<Integer> s = list.stream().map(x -> x + 1); // x는 Integer
```

➡️ 계산 시마다 `Integer` 객체를 꺼내서 int로 바꾸고, 다시 결과를 Integer로 포장해야 함

➡️ 박싱/언박싱 비용 + GC 부담

**해결책:**

`mapToInt()`를 쓰면 **int 전용 스트림 (IntStream)** 으로 변환되어, 박싱이 없는 기본형 값으로만 계산 가능.

```java
IntStream s = list.stream().mapToInt(x -> x); // x는 int
```

✔️ 더 빠르고 메모리 효율 좋음

---

#### ❓ 2. `joining()` 이 뭐였지?

`Collectors.joining()`은 스트림의 문자열을 **하나로 이어 붙이는 Collector**야.

```java
List<String> words = Arrays.asList("Java", "in", "Action");
String result = words.stream().collect(Collectors.joining(" "));
// 결과: "Java in Action"
```

옵션으로 접두사, 구분자, 접미사 지정도 가능:

```java
Collectors.joining(", ", "[", "]") // 결과 예: "[a, b, c]"
```

---

#### ❓ 3. 숫자형 스트림을 사용하는 이유는?

✔️  **성능** : 오토박싱 없이 기본형 값을 다루므로 빠름

✔️  **편의성** : `sum()`, `average()`, `range()` 같은 전용 연산이 제공됨

✔️  **특화된 API** : `IntStream`, `DoubleStream` 등에서만 제공되는 API들이 있음

---

#### ❓ 4. `int max = maxCalories.orElse(1);`

**최댓값이 없는 상황은 어떤 경우인가?**

`OptionalInt maxCalories = menu.stream().mapToInt(Dish::getCalories).max();`

처럼 `max()`를 쓰면, **빈 스트림이면 Optional.empty()가 반환됨**

> 즉, `menu`가 빈 리스트일 때는 `max()`도 결과가 없으므로 `OptionalInt.empty()`를 반환하고
>
> 이 경우 `orElse(1)`이 호출되어 기본값 1을 대체로 설정하게 되는 것

---

#### ❓ 5. `boxed()`와 `mapToObj()`의 차이점

| 구분           | 설명                                                                            |
| -------------- | ------------------------------------------------------------------------------- |
| `boxed()`    | 기본형 스트림을 객체 스트림으로 변환 (`IntStream`→`Stream<Integer>`)       |
| `mapToObj()` | 기본형 값을 받아서 다른 객체로 직접 매핑 (`IntStream`→`Stream<다른 타입>`) |

```java
IntStream.range(1, 5).boxed(); // Stream<Integer>
IntStream.range(1, 5).mapToObj(i -> "숫자: " + i); // Stream<String>
```

✔️ `boxed()`는 단순 포장, `mapToObj()`는 변환

---

#### ❓ 6. `Stream<double[]> pythagoreanTriples2 = …`

**flatMap에서 lambda 식 안에 IntStream을 쓰는 이유는?**

```java
IntStream.rangeClosed(1, 100).boxed()
    .flatMap(a -> 
        IntStream.rangeClosed(a, 100)
            .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
            .mapToObj(b -> new double[]{a, b, Math.sqrt(a*a + b*b)})
    )
```

➡️ 이유는 `flatMap(Function)` 안에서 *b* 값을 여러 개 만들고 싶기 때문

즉, `a = 3`일 때 가능한 `b = 4~100` 까지 다 돌려보고, 조건에 맞는 `b`만 골라서 `{a, b, c}`를 만들어야 하니까

**IntStream으로 여러 b를 만들고 → 조건을 걸고 → 객체로 바꾼 후** → `flatMap`으로 평면화

📌 `boxed()`는 `IntStream`을 `Stream<Integer>`로 바꿔서 `flatMap`이 동작하게 만든 것

---

#### ❓ 7. `IntSupplier`와 `IntStream`의 차이

| 항목 | IntSupplier                                | IntStream                 |
| ---- | ------------------------------------------ | ------------------------- |
| 역할 | int 값을 "하나" 생성하는 함수형 인터페이스 | int 값을 담은 "스트림"    |
| 예시 | `() -> 42`                               | `IntStream.of(1, 2, 3)` |

`IntSupplier`는 `getAsInt()` 메서드를 가진  *"값을 공급하는 람다"* ,

`IntStream.generate(IntSupplier)`와 같이 쓰면 **무한 스트림 생성**이 가능함.

```java
IntSupplier supplier = () -> (int)(Math.random() * 100);
IntStream.generate(supplier).limit(5).forEach(System.out::println);
```

---

#### ❓ 8. 무한 스트림은 왜 limit이 없으면 아무 계산도 되지 않는가?

스트림은 **게으른 평가(lazy evaluation)** 이다.

```java
Stream.iterate(0, n -> n + 2)
    .filter(n -> n % 4 == 0)
    .forEach(System.out::println);
```

이 코드는 무한 스트림이기 때문에 `forEach`까지 도달해야 연산이 시작됨.

그런데 `limit()`이 없다면 **중단 조건이 없어서 무한히 실행됨 → 프로그램이 끝나지 않음**

또한 `sorted()`나 `reduce()`는  **전 요소를 한 번에 알아야 하므로** ,

무한 스트림처럼 *끝이 없는 경우에는 절대로 끝나지 않음* → 그래서 사용 불가

✔️ 무한 스트림은 **`limit()`과 함께 써야 계산이 실제로 일어남**

---

필요하면 이걸 너 발표할 때 질문 리스트로 쓰기 좋게

✅ 각 질문에 대해 간단 요약 버전도 따로 정리해줄게.

그걸 노션/Obsidian에 옮겨도 되고, 발표용으로 쓸 수도 있어. 원할까?
