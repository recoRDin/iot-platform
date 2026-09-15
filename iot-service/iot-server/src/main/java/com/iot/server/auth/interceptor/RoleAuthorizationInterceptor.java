package com.iot.server.auth.interceptor;

import com.iot.core.log.exception.ServiceException;
import com.iot.core.tool.api.ResultCode;
import com.iot.server.auth.annotation.RequireRole;
import com.iot.server.auth.context.AuthUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = findRequireRole(handlerMethod);
        if (requireRole == null) {
            return true;
        }

        if (!AuthUtil.isAuthenticated()) {
            throw new ServiceException(ResultCode.UNAUTHORIZED);
        }

        List<String> requiredRoles = Arrays.stream(requireRole.value())
                .filter(role -> role != null && !role.isBlank())
                .map(String::strip)
                .distinct()
                .toList();

        if (requiredRoles.isEmpty()) {
            throw new IllegalStateException("@RequireRole 至少需要配置一个角色编码");
        }

        boolean allowed = requiredRoles.stream().anyMatch(AuthUtil::hasRole);
        if (!allowed) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        return true;
    }

    private RequireRole findRequireRole(HandlerMethod handlerMethod) {
        RequireRole methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(
                handlerMethod.getMethod(), RequireRole.class);

        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        return AnnotatedElementUtils.findMergedAnnotation(
                handlerMethod.getBeanType(), RequireRole.class);
    }
}
