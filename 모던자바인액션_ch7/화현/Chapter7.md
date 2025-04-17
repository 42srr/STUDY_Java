Java 스트림은 `parallel()` 메서드 한 줄만으로도 손쉽게 병렬 처리를 구현할 수 있게 도와줍니다. 하지만 겉보기와 달리, 그 내부에서는 생각보다 복잡한 과정이 이뤄지고 있습니다.

이번 글에서는 **병렬 스트림이 데이터를 어떻게 나누고 처리하는지**, 그 작동 원리를 하나씩 짚어보려고 합니다. 특히, 스트림이 **데이터를 여러 청크로 분할하는 방식**을 이해한 뒤, 직접 **커스텀 `Spliterator`를 구현**해보며 분할 과정을 제어하는 방법까지 함께 알아봅니다.

## 병렬 스트림

병렬 스트림은 스트림 요소들을 여러 개의 청크로 나누고, 이를 여러 스레드에서 동시에 처리하게 하는 스트림입니다.

### 순차 스트림을 병렬 스트림으로 변환하기
예를 들어, `n = 10_000_000`일 때, 1부터 n까지의 숫자를 모두 더하는 방법을 세 가지로 비교해 보겠습니다.

1. 반복문으로 더하기
```java
public long iterativeSum(long n) {
    long result = 0;
    for (long i = 1; i <= n; i++) {
        result += i;
    }
    return result;
}
```

2. 순차 스트림으로 더하기
```java
public long sequentialSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
                 .limit(n)
                 .reduce(0L, Long::sum);
}
```
- `Stream.iterate`로 무한 자연수 스트림 생성
- `limit(n)`으로 갯수 제한
- `reduce`로 누적 합산

3. 병렬 스트림으로 더하기
```java
public long parallelSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
                 .limit(n)
                 .parallel()
                 .reduce(0L, Long::sum);
}
```
- `parallerl()` 추가하여 내부적으로 병렬처리해줌

### 스트림 성능 측정

앞선 세 개의 코드를 JMH(Java Microbenchmark Harness)를 사용해 성능을 비교해 봅시다.
`(Maven빌드로 사용)`
JMH란 OpenJDK에서 제공하는 마이크로 벤치마크 프레임워크이며, JVM최적화를 고려해 정확한 측정이 가능합니다. 또한, 어노테이션 기반으로 쉽게 코드를 작성할 수 있습니다.

`pom.xml`에 다음과 같은 의존성과 플러그인을 추가하였습니다.
```xml
<dependencies>
  <dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.17.4</version>
  </dependency>
  <dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.17.4</version>
  </dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <executions>
        <execution>
          <phase>package</phase>
          <goals><goal>shade</goal></goals>
          <configuration>
            <finalName>benchmarks</finalName>
            <transformers>
              <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <mainClass>org.openjdk.jmh.Main</mainClass>
              </transformer>
            </transformers>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

`ParallelStreamBenchmark` Class를 생성하여 성능 비교할 수 있는 코드를 살펴봅시다.
> 테스트 방법
> `mvn clean package`
> `java -jar target/benchmarks.jar 클래스명`
```java
@BenchmarkMode(Mode.AverageTime)  
@OutputTimeUnit(TimeUnit.NANOSECONDS)  
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})  
@State(Scope.Thread)  
public class ParallelStreamBenchmark {  
    private static final long N = 10_000_000L;  
  
    @Benchmark //   
    public long iterativeSum() {  
        long result = 0;  
        for (long i = 1L; i <= N; i++) {  
            result += i;  
        }  
        return result;  
    }  
  
    @Benchmark  
    public long sequentialSum() {  
        return Stream.iterate(1L, i -> i + 1)  
                .limit(N)  
                .reduce(0L, Long::sum);  
    }  
  
    @Benchmark  
    public long parallelSum() {  
        return Stream.iterate(1L, i -> i + 1)  
                .limit(N)  
                .parallel()  
                .reduce(0L, Long::sum);  
    }  
  
    @Benchmark  
    public long rangedSum() {  
        return LongStream.rangeClosed(1, N)  
                .reduce(0L, Long::sum);  
    }  
  
