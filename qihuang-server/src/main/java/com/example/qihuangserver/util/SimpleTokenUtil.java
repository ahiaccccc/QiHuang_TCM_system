package com.example.qihuangserver.util;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTokenUtil {
    // 内存存储 token 与用户ID的映射（生产环境请用Redis）
    private static final ConcurrentHashMap<String, Long> tokenStore = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRE_MS = 24 * 60 * 60 * 1000; // 24小时
    private static final ConcurrentHashMap<String, Long> tokenCreateTime = new ConcurrentHashMap<>();

    // 生成简单Token (UUID + Base64)，有效期检查
    public static String generateToken(long userId) {
        String token = Base64.getEncoder()
                .encodeToString((userId + "|" + UUID.randomUUID()).getBytes());
        tokenStore.put(token, userId);
        tokenCreateTime.put(token, System.currentTimeMillis());
        return token;
    }

    // 验证Token
    public static long validateToken(String token) {
        Long createTime = tokenCreateTime.get(token);
        if (createTime == null || (System.currentTimeMillis() - createTime) > TOKEN_EXPIRE_MS) {
            removeToken(token);
            return 0L;
        }
        return tokenStore.getOrDefault(token, 0L);
    }

    // 移除Token
    public static void removeToken(String token) {
        tokenStore.remove(token);
    }
}
