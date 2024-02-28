package lanajauhar.contactapi.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateContactRequest {

    @NotBlank(message = "First name dibutuhkan")
    @Size(max =100)
    private String firstName;

    @Size(max =100)
    private String lastName;

    @Size(max =100)
    @Email(message = "Invalid email dibutuhkan")
    private String email;

    @Size(max =100)
//    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$")
    private String phone;


}
