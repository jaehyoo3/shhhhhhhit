package com.foorend.api.common.jwt;

import com.foorend.api.common.constants.JwtConstants;
import com.foorend.api.common.exception.GlobalException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * - 모든 요청에서 JWT 토큰을 검증하고 인증 정보를 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 토큰 추출
            String token = jwtTokenProvider.resolveToken(request);

            // 2. 토큰 검증 및 인증 처리
            if (token != null && jwtTokenProvider.validateTokenSilently(token)) {
                // 3. Access Token인지 확인
                if (jwtTokenProvider.isAccessToken(token)) {
                    // 4. Authentication 객체 생성
                    Authentication auth = jwtTokenProvider.getAuthentication(token);
                    
                    if (auth instanceof UsernamePasswordAuthenticationToken authToken) {
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    }
                    
                    // 5. SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    
                    log.debug("인증 성공 - userSeq: {}", jwtTokenProvider.getUserSeq(token));
                } else {
                    log.debug("Access Token이 아닙니다. tokenType: {}", jwtTokenProvider.getTokenType(token));
                }
            }
        } catch (GlobalException e) {
            log.debug("JWT 인증 실패: {}", e.getMessage());
            // 인증 실패 시 SecurityContext 비움 (EntryPoint에서 처리)
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 필터를 적용하지 않을 경로 설정
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 인증이 필요 없는 경로들
        return path.startsWith("/api/auth/") ||
               path.startsWith("/oauth2/") ||
               path.startsWith("/login/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/health") ||
               path.equals("/");
    }
}
