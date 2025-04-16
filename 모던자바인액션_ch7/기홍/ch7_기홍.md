# 7. 병렬 데이터 처리와 성능

자바 7 이전에는 데이터 컬렉션을 병렬로 처리하기가 어려웠다. 자바 7은 더 쉽게 병렬화를 수행하면서 에러를 최소화할 수 있도록 **포크/조인 프레임워크** 기능을 제공한다. 이 장에서는 스트림으로 데이터 컬렉션 관련 동작을 얼마나 쉽게 병렬로 실행할 수 있는지 설명한다.

우선 청크를 병렬로 처리하기 전에 병렬 스트림이 요소를 여러 청크로 분할하는 방법을 설명할 것이다.

## 7.1 병렬 스트림

병렬 스트림은 멀티코어 아키텍처를 효율적으로 사용하여 데이터를 빠르게 처리할 수 있게 한다. 하지만 항상 좋은 성능을 보장하는 건 아니다.

### 7.1.1 순차 스트림을 병렬 스트림으로 변환하기

* 기존 순차 스트림을 병렬 스트림으로 변환할 때는 `parallel()` 메서드를 사용한다.
* 병렬 스트림을 다시 순차 스트림으로 바꿀 때는 `sequential()` 메서드를 쓴다.
* `parallel()` 과 `sequential()` 은 플래그만 바꿀 뿐, 터미널 메서드(아래 예시에서는 `reduce()`) 직전에 호출된 메서드만 전체적으로 영향을 미친다.

```java
public long parallelSum(long n) {
	return Stream.iterate(1L, i -> i + 1)
			.limit(n)
			.parallel()
			.sequential()
			.parallel() // 위의 두 parralel() 과 sequential() 은 영항 X
			.reduce(0L, Long::sum);
}
```

### 7.1.2 스트림 성능 측정

소프트웨어 공학에서 추측은 위험한 방법이다. 특히 성능을 최적화할 때는 오로지 측정만을 믿어야 한다.

* 스트림 성능 측정 시 JMH(Java Microbenchmark Harness)를 이용한다. JMH를 이용하면 간단하고, 어노테이션 기반 방식을 지원하며, 안정적으로 자바 프로그램이나 JVM을 대상으로 하는 다른 언어용 벤치마크를 구현할 수 있다.
* 병렬 스트림이 항상 빠른 것은 아니며, 데이터의 크기와 처리 유형에 따라 다르다. 예를 들어 iterate 는 본질적으로 순차적이기 때문에 스트림을 병렬로 처리할 수 있도록 청크로 구분할 수 없다. 더 특화된 메소드를 사용하면 된다. (예를 들어 LongStream.rangeClosed 라는 메서드는 기본형 long 을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라지고, 쉽게 청크로 분할할 수 있는 숫자 범위를 생선한다. ex. 1 \~ 20 을 1-5, 6-10, 11-15, 16-20 범위의 숫자로 분할할 수 있다.)

### 7.1.3 병렬 스트림의 올바른 사용법

병렬 스트림 사용 시 고려할 사항:

* 데이터 레이스가 발생하지 않도록 해야한다.

### 7.1.4 병렬 스트림 효과적으로 사용하기

병렬 스트림의 효과적인 활용을 위해서는 다음을 확인해야 한다.

* 확신이 서지 않을 때는 직접 측정하라.
* 박싱의 성능저하에 주의하라 (`IntStream`, `LongStream`, `DoubleStream`) 등 기본형 특화 스트림을 사용하자.
* 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있다. (순서에 의존하는 연산)
* 스트림에서 수행하는 전체 파이프라인의 연산 비용을 고려하라. 처리해야 할 요소 수가 N이고 하나의 요소를 처리하는데 드는 비용을 Q라 하면 전체 스트림 파이프라인 처리 비용을 N\*Q 로 예상할 수 있다. Q가 높다면 병렬 스트림으로 성능을 개선할 수 있는 가능성이 있음을 의미한다.
* 소량의 데이터에서는 병렬 스트림이 도움 되지 않는다.
* 스트림을 구성하는 자료구조가 적절한지 확인하라. 예를 들어 ArrayList를 LinkedList 보다 효율적으로 분할할 수 있다.또한 range 팩토리 메서드로 만든 기본형 스트림도 쉽게 분해할 수 있는데, 커스텀 Spliterator 를 구현해서 분해 과정을 완벽하게 제어할 수 있다.
* 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다.
* 최종 연산의 병합 과정의 비요잉 비싸다면 병렬 스트림에서 얻은 성능의 이익이 서브스트림의 부분 결과를 합치는 과정에서 상쇄될 수 있다.

스트림 소스의 병렬화 친밀도 표

