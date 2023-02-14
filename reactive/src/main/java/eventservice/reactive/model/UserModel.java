package eventservice.reactive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    @Id
    @JsonIgnore
    private String id;

    @NotNull
    @Size(min = 5, max = 20, message="Username has to be 5-20 characters long")
    private String username;

    @NotNull
    @Email(message = "Valid email has to be provided")
    @Size(min = 10, max=50, message = "Email has to be 10-50 characters")
    private String email;

    @NotNull
    @Size(min = 8, message="Password has to be 8-20 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role;

}
