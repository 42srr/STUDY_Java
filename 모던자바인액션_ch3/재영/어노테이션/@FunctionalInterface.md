
함수형 인터페이스임을 가리키는 어노테이션이다.

@FunctionalInterface로 인터페이스를 선언했지만 실제로 함수형 인터페이스가 아니면 컴파일러가 에러를 발생시킨다.

예를 들어 추상 메서드가 한 개 이상이라면
->`Multiple nonoverriding abstract methods found in interface *` 같은 에러가 발생할 수 있다.
