package greencity.security.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface CustomOAuth2UserService {
    OidcUser loadUser(OidcUserRequest userRequest);
    OidcUser processOidcUser(OidcUser oidcUser);
}
