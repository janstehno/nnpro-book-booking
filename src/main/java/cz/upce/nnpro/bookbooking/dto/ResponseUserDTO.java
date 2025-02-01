package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import lombok.Data;

@Data
public class ResponseUserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private RoleE role;

    public ResponseUserDTO(AppUser user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().getName();
    }
}
