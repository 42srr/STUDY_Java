자바 7부터 도입된 문법. 자원을 자동으로 관리해주는 기능이다.

주로 파일, 네트워크 연결, DB 연결 등과 같이 사용 후 반드시 닫아야 하는 자원을 사용할 때 활용한다.

# 특징
- 자동 자원 해제
try 괄호 안에 선언된 자원들은 try 블록이 종료될 때 자동으로 close 메서드가 호출되어 닫힙니다. 이를 통해 명시적으로 finally 블록에서 자원을 닫는 코드를 작성할 필요가 없어집니다.

- AutoCloseable 인터페이스
try-with-resources 구문은 `java.lang.AutoCloseable`인터페이스를 구현한 객체에 적용할 수 있습니다. `java.io.Closeable`도 이 인터페이스를 상속하므로, 대부분 입출력 관련 자원에 사용 가능합니다.

- 예외 처리 간소화
자원 해제 과정에서 발생하는 예외도 자동으로 처리되어, 코드의 가독성과 안정성이 향상됩니다.

# 예제
```java
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}
// BufferedReader는 try 블록 종료 후 자동으로 close()가 호출되어 닫힘

```

