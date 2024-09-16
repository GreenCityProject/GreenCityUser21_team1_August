package greencity.security.service;

import greencity.constant.AppConstant;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CustomOAuth2UserServiceImpl extends OidcUserService implements CustomOAuth2UserService {

    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final JwtTool jwtTool;
    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        return processOidcUser(oidcUser);
    }

    @Transactional
    @Override
    public OidcUser processOidcUser(OidcUser oidcUser) {
        Map<String, Object> attributes = oidcUser.getAttributes();

        String email = (String) attributes.get("email");
        if (email == null || email.isEmpty()) {
            log.error("Email is missing in OAuth2 user attributes");
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        log.info("Processing OAuth2 login for email: {}", email);

        Optional<User> existingUser = userRepo.findByEmail(email);
        if (existingUser.isPresent()) {
            log.info("User with email {} already exists", email);
            return oidcUser;
        }

        String name = (String) attributes.get("name");
        if (name == null || name.isEmpty()) {
            log.warn("Name is missing in OAuth2 user attributes for email: {}", email);
            name = "Unknown";
        }

        String refreshTokenKey = attributes.get("refresh_token_key") != null
                ? attributes.get("refresh_token_key").toString()
                : generateDefaultRefreshTokenKey();

        createUser(name, email, refreshTokenKey);
        log.info("Created new user with email: {}", email);

        return oidcUser;

    }

    private User createUser(String name, String email , String refreshTokenKey) {
        User user = User.builder()
                .name(name)
                .firstName(name)
                .email(email)
                .dateOfRegistration(LocalDateTime.now())
                .role(Role.ROLE_USER)
                .refreshTokenKey(refreshTokenKey)
                .lastActivityTime(LocalDateTime.now())
                .userStatus(UserStatus.CREATED)
                .emailNotification(EmailNotification.DISABLED)
                .rating(AppConstant.DEFAULT_RATING)
                .language(Language.builder()
                        .id(modelMapper.map("en", Long.class))
                        .build())
                .build();
        return userRepo.save(user);
    }

    private String generateDefaultRefreshTokenKey() {
        return jwtTool.generateTokenKey();
    }
}