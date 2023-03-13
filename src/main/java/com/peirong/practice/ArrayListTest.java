package com.peirong.practice;

import java.util.ArrayList;
import java.util.Iterator;

public class ArrayListTest {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();

        list.add("aa");
        list.add("bb");
        list.add("bb");
        list.add("aa");
        list.add("cc");

        remove(list, "bb");
        for (String str : list) {
            System.out.print(str + " ");
        }
    }

    public static void remove (ArrayList < String > list, String elem){
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).equals(elem)) {
                list.remove(list.get(i));
            }

        }

        //普通for循环倒序删除，删除过程中元素向左移动，可以删除重复的元素
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals(elem)) {
                list.remove(list.get(i));
            }
        }

        //增强for循环删除，使用ArrayList的remove()方法删除，产生并发修改异常 ConcurrentModificationException
        for (String str : list) {
            if (str.equals(elem)) {
                list.remove(str);
            }
        }

        //迭代器，使用list的remove方法删除，产生并发修改异常ConcurrentModificationException
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(elem)) list.remove(iterator.next());
        }


        //迭代器，使用迭代器的remove方法删除，但不推荐这种方法。
        Iterator iterator1 = list.iterator();
        while (iterator1.hasNext()) {
            if (iterator1.next().equals(elem)) iterator1.remove();
        }

        /**
         * 针对上述结果总结如下：
         * 1.普通for循环删除，无论正向或者反向，不会抛出异常。但是由于删除过程中，list整体左移，所以正向删除无法删除连续的重复元素。
         *
         * 2.使用增强的for循环或者迭代器，只要是调用list本身的remove函数，由于在remove中会修改list内部的modCount，
         *   导致expectedModCount!=modCount，当调用迭代器的next函数时，首先会检查两个计数是否相等，由于不相等，因此发生异常。
         *
         * 3.如果使用迭代器并调用迭代器的remove方法来删除元素，由于迭代器的remove函数中对两个计数进行了同步，所以不会出现异常。
         * */

    }
}
