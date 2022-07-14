package com.likc.seckill.controller;

import com.likc.seckill.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author likc
 * @date 2022/6/9
 * @description
 */
@RestController
public class SeckillController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/seckill")
    public String seckill() {
        int userId = new Random().nextInt(1000);

        //KEYS[1] 用来表示在redis 中用作键值的参数占位，主要用來传递在redis 中用作keyz值的参数。
        //ARGV[1] 用来表示在redis 中用作参数的占位，主要用来传递在redis中用做 value值的参数。
        String script = "if tonumber(redis.call('get', KEYS[1])) > 0 then redis.call('decr', KEYS[1]) redis.call('lpush', KEYS[2], ARGV[1]) return 1 else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        ArrayList<String> keyList = new ArrayList<>();
        keyList.add("goods");
        keyList.add("user");

        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        ListOperations<String, Object> opsForList = redisTemplate.opsForList();

        Long result = (Long) redisTemplate.execute(redisScript, keyList, String.valueOf(userId));

        if (result == 1) {
            System.out.println(opsForValue.get("goods")+""+opsForList.range("user", 0 , -1));
            return "success";
        } else {
            return "fail";
        }
    }

    @GetMapping("/rest")
    public Boolean rest() {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        ListOperations<String, Object> opsForList = redisTemplate.opsForList();
        opsForValue.set("goods", 10);
        for (int i = 0; i < 100; i++) {
            opsForList.rightPop("user");
        }

        System.out.println(opsForValue.get("goods"));
        System.out.println(opsForList.range("user", 0 , -1));
        return true;
    }
}
