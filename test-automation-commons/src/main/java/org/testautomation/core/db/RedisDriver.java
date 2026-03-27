package org.testautomation.core.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis access wrapper supporting both JSON (via RedisTemplate) and
 * MessagePack (via raw Jedis) serialisation formats.
 *
 * <p>Includes a lightweight in-process cache to reduce Redis round-trips
 * during a single scenario run.</p>
 */
@Component
@Slf4j
public class RedisDriver implements InitializingBean {

    private static final ObjectMapper JSON_MAPPER    = new ObjectMapper();
    private static final ObjectMapper MSGPACK_MAPPER = new ObjectMapper(new MessagePackFactory());
    private static final int          MAX_CACHE      = 1000;

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    @Value("${spring.data.redis.host}")
    @Getter private String host;

    @Value("${spring.data.redis.port}")
    @Getter private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database:0}")
    @Getter private int database;

    private RedisTemplate<String, String> template;
    private JedisPool jedisPool;

    @Autowired
    public void setTemplate(RedisTemplate<String, String> template) {
        this.template = template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration cfg = new RedisStandaloneConfiguration(host, port);
        cfg.setPassword(password);
        cfg.setDatabase(database);
        JedisConnectionFactory factory = new JedisConnectionFactory(cfg);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> t = new RedisTemplate<>();
        t.setConnectionFactory(factory);
        StringRedisSerializer ser = new StringRedisSerializer();
        t.setKeySerializer(ser);
        t.setValueSerializer(ser);
        t.setHashKeySerializer(ser);
        t.setHashValueSerializer(ser);
        t.afterPropertiesSet();
        return t;
    }

    @Override
    public void afterPropertiesSet() {
        JedisPoolConfig cfg = poolConfig();
        this.jedisPool = new JedisPool(cfg, host, port, 2000, password, database);
    }

    // -------------------------------------------------------------------------

    public void set(String key, Object value, Duration ttl) {
        try {
            String str = value instanceof String ? (String) value : JSON_MAPPER.writeValueAsString(value);
            template.opsForValue().set(key, str, ttl);
            putCache(key, value);
        } catch (Exception e) {
            log.error("Redis set failed for key {}: {}", key, e.getMessage());
        }
    }

    public <T> T get(String key, Class<T> type) {
        Object cached = cache.get(key);
        if (type.isInstance(cached)) return type.cast(cached);

        try {
            String raw = template.opsForValue().get(key);
            if (raw == null) return null;
            T result = type.equals(String.class) ? type.cast(raw) : JSON_MAPPER.readValue(raw, type);
            putCache(key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis get failed for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    /** Reads a MessagePack-encoded value directly via Jedis. */
    public <T> T getMsgPack(String key, Class<T> type) {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes == null) return null;
            T result = MSGPACK_MAPPER.readValue(bytes, type);
            putCache(key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis getMsgPack failed for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    public void delete(String key) {
        template.delete(key);
        cache.remove(key);
    }

    // -------------------------------------------------------------------------

    private void putCache(String key, Object value) {
        if (cache.size() >= MAX_CACHE) cache.clear();
        cache.put(key, value);
    }

    private JedisPoolConfig poolConfig() {
        JedisPoolConfig cfg = new JedisPoolConfig();
        cfg.setMaxTotal(8);
        cfg.setMaxIdle(8);
        cfg.setMinIdle(0);
        cfg.setTestOnBorrow(true);
        cfg.setMaxWait(Duration.ofSeconds(2));
        return cfg;
    }
}
