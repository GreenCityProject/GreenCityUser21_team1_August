package greencity.message;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Message, that is used for sending emails about not marked habits.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SendHabitNotification implements Serializable {
    @Pattern(regexp = "^[A-Z][a-zA-Z]{0,14}$", message = "Name must start with a capital letter, contain no digits" +
            ",and be up to 15 characters long.")
    private String name;
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    private String email;
}