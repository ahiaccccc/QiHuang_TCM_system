package com.example.qihuangserver.interceptor;

import com.example.qihuangserver.util.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Set;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    // 定义无需认证的白名单路径
    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/api/user/login",
            "/api/user/register",
            "/api/user/email-reset-password",
            "/avatars/",       // 放行头像目录
            "/static/",        // 放行静态资源
            "/error"           // 放行错误端点
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 1. 放行所有OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // 2. 放行白名单路径（支持前缀匹配）
        if (ALLOWED_PATHS.stream().anyMatch(uri::startsWith)) {
            return true;
        }

        // 3. 验证Token（支持Bearer格式）
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (JWTUtils.validateJwtToken(token)) {
                    return true;
                }
            } catch (Exception e) {
                sendError(response, 401, "Token验证失败: " + e.getMessage());
                return false;
            }
        }

        // 4. 认证失败响应
        sendError(response, 401, "请提供有效的Authorization头");
        return false;
    }

    // 统一错误响应方法
    private void sendError(HttpServletResponse response, int code, String message) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(code);
            response.getWriter().write(
                    String.format("{\"code\":%d,\"message\":\"%s\"}", code, message)
            );
        } catch (IOException e) {
            System.out.println("写入响应失败: {}"+ e.getMessage());
        }
    }
}