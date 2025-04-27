이번 장에서는 자바8과 자바9에 추가된 새로운 컬렉션 기능을 소개합니다.

- 작은 컬렉션을 쉽게 만드는 법 - 자바 9 컬렉션 팩토리 메서드
- 리스트와 집합의 요소를 간편하게 삭제하거나 교체하는 방법
- 맵과 관련해 추가된 새로운 편리 기능

## 컬렉션 팩토리

친구 이름 세 개를 담은 리스트를 만들어봅시다.
기존 방법은 이랬습니다:
```java
List<String> friends = new ArrayList<>();
friends.add("Raphael");
friends.add("Olivia");
friends.add("Thibaut");
```
**3개**의 문자열을 넣는 데 이렇게 긴 코드가 필요했습니다.

조금 나아진 방법도 있습니다:
```java
List<String> friends = Arrays.asList("Raphael", "Olivia", "Thibaut");
```
하지만 `Arrays.asList()`로 만든 리스트는 **고정 크기**라, 요소를 추가하거나 삭제하려 하면 `UnsupportedOperationException`이 발생합니다.

```java
friends.set(0, "Richard"); // OK
friends.add("Chih-Chun");  // 예외 발생
```

```
Exception in thread "main" java.lang.UnsupportedOperationException
	at java.base/java.util.AbstractList.add(AbstractList.java:155)
	at java.base/java.util.AbstractList.add(AbstractList.java:113)
	at collectionFactory.ListFactory.main(ListFactory.java:25)
```

또한, 집합(Set)은 `Arrays.asSet()` 같은 간단한 팩토리가 아예 없어서, 결국 이런 식으로 생성해야 했습니다:
```java
Set<String> friends = new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));

Set<String> friends = Stream.of("Raphael", "Olivia", "Thibaut")
                             .collect(Collectors.toSet());
```

자바 9에서는 `List.of()`, `Set.of()`, `Map.of()` 같은 팩토리 메서드가 추가되어, **코드를 훨씬 간결하게** 만들 수 있게 되었습니다.

### 리스트 팩토리 - `List.of()`

간단히 리스트를 만들 수 있습니다.
```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends); // [Raphael, Olivia, Thibaut]
```

하지만 `List.of()`로 만든 리스트는 변경이 불가합니다.
요소를 추가하거나 수정하려고 하면 예외가 발생합니다.
```java
friends.add("Chih-Chun"); // UnsupportedOperationException 발생
friends.set(0, "Richard"); // UnsupportedOperationException 발생
```

```
Exception in thread "main" java.lang.UnsupportedOperationException
```

이러한 불변성으로 의도치 않은 컬렉션 변경을 방지할 수 있습니다. 또한, `null`요소를 사용하지 않아, 잠재적인 버그를 막을 수 있습니다.

### 집합 팩토리 - `Set.of()`

집합도 비슷하게 간편하게 만들 수 있습니다.
```java
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");  
System.out.println("friends = " + friends);  
```

하지만, 중복 요소가 있다면, `IllegalArgumentException`이 발생합니다:
```java
Set<String> friends2 = Set.of("Raphael", "Olivia", "Olivia");  
System.out.println("friends2 = " + friends2);
```

```
Exception in thread "main" java.lang.IllegalArgumentException: duplicate element: Olivia
```
집합은 항상 고유한 값만 가져야 하기 때문입니다.

### 맵 팩토리 - `Map.of()`, `Map.ofEntries()`

맵은 key-value로 만들 수 있습니다.
```java
Map<String, Integer> ageOfFriends = Map.of(
	"Raphael", 23, 
	"Olivia", 24, 
	"Thibaut", 25
);  
System.out.println("ageOfFriends = " + ageOfFriends);
```

key-value 10개를 초고하면, `Map.ofEntries()`를 사용해야 합니다.
```java
Map<String, Integer> ageOfFriends2 = Map.ofEntries(
	entry("Raphael", 23), 
	entry("Olivia", 24), 
	entry("Thibaut", 25)
);  
System.out.println("ageOfFriends2 = " + ageOfFriends2);
```

`Map.entry()`는 간단하게 `Map.Entry` 객체를 만드는 팩토리 메서드입니다.

## 리스트와 집합 처리

### `removeIf`메서드

`removeIf`는 프레디케이트를 만족하는 요소를 컬렉션에서 삭제하는 메서드입니다.

```java
List<Transaction> transactions = new ArrayList<>();  
transactions.add(new Transaction("123ABC"));  
transactions.add(new Transaction("A456DEF"));  
transactions.add(new Transaction("789GHI")); 
  
for (Transaction transaction : transactions) {  
    if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {  
        transactions.remove(transaction);  
    }  
}  
  
System.out.println("transactions = " + transactions);
```

