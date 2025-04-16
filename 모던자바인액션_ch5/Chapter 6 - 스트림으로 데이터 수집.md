# 목표
- Collectors 클래스로 컬렉션을 만들고 사용하기
	- 다양한 요소의 누적 방식
	- Collection, Collector, collect 용어 구분 주의
- 하나의 값으로 데이터 스트림 리듀스하기
- 특별한 리듀싱 요약 연산
- 데이터 그룹화와 분할
- 자신만의 커스텀 컬렉터 개발

collect vs Collector
- 통화별로 트랜잭션을 그룹화한 다음에 해당 통화로 일어난 모든 트랜잭션 합계 계산
	- `Map<Currency, Integer>`
- 트랜잭션을 비싼 트랜잭션과 저렴한 트랜잭션 두 그룹으로 분류
	- `Map<Boolean, List<Transaction>>`
- 트랜잭션을 도시 등 다수준으로 그룹화, 각 트랜잭션이 비싼지 저렴한지 구분
	- `Map<String, Map<Boolean, List<Transaction>>>`

## 6.1 Collector란 무엇인가?
- Collector 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할지 결저앟ㄴ다.

5장에서는 toList를 Collector 인터페이스의 구현으로 사용했다.

### 6.1.1 고급 리듀싱 기능을 수행하는 컬렉터
collect로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 것이 Collector의 최대 강점.

스트림에 collect를 호출하면 스트림의 요소에(컬렉터로 파라미터화된) 리듀싱 연산이 수행된다.

collect에서는 리듀싱 연산을 이용해서 스트림의 각 요소를 방문하면서 컬렉터가 작업을 처리한다.
![[모던자바인액션_6_리듀싱연산|5000]]

1. 그냥 스트림 내부 반복일 거고
2. 변환 함수는 중간연산일 거고
3. 3번이 collect?

### 6.1.2 미리 정의된 컬렉터
Collectors 클래스에서 제공하는 팩토리 메서드의 기능
- 스트림 요소를 하나의 값으로 리듀스하고 요약
	- 트랜잭션 리스트에서 트랜잭션 총합
- 요소 그룹화
	- multilevel로 그룹화, 각각의 결과 서브 그룹에 추가로 리듀싱 연산 적용
- 요소 분할
	- 그룹화의 특별한 연산인 partitioning

## 6.2 리듀싱과 요약
컬렉터 (Stream.collect 메서드의 인수)로 스트림의 항목을 컬렉션으로 재구성할 수 있다.
즉, 스트림의 모든 항목 -> 하나의 결과(컬렉터)

### counting() 팩토리 메서드
```java
long howManyDishes = menu.stream().collect(Collectors.counting());

long howManyDishes = menu.stream().count();

Collectors.counting() -> count();
```

### 6.2.1 스트림 값에서 최댓값과 최솟값 검색
최댓값 : `Collectors.maxBy`
최솟값 : `Collectors.minBy`

```java
Comparator<Dish> dishCaloriesComparator = 
	Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCalorieDish = 
	menu.stream()
		.collect(maxBy(dishCaloriesComaparator));
```

애는 왜 위에처럼
.stream().maxBy(dishCaloriesComparator)로 안하는거지??

`Optional<Dish>`의 역할
- 만약 포함하거나 포함하지 않을 수 있는 컨테이너

### 6.2.2 요약 연산
`Collectors.summingInt` : 객체를 int로 매핑하는 함수를 인수로 받음
`Collectors.summingLong`
`Collectors.summingDouble`
`Collectors.averagingInt` 
`Collectors.averagingLong`
`Collectors.averagingDouble`

```java
int totalCalories = 
	menu.stream().collect(summingInt(Dish::getCaloreis));
```

```java
IntSummaryStatistics menu = 
	menu.stream().collect(summarizingInt(Dish::getCalories));
```

### 6.2.3 문자열 연결
Collector의 `joining` 팩토리 메서드 사용
내부적으로는 StringBuilder 사용

```java
String shortMenu = 
	menu.stream().map(Dish::getName).collect(joining());

String shortMenu = 
	menu.stream().map(Dish::getName).collect(joining(", "));
```

