package ru.otus.listener.homework;

import ru.otus.listener.Listener;
import ru.otus.model.Message;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;

public class HistoryListener implements Listener, HistoryReader {

    private final Deque<Message> history = new ArrayDeque<>();

    @Override
    public void onUpdated(Message msg) {
        history.push(msg.toBuilder().build());
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return history.stream().filter(msg->msg.getId() == id).findFirst();
    }
}
