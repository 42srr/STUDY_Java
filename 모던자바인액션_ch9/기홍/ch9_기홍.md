## 9.1 가독성과 유연성을 개선하는 리팩터링

### 9.1.2 익명 클래스를 람다 표현식으로 리팩터링하기

```java
Runnable r1 = new Runnable() { // 익명 클래스를 사용한 이전 코드
	public void run() {
		System.out.println("Hello");
	}
};

Runnable r2 = () -> System.out.println("Hello"); // 람다 표현식을 사용한 최신 코드
```

#### 익명 클래스를 람다 표현식으로 변활할 때 주의점

1. 익명 클래스에서 사용한 `this` 와 `super` 는 람다 표현식에서 다른 의미를 갖는다.
	- 익명 클래스에서 `this`: 익명 클래스 자기 자신
	- 람다에서 `this`: 람다를 감싸는 클래스

2. 익명 클래스는 감싸고 있는 클래스의 변수를 가릴 수 있다.
	- 익명 클래스에서는 스코프가 자기 자신 안으로 한정되는 반면, 람다 표현식에서는 그렇지 않기 때문

```java
int a = 10;
Runnable r1 = () -> {
	int a = 2; // 컴파일 에러
	System.out.println(a);
};

Runnable r2 = new Runnable() {
	public void run() {
		int a = 2; // 잘 작동함
		System.out.println(a);
	}
}
```

3. 익명 클래스를 람다 표현식으로 바꾸면 콘텍스트 오버로딩에 따른 모호함이 초래될 수 있다. 익명 클래스는 인스턴스화할 때 명시적으로 형식이 정해지지만, 람다의 형식은 콘텍스트에 따라 달라진다.

```java
/* 선언부 */
interface Task { public void execute(); }

public static void doSomething(Runnable r) { r.run(); } // a
public static void doSomething(Task a) { r.execute(); } // b

/* 실행부 */

// 익명 클래스를 파라미터에 넣어서 호출
doSomething(new Task() { // b 를 호출하는게 명확한 반면...
	public void execute() {
		System.out.println("Danger danger!!");
	}
});

// 람다 표현식을 파라미터에 넣어서 호출
// a와 b중 뭘 호출하는지 명확하지 않음
doSomething(() -> System.out.println("Danger danger!!"));
```

해결책: 명시적 형변환을을 이용해서 모호함을 제거

```java
doSomething((Task)() -> System.out.println("Danger danger!!"));
```

### 9.1.3 람다 표현식을 메서드 참조로 리팩터링하기

람다 표현식은 쉽게 전달할 수 있는 짧은 코드다. 하지만 람다 표현식 대신 메서드 참조를 이용하면 가독성을 높일 수 있다. 메서드 참조의 메서드명으로 코드의 의도를 명확하게 알릴 수 있기 때문이다.

```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
	menu.stream()
		.collect(
			groupingBy(dish -> {
				if (dish.getCalories() <= 400) return CaloricLevel.DIET;
				else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
				else return CaloricLevel.FAT;
}));
```

Dish 클래스에 `getCaloridLevel` 이라는 메서드를 추가하면 해당 메서드를 별도로 추출한 다음 `groupingBy` 에 인수로 전달할 수 있다.

다음처럼 코드가 간결하고 의도도 명확해진다.

```java
public class Dish {
	...
	public CaloricLevel getCaloricLevel() {
		if (this.getCalories() <= 400) return CaloricLevel.DIET;
		else if (this.getCalories() <= 700) return CaloricLevel.NORMAL;
		else return CaloricLevel.FAT;
	}
}

Map<CaloridLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(groupingBy(Dish::getCaloricLevel));
```

추가: 메서드 참조. 이들은 메서드 참조와 조화를 이루도록 설계되었다.

```java
/* 람다식: 비교 구현에 신경 써야함 */
inventory.sort((Apple a1, Apple a2) ->
	a1.getWeight().compareTo(a2.getWeight())
);

/* 메서드 참조: 코드가 문제 자체를 설명함 */
inventory.sort(comparing(Apple::getWeight));
```

