package greencity.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/oauth2")
public class OAuth2RedirectController {

    @Operation(summary = "Sign-up with Google")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirected to main page after successful Google sign-up"),
            @ApiResponse(responseCode = "403", description = "Access Denied")
    })
    @GetMapping("/loginSuccessWithGoogle")
    public RedirectView loginSuccessGoogle() {
        return new RedirectView("https://www.greencity.cx.ua/#/greenCity");
    }

    @Operation(summary = "Sign-up with Facebook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Facebook sign-up"),
            @ApiResponse(responseCode = "403", description = "Access Denied")
    })
    @GetMapping("/loginSuccessWithFacebook")
    public ResponseEntity<String> loginSuccessFaceBook() {
        return new ResponseEntity<>("You registered nicely", HttpStatus.OK);
    }
}