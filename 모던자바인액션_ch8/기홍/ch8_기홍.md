# 8. 컬렉션 API 개선

> 이 장의 내용:
> - 컬렉션(리스트, 집합, 맵) 팩토리 사용하기
> - 리스트, 집합, 맵 의 요소를 다루는 메서드들

8장에서는 자바8, 자바 9에 추가된 새로운 Collection API 의 기능을 배운다.

1. 컬렉션 팩토리: 작은 리스트, 집합, 맵을 쉽게 만들어줌
2. 관용 패턴 적용 방법: 리스트와 집합에서 요소를 삭제하거나 바꿈

9장에서는 기존 형식의 자바 코드를 리팩터링하는 다양한 기법을 설명한다.

## 8.1 컬렉션 팩토리

> 주제: 컬렉션(리스트, 집합, 맵) 팩토리가 새로 필요했던 이유들

```java
List<String> friends = Arrays.asList("Raphael", "Olivia", Thibaut");
```

위 코드는 `고정 크기의 리스트` 를 만든다. 따라서 요소를 갱신할 순 있찌만 새 요소를 추가하거나 요소를 삭제할 순 없다. <- `UnsupportedOperationException` 발생

`Set` 의 경우 `Arrays.asSet()` 같은 팩토리 메서드가 없고 아래와 같은 코드로 선언 가능하지만 내부적으로 불필요한 객체 할당을 필요로 하고 `immutable` 을 원하더라도 변할 수 있는 집합으로만 만들 수 있다.

```java
Set<String> friends = new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));

Set<String> friends = Stream.of("Raphael", "Olivia", "Thibaut").collect(Collectors.toSet());
```

자바 9에서는 작은 리스트, 집합 뿐만 아니라 맵도 쉽게 만들 수 있도록 팩토리 메서드를 제공한다.

### 8.1.1 리스트 팩토리

> 주제: `리스트 팩토리` 라는 새로운 기능

```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends) // -> [Raphael, Olivia, Thibaut]
```

위 `List.of` 팩토리 메소드를 이용해서 만든 리스트는 다음 처럼 `add()`, `set()` 메서드로 수정이 불가능하다. (요소 자체를 바꾸는건 막을 수 없다.)

```java
friends.add("Chih-Chun"); // -> UnsupportedOperationException
```

데이터 처리 형식을 설정하거나 데이터를 변환할 필요가 없다면 사용하기 간편한 팩토리 메서드를 이용하기를 권장한다. 구현이 더 단순하고 목적을 달성하는데 충분하기 때문이다.

그렇지 않다면 7장에서 살펴본 것 처럼 `Collectors.toList()` 컬렉터로 스트림을 리스트로 변활할 수 있다.

### 8.1.2 집합 팩토리

> 주제: `집합 팩토리` 라는 새로운 기능

`List.of()` 와 비슷하게 `Set.of()` 로 바꿀 수 없는 집합을 만들 수 있다.

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends); // -> [Raphael, Olivia, Thibaut]
```

중복된 요소를 제공해 집합을 만드려고 하면 Olivia 요소가 중복되어 있다는 설명과 함께 `IllegalArgumentException` 이 발생한다.

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Olivia"); // -> IllegalArgumentException
```

### 8.1.3 맵 팩토리

> 주제: `맵 팩토리` 라는 새로운 기능

자바 9에서는 두 가지 방법으로 바꿀 수 없는 맵을 초기화할 수 있다.

1. `Map.of()` 팩토리 메서드

```java
Map<String, Integer> ageOfFriends
	= Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
System.out.println(ageOfFriends); // -> {Olivia=25, Raphael=30, Thibaut=26}
```

열 개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때는 위 `Map.of()` 메소드가 유용하다.

그렇지 않다면 아래의 팩토리 메서드를 이용하자.

2. `Map.ofEntries()` 팩토리 메서드

`Map.ofEntries()` 팩토리 메서드는 `Map.Entry<K, V>` 객체를 인수로 받으며 가변 인수로 구현되어 있다. 위의 `Map.of()` 메서드와 다른 점은 인수로 키와 값을 감쌀 추가 객체 할당을 필요로 한다는 점이다.

```java
import static java.util.Map.Entry;

Map<String, Integer> ageOfFriends = Map.ofEntries(
	entry("Raphael", 30),
	entry("Olivia", 25),
	entry("Thibaut", 26)
);

System.out.println(ageOfFriends); // -> {Olivia=25, Raphael=30, Thibaut=26}
```

## 8.2 리스트와 집합 처리

> 주제: 리스트와 집합에서 요소를 다루는 메서드들

자바 8 에서 다음과 같은 List 와 Set 에서 사용 가능한 메서드들이 추가됐다.

- removeIf : 프레디케이트를 만족하는 요소를 제거한다. `List 나 Set` 을 구현하거나 그 구현을 상속받은 모든 클래스에서 이용할 수 있다.
- replaceAll : `리스트에서` 이용할 수 있는 기능으로 UnaryOperator 함수를 이용해 요소를 바꾼다.
- sort : List 인터페이스에서 제공하는 기능으로 `리스트`를 정렬한다.