리듀싱 연산은 메서드 참조와 함께 사용할 수 있는 내장 헬퍼 메서드를 제공한다. 람다 표현식과 저수준 리듀싱 연산을 조합하는 것보다 Collectors API 를 사용하면 코드의 의도가 더 명확해진다.

```java
/* 저수준 리듀싱 연산을 조합한 코드 */
int totalCalories =
	menu.stream().map(Dish::getCalories)
				.reduce(0, (c1, c2) -> c1 + c2);

/* 내장 컬렉터(summingInt)를 이용한 코드 */
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

### 9.1.4 명령형 데이터 처리를 스트림으로 리팩터링하기

이론적으로는 반복자를 이용한 기존의 모든 컬렉션 처리 코드를 스트림 API 로 바꿔야 한다. 스트림은 쇼트서킷과 게으름이라는 강력한 최적화뿐 아니라 멀티코어 아키텍처를 활용할 수 있는 지름길을 제공한다.

```java
List<String> dishNames = new ArrayList<>();

// 기존의 코드
for (Dish dish : menu) {
	if (dish.getCalories() > 300) dishNames.add(dish.getName());
}

// 스트림 API로 리팩토링
menu.parallelStream()
	.filter(d -> d.getCalories() > 300)
	.map(Dish::getName)
	.collect(toList());
```

### 9.1.5 코드 유연성 개선

#### 함수형 인터페이스 적용

##### 조건부 연기 실행

`조건부 연기 실행(conditional evaluation)` 은 람다 표현식이나 함수형 인터페이스를 사용하여 조건에 따라 코드를 실행하는 것을 말한다. 즉, 특정 조건이 만족될 때만 코드를 실행하고, 그렇지 않은 경우에는 실행하지 않도록 코드를 구성하는 방법을 말한다.

조건부 연기 실행의 장점:
1. 코드 가독성 향상: 복잡한 제어 흐름문을 람다 표현식으로 간단하게 표현하여 코드의 가독성을 높일 수 있다.
2. 코드 유지보수 용이성: 람다를 사용하여 불필요한 if 문을 제거하고 코드를 간결하게 유지할 수 있다.
3. 코드 재사용성 향상: 람다를 사용하여 중복되는 코드를 줄이고, 재사용 가능한 코드를 만들 수 있다.

아래 코드는 다음과 같은 사항에 문제가 있다.
- logger 의 상태가 isLoggable 이라는 메서드에 의해 클라이언트 코드로 노출된다.
- 메시지를 로깅할 때마다 logger 객체의 상태를 매번 확인해야 한다.

```java
if (logger.isLoggable(Log.FINER))
	logger.finer("Problem: " + generateDiagnostic());