    @Benchmark  
    public long parallelRangedSum() {  
        return LongStream.rangeClosed(1, N)  
                .parallel()  
                .reduce(0L, Long::sum);  
    }  
	// 벤치마크가 끝날 때마다, GC가 실행되도록 하기 위한 메서드
    @TearDown(Level.Invocation)  
    public void tearDown() {  
        System.gc();  
    }   
}
```

```result
결과 :
ParallelStreamBenchmark.iterativeSum       avgt  200   3377341.960 ±    3451.363  ns/op
ParallelStreamBenchmark.parallelRangedSum  avgt  200    665143.050 ±    3030.207  ns/op
ParallelStreamBenchmark.parallelSum        avgt  200  63415187.606 ± 1127846.734  ns/op
ParallelStreamBenchmark.rangedSum          avgt  200   3476418.696 ±    3394.612  ns/op
ParallelStreamBenchmark.sequentialSum      avgt  200  56796079.555 ±  368493.533  ns/op
```

- `iterativeSum` 
	- 기본형(`long`)을 사용하기에, 박싱/언박싱이 일어나지 않음
	- 오히려 성능면에서 우수함
- `sequentialSum`
	- `Stream.iterate`는 앞의 값을 기반으로 다음 값을 생성함
	- `Long` 객체를 사용하므로 덧셈 연산 시 박싱/언박싱 비용이 발생
	- 순차적으로 동작하므로 병렬성은 없음
- `parallelSum`
	- 병렬 스트림을 이용했지만, 기대만큼의 성능 향상은 없음
	- `Stream.iterate(1, i -> i + 1)`과 같이 앞 결과에 따라 다음 값이 정해지는 형태는 처음 값도 알아야 다음 값도 만들 수 있고, 다음 값도 그 다음 값을 만드는데 필요하기 때문에, 데이터를 미리 나눌 수 없음. 즉, n개의 데이터를 순차적으로 계산해서 만든 다음 병렬 처리하는데, 계산하는 과정 자체가 순차적이라 병렬화의 이득을 못봄. 
	- `Stream.iterate()`는 기본형이 아니라, 객체로 처리가 되어 박싱/언박싱 비용 발생
- `rangedSum` 
    - `LongStream`은 기본형 `long`기반으로 작동되기에 박싱/언박싱 비용 없음
    - `Spliterator`의 `trySplit()`을 통해 1~n사이의 값을 절반으로 나누고 이 과정은 재귀적으로 이루어짐. 각 스레드가 작업을 한 후, `reduce()`를 통해 병합함
  
- `parallelRangeSum` 
    - `rangeClosed()`는 스레드가 나누어진 채 작업이 진행되기에, 병렬 처리가 가능함.
    - 하지만 작업량이 작거나 데이터 크기가 작을 경우에는 병렬화가 느려질 수도 있음

> **용어 정리**
> 박싱 : 기본형 값을 객체로 감싸는 것 ex) int -> Integer
> 언박싱 : 객체에서 기본형 값을 꺼내는 것 ex) Integer -> int

### 병렬 스트림의 올바른 사용법

병렬 스트림을 잘못 사용하였을 경우, 성능이 떨어지고, 결과도 틀려질 수 있습니다. 예시를 통해 살펴보도록 합시다.
1부터 n까지의 자연수를 더하는 코드를 살펴봅시다. 병렬 처리를 하지 않은 올바른 값과 비교했을 때, 병렬 처리를 하여 계산한 결과 값은 매번 틀리다는 것을 확인할 수 있습니다.
```java
public class SideEffect {  
  
	public static class Accumulator {  
	    public long total;  
	  
	    public void add(long value) {  
	        total += value;  
	    }  
	}
    public static void main(String[] args) {  
        long num = 10_000_000L;  
        long sideEffectSum = sideEffectSum(num);  
        long sideEffectParallelSum = sideEffectParallelSum(num);  
        System.out.println("sideEffectSum = " + sideEffectSum);  
        System.out.println("sideEffectParallelSum = " + sideEffectParallelSum);  
    }  
  
