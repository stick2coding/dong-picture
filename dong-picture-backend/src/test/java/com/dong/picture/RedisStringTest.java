package com.dong.picture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RedisStringTest {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedisStringOperation() {
        // 获取操作对象
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();

        // 设置key和value
        String key = "test:string";
        String value = "testValue";

        // 测试新增
        valueOperations.set(key, value);
        String storedValue = valueOperations.get(key);
        System.out.println("存储前的数据：" + value);
        System.out.println("存储后的数据：" + storedValue);
        assertEquals(value, storedValue, "存储后的数据和预期存储的数据不一致");

        // 修改
        String updateValue = "updatedValue";
        valueOperations.set(key, updateValue);
        storedValue = valueOperations.get(key);
        System.out.println("修改后的数据：" + storedValue);
        assertEquals(updateValue, storedValue, "修改后的数据和预期存储的数据不一致");

        // 查询
        storedValue = valueOperations.get(key);
        System.out.println("查询后的数据：" + storedValue);

        // 删除
        stringRedisTemplate.delete(key);
        storedValue = valueOperations.get(key);
        System.out.println("删除后的数据：" + storedValue);
    }

}