```

아래 처럼 메시지를 로깅하기 전에 logger 객체가 적절한 수준으로 설정되었는지 내부적으로 확인하는 log 메서드를 사용하는 것이 바람직하다.

```java
logger.log(Level.FINER, "Problem: " + generateDiagnostic());
```

아직 인수로 전달된 메시지 수준에서 logger 가 활성화되어 있지 않더라도 항상 로깅 케시지를 평가하게 되는 문제가 남아있다. (위 코드에서 `"Problem: " generateDiagnostic()` 부분을 먼저 실행한다고 한다.)

따라서 메시지 생성 과정을 연기(delay)할 수 있어야 하는데, 자바 8 API 설계자는 이와 같은 logger 문제를 해결할 수 있도록 Supplier 를 인수로 갖는 오버로드된 log 메서드를 제공했다.

```java
public void log(Level level, Supplier<String> msgSupplier) {
	if (logger.isLoggable(level)) {
		log(level, msgSupplier.get()); // 람다 실행
	}
}
```

위 log 메서드는 `Supplier` 를 인수로 갖기 때문에 다음 처럼 log 메서드를 호출하면 평가를 지연시킬 수 있다. 또한 캡슐화도 강화된다(객체 상태가 클라이언트 코드로 노출되지 않는다.)

```java
logger.log(Level.FINER, () -> "Problem: " + generateDiagnostic());
```

###### **\+ 자바의 평가 시점이란?**

한 줄 요약: 자바는 기본적으로 `즉시 평가`를 하기 때문에 코드가 읽히는 순간 계산이 일어난다. 조건부 실행, 성능, 최적화, 사이드 이펙트 방지가 필요할 땐 Supplier, Stream, 람다 등을 이용해 `지연 평가`를 직접 설계해야 한다.
 
1. 즉시 평가(Eager Evaluation)

코드를 읽는 순간, 그 표현식은 즉시 계산된다. <- 자바의 기본 동작
예시: `int x = 2 + 3;` 이 줄이 실행되면 `2 + 3`은 그 자리에서 바로 계산돼서 `x` 에 `5` 가 들어간다.
함수 호출도 마찬가지. `int len = "hello".length();`
이것은 `"Problem: " + generateDiagnostic()` 도 마찬가지다.
그래서 `Level.FINER` 가 꺼져 있어도 diagnostic 메시지는 계산되고 메모리도 사용된다.
 
2. 지연 평가(Lazy Evaluation)

자바의 기본은 eager 이지만 람다를 이용해 개발자가 직접 지연 평가를 구현할 수 있다. 람다는 실행 자체를 값처럼 넘기고 나중에 실행한다.

자바의 지연 평가 구현 방식:

`Supplier<T>` : 함수형 인터페이스로 `T` 를 나중에 계산하도록 캡슐화
`Optional.orElseGet(Supplier)` : 필요할 때만 값을 계산
`Stream` : 중간 연산은 지연, 최종 연산이 호출될 때만 실행

##### 실행 어라운드

매번 같은 준비, 종료 과정을 반복적으로 수행하는 코드가 있다면 이를 람다로 변환할 수 있다. 준비, 종료 과정을 처리하는 로직을 재사용함으로써 코드 중복을 줄일 수 있다.

다음은 3장에서 소개한 코드다. 이 코드는 파일을 열고 닫을 때 같은 로직을 사용했지만 람다를 이용해서 다양한 방식으로 파일을 처리할 수 있도록 파라미터화되었다.

> try 문 소괄호에 oneLine, twoLine 을 넣어서 p.process 의 방식을 다르기 할 수 있다.

```java
/* 인터페이스 선언. process 라는 함수를 선언할 테니 그 다음은 사용자가 알아서 구현하라. */
public interface BufferedReaderProcessor {
	String process(BufferedReader b) throws IOException;
	/* 추가: 여기에 함수를 하나 더 선언하면 컴파일 오류가 발생함. 함수형 인터페이스의 핵심 조건은 "추상 메서드가 하나만 있어야 한다" 라는 것이다. 다만 default 또는 static 메서드는 여러 개 있어도 괜찮다. */
}

/* processFile 함수 선언: process 함수가 어떻게 구현되있냐에 따라서 다른 값을 리턴한다. */
public static String processFile(BufferedReaderProcessor p ) throws IOException {
	try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
		return p.process(br);
	}
}

/* 람다 식으로 BufferedReaderProcess 인터페이스를 구현 */
String oneLine = processFile((BufferedReader b) -> b.readLine()); // 람다 전달
String twoLine = processFile((BufferedReader b) -> b.readLine() + b.readLine()); // 다른 람다 전달

```

## 9.2 람다로 객체지향 디자인 패턴 리팩터링하기

이 절에서는 다음 다섯 가지 패턴을 살펴보며 각 패턴에서 람다를 어떻게 활용할 수 있는지 설명한다.

- 전략(strategy)
- 템플릿 메서드(template method)
- 옵저버(observer)
- 의무 체인(chain of responsibility)
- 팩토리(factory)

### 9.2.1 전략

전략 패턴은 한 유형의 알고리즘을 보유한 상태에서 런타임에 적절한 알고리즘을 선택하는 기법이다.

- 알고리즘을 나타내는 인터페이스 (Strategy 인터페이스)
- 다양한 알고리즘을 나타내는 한 개 이상의 인터페이스 구현 (ConcreteStrategyA, ConcreteStrategyB 같은 구체적인 구현 클래스)
- 전략 객체를 사용하는 한 개 이상의 클라이언트

예를 들어 오직 소문자 또는 숫자로 이루어져야 하는 등 텍스트 입력이 다양한 조건에 맞게 포맷 되어 있는지 검증한다고 가정할 때, 먼저 String 문자열을 검증하는 인터페이스부터 구현한다.

```java
public interface ValidationStrategy {
	boolean execute(String s);
}
```

이번에는 위에서 정의한 인터페이스를 구현하는 클래스를 하나 이상 정의한다.

```java
public class IsAllLowerCase implements ValidationStrategy {
	public boolean execute(String s) { return s.matches("[a-z]+"); }
}