    public static long sideEffectSum(long n) {  
        Accumulator accumulator = new Accumulator();  
        LongStream.rangeClosed(1, n).forEach(accumulator::add);  
  
        return accumulator.total;  
    }  
  
    public static long sideEffectParallelSum(long n) {  
        Accumulator accumulator = new Accumulator();  
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);  
  
        return accumulator.total;  
    }  
}
```

```java
public class ParallelStreamsHarness {  
  
    public static void main(String[] args) {  
        System.out.println("ParallelStreams parallel sum done in: " +  
                measurePerf(ParallelStreams::sideEffectParallelSum, 10_000_000L) + " msecs");  
    }  
  
    public static long measurePerf(LongUnaryOperator f, long n) {  
        long fastest = Long.MAX_VALUE;  
        for (int i = 0; i < 10; i++) {  
            long start = System.currentTimeMillis();  
            long result = f.applyAsLong(n);  
            long duration = System.currentTimeMillis() - start;  
            System.out.println("Result : " + result);  
            if (duration < fastest) {  
                fastest = duration;  
            }  
        }  
        return fastest;  
    }  
}
```

```result
결과 - 첫 시도
sideEffectSum = 50000005000000
sideEffectParallelSum = 8668421376610

결과 - 두번째 시도
sideEffectSum = 50000005000000
sideEffectParallelSum = 9186093524631

결과 - 세번째 시도
sideEffectSum = 50000005000000
sideEffectParallelSum = 16856490416980

harness 결과
Result : 23057697910438
Result : 6396484843750
Result : 13746561445283
Result : 1683330246769
Result : 17808700401277
Result : 8598433056848
Result : 4576139519369
Result : 11356070118985
Result : 8329211996467
Result : 2850978486113
ParallelStreams parallel sum done in: 1 msecs
```

여기서 문제는 `Accumulator`클래스의 `total` 필드에서 나타납니다. 병렬 스트림이 여러 스레드에서 동시에 `add()`메서드를 호출하게 되는데, 이는 `atomic연산`이 아닙니다. 즉, 병렬화라는 특성이 없어집니다. `total += value`는 사실상 현재 total값 읽기 -> value 더한 후 다시 저장의 단계로 진행됩니다. 동시에 여러 스레드가 실행되면 값이 꼬이고, 결과가 올바르지 않을 수 밖에 없습니다.

### 병렬 스트림 효과적으로 사용하기

- 벤치마크를 통해 성능 비교 해보기
- `List<Integer>`같은 참조형 데이터를 스트림에서 처리하면 박싱 언박싱 비용이 발생하기에, `IntStream`, `LongStream`, `DoubleStream`과 같은 기본형 특화스트림 사용하기
- `findFirst` , `limit()`과 같은 순서를 보장해야 하는 연산은 병렬에 불리함
- 작업 단위가 클수록 병렬화에 유리함
- `ArrayList`, `Intstream.range()`는 병렬에 유리함
- `filter()`나 `map()`등의 중간 연산이 많으면 병렬에 불리함
- `Collector`나 `combiner()`와 같은 최종연산(부분 결과 모으는 과정)은 병렬에 불리함

## 포크/조인 프레임워크

포크/조인 프레임워크는 큰 작업을 작은 단위로 나누고(Fork), 결과를 합쳐서(Join) 하나의 결과를 만들어내는 병렬 처리기법입니다.

### Recursive Task 활용

포크/조인 프레임워크를 활용하려면 `RecursiveTask<R>` 클래스를 상속받아 태스크를 구현해야 합니다. 여기서 `R`은 반환 타입을 의미합니다. 
`RecursiveTask`를 정의하려면 추상메서드 `compute()`를 구현해야 합니다.
이 메서드 안에는,
- 작업을 나눌지 여부를 판단
- 나눈 작업을 재귀적으로 처리
- 결과를 다시 합치는(Join) 로직

1부터 n까지의 숫자를 모두 더하는 병렬 처리를 포크/조인 방식으로 구현한 예제를 통해 살펴보도록 합시다.

```java
public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long> {  
/*[ 필드 설명 ]
- `numbers`: 합산할 숫자 배열
- `start`, `end`: 배열에서 현재 이 태스크가 담당할 범위
- `THRESHOLD`: 이 값 이하로 작아지면 더 이상 태스크를 나누지 않고 직접 계산 (즉, 순차 처리)
*/
    private final long[] numbers;  
    private final int start;  
    private final int end;  
    private static final long THRESHOLD = 10_000; 
  
    public ForkJoinSumCalculator(long[] numbers) {  
        this(numbers, 0, numbers.length);  
    }  
  
    private ForkJoinSumCalculator(long[] numbers, int start, int end) {  
        this.numbers = numbers;  
        this.start = start;  
        this.end = end;  
    }  
	  public static long forkJoinSum(long n) {  
        long[] numbers = LongStream.rangeClosed(1, n).toArray();  
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);  
        return new ForkJoinPool().invoke(task);  
    }  
    @Override  
    protected Long compute() {  
        int length = end - start;  
        if (length <= THRESHOLD) {  
            return computeSequentially();  // 순차 처리
        }  
        // 배열을 반으로 나누어 두 개의 서브 태스크 생성
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);  
        // 왼쪽 태스크는 다른 스레드에서 비동기적 실행
        leftTask.fork();  
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);  
        // 오른쪽 태스크는 현재 스레드에서 직접 실행
        Long rightResult = rightTask.compute();   
        // 왼쪽 결과 기다림
        Long leftResult = leftTask.join();  
		// 결과 합치기
        return leftResult + rightResult;  
    }  
