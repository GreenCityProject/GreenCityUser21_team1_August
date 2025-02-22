package greencity.security.dto.ownsecurity;

import greencity.annotations.PasswordValidation;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordDto {
    @NotBlank
    private String oldPassword;

    @NotBlank
    @PasswordValidation
    private String password;

    @NotBlank
    private String confirmPassword;
}