public class IsNumeric implements ValidationStrategy {
	public boolean execute(String s) { return s.matches("\\d+")};
}
```

지금까지 구현한 클래스를 다양한 검증 전략으로 활용할 수 있다.

```java
public class Validator {
	private final ValidationStrategy strategy;

	public Validator(ValidationStrategy v) { this.strategy = v;}
	public boolean validate(String s) { return strategy.execute(s); }
}

Validator numericValidator = new Validator(new IsNumeric());
boolean b1 = numericValidator.validate("aaaa"); // false 반환

Validator loserCaseValidator = new Validator(new IsAllLowerCase());
boolean b2 = lowerCaseValidator.validate("bbbb"); // true 반환
```

#### 람다 표현식 사용

`Validator` 의 `ValidationStrategy` 를 받는 생성자 안에 람다식을 직접 전달할 수 있다.

```java
Validator numericValidator = new Validator((String s) -> s.matches("\\d+"));
boolean b1 = numericValidator.validate("aaaa");

Validator lowerCaseValidator = new Validator((String s) -> s.matches("[a-z]"));
boolean b2 = lowerCaseValidator.validate("bbbb");
```

### 9.2.2 템플릿 메서드

알고리즘의 개요를 제시한 다음에 알고리즘의 일부를 고칠 수 있는 유연함을 제공해야 할 때 템플릿 메서드 디자인 패턴을 사용한다. 다시 말해 '이 알고리즘을 사용하고 싶은데 그대로는 안 되고 조금 고쳐야 하는 상황' 에 적합하다.

예시: 각각의 은행 지점은 `OnlineBanking` 클래스를 상속받아 `makeCustomerHappy` 메서드가 원하는 동작을 수행하도록 구현할 수 있다.

```java
abstract class OnlineBanking {
	public void processCustomer(int id) { // 온라인 뱅킹 알고리즘이 해야 할 일
		Customer c = Database.getCustomerWithId(id);
		makeCustomerHappy(c);
	}
	
	abstract void makeCustomerHappy(Customer c);
}
```

#### 람다 표현식 사용

알고리즘의 개요를 만든 다음 구현자가 원하는 기능을 추가할 수 있게 만들 수 있다. 위 OnlineBanking 클래스가 아닌 아래 OnlineBankingLambda 클래스로 OnlineBanking 클래스를 상속받지 ㅇ낳고 직접 람다 표현식을 전달해서 다양한 동작을 추가할 수 있다.

```java
abstract class OnlineBankingLambda { // 이 라인은 내가 추측해서 넣은 코드
	public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
		Customer c = Database.getCustomerWithId(id);
		makeCustomerHappy.accept(c);
	}
}

new OnlineBankingLambda().processCustomer(1337, (Customer c) ->
	System.out.println("Hello " + c.getName())
);
```

### 9.2.3 옵저버

어떤 이벤트가 발생했을 때 한 객체(주체, subject)가 다른 객체 리스트(옵저버, observer)에 자동으로 알림을 보내야 하는 상황에서 옵저버 디자인 패턴을 사용한다.

우선 다양한 옵저버를 그룹화할 Observer 인터페이스가 필요하다. Observer 인터페이스는 새로운 트윗이 있을 때 주제(Feed)가 호출할 수 있도록 notify 라고 하는 하나의 메서드를 제공한다.

```java
interface Observer { void notify(String tweet); }
```

이제 트윗에 포함된 다양한 키워드에 다른 동작을 수행할 수 있는 여러 옵저버를 정의할 수 있다.

```java
class NYTimes implements Observer {
	public void notify(String tweet) {
		if (tweet != null && tweet.contains("money")) {
			System.out.println("Breaking new in NY! " + tweet);
		}
	}
}

