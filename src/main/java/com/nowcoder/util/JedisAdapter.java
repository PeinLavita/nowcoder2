package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

/**
 * Created by yby on 2016/7/19.
 */

@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private  JedisPool pool = null;

    private  static int index = 0;

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object obj){
        set(key, JSON.toJSONString(obj));
    }
    public <T> T getObject(String key, Class<T> clazz){
        String value = get(key);
        if (value != null){
            return JSON.parseObject(value, clazz);
        }
        return null;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    public void set(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.set(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.get(key);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return null;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return false;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }



    private Jedis getJedis(){
        return pool.getResource();
    }



    private static void print(Object obj) {
        index++;
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

//    public static void main(String[] args){
//        Jedis jedis = new Jedis();
//        jedis.flushAll();
//
//        jedis.set("hello", "world");
//        print(jedis.get("hello"));
//
//       // jedis.setex("hello2",15,  "worldex");
//       // print(jedis.get("hello2"));
//
//        //
//        jedis.set("pv", "100");
//        print(jedis.get("pv"));
//        jedis.incr("pv");
//        print(jedis.get("pv"));
//        jedis.incrBy("pv", 3);
//        print(jedis.get("pv"));
//
//
//        //list
//        String listName = "list";
//        jedis.del("list");
//        for (int i=0; i<10; ++i){
//            jedis.lpush("list", "test"+String.valueOf(i));
//        }
//        print(jedis.lrange("list", 0,7));
//        print(jedis.llen("list"));
//        print(jedis.lpop("list"));
//        print(jedis.llen("list"));
//        print(jedis.lrange("list", 0,7));
//        print(jedis.lindex(listName, 3));
//        print(jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "test8", "insert After"));
//        print(jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "test8", "insert before"));
//        print(jedis.lrange(listName, 0,123));
//
//        //hash
//        String userKey = "userAAA";
//        jedis.hset(userKey, "name", "jim");
//        jedis.hset(userKey, "age", "13");
//        jedis.hset(userKey, "sex", "male");
//        jedis.hset(userKey, "phone", "123456");
//        print(jedis.hget(userKey, "name"));
//        print(jedis.hgetAll(userKey));
//        print(jedis.hexists(userKey, "name"));
//        print(jedis.hexists(userKey, "name2"));
//        print(jedis.hkeys(userKey));
//        print(jedis.hvals(userKey));
//        print(jedis.hsetnx(userKey, "school", "zjjj"));
//        print(jedis.hsetnx(userKey, "school", "zj23423jj"));
//        print(jedis.hgetAll(userKey));
//
//        //set
//        String likeKey1 = "newsLike1";
//        String likeKey2 = "newsLike2";
//        for (int i=0; i<10; ++i){
//            jedis.sadd(likeKey1, String.valueOf(i));
//            jedis.sadd(likeKey2, String.valueOf(i*2));
//        }
//        print(jedis.smembers(likeKey1));
//        print(jedis.smembers(likeKey2));
//        print(jedis.sinter(likeKey1, likeKey2));
//        print(jedis.sunion(likeKey1, likeKey2));
//        print(jedis.sdiff(likeKey1, likeKey2));
//        print(jedis.sismember(likeKey1, "5"));
//        print(jedis.srem(likeKey1, "5"));
//        print(jedis.sismember(likeKey1, "5"));
//        print(jedis.scard(likeKey1));
//        jedis.smove(likeKey1, likeKey2, "2");
//        print(jedis.scard(likeKey1));
//
//        //排序
//        print("**************************");
//        String rankKey = "rankKey";
//        jedis.zadd(rankKey, 15, "Jim");
//        jedis.zadd(rankKey, 60, "Ben");
//        jedis.zadd(rankKey, 90, "Lee");
//        jedis.zadd(rankKey, 75, "Lucy");
//        jedis.zadd(rankKey, 80, "Mei");
//        print(jedis.zcard(rankKey));
//        print(jedis.zcount(rankKey, 61, 100));
//        print(jedis.zrange(rankKey, 1, 3));
//        print(jedis.zrevrange(rankKey, 1, 3));
//        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "0", "100")){
//            print(tuple.getElement() + ":" + tuple.getScore());
//        }
//        print(jedis.zrank(rankKey, "Ben"));
//        print(jedis.zrevrank(rankKey, "Ben"));
//
//
////        JedisPool pool = new JedisPool();
////        for (int i=0; i<100; ++i){
////            Jedis j = pool.getResource();
////            j.get("a");
////            System.out.print(String.valueOf(i));
////            j.close();
////        }
//
//
//    }
}
