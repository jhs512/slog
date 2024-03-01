package com.ll.rsv.global.rqCache;

import lombok.experimental.Delegate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.Map;

@RequestScope
@Component
public class RqCache {
    @Delegate
    private final Map<String, Object> cache = new HashMap<>();

    public <T> T get(String key) {
        return (T) cache.get(key);
    }
}
