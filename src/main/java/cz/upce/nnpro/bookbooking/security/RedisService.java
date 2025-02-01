package cz.upce.nnpro.bookbooking.security;


import cz.upce.nnpro.bookbooking.configuration.RedissonConfiguration;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisService {
    private final RedissonClient redissonClient;

    public RLock getLock(String key) {
        return redissonClient.getLock(key);
    }

    public boolean tryLock(RLock lock, long timeout, TimeUnit unit) {
        try {
            return lock.tryLock(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseLock(RLock lock) {
        if (lock.isHeldByCurrentThread()) lock.unlock();
    }
}
