package greencity.security.service;

import greencity.entity.User;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomOAuth2UserServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtTool jwtTool;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomOAuth2UserServiceImpl customOAuth2UserServiceImpl;

    private OidcUser oidcUser;
    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
        attributes.put("sub", "1234567890");
        attributes.put("email", "existing@example.com");

        OidcIdToken idToken = new OidcIdToken(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(60),
                attributes
        );


        oidcUser = new DefaultOidcUser(Collections.emptyList(), idToken, "sub");
    }

    @Test
    void processOidcUser_UserExists() {
        when(userRepo.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        OidcUser result = customOAuth2UserServiceImpl.processOidcUser(oidcUser);

        verify(userRepo, times(1)).findByEmail("existing@example.com");
        verify(userRepo, never()).save(any(User.class));
        assert result == oidcUser;
    }

    @Test
    void processOidcUser_UserDoesNotExist() {
        attributes.put("email", "newuser@example.com");

        OidcIdToken idToken = new OidcIdToken(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(60),
                attributes
        );

        oidcUser = new DefaultOidcUser(Collections.emptyList(), idToken, "sub");

        when(userRepo.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        OidcUser result = customOAuth2UserServiceImpl.processOidcUser(oidcUser);

        verify(userRepo, times(1)).findByEmail("newuser@example.com");
        verify(userRepo, times(1)).save(any(User.class));
    }
    
    @Test
    void processOidcUser_NameMissing() {
        attributes.put("email", "newuser@example.com");
        attributes.remove("name");

        OidcIdToken idToken = new OidcIdToken(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(60),
                attributes
        );

        oidcUser = new DefaultOidcUser(Collections.emptyList(), idToken, "sub");

        when(userRepo.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(jwtTool.generateTokenKey()).thenReturn("defaultKey");

        OidcUser result = customOAuth2UserServiceImpl.processOidcUser(oidcUser);

        verify(userRepo, times(1)).findByEmail("newuser@example.com");
        verify(userRepo, times(1)).save(any(User.class));
        assertEquals(oidcUser, result);
    }

    @Test
    void processOidcUser_UserDoesNotExist_UsesDefaultRefreshTokenKey() {
        attributes.put("email", "newuser@example.com");

        OidcIdToken idToken = new OidcIdToken(
                "tokenValue",
                Instant.now(),
                Instant.now().plusSeconds(60),
                attributes
        );

        oidcUser = new DefaultOidcUser(Collections.emptyList(), idToken, "sub");

        when(userRepo.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(jwtTool.generateTokenKey()).thenReturn("defaultKey");

        OidcUser result = customOAuth2UserServiceImpl.processOidcUser(oidcUser);

        verify(userRepo, times(1)).findByEmail("newuser@example.com");
        verify(userRepo, times(1)).save(argThat(user -> "defaultKey".equals(user.getRefreshTokenKey())));
    }
}