| 소스            | 분해성 |
| --------------- | ------ |
| ArrayList       | 훌륭함 |
| LinkedList      | 나쁨   |
| IntStream.range | 훌륭함 |
| Stream.iterate  | 나쁨   |
| HashSet         | 좋음   |
| TreeSet         | 좋음   |

## 7.2 포크/조인 프레임워크

포크/조인 프레임워크는 병렬 처리 작업을 작은 하위 작업으로 나누고 결과를 합치는 구조다.

### 7.2.1 RecursiveTask 활용

* 스레드 풀을 이용하려면 `RecursiveTask<R>`의 서브 클래스를 만들어야 한다. 여기서 R은 병렬화된 태스크가 생성하는 결과 형식 또는 결과가 없을 때는 `RecursiveAction` 형식이다.
  * `RecursiveAction` : 결과 필요 없음, `RecursiveTask` : 결과가 필요함
* `compute()` 메서드는 태스크를 서브태스크로 분할하는 로직과 더 이상 분할할 수 없을 때 개별 서브태스크의 결과를 생산할 알고리즘을 정의한다. compute 의 의사코드는 다음과 같다.

```java
if (태스크가 충분히 작거나 더 이상 분할할 수 없으면) {
	순차적으로 태스크 계산
} else {
	태스크를 두 서브태스크로 분할
	태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
	모든 서브태스크의 연산이 완료될 때 까지 기다림
	각 서브태스크의 결과를 합침
}
```

### 7.2.2 포크/조인 프레임워크를 제대로 사용하는 방법

* join 메서드를 두 서브태스크가 모두 시작된 다음에 호출하지 않으면 각각의 서브태스크가 다른 태스크가 끝나길 기다리는 일이 발생하며 원래 순차 알고리즘보다 느리고 복잡한 프로그램이 되어버릴 수 있다.
* RecursiveTask 내에서는 ForkJoinPool 의 invoke 메서드를 사용하지 말아야 한다. 대신 compute 나 fork 메서드를 직접 호출할 수 있다. 순차 코드에서 병렬 계산을 시작할 때만 invoke 를 사용한다.
  * Why? `invoke()` 는 새로운 병렬 작업을 "외부에서" 시작하는 용도인데, 이걸 `compute()` 안에서 쓰면 새 ForkJoinPool 을 또 시작하거나 공용 풀에서 블로킹이 생길 수 있다. 따라서 `invoke()` 대신 `compute(), fork(), join()` 을 써야한다. 이것들은 ForkJoinPool 내부의 흐름에 맞춰 최적화된 메서드들이기 때문에 스레드 낭비 없이 작업을 나누고 합칠 수 있다.
* 병렬 스트림처럼 멀티코어에 포크/조인 프레임워크를 사용하는 것이 순차 처리보다 무조건 빠를 생각은 버려야 한다. 병렬 처리로 성능을 개선하려면 태스크를 여러 독립적인 서브태스크로 분할할 수 있어야 한다.
  * I/O와 계산을 병렬로 실행할 수 있다.
  * 컴파일러 최적화는 병렬 버전보다는 순차 버전에 집중될 수 있다는 사실도 기억하자.(예를 들어 순차 버전에서는 죽은 코드를 분석해서 사용되지 않는 계산은 아예 삭제하는 등의 최적화를 달성하기 쉽다.)
    * 죽은 코드란? `int x = 1; int y = 2; int z = x + y;` 이 코드에서 `z` 는 사용되지 않기 때문에 컴파일러가 필요 없다고 판단하여 `int z = x + y;` 연산을 하지 않는다. 하지만 병렬 버전에서는 이 연산이 다른 스레드에서 쓸지 안쓸지 판단할 수 없기 때문에 최적화를 함부로 하지 못한다.

### 7.2.3 작업 훔치기

* 작업 훔치기(work-stealing)는 포크/조인 프레임워크에서 각 스레드가 맡은 작업이 끝났을 때 다른 스레드의 작업을 "훔쳐와서" 효율성을 높이는 방식이다.
* 작업 훔치기로 부하를 동적으로 분산한다.

## 7.3 Spliterator 인터페이스

Spliterator는 스트림의 요소를 병렬로 분할하기 위한 인터페이스다. Iterator 처럼 순차적으로 요소를 탐색 가능함과 동시에 분할할 수도 있다. (분할할 수 있는 반복자 splitable iterator)

```java
public interface Spliterator<T> {
	boolean tryAdvance(Consumer<? super T> action);
	Spliterator<T> trySplit();
	long estimateSize();
	int characteristics();
}
```

- `boolean tryAdvance(Consumer<? super T> action)` : 다음 요소가 있다면 그걸 소비자(Consumer)에 넘기고 true 반환, 없으면 false 반환. 이터레이터의 `hasNext()` + `next()` 를 합쳐놓은 느낌 -> 한 요소씩 순회할 때 사용됨
- `Spliterator<T> trySplit()` : 현재 Splitator 의 절반을 분할해서 새 Spliterator 반환. 리턴값은 잘려나간 절반, 나 자신은 남은 절반을 유지.

  ```java
  Spliterator<String> s1 = list.spliterator(); // s1 은 뒤쪽 절반
  Spliterator<String> s2 = s1.trySplit(); // s2 는 앞쪽 절반
  ```
