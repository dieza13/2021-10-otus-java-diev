package ru.otus.d13.grpc.service;


import java.util.List;

public interface NumberDBService {
    List<Integer> generateSequence(int firstNumber, int lastNumber);
}
