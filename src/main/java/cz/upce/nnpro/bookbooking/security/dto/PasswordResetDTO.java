package cz.upce.nnpro.bookbooking.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDTO {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
