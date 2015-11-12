package com.joinlang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainAOP implements SortExecutor {

    public static void main(String[] args) {
        System.out.println("This is bubble sort O(N*N)");

        // WithOUT Proxy
        AuditProxy.apply(null, e -> new MainAOP().execute());

        // With Proxy
        SortExecutor main = (SortExecutor) AuditProxy.newInstance(new MainAOP());
        main.execute();
    }

    public boolean execute() {
        Integer[] array = makeArray();
        BubbleSort.sort(array);
        System.out.println("after: " + Arrays.toString(array));
        return true;
    }

    public static Integer[] makeArray() {
        List<Integer> list = new ArrayList<>();
        for (Integer i = 0; i < 10; i++) {/*Integer.MAX_VALUE*/
            list.add(i);
        }
        Collections.shuffle(list);
        Integer[] array = new Integer[list.size()];
        System.out.println("before: " + list);
        return list.toArray(array);
    }


}