// 더이상 분할하지 않을 때의 계산 
    private long computeSequentially() {  
        long sum = 0;  
        for (int i = start; i < end; i++) {  
            sum += numbers[i];  
        }  
        return sum;  
    }  
}
```

```java
import java.util.concurrent.ForkJoinPool;  
import java.util.concurrent.ForkJoinTask;  
import java.util.stream.LongStream;  
  
public class ForkJoinSum {  
    public static void main(String[] args) {  
        long ans = forkJoinSum(10_000);  
        System.out.println("ans = " + ans);  
    }  
  
    public static long forkJoinSum(long n) {  
        long[] numbers = LongStream.rangeClosed(1, n).toArray();  
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);  
        return new ForkJoinPool().invoke(task);  
    }  
}
```

```java
public class ForkJoinHarness {  
    public static void main(String[] args) {  
        System.out.println("Forkjoin sum done in: " +  
                measurePerf(ForkJoinSumCalculator::forkJoinSum, 10_000_000L) + " msecs");  
    }  
  
    public static long measurePerf(LongUnaryOperator f, long n) {  
        long fastest = Long.MAX_VALUE;  
        long start = System.currentTimeMillis();  
        long result = f.applyAsLong(n);  
        long end = System.currentTimeMillis();  
        long duration = end - start;  
        if (duration < fastest) {  
            fastest = duration;  
        }  
        return fastest;  
    }  
}
```

```result
결과
ans = 50005000