class Guardian implements Observer {
	public void notify(String tweet) {
		if (tweet != null && tweet.contains("queen")) {
			System.out.println("Yet more new from London... " + tweet);
		}
	}
}

class LeMonde implements Observer { ... }
```

이제 Subject 구현도 해야 한다.

```java
interface Subject {
	void registerObserver(Observer o);
	void notifyObservers(String tweet);
}
```

Subject 는 registerObserver 메서드로 새로운 옵저버를 등록한 다음에 notifyObservers 메서드로 트윗의 옵저버에 이를 알린다.

```java
class Feed implements Subject {
	private final List<Observer> observers = new ArrayList<>();

	public void registerObserver(Observer o) { this.observers.add(o); }
	public void notifyObservers(String tweet) { observers.forEach(o -> o.notify(tweet)); }
}
```

구현은 간단하다. Feed 는 트윗을 받았을 때 알림을 보낼 옵저버 리스트를 유지한다. 이에 주제와 옵저버를 연결하는 데모 애플리케이션을 만들 수 있다.

```java
Feed f = new Feed();

f.registerObserver(new NYTimes());
f.registerObserver(new Guardian());
f.registerObserver(new LeMonde());
f.notifyObservers("The Queen said her favourite book is ...");
```

이 예제에서는 실행해야 할 동작이 비교적 간단하므로 람다 표현식으로 불필요한 코드를 제거하는 것이 바람직하지만, 옵저버가 상태를 가지며 여러 메서드를 정의하는 등 복잡하다면 람다 표현식보다 기존의 클래스 구현방식을 고수하는 것이 바람직할 수도 있다.

### 9.2.4 의무 체인

작업 처리 객체의 체인(동작 체인 등)을 만들 때는 의무 체인 패턴을 사용한다. 한 객체가 어떤 작업을 처리한 다음에 다른 객체로 결과를 전달하고, 다른 객체도 해야 할 작업을 처리한 다음에 또 다른 객체로 전달하는 식이다.

일반적으로 다음으로 처리할 객체 정보를 유지하는 필드를 포함하는 작업 처리 추상 클래스로 의무 체인 패턴을 구성한다. 작업 처리 객체가 자신의 작업을 끝냈으면 다음 작업 처리 객체로 결과를 전달한다.

```java
public abstract class ProcessingObject<T> {
	protected ProcessingObject<T> successor;
	public void setSuccessor(ProcessingObject<T> successor) { this.successor = successor; }
	public T handle(T input) {
		T r = handleWork(input);
		if (successor != null) return successor.handle(r);
		return r;
	}
	abstract protected T handleWork(T input);
}
```

코드 예제(람다 x):

```java
public class HeaderTextProcessing extends ProcessingObject<String> {
	public String handleWork(String text) { return "From Raoul: " + text };
}

public class SpellCheckerProcessing extends ProcessingObject<String> {
	public String handleWork(Strint text) { return text.replaceAll("labda", "lambda"); }
}

ProcessingObject<String> p1 = new HeaderTextProcessing();
ProcessingObject<String> p2 = new SpellCheckerProcessing();

p1.setSuccessor(p2); // 두 작업 처리 객체를 연결한다

String result = p1.handle("Aren't labdas really sexy?!!"); // p1.handle() 이 호출한 p2.handle() 의 리턴을 리턴

System.out.println(result);
// 'From Raoul: Aren't lambdas really sexy?!!' 출력
```

#### 람다 표현식 사용

작업 처리 객체를 `Function<String, String>`, 더 정확히 표현하자면 `UnaryOperator<String>` 형식의 인스턴스로 표현할 수 있다. `andThen` 메서드로 이들 함수를 조합해서 체인을 만들 수 있다.

```java
UnaryOperator<String> headerProcessing = (String text) -> 
	"From Raoul, Mario and Alan: " + text;