위와 같이 첫 글자가 숫자인 경우의 코드를 작성할 경우, `Exception in thread "main" java.util.ConcurrentModificationException`와 같은 예외를 발생시킵니다. 

내부적으로 for-each를 사용해 Iterator 객체를 통해 해석하려 했지만, 똑같은 예외가 발생합니다.
```java
for (Transaction transaction : transactions) {
    if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        transactions.remove(transaction); // 예외 발생
    }
}
```
이는 for - each문은 내부적으로 Iterator를 사용하는데, 컬렉션과 반복이 따로 놀게 되어 동기화 문제가 발생하기 때문입니다.

```java
for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
    Transaction transaction = iterator.next();
    if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        iterator.remove();
    }
}
```
Iterator 객체를 명시적으로 사용하고 그 객체의 remove() 메서드를 호출함으로 이 문제를 해결할 수 있습니다. 하지만, 이는 코드가 길기 때문에, `removeIf` 메서드로 바꾸어 작성할 수 있습니다.

```java
transactions.removeIf(transaction -> Character.isDigit(transaction.getReferenceCode().charAt(0)));  
System.out.println("transactions = " + transactions);
```

### `replaceAll` 메서드

`replaceAll` 은 리스트의 각 요소를 변경할 때 사용합니다. 기존 요소를 새로운 값으로 치환하는 기능입니다.

예를 들어 소문자로 시작하는 참조코드를 첫 글자만 대문자로 바꾸는 경우를 살펴보도록 합시다.
```java
List<String> referenceCodes = Arrays.asList("a12", "c14", "b13");  
  
List<String> updatedCodes = referenceCodes.stream()  
        .map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))  
        .collect(Collectors.toList());  
  
updatedCodes.forEach(System.out::println);
```

```result
A12
C14
B13
```
하지만 이 코드는 새 문자열 컬렉션을 만드는 것입니다. 기존 리스트를 직접 변경하고 싶으면 `ListIterator`객체를 이용할 수 있습니다.

```java
for (ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext(); ) {  
    String code = iterator.next();  
    iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));  
}  
  
referenceCodes.forEach(System.out::println);
```

```result
A12
C14
B13
```

기존 값이 다음과 같이 바뀐 것을 확인할 수 있습니다. 하지만 비교적 복잡하기에, `replaceAll`을 사용해 다음의 코드를 간결하게 표현할 수 있습니다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
```

## 맵 처리

`Map` 인터페이스에 다양한 디폴트 메서드가 추가되었습니다.

### `forEach`메서드

기존에는 맵을 반복하려면 `entrySet()`을 이용해 처리해야 했습니다.

```java
Map<String, Integer> ageOfFriends = Map.of("Raphael", 23, "Olivia", 24, "Thibaut", 25);  
  
for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {  
    String friend = entry.getKey();  
    Integer age = entry.getValue();  
    System.out.println(friend + " is " + age + " years old.");  
}
```

```result
Thibaut is 25 years old.
Raphael is 23 years old.
Olivia is 24 years old.
```

하지만 자바 8부터는 `forEach`를 사용해 훨씬 간결히 나타낼 수 있게 되었습니다.

```java
ageOfFriends.forEach((friend, age)  
        -> System.out.println(friend + " is " + age + " years old."));
```
key와 value를 인수로 받는 `BiConsumer`로 인수를 받아 한번에 다룰 수 있습니다.
### 정렬 메서드

맵의 key나 value를 기준으로 정렬할 수 있는 메서드가 추가되었습니다.
- `Entry.comparingByKey()`    
- `Entry.comparingByValue()`

사람의 이름을 알파벳 순으로 정렬한 후, 값을 출력해보도록 합시다.
```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),  
        entry("Cristina", "Matrix"),  
        entry("Olivia", "James Bond"));  
  
favouriteMovies.entrySet()  
        .stream()  
        .sorted(Entry.comparingByKey())  
        .forEachOrdered(System.out::println);
```

```java
Cristina=Matrix
Olivia=James Bond
Raphael=Star Wars
```

### `getOrDefault` 메서드

key가 없을 경우 기본값을 쉽게 반환할 수 있습니다. 기존에는 찾으려는 key가 없다면 `NullPointerException`이 발생했었습니다.


```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "StarWars")  
        , entry("Olivia", "James Bond"));  
  
