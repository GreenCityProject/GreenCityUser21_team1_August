package greencity.config;

import greencity.security.service.CustomOAuth2UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {

    private final CustomOAuth2UserServiceImpl customOAuth2UserServiceImpl;

    @Autowired
    public OAuth2SecurityConfig(CustomOAuth2UserServiceImpl customOAuth2UserServiceImpl) {
        this.customOAuth2UserServiceImpl = customOAuth2UserServiceImpl;
    }

    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v2/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/oauth2/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserServiceImpl)
                        )
                        .successHandler((request, response, authentication) -> {
                            String registrationId = ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
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
