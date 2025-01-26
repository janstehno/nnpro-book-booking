package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordResetDTO {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