System.out.println(favouriteMovies.getOrDefault("Olivia", "matrix")); 
System.out.println(favouriteMovies.getOrDefault("Thibaut", "matrix"));
```

```result
James Bond
matrix
```

key가 있으면, 존재하는 해당 값을, 없다면 두번 째 인수로 받은 기본값을 반환합니다.
### 계산 패턴

맵을 다룰 때 특정 키가 존재하는지에 따라 값을 계산하거나 갱신해야 할 때 유용한 메서들이 추가되었습니다.

- `computeIfAbsent`
- `computeIfPresent`
- `compute`

#### `computeIfAbsent`
키가 없거나 값이 null일 때만 새로운 값을 계산하고 추가합니다.

```java
Map<String, List<String>> friendsToMovies = new HashMap<>();  
friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())  
        .add("Star Wars");
System.out.println(friendsToMovies);  
  
friendsToMovies.computeIfAbsent("Rapheal", name -> new ArrayList<>())  
        .add("Star Wars");  
System.out.println(friendsToMovies);
```

```result
{Raphael=[Star Wars]}
{Raphael=[Star Wars], Rapheal=[Star Wars]}
```

#### `computeIfPresent`
key가 존재할 때만 새로운 값을 계산해 저장합니다. 현재 key와 관련된 값이 맵에 존재하며, null이 아닐 경우에만 새 값을 계산합니다.

#### `compute`

제공된 key로 새 값을 계산하고 맵에 저장합니다.

### 삭제 패턴

특정 key와 값이 모두 일치할 때만 항목을 삭제하는 `remove`메서드 뿐만 아닌, key가 특정한 값과 연관되었을 때만 항목을 제거하는 오버로드 버전 메서드를 제공합니다.

```java
if (favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {  
    favouriteMovies.remove(key);  
    return true;  
} else {  
    return false;  
}
```

```java
favouriteMovies.remove(key, value)
```

### 교체 패턴

맵의 값을 일괄적으로 변경하거나 특정 key의 값을 교체할 수 있는 기능이 추가되었습니다.

#### `replaceAll`

```java
Map<String , String> favouriteMovies = new HashMap<>();  
favouriteMovies.put("Raphael", "StarWars");  
favouriteMovies.put("Olivia", "James Bond");  
favouriteMovies.put("Thibaut", "Matrix");  
  
favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());  
System.out.println(favouriteMovies);
```

```result
{Olivia=JAMES BOND, Raphael=STARWARS, Thibaut=MATRIX}
```
위와 같이 소문자를 모든 대문자 값을 변환할 수 있습니다. 

#### `replace`
특정 key가 존재하면 맵의 값을 새 값으로 바꿀 수 있습니다.

### 합침

두 개의 맵을 합칠 때 키 충돌이 발생할 수도 있습니다. 이때, `merge`메서드를 이용해 충돌을 해결할 수 있습니다.

```java
Map<String, String> family = Map.ofEntries(  
        entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));  
  
Map<String, String> friends = Map.ofEntries(  
        entry("Raphael", "Star Wars")  
);  
  
Map<String, String> everyone = new HashMap<>(family);  
everyone.putAll(friends);  
System.out.println(everyone);

Map<String, String> family2 = Map.ofEntries(  
        entry("Teo", "Star Wars"), entry("Cristina", "James Bond")  
);  
Map<String, String> friends2 = Map.ofEntries(  
        entry("Raphael", "Star Wars"), entry("Cristina", "Matrix")  
);  
Map<String, String> everyone2 = new HashMap<>(family2);  
everyone2.putAll(friends2);  
System.out.println(everyone2);
```

```result
{Cristina=James Bond, Raphael=Star Wars, Teo=Star Wars}
{Cristina=Matrix, Raphael=Star Wars, Teo=Star Wars}
```

다음과 같이 cristina의 경우 다른 영화 값으로 존재하지만, 두 값이 모두 적용이 안된 걸 결과로 확인할 수 있습니다.

```java
Map<String, String> everyone3 = new HashMap<>(family2);  
friends2.forEach((k, y) ->  
        everyone3.merge(k, y, (movie1, movie2) -> movie1 + " & " + movie2));  
System.out.println(everyone3);
```

```result
{Raphael=Star Wars, Cristina=James Bond & Matrix, Teo=Star Wars}
```

```java
Map<String, Long> moviesToCount = new HashMap<>();  
String movieName = "JamesBond";  
Long count = moviesToCount.get(movieName);  
if (count == null) {  
    moviesToCount.put(movieName, 1L);  
} else {  
    moviesToCount.put(movieName, count + 1);  
}  
System.out.println(moviesToCount);  
  
