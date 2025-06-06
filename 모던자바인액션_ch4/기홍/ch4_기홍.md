**CHAPTER 4 스트림 소개**

이 장의 내용

- 스트림이란 무엇인가?
- 컬렉션과 스트림
- 내부 반복과 외부 반복
- 중간 연산과 최종 연산

대부분의 데이터베이스에서는 선언형으로 연산을 표현하고 이는 대부분의 비즈니스 로직의 연산에서 필요하다. 예를 들어 `SELECT name FROM dishes WHERE calorie < 400` 이라는 문장은 칼로리가 낮은 요리명을 선택하라는 SQL 질의다.

많은 요소를 포함하는 컬렉션은 어떻게 이를 처리해야 할까? 성능을 높이려면 멀티코어 아키텍처를 활용해서 병렬로 컬렉션의 요소를 처리해야 하지만 이를 구현하는 것은 단순 반복 처리 코드에 비해 복잡하고 어렵다.

이처럼 1. **요소를 병렬로 처리**하고 2. **선언형으로 연산을 표현**하기 위해 자바 언어 설계자들은 **스트림** 을 추가했다.

### 4.1 스트림이란 무엇인가?

스트림(Streams) 은 자바 8 API 에 새로 추가된 기능으로 선언형(즉, 데이터를 처리하는 임시 구현 코드 대신 질의로 표현할 수 있다)으로 컬렉션 데이터를 처리하고, 멀티스레드 코드를 구현하지 않아도 데이터를 [투명하게](./스트림의_투명성.md) 병렬로 처리할 수 있다.

기존의 코드(자바 7): (lowCaloricDishes 라는 중간 컨테이너 역할의 가비지 변수 사용)

```java
List<Dish> lowCaloricDishes = new ArrayList<>();
for (Dish dish : menu) { // 누적자로 요소 필터링
    if (dish.getCalories() < 400) {
        lowCaloricDishes.add(dish);
    }
}

Collections.sort(lowCaloricDishes, new Comparator<Dish>() { // 익명 클래스로 요리 정렬
    public int compare(Dish dish1, Dish dish2) {
        return Integer.compare(dish1.getCalories(), dish2.getCalories());
    }
});

List<String> lowCaloricDishesName = new ArrayList<>();
for (Dish dish : lowCaloricDishes) {
    lowCaloricDishesName.add(dish.getName()); // 정렬된 리스트를 처리하면서 요리 이름 선택
}
```

최신 코드(자바 8): (세부 구현은 라이브러리 내에서 모두 처리)

```java
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

List<String> lowCaloricDishesName = 
            menu.stream()
                .filter(d -> d.getCalories() < 400) // 400 칼로리 이하 요리 선택
                .sorted(comparing(Dish::getCalories)) // 칼로리로 요리 정렬
                .map(Dish::getName) // 요리명 추출(T -> R)
                .collect(toList()); // 모든 요리명을 리스트에 저장
```

`stream()` 을 `parallelStream()` 으로 바꾸면 이 코드를 멀티코어 아키텍처에서 병렬로 실행할 수 있다. <- 무슨 일이 일어나는지는 7장에서 설명

스트림이 소프트웨어공학적으로 제공하는 다음의 이득:

- 선언형으로 코드를 구현 (루프나 if 문 으로 어떻게 동작을 구현할지 지정할 필요 없이 '저칼로리의 요리만 선택하라' 같은 동작의 수행을 지정. 선언형 코드와 동작 파라미터화를 활용하는 변하는 요구사항에 쉽게 대응할 수 있다.)
- filter, sorted, map, collect 와 같은 여러 빌딩 블록 연산을 연결해 복잡한 데이터 처리 파이프라인을 만들수 있음. -> 여러 연산을 파이프라인으로 연결해도 여전히 가독성과 명확성이 유지된다.

### 4.2 스트림 시작하기

스트림이란 **데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소(Sequence of elements)** 로 정의할 수 있다. 이 정의를 하나씩 살펴보면

