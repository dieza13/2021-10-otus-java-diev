package ru.otus.d13;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.stream.Collectors;

public class HelloOtus {

    public static void main(String[] args) {

        Multimap<String, String> multimap = ArrayListMultimap.create();

        multimap.put("животное","слон");
        multimap.put("животное","кот");
        multimap.put("животное","пес");
        multimap.put("птица","воробей");
        multimap.put("птица","ворона");
        multimap.put("птица","сорока");

        System.out.printf(multimap.get("животное").stream().collect(Collectors.joining(" съел ")));

    }
}