### 6.2.4 범용 리듀싱 요약 연산
지금까지 살펴본 모든 컬렉터는 reducing 팩토리 메서드로 정의할 수 있다.

메뉴의 모든 칼로리 합계 
```java
int totalCaloreis = 
	menu.stream().collect(reducing(
					0, Dish::getCalories, (i, j) -> i + j));
```

reducing의 인자
1. 리듀싱 연산의 시작 값, 스트림에 인수가 없을 때는 반환 값
2. 정수로 변환할 함수
3. 액션

그 뒤의 내용 이해안감.
## 6.3 그룹화
명령형으로 그룹화를 구현하려면 까다롭지만, 자바 8의 함수형을 이용하면 가독성 있고 간결하게 그룹화를 구현할 수 있다.

```java
Map<Dish.Type, List<Dish>> dishesByType = 
	menu.stream().collect(groupingBy(Dish::getType));

{FISH=[prawns, salmon], OTHER=[french fries, rice]}
```

모든 요리를 추출하는 함수를 groupingBy 메서드로 전달했다.
이 함수를 기준으로 스트림이 그룹화되므로 이를 **분류 함수**(classification function)이라고 한다.
![[모던자바인액션6_그룹화|5000]]
- 단순한 접근자 대신 분류 기준이 필요한 상황에서는 메서드 찹조를 분류 함수로 사용할 수 없다.
```java
public enum CaloricLevle {DIET, NORMAL, FAT}

MAP<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
	menu.stream().collect(
		gourpingBy(dish -> {
			if (dish.getCalories() <= 400) 
				return CaloricLevel.DIET;
			else if (dish.getCalories() <= 700) 
				return CaloricLevel.NORMAL;
			else 
				return CaloricLevel.FAT;
		})
	);
```
### 6.3.1 그룹화된 요소 조작
요소를 그룹화 한 다음에는 각 결과 그룹의 요소를 조작하는 연산이 필요하다.

- 프레디케이트로 필터를 적용
```java
Map<Dish.Type, List<Dish>> caloricDishesByType =
	menu.stream().filter(dish->dish.getCalories() > 500)
				 .collect(groupingBy(Dish::getType));
```

단점 : 맵에 코드를 적용해야 함
`{OTHER=[french fries, pizza], MEAT=[pork, beef]}`

우리의 필터 프레디케이트를 만족하는 FISH 종류 요리는 없으므로 결과 맵에서 해당 키 자체가 사라진다.
Collectors 클래스는 일반적인 분류 함수에 Collector 형식의 두 번째 인수를 갖도록 groupingBy 팩토리 메서드를 오버로드해 이 문제를 해결한다.

하단 코드처럼 두 번째 Collector안으로 필터 프레디케이트를 이동함으로 이 문제를 해결할 수 있다.
```java
Map<Dish.Type, List<Dish>> caloricDishesByType =
	menu.stream()
		.collect(groupingBy(Dish::getType,
				filtering(dish -> dish.getCalories() > 500, toList())));
```

filtering 메소드는 Collectors 클래스의 또 다른 정적 팩토리 메서드로 프레디케이트를 인수로 받는다.
즉 FISH의 빈공간이 할당된다.

- 매핑 함수를 이용해 요소를 변환하는 작업
filtering 컬렉터와 같은 이유로 Collectors 클래스는 매핑 함수와 각 항목에 적용한 함수를 모으는데 사용하는 또 다른 컬렉터를 인수로 받는 mapping 메서드를 제공한다.

쓰게되면 찾아보자.
### 6.3.2 다수준 그룹화

### 6.3.3 서브 그룹으로 데이터 수집

## 6.4 분할
### 6.4.1 분할의 장점
### 6.4.2 숫자를 소수와 비소수로 분할하기
## 6.5 Collector 인터페이스
### 6.5.1 Collector 인터페이스의 메서드 살펴보기
### 6.5.2 응용하기
## 6.6 커스텀 컬렉터를 구현해서 성능 개선하기
### 6.6.1 소수로만 나누기
### 6.6.2 컬렉터 성능 비교
## 6.7 마치며
