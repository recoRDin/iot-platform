package com.iot.core.log.filter;

import com.iot.core.log.constant.LogConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.UUID;

public class TraceIdFilter extends OncePerRequestFilter {

    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{8,64}$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String traceId = resolveTraceId(request);

        MDC.put(LogConstants.TRACE_ID, traceId);
        response.setHeader(LogConstants.TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(LogConstants.TRACE_ID);
        }
    }

    //优先使用上游传入的id，否则生成新的id
    private String resolveTraceId(HttpServletRequest request) {

        String traceId = request.getHeader(LogConstants.TRACE_ID_HEADER);

        if (traceId != null && TRACE_ID_PATTERN.matcher(traceId).matches()) {
            return traceId;
        }

        return UUID.randomUUID()
                .toString()
                .replace("-", "");
    }

}