harness 실행 결과
Forkjoin sum done in: 55 msecs
```

![[Pasted image 20250417012046.png]]
- 큰 배열에서 계속 반으로 나눔 (Fork)
- 실행하고자 하는 숫자의 크기가 10,000이하가 되면 순차 계산
- 결과 차례로 합쳐짐(Join)
- 병렬로 빠르게 합을 계산

### 포크/조인 프레임워크 제대로 사용하는 방법

- 서브테스크가 모두 시작된 후 `join()`호출하기
	- 하나의 테스크가 다른 태스크의 종료를 기다리지 않기 위해서입니다.
- 한쪽 서브태스크는 `compute()`, 다른 한쪽은 `fork()`
	- 불필요한 스레드 낭비를 줄이고 재사용 효과를 얻습니다.
- 작업을 너무 작게 나누는 건 오히려 손해    
    - 작업 나누는 비용이 결과보다 크면 성능이 떨어집니다.
- 디버깅이 어렵다
    - compute가 다른 스레드에서 실행되기 때문에, IDE에서 디버깅하기 어렵습니다.
- 병렬화가 항상 빠르지는 않다
     - 간단한 작업이나 I/O가 많은 작업은 오히려 병렬 처리가 더 느릴 수 있습니다.

### 작업 훔치기

포크 조인 방식은 분할기법이 효율적이지 않았을 경우나 디스크 접근 속도가 저하되었거나 외부 서비스와 협력하는 과정에서 지연이 생길 수도 있습니다. 따라서, 일이 없는 스레드가 다른 스레드의 작업 큐에서 할 일을 훔쳐오는 것이 작업 훔치기입니다. 즉, 각 스레드는 자신만의 이중 연결 리스트를 유지하며, 새로운 작업은 리스트의 한쪽 끝에 삽입하고 다른 스레드의 작업을 훔쳐올 때는 반대쪽 끝에서 가져와 작업을 처리하는 것입니다. 이는 스레드 간 경쟁도 최소화하며, 자동으로 CPU 활용률을 최적화할 수 있습니다.

![[Pasted image 20250417014335.png]]
- 초반에는 작업자 2, 3, 4는 아무 작업도 가지고 있지 않은 상태 -> 유휴 상태
- 작업을 분할하여 큐에 넣고, 다른 스레드가 가져갈 수 있도록 준비
- 작업이 자렉 나눠지고, 유휴 스레드가 훔쳐감
- 모든 스레드가 동시에 작업 -> 병렬 처리 완성

## Spliterator 인터페이스

분할 로직을 따로 개발하지 않고, 병렬 스트림을 이유할 수 있는데, 이는 자동으로 스트림을 분할하는 기법인 `Spliterator`인터페이스에서 활용할 수 있습니다.

`Spliterator`란 `split` 와 `Iterator`가 합쳐진 말로, 병렬 처리를 고려하여 설계된 `Iterator`의 진화형입니다. 
```java
public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);
    Spliterator<T> trySplit();
    long estimateSize();
    int characteristics();
}
```
- `tryAdvance()` : 요소 하나를 소비하고, 처리할 게 더 있다면 `true`를 반환, `Iterator`와 유사
- `trySplit()` : 일부 요소를 잘라내어 새로운 `Spliterator`를 반환함
- `estimateSize()`: 남은 요소의 개수를 추정함
- `characteristics()` : 이 `Spliterator`가 가진 속성(정렬, 중복, null 등)을 반환함

### 분할 과정 

자바의 병렬 스트림은 내부적으로 재귀적으로 `Spliterator`를 분할합니다. `trySplit()`이 null을 반환할 때까지 나누며, 서로 다른 스레드에 나눠서 작업을 수행하게 됩니다.
### Spliterator의 특성

| 특성           | 설명                |
| ------------ | ----------------- |
| `ORDERED`    | 순서가 중요함 (ex. 리스트) |
| `DISTINCT`   | 모든 요소가 유일함        |
| `SORTED`     | 정렬되어 있음           |
| `SIZED`      | 크기를 정확히 알고 있음     |
| `NONNULL`    | null이 아님          |
| `IMMUTABLE`  | 읽는 동안 변경되지 않음     |
| `CONCURRENT` | 동시 접근 안전          |
| `SUBSIZED`   | 분할된 것도 크기를 정확히 앎  |

### 커스텀 Spliterator 구하기

문자열 안의 단어 수를 계산하는 메서드를 커스텀 Spliterator를 만들어봅시다.
```java
static final String SENTENCE =  
        "Nel mezzo del cammin di nostra vita " +  
                "mi ritrovai in una selva oscura" +  
                " ch la dritta via era smarrita " ;
```

**1. 반복형으로 단어 수 세는 메서드**
```java
public class CustomSpliterator {   
    public static int countWordsIteratively(String s) {  
        int counter = 0;  
        boolean lastSpace = true;  
        for (char c: s.toCharArray()) {  
            if (Character.isSpaceChar(c)) {  
                lastSpace = true;  
            } else {  
                if (lastSpace) {  
                    counter++;  
                }  
                lastSpace = false;  
            }  
        }  
        return counter;  
    }  
}
```

```result
결과 :
Found 19 words
```

**2. 함수형으로 단어 수 세는 메서드 리팩토링**

```java
public static void main(String[] args) {    
    Stream<Character> stream = IntStream.range(0, SENTENCE.length())  
            .mapToObj(SENTENCE::charAt);  
    System.out.println("Found " + countWords(stream) + " words");  
}

