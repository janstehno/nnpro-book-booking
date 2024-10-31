package cz.upce.nnpro.bookbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserNameDTO {
    private String firstname;
    private String lastname;
    private String email;
    private LocalDateTime updateDate;
}