- 연속된 요소: 컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공한다. 컬렉션은 자료구조이므로 시간과 공간의 복잡성과 관련된 요소 저장 및 접근 연산이 주를 이룬다면, 스트림은 filter, sorted, map 처럼 표현 계산식이 주를 이룬다. 즉, 컬렉션의 주제는 데이터고 스트림의 주제는 계산이다.
- 소스: 스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다. 정렬된 컬렉션으로 스트림을 생성하면 정렬이 그대로 유지된다. 즉, 리스트로 스트림을 만들면 스트림의 요소는 리스트와 같은 순서를 유지한다.
- 데이터 처리 연산: 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다. ex) filter, map, reduce, find, match, sort 등. 스트림 연산은 순차적으로 또는 병렬로 실행할 수 있다.

스트림의 두가지 중요 특징

- 파이프라이닝(Pipelining): 대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환한다. 그 덕에 게으름(laziness), 쇼트서킷(short-circuiting) 같은 최적화도 얻을 수 있다. 연산 파이프라인은 데이터 소스에 적용하는 데이터베이스 질외와 비슷하다.
- 내부 반복: 반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다. 4.3.2 절에서 자세히 설명

위의 내용을 예제로 확인

```java
import static java.util.stream.Collectors.toList;

List<String> threeHighCaloricDishNames = 
            menu.stream() // 요리 리스트에서 스트림을 얻음
                .filter(dish -> dish.getCalories() > 300) // 파이프라인 연산 만들기. 첫 번째로 고칼로리 요리를 필터링한다.
                .map(Dish::getName) // 요리명 추출
                .limit(3) // 선착순 세 개만 선택
                .collect(toList()); // 결과를 다른 리스트로 저장

System.out.println(threeHighCaloricDishNames);
// 결과: [pork, beef, chicken]
```

이 코드는 자바 8 이전의 방식과 비교해서 '고칼로리 요리 3개를 찾아라' 를 좀 더 선언형으로 데이터를 처리할 수 있었다. 스트림 라이브러리에서 필터링(filter), 추출(map), 축소(limit) 기능을 제공하므로 직접 이 기능을 구현할 필요가 없다.

### 4.3 스트림과 컬렉션

이제 컬렉션 API와 스트림 API의 개념적인 차이를 확인하자.

둘 다 공통적으로 **연속된(sequenced)** 요소 형식의 값을 저장하는 자료구조의 인터페이스를 제공한다(아무 값에나 접근 하는게 아니라 순차적으로 값에 접근함).

둘 사이의 가장 큰 차이점은 데이터를 **언제** 계산하느냐다.

컬렉션은 현재 자료구조가 포함하는 **모든** 값을 메모리에 저장하는 자료구조이며, 컬렉션의 모든 요소는 컬렉션에 추가 하기 전에 계산되어야 한다.

반면 스트림은 이론적으로 **요청할 때만 요소를 계산** 하는 고정된 자료구조다.

![img](./streamAndCollection.png)

#### 4.3.1 딱 한 번만 탐색할 수 있다

탐색된 스트림의 요소는 **소비**된다. 한 번 탐색한 요소를 다시 탐색하려면 초기 데이터 소스에서 새로운 스트림을 만들어야 한다.

그러려면 컬렉션처럼 반복 사용할 수 있는 데이터 소수여야 한다. 만일 데이터 소스가 I/O 채널이라면 소스를 반복 사용할 수 없으므로 새로운 스트림을 만들 수 없다.

```java
List<String> title = Arrays.asList("JavaB", "In", "Action");
Stream<String> s = title.stream();
s.forEach(System.out::println); // 성공적으로 출력
// s.forEach(System.out::println); // java.lang.IllegalStateException: 스트림이 이미 소비되었거나 닫힘
```

스트림은 단 한 번만 소비할 수 있다는 점을 명시하자!

