package ru.otus.d13.grpc.service;

import io.grpc.stub.StreamObserver;
import ru.otus.d13.grpc.generated.Number;
import ru.otus.d13.grpc.generated.RemoteDBServiceGrpc;
import ru.otus.d13.grpc.generated.RemoteSequence;

import java.util.List;


public class RemoteDBServiceImpl extends RemoteDBServiceGrpc.RemoteDBServiceImplBase {

    private final NumberDBService realDBService;

    public RemoteDBServiceImpl(NumberDBService realDBService) {
        this.realDBService = realDBService;
    }

    @Override
    public void generateSequence(RemoteSequence request, StreamObserver<Number> responseObserver) {
        List<Integer> allUsers = realDBService.generateSequence(request.getFirstNumber(),request.getLastNumber());
        allUsers.forEach(num -> {
            responseObserver.onNext(integer2Number(num));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        responseObserver.onCompleted();
    }

    private Number integer2Number(Integer number) {
        return Number.newBuilder()
                .setValue(number)
                .build();
    }
}
