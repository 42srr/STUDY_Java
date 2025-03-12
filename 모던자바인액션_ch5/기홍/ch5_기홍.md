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
