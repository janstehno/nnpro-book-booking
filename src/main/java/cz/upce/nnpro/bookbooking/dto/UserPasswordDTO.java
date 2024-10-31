package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserPasswordDTO {
    private String oldPassword;
    private String password;
    private LocalDateTime updateDate;
}
