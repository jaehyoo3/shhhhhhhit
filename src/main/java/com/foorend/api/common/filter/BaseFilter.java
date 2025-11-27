package com.foorend.api.common.filter;

import com.foorend.api.common.util.CommonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 요청 본문을 읽어 requestBody로 request에 저장하는 필터
 * - Request Body를 여러 번 읽을 수 있도록 RequestWrapper로 래핑
 */
@Slf4j
@Component
@Order(1)
public class BaseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("BaseFilter 초기화");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        if (!(servletRequest instanceof HttpServletRequest httpRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Content-Type이 JSON인 경우만 래핑 (파일 업로드 등 제외)
        String contentType = httpRequest.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            var requestWrapper = new RequestWrapper(httpRequest);
            String requestBody = CommonUtil.getRequestBody(requestWrapper);
            requestWrapper.setAttribute("requestBody", requestBody);

            log.debug("Request Body 캐싱 완료 - URI: {}", httpRequest.getRequestURI());

            filterChain.doFilter(requestWrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        log.info("BaseFilter 종료");
    }
}