UnaryOperator<String> spellCheckerProcessing = (String text) ->
	text.replaceAll("labda", "lambda");

Function<String, String> pipeline = 
	headerProcessing.andThen(spellCheckerProcessing);

String result = pipeline.apply("Aren't labdas really sexy?!!");
```

### 9.2.5 팩토리

인스턴스화 로직을 클라이언트에 노출하지 않고 객체를 만들 때 팩토리 디자인 패턴을 사용한다.

```java
public class ProductFactory {
	public static Product createProduct(String name) {
		switch(name) {
			case "loan" : return new Loan();
			case "stock" : return new Stock();
			case "bond" : return new Bond();
			default: throw new RuntimeException("No Such procuet " + name);
		}
	}
}

Product p = ProductFactory.createProduct("loan");
```

위에서 Loan, Stock, Bond 는 모두 Product 의 서브 형식이다.

#### 람다 표현식 사용

아래 코드는 람다 표현식으로 깔끔하게 정리됐지만 팩토리 메서드 createProduct 가 상품 생성자로 여러 인수를 전달하는 상황에서는 이 기법을 적용하기 어렵다. 단순한 Supplier 함수형 인터페이스로는 이 문제를 해결할 수 없다.

```java
Supplier<Product> loanSupplier = Loan::new;
Loan loan = loanSupplier.get();
/* stock, bond 생략 */

final static Map<String, Supplier<Product>> map = new HashMap<>();

static {
	map.put("loan", Loan::new);
	map.put("stock", Stock::new);
	map.put("bond", Bond::new);
}

