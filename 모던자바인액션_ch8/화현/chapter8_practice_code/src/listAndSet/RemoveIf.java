package listAndSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveIf {
    public static void main(String[] args) {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("123ABC"));
        transactions.add(new Transaction("A456DEF"));
        transactions.add(new Transaction("789GHI"));
//
//        for (Transaction transaction : transactions) {
//            if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
//                transactions.remove(transaction);
//            }
//        }
//
//        System.out.println("transactions = " + transactions);
//
//        for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
//            Transaction transaction = iterator.next();
//            if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
//                //transactions.remove(transaction); -> 반복하면서 별도의 두 객체를 통해 컬렉션을 바꾸고 있는 문제
//                // Iterator 객체, next(), hasNext()를 이용해 소스를 질의함
//                // Collection 객체 자체, remove()를 호출해 요소를 삭제
//                iterator.remove();
//            }
//        }

        transactions.removeIf(transaction -> Character.isDigit(transaction.getReferenceCode().charAt(0)));
        System.out.println("transactions = " + transactions);
    }
}
