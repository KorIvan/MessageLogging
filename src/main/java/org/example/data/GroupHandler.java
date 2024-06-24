package org.example.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GroupHandler implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupHandler.class);

    public synchronized int addClient(String name, Map<String, AtomicInteger> map, AtomicInteger counter) {
        if (!map.containsKey(name)) {
            int n = counter.getAndIncrement();
            map.put(name, new AtomicInteger(n));
            LOGGER.info("Client [{}] is added with id [{}]", name, n);
            return n;
        } else {
            return map.get(name).get();
        }
    }

    public synchronized void removeClient(String name, Map<String, AtomicInteger> map, AtomicInteger counter) {
        if (!map.containsKey(name)) {
            LOGGER.info("Client [{}] is not found", name);
        } else {
            int n = map.get(name).get();
            if (n + 1 == counter.get()) {
                map.remove(name);
                LOGGER.info("Client [{}] is removed", name);
            } else {
                LOGGER.info("Clients ready to reconfigurations {}", map);
                map.remove(name);
                map.entrySet().stream().filter(e -> e.getValue().get() > n).forEach(e -> e.getValue().decrementAndGet());
                LOGGER.info("Clients are reconfigured {}", map);

            }
        }
    }
}
