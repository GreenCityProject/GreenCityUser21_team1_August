package greencity.config;

import greencity.enums.Role;
import greencity.security.jwt.JwtTool;
import greencity.security.service.CustomOAuth2UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTool jwtTool;

    @Autowired
    public OAuth2SecurityConfig(CustomOAuth2UserService customOAuth2UserService, JwtTool jwtTool) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTool = jwtTool;
    }

    @Bean
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTool), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService((OAuth2UserService<OidcUserRequest, OidcUser>) customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                            String email = oauthToken.getPrincipal().getAttribute("email");

                            String accessToken = jwtTool.createAccessToken(email, Role.ROLE_USER);

                            response.setHeader("Authorization", "Bearer " + accessToken);

                            String targetUrl = "https://www.greencity.cx.ua/#/greenCity?token=" + accessToken;
                            response.sendRedirect(targetUrl);
                        })
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"))
                );
        return http.build();
    }

    private static class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtTool jwtTool;

        public JwtAuthenticationFilter(JwtTool jwtTool) {
            this.jwtTool = jwtTool;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
            String token = jwtTool.getTokenFromHttpServletRequest(request);

            if (token != null && jwtTool.isTokenValid(token, jwtTool.getAccessTokenKey())) {
                String email = jwtTool.getEmailOutOfAccessToken(token);
            }
            chain.doFilter(request, response);
        }
    }
}

