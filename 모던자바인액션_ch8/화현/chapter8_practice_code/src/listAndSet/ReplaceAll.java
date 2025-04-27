package listAndSet;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class ReplaceAll {
    public static void main(String[] args) {
        List<String> referenceCodes = Arrays.asList("a12", "c14", "b13");

//        List<String> updatedCodes = referenceCodes.stream()
//                .map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
//                .collect(Collectors.toList());
//
//        updatedCodes.forEach(System.out::println);

//        for (ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext(); ) {
//            String code = iterator.next();
//            iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
//        }

        referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));

        referenceCodes.forEach(System.out::println);
    }
}
