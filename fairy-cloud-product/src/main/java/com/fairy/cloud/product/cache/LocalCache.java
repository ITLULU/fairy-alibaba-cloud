package com.fairy.cloud.product.cache;

import com.fairy.cloud.mbg.model.PmsProduct;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


/**
 * 缓存管理工具，采用LRU淘汰策略
 */
@Slf4j
@Component
public class LocalCache {

    private Cache<String, PmsProduct> localCache;

    @PostConstruct
    private void init() {
        localCache = CacheBuilder.newBuilder()
                //设置本地缓存容器的初始容量
                .initialCapacity(10)
                //设置本地缓存的最大容量
                .maximumSize(500)
                //设置写缓存后多少秒过期
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }

    public synchronized void setLocalCache(String key, PmsProduct object) {
        localCache.put(key, object);
    }

    public synchronized PmsProduct get(String key) {
        return localCache.getIfPresent(key);
    }

}
