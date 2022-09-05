package ru.jrmbot.store;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class HashMapStore implements BaseStore{
    private Map<LocalDate, List<String>> localStore = new HashMap<>();

    @Override
    public void save(LocalDate key, String deal) {
        if (localStore.containsKey(key)) {
            ArrayList<String> alreadyExistDeals = new ArrayList<>(localStore.get(key));
            alreadyExistDeals.add(deal);
            localStore.put(key, alreadyExistDeals);
        } else {
            localStore.put(key, Arrays.asList(deal));
        }
    }

    @Override
    public List<String> selectAll(LocalDate key) {
        return localStore.get(key);
    }
}













