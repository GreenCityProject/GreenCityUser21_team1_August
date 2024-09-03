package greencity.security.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OAuth2RedirectControllerTest {

    private static final String LINK = "/oauth2";
    private MockMvc mockMvc;

    @InjectMocks
    private OAuth2RedirectController oAuth2RedirectController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(oAuth2RedirectController)
                .build();
    }

    @Test
    void loginSuccess_ReturnsExpectedResponse() throws Exception {
        mockMvc.perform(get(LINK + "/loginSuccessWithGoogle"))
                .andExpect(status().isOk())
                .andExpect(content().string("You registered nicely"));
    }

    @Test
    void nonExistentEndpoint_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(LINK + "/nonExistentEndpoint"))
                .andExpect(status().isNotFound());
    }
}
