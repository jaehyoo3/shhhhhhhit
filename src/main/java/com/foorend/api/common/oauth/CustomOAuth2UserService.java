package com.foorend.api.common.oauth;

import com.foorend.api.common.repository.GenericDAO;
import com.foorend.api.user.domain.User;
import com.foorend.api.user.domain.UserStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 사용자 정보 처리 서비스
 * - Google OAuth2 로그인 시 사용자 정보를 DB에 저장/업데이트
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final GenericDAO genericDAO;

    public CustomOAuth2UserService(@Qualifier("mainDB") GenericDAO genericDAO) {
        this.genericDAO = genericDAO;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Google OAuth2 사용자 정보 추출
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        // 사용자 저장 또는 업데이트
        User user = saveOrUpdate(googleId, email, name, picture);

        // attributes에 userId 추가 (SuccessHandler에서 사용)
        Map<String, Object> modifiedAttributes = new HashMap<>(attributes);
        modifiedAttributes.put("userId", user.getUserId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                modifiedAttributes,
                "sub"
        );
    }

    /**
     * 사용자 저장 또는 업데이트
     */
    @SuppressWarnings("unchecked")
    private User saveOrUpdate(String googleId, String email, String name, String picture) {
        // 이메일로 기존 사용자 조회
        User existingUser = (User) genericDAO.selectOne("user.findByEmail", email);

        if (existingUser != null) {
            // 기존 사용자 - 프로필 업데이트
            Map<String, Object> params = new HashMap<>();
            params.put("userId", existingUser.getUserId());
            params.put("name", name);
            params.put("profileImageUrl", picture);

            genericDAO.update("user.updateUserProfile", params);

            // 업데이트된 정보 반영
            existingUser.setName(name);
            existingUser.setProfileImageUrl(picture);
            return existingUser;
        } else {
            // 신규 사용자 등록
            User newUser = User.builder()
                    .googleId(googleId)
                    .email(email)
                    .name(name)
                    .profileImageUrl(picture)
                    .userStatus(UserStatus.ACTIVE)
                    .build();

            genericDAO.insert("user.insertUser", newUser);

            return newUser;
        }
    }
}
