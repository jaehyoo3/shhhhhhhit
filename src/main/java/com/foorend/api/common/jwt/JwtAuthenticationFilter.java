package com.foorend.api.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foorend.api.common.constants.ErrorCode;
import com.foorend.api.common.constants.JwtConstants;
import com.foorend.api.common.domain.BaseRes;
import com.foorend.api.common.exception.GlobalException;
import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.user.domain.User;
import com.foorend.api.user.domain.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 인증 필터
 * - 모든 요청에서 JWT 토큰을 검증하고 인증 정보를 설정
 * - 탈퇴/정지 사용자 접근 차단
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final GenericDAO genericDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, 
                                   @Qualifier("mainDB") GenericDAO genericDAO) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.genericDAO = genericDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 토큰 추출
            String token = jwtTokenProvider.resolveToken(request);

            // 2. 토큰이 있으면 검증 진행
            if (token != null) {
                // 3. 토큰 유효성 검증 (예외 발생 시 catch에서 처리)
                jwtTokenProvider.validateToken(token);

                // 4. Access Token인지 확인
                if (!jwtTokenProvider.isAccessToken(token)) {
                    throw new GlobalException(ErrorCode.AUTH_INVALID_JWT, "Access Token이 아닙니다.");
                }

                // 5. 사용자 상태 확인 (탈퇴/정지 체크)
                Long userId = jwtTokenProvider.getUserSeq(token);
                checkUserStatus(userId);

                // 6. Authentication 객체 생성
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                
                if (auth instanceof UsernamePasswordAuthenticationToken authToken) {
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                }
                
                // 7. SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);

        } catch (GlobalException e) {
            log.error("JWT 인증 실패: {} (code: {})", e.getMessage(), e.getCode());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, e.getErrorCode());
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류 발생: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, ErrorCode.AUTH_INVALID_JWT);
        }
    }

    /**
     * 사용자 상태 확인 (탈퇴/정지 시 예외 발생)
     */
    @SuppressWarnings("unchecked")
    private void checkUserStatus(Long userId) {
        if (userId == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND, "사용자 ID가 없습니다.");
        }

        User user = (User) genericDAO.selectOne("user.findByUserId", userId);
        
        if (user == null) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        if (user.getUserStatus() == UserStatus.WITHDRAWAL) {
            throw new GlobalException(ErrorCode.AUTH_FORBIDDEN, "탈퇴한 사용자입니다.");
        }

        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            throw new GlobalException(ErrorCode.AUTH_FORBIDDEN, "정지된 사용자입니다.");
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new GlobalException(ErrorCode.AUTH_FORBIDDEN, "비활성 사용자입니다.");
        }
    }

    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        BaseRes errorResponse = new BaseRes();
        errorResponse.setBaseResData(errorCode.getCode(), errorCode.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
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