> 스트림과 컬렉션의 철학적 접근:
>
> 스트림은 시간적으로 흩어진 값의 집합으로 간주할 수 있다.
>
> 반면 컬렉션은 특정 시간에 모든 것이 존재하는 공간(컴퓨터 메모리)에 흩어진 값으로 비유할 수 있다.

#### 4.3.2 외부 반복과 내부 반복

컬렉션과 스트림의 또 다른 차이점은 데이터 반복 처리 방법이다.

**외부 반복(external iteration)** : 사용자가 for-each 등을 사용해서 직접 요소를 반복해야 한다.

**내부 반복(internal iteration)** : 함수에 어떤 작업을 수행할지만 지정하면 모든 것이 알아서 처리된다.

스트림 라이브러리는 내부 반복을 사용하며, 내부 반복은 반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해준다.

컬렉선 for-each 루프 외부 반복:

```java
List<String> names = new ArrayList<>();
for (Dish dish : menu) // 메뉴 리스트를 범위 기반 loop 로 명시적으로 순차 반복한다.
    names.add(dish.getName()); // 이름을 추출해서 리스트에 추가한다.
```

스트림의 내부 반복:

```java
List<String> names = menu.stream()
                    .map(Dish::getName) // map 메서드를 getName 메서드로 파라미터화해서 요리명을 추출
                    .collect(toList()); // 파이프라인을 실행한다. 반복자는 필요 없다.
```

내부 반복 뿐 아니라 자바 8에서 스트림을 제공하는 더 다양한 이유가 있다. 스트림 라이브러리의 내부 반복은 데이터 표현과 하드웨어를 활용한 **병렬성 구현을 자동으로 선택**한다. 반면 for-each 를 이용하는 외부 반복은 병렬성을 스스로 관리해야 한다.

### 4.4 스트림 연산

**중간 연산(intermediate operation)** : 연결할 수 있는 스트림 연산

**최종 연산(terminal operation)** : 스트림을 닫는 연산

```java
import java.util.stream.Stream;

List<String> names = menu.stream()
                         .filter(dish -> dish.getCalories() > 300)
                         .map(Dish::getName)
                         .limit(3)
                         .collect(toList());
```

- filter, map, limit 는 서로 연결되어 파이프라인을 형성 (중간 연산)
- collect 로 파이프라인을 실행한 다음에 닫음 (최종 연산)

#### 4.4.1 중간 연산

filter 나 sorted 같은 중간 연산은 다른 스트림을 반환한다. 따라서 여러 중간 연산을 연결해서 질의를 만들 수 있다.

중간 연산의 중요한 특징은 **단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다는 것**, 즉 **게으르다(lazy)** 는 것이다. 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한 번에 처리하기 때문이다.

```java
List<String> names = 
        menu.stream()
        .filter(dish -> {
            System.out.println("filtering:" + dish.getName());
            return dish.getCalories() > 300;
    })
    .map(dish -> {
        System.out.println("mapping:" + dish.getName());
        return dish.getName();
    })
    .limit(3)
    .collect(toList());
System.out.println(names);
```

실행 결과

```java
filtering:pork
mapping:pork
filtering:beef
mapping:beef
filtering:chicken
mapping:chicken
[pork, beef, chicken]
```

#### 4.4.2 최종 연산

최종 연산은 스트림 파이프라인에서 결과를 도출한다. 보통 최종 연산에 의해 List, Integer, void 등 스트림 이외의 결과가 반환된다.

예를 들어 다음 파이프라인에서 forEach 는 소스의 각 요리에 람다를 적용한 다음 void 를 반환하는 최종 연산이다.

`menu.stream().forEach(System.out::println);`

#### 4.4.3 스트림 이용하기

스트림 이용 과정은 다음과 같이 세 가지로 요약할 수 있다.

- 질의를 수행할 (컬렉션 같은 데이터 소스)
- 스트림 파이프라인을 구성할 중간 연산 연결
- 스트림 파이프라인을 실행하고 결과를 만들 최종 연산
