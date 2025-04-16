package com.yeoboya.lunch.config.redis;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    public RedisUtil(StringRedisTemplate stringRedisTemplate, RedisConnectionFactory redisConnectionFactory) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    // string (opsForValue)
    public void setStringOps(String key, String value, long ttl, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    public String getStringOps(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }

    // list (opsForList)
    public void setListOps(String key, List<String> values){
        stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    public List<String> getListOps(String key){
        Long len = stringRedisTemplate.opsForList().size(key);
        return len == 0 ? new ArrayList<>() : stringRedisTemplate.opsForList().range(key, 0, len-1);
    }

    // hash (opsForHash)
    public void setHashOps(String key, HashMap<String, String> value){
        stringRedisTemplate.opsForHash().putAll(key, value);
    }

    public String getHashOps(String key, String hashKey){
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey) ? (String) stringRedisTemplate.opsForHash().get(key, hashKey) : "";
    }

    // set (opsForSet)
    public void setSetOps(String key, String... values){
        stringRedisTemplate.opsForSet().add(key, values);
    }

    public Set<String> getSetOps(String key){
        return stringRedisTemplate.opsForSet().members(key);
    }

    // sorted set (opsForZSet)
    public void setSortedSetOps(String key, List<Struct.SortedSet> values){
        for(Struct.SortedSet v : values){
            stringRedisTemplate.opsForZSet().add(key, v.getValue(), v.getScore());
        }
    }

    public Set<?> getSortedSetOps(String key){
        Long len = stringRedisTemplate.opsForZSet().size(key);
        return len == 0 ? new HashSet<String>() : stringRedisTemplate.opsForZSet().range(key, 0, len-1);
    }

    public List<String> getAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(2).match(key).build();

        List<String> values = new ArrayList<>();
        Cursor<byte[]> cursor = redisConnection.scan(options);

        while (cursor.hasNext()){
            String val = new String(cursor.next());
            values.add(val);
        }

        return values;
    }

    public void delAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(2).match(key).build();

        List<String> values = new ArrayList<>();
        Cursor<byte[]> cursor = redisConnection.scan(options);

        while (cursor.hasNext()){
            String val = new String(cursor.next());
            values.add(val);
        }

         stringRedisTemplate.delete(values);

    }

    public List<String> getHashAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(10).match("*").build();
        Cursor<byte[]> outCursor = redisConnection.scan(options);

        List<String> values = new ArrayList<>();

        // out key
        while (outCursor.hasNext()){
            String outKey = new String(outCursor.next());

            ScanOptions hashOptions = ScanOptions.scanOptions().count(10).match(key).build();
            Cursor<Map.Entry<byte[],byte[]>> inCursor = redisConnection.hScan(outKey.getBytes(), hashOptions);

            // in key
            while (inCursor.hasNext()){
                Map.Entry<byte[], byte[]> val = inCursor.next();
                values.add(outKey+"|"+new String(val.getKey())+"|"+new String(val.getValue()));
            }
        }

        return values;
    }

    public long delHashAsterOps(String key){
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        ScanOptions options = ScanOptions.scanOptions().count(10).match("*").build();
        Cursor<byte[]> outCursor = redisConnection.scan(options);

        long resultCode = 0L;

        // out key
        while (outCursor.hasNext()){
            String outKey = new String(outCursor.next());

            ScanOptions hashOptions = ScanOptions.scanOptions().count(10).match(key).build();
            Cursor<Map.Entry<byte[],byte[]>> inCursor = redisConnection.hScan(outKey.getBytes(), hashOptions);

            // in key
            while (inCursor.hasNext()){
                Map.Entry<byte[], byte[]> val = inCursor.next();
                Long delCnt = stringRedisTemplate.opsForHash().delete(outKey, new String(val.getKey()));
                resultCode += delCnt;
            }
        }

        return resultCode;
    }

    public Set<ZSetOperations.TypedTuple<String>> getZSetWithScore(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    // 해시태그 점수 추가
    public void incrementHashtagScore(String tag) {
        String key = "popular:hashtag"; // 인기 해시태그 랭킹용 키
        stringRedisTemplate.opsForZSet().incrementScore(key, tag, 1);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

}