moviesToCount.merge(movieName, 1L, (count1, increcement) -> count1 + 1L);  
System.out.println("moviesToCount = " + moviesToCount);
```

```result
{JamesBond=1}
moviesToCount = {JamesBond=2}
```

`merge`메서드는 `null`이면 새 값을 그대로 추가하고, 이미 값이 존재하면 함수 결과에 따라 업데이트 하거나 제거할 수 있습니다.

## 개선된 ConcurrentHashMap

`ConcurrentHashMap`은 동시성 친화적인 자료구조로, 여러 스레드가 동시에 추가하고 수정할 수 있도록 최적화된 `HashMap` 버전입니다.
### 리듀스와 검색

`ConcurrentHashMap`은 스트림 API와 비슷한 형태로 다음과 같은 세 가지 핵심 연산을 지원합니다:

- `forEach` : 모든 `(키, 값)` 쌍에 대해 특정 액션 실행    
- `reduce` : 모든 `(키, 값)` 쌍을 하나의 결과로 합침
- `search` : 조건에 맞는 값을 찾을 때까지 `(키, 값)` 쌍을 탐색

이 각각은 세 가지 타입으로 나눌 수 있습니다:

- 키 기반 연산 (`forEachKey`, `reduceKeys`, `searchKeys`)
- 값 기반 연산 (`forEachValue`, `reduceValues`, `searchValues`)
- 엔트리 기반 연산 (`forEachEntry`, `reduceEntries`, `searchEntries`)

> **주의**: `ConcurrentHashMap`은 연산 중에도 데이터가 변경될 수 있기 때문에, 함수는 변경 가능한 상태나 순서에 의존하지 않도록 작성해야 합니다.

이 연산은 병렬성 기준값을 지정해야 합니다.
- 기준값 =  1 : 최대한 병렬로 실행 (공통 스레드 풀 사용)
- 기준값 = `Long.MAX_VALUE` : 한 개의 스레드만 사용 (순차 실행)
- 기준값 설정 : 맵의 크기가 기준값보다 작으면 순차 실행, 크면 병렬 실행

```java
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
long parallelismThreshold = 1; // 병렬 처리 활성화

Optional<Long> maxValue = Optional.ofNullable(
    map.reduceValues(parallelismThreshold, Long::max)
);
```

이 경우는 값이 비어있기 때문에, empty로 출력됩니다.

```java
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();  
map.put("Raphael", 23L);  
map.put("Raphael1", 21L);  
map.put("Raphael2", 27L);  
map.put("Raphael3", 23L);  
long parallelismThreshold = 1;  
Optional<Long> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));  
System.out.println("maxValue = " + maxValue);

```

```result
maxValue = Optional[27]
```

값을 추가할 경우, 위에서 27이 제일 큰 값이기에, 27이 출력되는 걸 확인할 수 있습니다.

`reduceValuesToInt`, `reduceKeysToLong` 같은 메서드를 쓰면 **박싱(Boxing)** 없이 더 빠르게 처리할 수 있습니다.

### 계수

맵의 매핑 개수를 반환하는 `mappingCount`메서드가 제공됩니다.
`size()`메서드 보다 `long`을 반환하는 `mappingCount`가 더 좋습니다. 이는 size()는 결과가 int타입이라 최대 출력할 수 있는 숫자가 mappingCount가 더 크기 때문입니다.

### 집합뷰

key만 따로 모아 집합처럼 사용할 수 있습니다.

- `keySet()` : key들만 따로 모아 보여주는 메서드 
	- 얻은 set은 원본 맵과 연결되어 있음
	- Set을 수정하면 맵도 함께 바뀌고, 맵을 수정하면 자동으로 Set에도 반영됨
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();  
map.put("apple", 1);  
map.put("banana", 2);  
  
Set<String> keys = map.keySet();  
System.out.println(keys);  
  
keys.remove("apple");  
System.out.println(map);
```

```result
[banana, apple]
{banana=2}
```

- `newKeySet()` : Set처럼 작동하는 새로운 안전한 집합을 새로 만들고 싶을 때 사용
	- Map 없이 동작함 - Set만 있음
	- 여러 스레드가 동시에 추가, 삭제해도 안전함
```java
Set<String> concurrentSet = ConcurrentHashMap.newKeySet();  
  
concurrentSet.add("apple");  
concurrentSet.add("banana");  
  
System.out.println(concurrentSet);   
  
concurrentSet.remove("apple");  
System.out.println(concurrentSet);
```

```
[banana, apple]
[banana]
```