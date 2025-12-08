package com.ordertogether.paymentservice.payment.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
public class LogFilter extends OncePerRequestFilter {

	private static final int TRACE_ID_LENGTH = 8;
	private static final String ACTUATOR_PREFIX = "/actuator";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MDC.put("traceId", generateTraceId());
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 0);
		filterChain.doFilter(wrappedRequest, response);

		String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        log.info(createHttpRequestBodyLogMessage(requestBody));
		MDC.clear();
	}

	private static String generateTraceId() {
		return UUID.randomUUID().toString().substring(0, TRACE_ID_LENGTH);
	}

	private static String createHttpRequestBodyLogMessage(String requestBody) {
		if (requestBody == null || requestBody.isBlank()) {
			return "[REQ-BODY] (empty)";
		}

		String compactBody = requestBody.replaceAll("\\s+", " ");
		int length = compactBody.getBytes(StandardCharsets.UTF_8).length;

		return String.format("""
        
        [REQ-BODY]
        ├─ LENGTH : %d bytes
        └─ BODY   : %s
        """, length, compactBody);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri != null && uri.startsWith(ACTUATOR_PREFIX);
	}
}
