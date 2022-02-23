package ru.otus.d13.grpc;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.d13.grpc.generated.Number;
import ru.otus.d13.grpc.generated.RemoteDBServiceGrpc;
import ru.otus.d13.grpc.generated.RemoteSequence;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GRPCClient {
    private final Logger log = LoggerFactory.getLogger(GRPCClient.class);

    private final String SERVER_HOST = "localhost";
    private final int SERVER_PORT = 8081;

    public static void main(String[] args) throws Exception {
        new GRPCClient().startClient();

    }

    private void startClient() throws Exception {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        final AtomicReference<Integer> serverValue = new AtomicReference<>();
        var latch = new CountDownLatch(1);

        var stub = RemoteDBServiceGrpc.newStub(channel);
        stub.generateSequence(RemoteSequence.newBuilder().setFirstNumber(0).setLastNumber(30).build()
                , new StreamObserver<>() {
                    @Override
                    public void onNext(Number value) {
                        serverValue.set(value.getValue());
                    }

                    @Override
                    public void onError(Throwable t) {
                        throw new RuntimeException(t);

                    }

                    @Override
                    public void onCompleted() {
                        log.info("finita la commedia");
                        latch.countDown();
                    }
                });


        handleNumbers(serverValue);
        latch.await();
        channel.shutdown();

    }

    private void handleNumbers(AtomicReference<Integer> serverValue) throws Exception {
        Integer currentNumber = 0;
        for (int i = 0; i < 50; i++) {
            Integer serverNumber = serverValue.getAndSet(null);
            currentNumber = newNumber(currentNumber,serverNumber);
            logNumber(currentNumber,serverNumber);
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }


    private Integer newNumber(Integer currentNumber, Integer newNumber) {
        return currentNumber + 1 + Optional.ofNullable(newNumber).orElse(0);
    }

    private void logNumber(Integer currentNumber, Integer newNumber) {
        if (newNumber != null)
            log.info("New number {}",newNumber);
        log.info("Current number {}",currentNumber);
    }
}
