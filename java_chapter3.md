# JAVA 스터디 챕터 3

# 반복문과 배열 그리고 예외처리 (p113 ~ p160)

# 반복문

## 반복문의 종류

- for 문
- while 문
- do-while 문

## for 문

for (초기문; 조건문; 반복 후 작업)

- 초기문
  - 시작할 때 한번만 수행
  - ,를 이용하여 여러 문장 나열 가능
- 조건식
  - 말그대로 조건식 true이면 반복 false이면 탈출
- 반복 후 작업
  - 초기문과 마찬가지로 ,로 분리하여 여러문장 나열 가능

### 예시

// 콤마를 사용하여 여러 변수를 동시에 초기화하고 증감
for (int i = 0, j = 10; i < 5; i++, j--) {
System.out.println("i = " + i + ", j = " + j);
}

## while 문

- while 문은 반복횟수를 알 수 없는 경우에 적합.
- 조건식에 사용되는 변수를 while문 실행 전 초기화가 필요.
  다들 너무나 잘 아는 것.

## do-while문

- while 문과 달리 작업문 실행 후 조건식을 검사하므로 작업문이 최초 한 번은 반드시 실행된다.
- 조건식에 사용되는 변수를 do-while문 실행 전 초기화가 필요.

# 배열

## 배열의 개념

배열은 인덱스와 인덱스에 대응하는 데이터들로 이루어진 연속적인 자료구조.
같은 종류의 데이터들이 순차적으로 저장된다.

## 배열 선언 및 생성

C / C++과 달리 두 단계로 이루어짐

- 배열에 대한 레퍼런스 변수 선언
- 배열 생성 - 배열의 저장 공간 할당.

### 배열에 대한 레퍼런스 변수 선언

배열 레퍼런스 변수의 선언 방식 두 가지

1. int intArray[]; -> 타입 배열이름[];
2. int [] intArray; -> 타입[] 배열이름;

위의 두 가지 중 int[] intArray 방식이 더 권장되는 표준 스타일.
배열이 데이터 타입의 일부임을 더 명확하게 보여줌 즉 의미가 더 직관적임.

### 배열 선언시 []에 크기를 지정하면 안됨.

ex. int intArray[10]; // 컴파일 오류.

### 배열 생성

배열 생성은 데이터를 저장할 배열 공간을 할당 받는 과정
반드시 New 연산자를 이용하여 배열을 생성하며 [] 안에 생성할 원소 개수를 지정한다.

```
int[] array = new int[size];
```

#### New 연산자란?

자바에서 새로운 객체를 생설할 때 사용하는 연산자
메모리에 새로운 공간을 할당하고 객체를 생성하는 역할
주요특징

1. 힙(Heap) 메모리에 새로운 공간을 할당합니다.
2. 객체의 생성자를 호출합니다.
3. 생성된 객체의 메모리 주소를 반환합니다.

## 레퍼런스 치환과 배열 공유

자바에서는 레퍼런스 변수와 배열 공간이 분리되어 있기 때문에, 다수의 레퍼런스 변수가 하나의 배열 공간을 가리키는 배열 공유가 쉽게 이루어진다.

### 배열의 크기, length 필드

자바는 배열을 객체로 다룸. 배열이 생성되면 배열 객체가 생성된다. 이 객체에는 배열의 저장 공간과 함께 배열의 크기 값을 가진 length 필드가 존재한다.
length 필드를 이용해 배열의 크기를 간단히 알아낼 수 있다.

### 배열과 for-each문

기존의 for 문을 변형하여, 배열의 크기만큼 루프를 돌면서 원소를 순차적으로 접근하는데 가능하게 한다. 이는 배열뿐만 아니라 나열 타입(enum)에 대해서도 사용이 가능하다.

```
for (변수 : 배열레퍼런스){
반복 작업문
}
```

## 다차원 배열

### 비정방형 배열

비정방형 배열은 행마다 열의 개수가 서로 다른 배열을 말한다.
어떤 메소드가 배열을 매개변수로 받을 때, 배열이 정방향인지 비정방향인지 표시되지 않기 때문에 항상 lenght 필드를 사용하여 각 행의 열의 개수를 파악해야한다.

## 메소드에서 배열 리턴

메소드에서 어떤 배열이든지 리턴하면, 배열 공간 전체가 아니라 배열에 대한 레퍼런스만 리턴된다.

## main() 메소드

### main()메소드의 특징

- JVM이 프로그램을 실행할 때 가장 먼저 찾는 진입점(entry point)
- public: 어디서나 접근 가능
- static: 객체 생성 없이 실행 가능
- void: 반환값 없음
- args: 커맨드 라인 매개변수를 받는 문자열 배열

배열 이름은 args 대신 다른 이름을 사용해도 되지만, 관례적으로 args를 사용합니다.

### JVM?

JVM은 자바 프로그램을 실행하는 가상 컴퓨터입니다.

주요 기능:

1. 자바 프로그램을 어떤 컴퓨터에서도 실행가능하게 함
2. 메모리 자동 관리 (쓰레기 수집)
3. 프로그램 안전하게 실행.

실행 순서 :