**이들 메서드는 호출한 컬렉션 자체를 바꾼다.** 새로운 결과를 만드는 스트림 동작과 달리 이들 메서드는 기존 컬렉션을 바꾼다.

### 8.2.1 removeIf 메서드

> 주제: List 와 Set 의 `CRUD` 메서드중 `Delete` 를 맡은 메서드인 `removeIf` 메서드에 대한 설명

아래 코드를 이해하기 전에 알아야 할 점: for-each 루프는 내부적으로 `Iterator` 객체를 사용한다. 따라서 for-each 문을 이용하면 `Iterator` 의 `next()`, `hasNext()` 와 리스트 `Collection` 의 메서드인 `remove()` 양쪽의 다른 방향에서 접근하기 때문에 `ConcurrentModificationException` 에러가 일어난다.

```java
for (Transaction transaction : transactions) { // <- 내부적으로는 Iterator<Transaction> it = transactions.iterator(); <- Iterator.next(), Iterator.hasNext() 를 이용해 질의
	if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
		transactions.remove(transaction);// <- 내부적으로는 Collection.remove() 메서드를 호출해 요소를 삭제
	}
}
```

결과적으로 반복자의 상태는 컬렉션의 상태와 서로 동기화되지 않는다.

`Iterator` 객체를 명시적으로 사용하고 그 객체의 `remove()` 메서드를 호출함으로써 이 문제를 해결할 수 있지만...

```java
for (Iterator<Transaction> it = transactions.iterator(); iterator.hasNext(); ) {
	Transaction tr = it.next();
	if (Character.isDigit(tr.getReferenceCode().charAt(0))) {
		it.remove();
	}
}
```

코드가 복잡해지는 단점이 나타난다.

이를 자바 8의 `removeIf()` 메서드로 바꿀 수 있다. 이러면 코드가 단순해지고 버그 예방이 가능하다. 이 메서드는 삭제할 요소를 가리키는 `Predicate` 를 인수로 받는다.

```java
tr.removeIf(tr -> Character.isDigit(tr.getReferenceCode().charAt(0)));
```

### 8.2.2 replaceAll 메서드

> 주제: List 와 Set 의 `CRUD` 메서드중 `Update` 를 맡은 메서드인 `replaceAll` 메서드에 대한 설명

`stream()` 을 사용한 뒤 스트림 API 를 사용하면 새 문자열 컬렉션을 만들기 때문에, 그리고 `listIterator()` 를 사용하면 위의 `tr.remove()` 예제처럼 코드가 복잡해지기 때문에 자바 8의 `replaceAll()` 메서드를 이용해 다음처럼 간단하게 구현할 수 있다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1)); // String list 의 모든 첫 문자를 대문자로 만드는 코드
```

## 8.2 맵 처리

> 주제: Map 에서 요소를 다루는 메서드들

자바 8에서 Map 인터페이스에 몇 가지 디폴트 메서드가 추가됐다.

> 디폴트 메서드: 기본적인 구현을 인터페이스에 제공하는 기능, 13장에서 자세히 다룸

### 8.3.1 forEach 메서드

`forEach` 는 `BiConsumer` (키와 값을 인수로 받음)를 인수도 받으므로 맵에서 키와 값을 더 간단하게 다룰 수 있다.

```java
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
```

### 8.3.2 정렬 메서드

1. `Entry.comparingByValue` : 값을 기반으로 정렬
2. `Entry.comparingByKey` : 키를 기반으로 정렬

```java
Map<String, String> favouriteMovies
	= Map.ofEntries(entry("Raphael", "Star Wars"),
	entry("Cristina", "Matrix"),
	entry("Olivia", "James Bond"));

favouriteMovies
	.entrySet()
	.stream()
	.sorted(Entry.comparingByKey()) // 키를 기반으로 정렬
	.forEachOrdered(System.out::println);
