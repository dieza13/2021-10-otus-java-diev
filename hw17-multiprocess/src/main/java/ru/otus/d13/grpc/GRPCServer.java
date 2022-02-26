package ru.otus.d13.grpc;


import io.grpc.ServerBuilder;
import ru.otus.d13.grpc.service.NumberDBServiceImpl;
import ru.otus.d13.grpc.service.RemoteDBServiceImpl;


public class GRPCServer {

    public static final int SERVER_PORT = 8081;

    public static void main(String[] args) throws Exception {

        var dbService = new NumberDBServiceImpl();
        var remoteDBService = new RemoteDBServiceImpl(dbService);

        var server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(remoteDBService).build();
        server.start();
        System.out.println("Waiting client...");
        server.awaitTermination();
    }
}
