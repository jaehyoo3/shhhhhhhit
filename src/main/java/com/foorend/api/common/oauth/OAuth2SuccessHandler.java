package com.foorend.api.common.oauth;

import com.foorend.api.common.jwt.JwtTokenProvider;
import com.foorend.api.common.repository.GenericDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 성공 핸들러
 * - JWT 토큰 생성 및 프론트엔드로 리다이렉트
 */
@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final GenericDAO genericDAO;

    @Value("${oauth2.redirect-uri:http://localhost:3000/oauth/callback}")
    private String redirectUri;

    @Value("${oauth2.redirect-uri-new-user:${oauth2.redirect-uri}}")
    private String redirectUriNewUser;

    public OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider,
                                @Qualifier("mainDB") GenericDAO genericDAO) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.genericDAO = genericDAO;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // CustomOAuth2UserService에서 추가한 정보 가져오기
        Long userId = oAuth2User.getAttribute("userId");
        String email = oAuth2User.getAttribute("email");
        Boolean isNewUser = oAuth2User.getAttribute("isNewUser");

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userId, email);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId, email);

        // Refresh Token DB 저장
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("refreshToken", refreshToken);
        genericDAO.update("user.updateRefreshToken", params);

        // 신규 회원 여부에 따라 리다이렉트 URL 분기 (둘 다 properties에서 관리되어 안전)
        String targetRedirectUri = (isNewUser != null && isNewUser) ? redirectUriNewUser : redirectUri;

        // 프론트엔드로 리다이렉트 (토큰을 fragment로 전달 - 서버 로그에 남지 않음)
        String targetUrl = UriComponentsBuilder.fromUriString(targetRedirectUri)
                .fragment("accessToken=" + accessToken + "&refreshToken=" + refreshToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
