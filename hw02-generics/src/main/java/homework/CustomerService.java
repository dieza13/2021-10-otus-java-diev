package homework;


import java.util.*;

public class CustomerService {

    //важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны
    private final TreeMap<homework.Customer, String> map = new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<homework.Customer, String> getSmallest() {
        //Возможно, чтобы реализовать этот метод, потребуется посмотреть как Map.Entry сделан в jdk
        return Optional.ofNullable(map.firstEntry()).map(e -> e == null ? e : new AbstractMap.SimpleImmutableEntry(new Customer(e.getKey().getId(), e.getKey().getName(), e.getKey().getScores()), e.getValue())).orElse(null);
    }

    public Map.Entry<homework.Customer, String> getNext(homework.Customer customer) {
        return Optional.ofNullable(map.higherEntry(customer)).map(e -> e == null ? e : new AbstractMap.SimpleImmutableEntry(new Customer(e.getKey().getId(), e.getKey().getName(), e.getKey().getScores()), e.getValue())).orElse( null);
    }

    public void add(homework.Customer customer, String data) {
        map.put(customer, data);
    }
}