private static int countWords(Stream<Character> stream) {  
    WordCounter wordCounter = stream.reduce(new WordCounter(0, true),  
            WordCounter::accumulate,  
            WordCounter::combine);  
  
    return wordCounter.getCounter();  
}
```

```java
public class WordCounter {  
    private final int counter;  
    private final boolean lastSpace;  
  
    public WordCounter(int counter, boolean lastSpace) {  
        this.counter = counter;  
        this.lastSpace = lastSpace;  
    }  

	// 공백확인 
    public WordCounter accumulate(Character c){  
        if (Character.isWhitespace(c)) {  
            return lastSpace ?  
                    this :  
                    new WordCounter(counter , true);  
        } else {  
            return lastSpace ?  
                    new WordCounter(counter + 1, false) :  
                    this;  
        }  
    }  
  
    public WordCounter combine(WordCounter other) {  
        return new WordCounter(counter + other.counter, other.lastSpace);  
    }  
  
    public int getCounter() {  
        return counter;  
    }  
}
```

```result
결과 :
Found 19 words
```

**3. 병렬로 수행하기**

```java
System.out.println("Found " + countWords(stream.parallel()) + " words");
```

```result
결과 :
Found 35 words // 책과 결과값이 다름
```
문자열이 단어 중간에서 잘릴 수도 있기 때문에, 하나의 단어가 두 개로 인식되어질 수도 있어 다음과 같은 잘못된 결과가 나오게 됩니다.

**4. WordCounterSpliterator 활용**

```java
import java.util.Spliterator;  
import java.util.function.Consumer;  
  
public class WordCounterSpliterator implements Spliterator<Character> {  
/*
- `string`: 우리가 처리할 전체 문장 (불변)
- `currentChar`: 지금까지 몇 번째 문자까지 봤는지 위치를 기록하는 커서
*/
    private final String string;  
    private int currentChar = 0;  
  
    public WordCounterSpliterator(String string) {  
        this.string = string;  
    }  
  
    @Override  
    public boolean tryAdvance(Consumer<? super Character> action) {  
        action.accept(string.charAt(currentChar++));  
        return currentChar < string.length();  
    }  
  
    @Override  
    public Spliterator<Character> trySplit() {  
        int currentSize = string.length() - currentChar;  
        if (currentSize < 10) return null;  
  
        for (int splitPos = currentChar + currentSize / 2; splitPos < string.length(); splitPos++) {  
            if (Character.isWhitespace(string.charAt(splitPos))) {  
                Spliterator<Character> spliterator =  
                        new WordCounterSpliterator(string.substring(currentChar, splitPos));  
                currentChar = splitPos;  
                return spliterator;  
            }  
        }  
        return null;  
    }  
  
    @Override  
    public long estimateSize() {  
        return string.length() - currentChar;  
    }  
  
    @Override  
    public int characteristics() {  
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;  
    }  
}
```

```java
Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);  
Stream<Character> stream = StreamSupport.stream(spliterator, true);  
System.out.println("Found " + countWords(stream) + " words.");
```

```result
결과 :
Found 19 words.
```

- #### `tryAdvance()` 
	- 스트림이 하나하나 문자를 처리할 때마다 호출됨
	- `action.accept(...)`: 현재 글자를 처리하는 로직에게 넘김
	- `currentChar++`: 다음 글자로 이동
	- `return`: 아직 끝나지 않았다면 `true`, 끝났으면 `false`
- #### `trySplit()`
	- 병렬로 작업을 나눌 때 호출됨
	- 남은 글자 수 계산 → 너무 작으면 나누지 말고 `null` 리턴
	- **문장 중간쯤** 위치에서 공백(단어 끝)을 찾음
	- 거기서 문자열을 잘라서 새 Spliterator로 반환
- #### `estimateSize()`
	- 아직 읽지 않은 문자 개수를 리턴
- #### `characteristics()`
	- 특징을 알려주는 부분