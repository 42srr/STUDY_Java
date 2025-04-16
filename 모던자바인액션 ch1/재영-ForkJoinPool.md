- 모던 자바에서 병렬 스트림을 사용시, 내부적으로 ForkJoinPool을 활용해 작업을 병렬로 실행한다.

- ForkJoinPool은 워크 스틸링(work-stealing) 알고리즘을 사용해 여러 개의 작업을 여러 스레드에서 동시에 실행한다.

## 병렬 스트림의 동작 방식
1. 데이터를 적절한 크기로 split
	- 스트림의 데이터 소스를 여러 청크로 나눈다.
	- Spliterator 인터페이스를 활용해 데이터를 자동 분할
2. 여러 개의 스레드에서 각각의 작업을 수행
	- 기본적으로 CPU 코어 수를 고려하여 적절한 개수의 스레드(ForkJoinPool의 common pool)을 할당.
	- 각 스레드는 분할된 데이터 조각을 독립적으로 처리
3. 결과를 병합하여 최종 결과 생성
	- 모든 서브 작업이 완료되면 결과를 합쳐서 최종적인 결과 생성

## 주의점
- 병렬 처리가 항상 빠른 것은 아니다
	- 작은 데이터셋에서는 스레드 생성 오버헤드가 병렬처리 이점을 상쇄할 수 있음.
- 공유 자원 사용 시 동기화 문제 발생 가능
	- 병렬 스트림은 여러 스레드에서 동시에 실행되므로 공유 자원을 수정하는 경우 주의
- 병렬 스트림은 기본적으로 공용 ForkJoinPool을 사용
	- ForkJoinPool은 전체 애플리케이션에서 공유되므로, 다른 병렬 작업과 리소스를 공유할 수도 있다-
	- `System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");`
	  같은 설정으로 병렬 수준 조정 가능.

## 예제
- 순차 스트림
```java
import java.util.stream.IntStream;

public class SequentialStreamExample {
    public static void main(String[] args) {
        long start = System.nanoTime();
        int sum = IntStream.rangeClosed(1, 10_000_000)
                           .sum();
        long end = System.nanoTime();
        System.out.println("Sum: " + sum);
        System.out.println("Execution Time: " + (end - start) / 1_000_000 + " ms");
    }
}
```

- 병렬 스트림
```java
import java.util.stream.IntStream;

public class ParallelStreamExample {
    public static void main(String[] args) {
        long start = System.nanoTime();
        int sum = IntStream.rangeClosed(1, 10_000_000)
                           .parallel() // 병렬 스트림 활성화
                           .sum();
        long end = System.nanoTime();
        System.out.println("Sum: " + sum);
        System.out.println("Execution Time: " + (end - start) / 1_000_000 + " ms");
    }
}
```