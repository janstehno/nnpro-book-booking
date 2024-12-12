package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    private String firstname;
    private String lastname;
    private String email;
    private RoleE role;
}
