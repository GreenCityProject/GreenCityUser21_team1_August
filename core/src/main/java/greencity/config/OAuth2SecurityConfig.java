package greencity.config;

import greencity.security.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    public OAuth2SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService((OAuth2UserService<OidcUserRequest, OidcUser>) customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
                            String targetUrl = "/oauth2/loginSuccessWithGoogle";
                            if ("facebook".equals(registrationId)) {
                                targetUrl = "/oauth2/loginSuccessWithFacebook";
                            }
                            response.sendRedirect(targetUrl);
                        })
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"))
                );
        return http.build();
    }
}