- `long extimateSize()` : 남아 있는 요소 개수를 **대략적으로 추정** 해서 long 으로 반환. 정확하지 않아도 됨(그래서 estimate). 단, 성능 최적화에 중요한 메서드. 분할 전략을 결정할 때 기준이 되는 값(예: 너무 작으면 더 이상 분할하지 않도록).
- `int characteristics()` : 해당 spliterator 가 가진 특성(속성) 플래그들을 반환 (정수 비트 플래그)

| 특성           | 의미                   |
| -------------- | ---------------------- |
| `ORDERED`    | 요소 순서가 의미 있음  |
| `DISTINCT`   | 중복 없음              |
| `SORTED`     | 정렬되어 있음          |
| `SIZED`      | 정확한 크기 알 수 있음 |
| `SUBSIZED`   | 분할된 것도 SIZED      |
| `IMMUTABLE`  | 원본 변경되지 않음     |
| `CONCURRENT` | 병렬 접근 가능         |
| `NONNULL`    | null 요소 없음         |

```java
if ((spliterator.characteristics() & Spliterator.ORDERED) != 0) {
	// 순서 보장 됨
}
```

### 7.3.1 분할 과정

* `trySplit()`으로 스트림을 두 개로 나누고, 남은 스트림을 다시 분할한다.
* 분할된 스트림을 병렬로 처리한다.

### 7.3.2 커스텀 Spliterator 구현하기

* 자신만의 분할 전략을 구현할 수 있다.
* 커스텀 Spliterator는 병렬 성능을 크게 향상시킬 수 있다.

```java
public class WordCounterSpliterator implements Spliterator<Character> {
    private final String string;
    private int currentChar = 0;

    public WordCounter(String string) {
        this.string = string;
    }

    // 탐색 할 요소가 남아있다면 true 반환
    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
        action.accept(string.charAt(currentChar++)); // 현재 문자열을 소비
        return currentChar < string.length(); // 소비할 문자가 남아있으면 true 반환
    }

    // 요소를 분할해서 Spliterator 생성
    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = string.length() - currentChar;
        if (currentSize < 10) return null;

        // 파싱할 문자열의 중간을 분할 위치로 설정
        for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
            if (Character.isWhitespace(string.charAt(splitPos))) { // 공백 문자가 나올때
                // 문자열을 분할 해 Spliterator 생성
                Spliterator<Character> spliterator = new WordCounter(string.substring(currentChar, splitPos));
                // 시작을 분할 위치로 설정
                currentChar = splitPos;
                return spliterator;
            }
        }
        return null;
    }

    // 탐색해야 할 요소의 수
    @Override
    public long estimateSize() {
        return string.length() - currentChar;
    }

    // Spliterator 객체에 포함된 모든 특성값의 합을 반환
    @Override
    public int characteristics() {
        // ORDERED : 문자열의 순서가 유의미함
        // SIZED : estimatedSize 메서드의 반환값이 정확함
        // NONNULL : 문자열에는 null이 존재하지 않음
        // IMMUTABLE : 문자열 자체가 불변 클래스이므로 파싱하며 속성이 추가되지 않음
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }

}
```

## 7.4 마치며

* 내부 반복을 이용하면 명시적으로 다른 스레드를 사용하지 않고도 스트림을 병렬로 처리 할 수 있다.
* 간단하게 스트림을 병렬로 처리할 수 있지만 항상 병렬 처리가 빠른 것은 아니다. 병렬 소프트웨어 동작 방법과 성능은 직관적이지 않을 때가 많으므로 병렬 처리를 사용했을 때 성능을 직접 측정해봐야 한다.
* 병렬 스트림으로 데이터 집합을 병렬 실행할 때 특히 처리해야 할 데이터가 아주 많거나 각 요소를 처리하는 데 오랜 시간이 걸릴 때 성능을 높일 수 있다.
* 가능하면 기본형 특화 스트림을 사용하는 등 올바른 자료구조 선택이 어떤 연산을 병렬로 처리하는 것보다 성능적으로 더 큰 영향을 미칠 수 있다.
* 포크/조인 프레임워크에서는 병렬화할 수 있는 태스크를 작은 태스크로 분할한 다음에 분할된 태스크를 각각의 스레드로 실행하며 서브태스크 각각의 결과를 합쳐서 최종 결과를 생산한다.
* Spliterator 는 탐색하려는 데이터를 포함하는 스트림을 어떻게 병렬화할 것인지 정의한다.
