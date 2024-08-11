package backend.Loveline_backend.dto;

import backend.Loveline_backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "The name cannot be empty")
    private String name;

    private String surname;

    @NotBlank(message = "The username cannot be empty")
    private String username;

    @Email(message = "The email must be a valid email address")
    @NotBlank(message = "The password cannot be empty")
    private String email;

    @NotBlank(message = "The password cannot be empty")
    private String password;

    private String pfp;

    @NotNull(message = "The loverId cannot be empty")
    private int loverId;

    private User myLover;
}
