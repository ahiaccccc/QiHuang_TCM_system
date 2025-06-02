package com.example.qihuangserver.util;

import com.example.qihuangserver.exception.myException;
import com.example.qihuangserver.model.User;
import io.jsonwebtoken.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class JWTUtils {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);
    // 密钥
    private static String key = "qihuangServerUserLoginCreateTokenKeyAndValidateTokenKey";
    // 过期时间
    private static long failureTime = 3600000;

    /**
     * 设置认证token
     * @param user
     * @return
     */
    public static String createJWT(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        return Jwts.builder()
                .setClaims(claims)
                .setId(String.valueOf(user.getUserId()))
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + failureTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }


    /**
     * 解析token
     * @param token
     * @return
     */
    public static Claims parseJWT(String token) throws myException {
        if(!validateJwtToken(token)){
            throw new myException("token验证失败");
        }
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取用户名
     * @param token
     * @return
     */
    public static String getUserNameFromJwtToken(String token) throws myException {
        if (!validateJwtToken(token)) {
            throw new myException("token验证失败");
        }
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 获取用户id
     * @param token
     * @return
     */
    public static Long getUserIdFromJwtToken(String token) throws myException {
        if (!validateJwtToken(token)) {
            throw new myException("token验证失败");
        }
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getId());
    }

    /**
     * 获取用户邮箱
     * @param token
     * @return
     */
    public static String getEmailFromJwtToken(String token) throws myException {
        if (!validateJwtToken(token)) {
            throw new myException("token验证失败");
        }
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("email", String.class);
    }

    /**
     * 验证token
     * @param authToken
     * @return
     */
    public static boolean validateJwtToken(String authToken) {
        try {
            authToken = authToken.trim();
            if (authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            System.out.println("token验证失败1");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            System.out.println("token验证失败2");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            System.out.println("token验证失败3");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            System.out.println("token验证失败4");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            System.out.println("token验证失败5");
        }

        return false;
    }
}
