package org.example.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupData implements Serializable {
    @Serial
    private static final long serialVersionUID = 123431L;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

    public AtomicInteger getCounter() {
        return counter;
    }

    public Map<String, AtomicInteger> getMap() {
        return map;
    }
}