public static Product createProduct(String name) {
	Supplier<Product> p = map.get(name);
	if (p != null) return p.get();
	throw new IllegalArgumentException("No such product " + name);
}
```

## 9.3 람다 테스팅

> `Point` 클래스와 `moveRightBy(int x)` 메소드 생략

다음은 `moveRightBy` 메서드가 의도한 대로 동작하는지 확인하는 단위 테스트다.

```java
@Test
public void testMoveRightBy() throws Exception {
	Point p1 = new Point(5, 5);
	Point p2 = p1.moveRightBy(10);

	assertEquals(15, p2.getX());
	assertEquals(5, p2.getY());
}
```

### 9.3.1 보이는 람다 표현식의 동작 테스팅

통상적인 함수는 이름이 있기 때문에 테스트 케이스 내부에서 호출할 수 있지만 람다는 익명이므로 테스트 코드 이름을 호출할 수 없다.

필요하다면 람다를 필드에 저장해서 재사용할 수 있으며, 람다의 로직을 테스트 할 수 있다.
메서드를 호출하는 것처럼 람다를 사용할 수 있다.

예를 들어 Point 클래스에 `compareByXAndThenY` 라는 정적 필드를 추가했다고 가정하자. compareByXAndThenY 를 이요하면 메서드 참조로 생성한 Comparator 객체에 접근할 수 있다.

```java
public class Point {
	public final static Comparator<Point> compareByXAndThenY = 
		comparing(Point::getX).thenComparing(Point::getY);
}
```

```java
@Test
public void testComparingTwoPoints() throws Exception {
	Point p1 = new Point(10, 15);
	Point p2 = new Point(10, 20);

	int result = Point.compareByXAndThenY.compare(p1, p2);
	assertTrue(result < 0);
}
```

### 9.3.2 람다를 사용하는 메서드의 동작에 집중하라

> 내가 이해한 이번 절: 테스트 코드에 특정 메소드를 호출하지 말고, 혹은 해당 람다식을 사용하는 메소드를 넣어서 테스트 할 수 있도록 테스트 코드를 짜라는 말인 것 같다.

람다의 목표는 정해진 동작을 다른 메서드에서 사용할 수 있도록 하나의 조각으로 캡슐화하는 것이다. 그러려면 세부 구현을 포함하는 람다 표현식을 공개하지 말아야 한다. **람다 표현식을 사용하는 메서드의 동작을 테스트**함으로써 람다를 공개하지 않으면서도 람다 표현식을 검증할 수 있다.

```java
public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
	return points.stream()
					.map(p -> new Point(p.getX() + x, p.getY()))
					.collect(toList());
}
```

위 코드에 람다 표현식 `p -> new Point(p.getX() + x, p.getY())` 를 테스트 하는 부분은 없다. 이제 moveAllPointsByRightBy 메서드의 동작을 확인할 수 있다.

```java
@Test
public void testMoveAllPointsRightBy() throws Exception {
	List<Point> points = Arrays.asList(new Point(5, 5), new Point(10, 5));
	List<Point> expectedPoints = Arrays.asList(new Point(15, 5), new Point(20, 5));
	List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);

	assertEquals(expectedPoints, newPoints);
}
```

추가 팁: 위 단위 테스트에서 보여주는 것처럼 Point 클래스의 equals 메서드는 중요한 메서드다. 따라서 Object 의 기본적인 equals 구현을 그대로 사용하지 않으려면 equals 메서드를 적절하게 구현해야 한다.

### 9.3.3 복잡한 람다를 개별 메서드로 분할하기

복잡한 람다 표현식을 테스트 하는 한 가지 해결책은 8.1.3 절에서 설명한 것처럼 람다 표현식을 메서드 참조로 바꾸는 것이다(새로운 일반 메서드 선언). 그러면 일반 메서드를 테스트하듯이 람다 표현식을 테스트할 수 있다.

### 9.3.4 고차원 함수 테스팅

함수를 인수로 받거나 다른 함수를 반환하는 메서드(이를 고차원 함수 라고 한다.)는 좀 더 사용하기 어렵다. 메서드가 람다를 인수로 받는다면 다른 람다로 메서드의 동작을 테스트할 수 있다.

```java
@Test
public void testFilter() throws Exception {
	List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
	List<Integer> even = filter(numbers, i -> i % 2 == 0);
	List<Integer> smallerThanThree = filter(numbers, i -> i < 3);

	assertEquals(Arrays.asList(2, 4), even);
	assertEquals(Arrays.asList(1, 2), smallerThanThree);
}
```

## 9.4 디버깅

문제가 발생한 코드를 디버깅 할 때는 다음 두 가지를 가장 먼저 확인해야 한다.

- 스택 트레이스
- 로깅

하지만 람다 표현식과 스트림은 기존의 디버깅 기법을 무력화한다. 이번 절에서는 람다 표현식과 스트림 디버깅 방법을 살펴본다.

### 9.4.1 스택 트레이스 확인

람다 표현식과 관련한 스택 트레이스는 이해하기 어려울 수 있다는 점을 염두에 두자. 이는 미래의 자바 컴파일러가 개선해야 할 부분이다.

#### 9.4.2 정보 로깅

아래 코드에서는 forEach 를 호출하는 순간 전체 스트림이 소비된다. 따라서 스트림 파이프라인에 적용된 각각의 연산(map, filter, limit)이 어떤 결과를 도출하는지 확인할 수 없다.

```java
List<Integer> numbers = Arrays.asList(2, 3, 4, 5);

numbers.stream()
		.map(x -> x + 17)
		.filter(x -> x % 2 == 0)
		.limit(3)
		.forEach(System.out::println); // 20, 22
```

여기서 `peek` 스트림 연산을 활용할 수 있다. `peek` 은 스트림의 각 요소를 소비한 것 처럼 동작을 실행한다(forEach 처럼 실제로 스트림의 요소를 소비하지는 않는다.). peek 은 자신이 확인한 요소를 파이프라인의 다음 연산으로 그대로 전달한다.

```java
List<Integer> result = 
	numbers.stream()
			.peek(x -> System.out.println("from stream: " + x)) // 소스에서 처음 소비한 요소를 출력
			.map(x -> x + 17)
			.peek(x -> System.out.println("after map: " + x)) // map 동작 실행 결과를 출력
			...
			.collect(toList());
```