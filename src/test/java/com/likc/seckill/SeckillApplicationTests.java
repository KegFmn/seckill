package com.likc.seckill;

import com.likc.seckill.util.RedisUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    private RedisUtils redisUtils;

    @Test
    void contextLoads() {
    }

    @Test
    void seckill() {
        ExecutorService pool = Executors.newFixedThreadPool(20);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8081/seckill")
                .get()
                .build();

        for (int i = 0; i < 100; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    Call call = client.newCall(request);
                    try {
                        Response response = call.execute();
                        if (response.body() != null) {
                            String string = response.body().string();
                            System.out.println(string);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
