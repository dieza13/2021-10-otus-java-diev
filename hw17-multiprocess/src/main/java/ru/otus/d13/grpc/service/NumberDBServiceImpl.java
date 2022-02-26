package ru.otus.d13.grpc.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumberDBServiceImpl implements NumberDBService {


    @Override
    public List<Integer> generateSequence(int firstNumber, int lastNumber) {
        return IntStream
                .range(firstNumber,lastNumber)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