1. 소스코드(.java) → 바이트코드(.class) 변환
2. JVM이 바이트코드 읽고 실행

구체적인 사안은 개인적으로 더 공부하는 것으로..!

# 자바의 예외처리

## 예외(Exception)란?

자바에서 오동작이나 결과에 악영향을 미칠 수 있는 실행 중 발생한 오류를 예외라고 한다. 예외는 사용자의 잘못된 입력이나 배열의 인덱스가 배열의 크기를 넘어가는 등, 예기치 못한 상황에 의해 프로그램 실행 중에 발생한다.

실행 중에 예외가 발생하면 자바 플랫폼이 가장 먼저 알게 되어 현재 실행 중인 응용프로그램에게 예외를 전달한다. 만일 대처 코드가 없다면 자바 플랫폼은 응용 프로그램을 곧바로 종료시킨다.

즉 Error는 프로그램에서 복구가 어려운 심각한 문제를 나타내며, Exception은 프로그램에서 처리 가능한 오류를 나타낸다.

## 예외 처리, try-catch-finally 문

자바는 예외처리를 위해 try-catch-finally 문을 사용한다.

```
try {
	예외가 발생할 가능성이 있는 실행문(try 블록)
}
catch (처리할 예외 타입 선언) {
	예외 처리문 (catch 블록)
}
finally {
	예외 발생 여부와 상관없이 무조건 실행되는 문장
	finally는 생략 가능
}
```

try 블록에서 예외가 발생하면 응용프로그램은 남은 실행문을 실행하지 않고 바로 catch 블록의 예외 처리문으로 점프하여 실행한다.
finally 블록은 예외가 발생하든 않든 마지막에 반드시 실행된다.

## 자바의 예외 클래스

**Throwable**

- Error (시스템 레벨 문제)

  - OutOfMemoryError: JVM 메모리 부족
  - StackOverflowError: 스택 메모리 초과
  - VirtualMachineError: JVM 오류

- Exception (프로그램 레벨 문제)

  1. Checked Exception

  - 컴파일 시점에서 확인되는 예외
  - 반드시 예외처리 필요 즉 강제적으로 try -catch 문구를 강제한다.
  - Exception 클래스를 상속하되 RuntimeException을 상속하지 않음
  - 종류
    - IOException: 입출력 오류
    - SQLException: 데이터베이스 오류
    - ClassNotFoundException: 클래스 로드 실패

  2. Unchecked Exception (RuntimeException)

  - Unchecked Exception으로 명시적인 예외 처리(try-catch) 불필요 즉 컴파일러가 예외 처리를 강제하지 않으나, 필요한 경우 try-catch로 처리가능.
  - 프로그래머의 실수로 발생하는 예외들을 포함
  - 종류
    - NullPointerException: null 객체 참조
    - ArrayIndexOutOfBoundsException: 배열 범위 초과
    - ArithmeticException: 수학적 오류
    - ClassCaseException: 부적절한 클래스 형변환 시도
    - IllegalArgumentException: 메서드에 부적절한 인자 전달
    - NumberFormatException: 문자열을 숫자로 변환 실패 (IllegalArgumentException의 하위)
    - InputMismatchException: Scanner 클래스 사용 시 입력 타입 불일치

  ## 번외 try-catch 문을 제외한 예외처리 방식

  1.  throws - 호출한 쪽(부모)에게 예외 처리 위임
  2.  throw - 명확한 의미의 예외로 바로 처리.

### throw

throw는 직접 예외를 발생시키고 싶을 때 쓰는 것이다. 주로 UncheckedException 처리를 위해 쓰는 방식이다

```
throw new ExceptionType("에러 메시지");
```

```
if (balance < amount) {
    throw new InsufficientBalanceException("잔액이 부족합니다");
}
```

- 예외 객체를 생성하고 즉시 발생시킴
- throw 다음에는 반드시 예외 객체가 와야 함.
- 예외가 발생하면 즉시 메소드 실행이 중단 됨

  ### throws

  throws는 자신을 호출하는 메소드에 예외처리의 책임을 떠넘기는 것.

  ```
  public class ThrowTest {

    	public static void main(String[] args) {

        	int n1, n2;

        	n1=12;
        	n2=0;

       		try {
           	 throwTest(n1, n2);
        	} catch (ArithmeticException e) {
            	// n1/n2 라면 발생했을 것
            	System.out.println("ArithmeticException: " + e.getMessage());
        	}
    	}

    	public static void throwTest(int a, int b) throws ArithmeticException{
        	System.out.println("throw a/b: "+ a/b);
    	}
  }
  ```

반드시 try-catch구문으로 메서드 호출부분을 감싸줘야 한다. throws를 쓰면 그 호출한 메서드에서 try-catch 구문읋 해줘야 되는 것을 잊으면 안된다.

```
public void withdraw(int amount) throws InsufficientBalanceException {
    if (balance < amount) {
        throw new InsufficientBalanceException("잔액 부족");
    }
    this.balance -= amount;
}

// 호출하는 쪽에서 예외 처리
try {
    account.withdraw(5000);
} catch (InsufficientBalanceException e) {
    System.out.println(e.getMessage());
}
```
