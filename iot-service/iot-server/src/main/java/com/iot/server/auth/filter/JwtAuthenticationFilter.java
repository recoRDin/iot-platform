package com.iot.server.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.core.log.exception.ServiceException;
import com.iot.core.tenant.context.TenantContextHolder;
import com.iot.core.tool.api.R;
import com.iot.core.tool.api.ResultCode;
import com.iot.server.auth.context.AuthContextHolder;
import com.iot.server.auth.token.JwtTokenService;
import com.iot.server.auth.token.TokenPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/auth/login",
            "/error"
    );
    private static final Set<String> DEV_PUBLIC_PATHS = Set.of(
            "/system/tenant/create",
            "/system/role/create",
            "/system/user/create"
    );

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;
    private final boolean devMode;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                   ObjectMapper objectMapper,
                                   boolean devMode) {
        this.jwtTokenService = jwtTokenService;
        this.objectMapper = objectMapper;
        this.devMode = devMode;
    }

    //判断是否过滤
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return PUBLIC_PATHS.contains(path) || (devMode && DEV_PUBLIC_PATHS.contains(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        //清理上下文
        AuthContextHolder.clear();
        TenantContextHolder.clear();

        TokenPayload payload;
        try {

            //校验token
            payload = jwtTokenService.parseAccessToken(request.getHeader(AUTHORIZATION_HEADER));
            validatePayload(payload);
        } catch (ServiceException exception) {
            writeUnauthorized(response, exception.getMessage());
            return;
        }

        AuthContextHolder.set(payload);
        TenantContextHolder.setTenantId(payload.getTenantId());
        try {
            filterChain.doFilter(request, response);
        } finally {
            AuthContextHolder.clear();
            TenantContextHolder.clear();
        }
    }

    private void validatePayload(TokenPayload payload) {
        if (payload.getUserId() == null || payload.getUserId().isBlank()
                || payload.getTenantId() == null || payload.getTenantId().isBlank()
                || payload.getAccount() == null || payload.getAccount().isBlank()) {
            throw new ServiceException("Token无效");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getWriter(),
                R.fail(ResultCode.UNAUTHORIZED, message));
    }
}