```

> `stream().forEach(System.out::println);` : 빠르지만 순서 뒤죽박죽 가능
> `stream().forEachOrdered(System.out::println);` : 순서 보장되지만 느릴 수 있음
>
> 위의 예제 코드에서서는 `sorted` 가 정렬이 되어 있기 때문에 `forEach()` 와 `forEachOrdered()` 의 결과는 거의 같을 거지만, **병렬 스트림**으로 바꾸는 순간 `forEach()` 는 순서가 뒤섞일 수 있고, `forEachOrdered()` 만이 정렬된 순서로 출력하게 된다.

### 8.3.3 getOrDefault 메서드

찾으려는 키가 존재하지 않으면 `null` 이 아니라 기본 값을 반환하는 메서드

`getOrDefault("First", "Default");` : 첫번째 인자가 아니면 두번째 인자로 받은 기본값을 반환

### 8.3.4 계산 패턴

맵에 키가 존재하는지 아닌지에 따라 특정 동작을 실행하는 메소드들. 예를 들어 키가 존재하지 않으면 캐시하는 등의 동작을 수행하는데 활용할 수 있다.

- `computeIfAbsent(key, mappingFunction)`: 해당 키에 값이 없을 경우 mappingFunction 을 적용해 나온 값을 맵에 저장하고 리턴, 이미 값이 있다면 아무 작업도 하지 않고 기존 값을 그대로 리턴
- `computeIfPresent(key, remappingFunction)`: key가 존재 하고, value 도 null 이 아닐 경우 remappingFunction 을 적용. 결과가 null이면 key 를 제거. null 이 아니면 해당 key에 새 값을 저장 후 리턴
- compute(key, remappingFunction): key가 있든 없든 remappingFunction 을 적용. 결과가 null이면 key 를 제거하고, null 이 아니면 key 에 그 값을 저장 후 리턴. (key 자체가 null 이면 NullPointerException 발생)

### 8.3.5 삭제 패턴

`favouriteMovies.remove(key, value);` : 맵에 키가 있으면 그 키를 지우고 true 를 반환, 없으면 false 를 반환.

### 8.3.6 교체 패턴

- replaceAll: BiFunction 을 적용한 결과로 각 항목의 값을 교체한다. 이 메서드는 이전에 살펴본 List의 replaceAll 과 비슷한 동작을 수행한다.
- replace: 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 있다.
	- 기존 버전: `replace(K key, V newValue)` key 가 존재하면 newValue 로 바꿈
	- 오버로드 버전: `replace(K key, V oldValue, V newValue)` key 가 oldValue 와 매핑되어 있을 때만 newValue 로 바꿈

### 8.3.7 합침

> map1.putAll(Map map2): map2 의 모든 항목을 map1 에 복사 <- 중복된 키가 없다면 위 코드는 잘 동작한다. 중복된 키가 있으면 map2 의 값을 map1에 덮어 쓴다.

merge: 중복된 키가 있으면 두 값을 연결

```java
map1.forEach((k, v) ->
	map2.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2));
// output: {Raphael=Star Wars, Cristina=James Bond & Matrix, Teo=Star Wars}
```

## 8.4 개선된 ConcurrentHashMap

> 주제: 동시성 친화적 기술을 반영한 HashMap 버전인 ConcurrentHashMap 을 소개

ConcurrentHashMap은 내부 자료구조의 특정 부분만 잠궈 동시 추가, 갱신 작업을 허용한다. 따라서 동기화된 Hashtable 버전에 비해 읽기 쓰기 연산 성능이 월등하다.(표준 HashMap 은 비동기로 동작함)

### 8.4.1 리듀스와 검색

스트림과 비슷한 종류의 세가지 새로운 연산:

1. forEach: 각 (키, 값) 쌍에 주어진 액션을 실행
2. reduce: 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
3. search: 널이 아닌 값을 반환할 때 까지 각 (키, 값) 쌍에 함수를 적용

함수 받기, 값, Mao, Entry, (키, 값), 인수를 이용한 네 가지 연산 형태를 지원한다.

1. 키, 값으로 연산(forEach, reduce, search)
2. 키로 연산(forEachKey, reduceKeys, searchKeys)
3. 값으로 연산(forEachValue, reduceValues, searchValues)
4. Map.Entry객체로 연산(forEachEntry, reduceEntries, searchEntries)

또한 이들 연산에는 병렬성 기준값(threshold)을 지정해야 한다.

### 8.4.2 계수

> 계수(計數): 숫자를 셈, 개수를 셈

ConcurrentHashMap 클래스는 맵의 매핑 개수를 반환하는 mappingCount 메서드를 제공한다.

### 8.4.3 집합뷰

ConcurrentHashMap 클래스는 ConcurrentHashMap 을 집합 뷰로 반환하는 keySet 이라는 새 메서드를 제공한다. 맵을 바꾸면 집합도 바뀌고 반대로 집합을 바꾸면 맵도 영향을 ㅂ다는다. newKeySet 이라는 새 메서드를 이용해 ConcurrentHashMap 으로 유지되는 집합을 만들 수도 있다.

## 8.5 마치며

- 자바 9은 적은 원소를 포함하며 바꿀 수 없는 리스트, 집합, 맵을 쉽게 만들 수 있도록 List.of, Set.of, Map.of, Map.ofEntries 등의 컬렉션 팩토리를 지원한다.
- 이들 컬렉션 팩토리가 반환한 객체는 만들어진 다음 바꿀 수 없다.
- List 인터페이스는 removeIf, replaceAll, sort 세 가지 디폴트 메서드를 지원한다.
- Set 인터페이스는 removeIf 디폴트 메서드를 지원한다.
- Map 인터페이스는 자주 사용하는 패턴과 버그를 방지할 수 있도록 다양한 디폴트 메서드를 지원한다.
- ConcurrentHahMap은 Map에서 상속받은 새 디폴트 메서드를 지원함과 동시에 스레드 안전성도 제공한다.
