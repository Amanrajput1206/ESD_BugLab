package edu.iiitb.esdlab3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRegistrationRequest {

    @NotNull(message = "Target event ID is required")
    private Long targetEventId;

    @NotBlank(message = "Student email is required")
    @Email(message = "Invalid email format")
    private String studentEmail;
}
