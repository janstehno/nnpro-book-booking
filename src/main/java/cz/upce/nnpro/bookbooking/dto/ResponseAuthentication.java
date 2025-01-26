package cz.upce.nnpro.bookbooking.dto;

import cz.upce.nnpro.bookbooking.entity.AppUser;
import lombok.Data;

@Data
public class ResponseAuthentication {
    private Long id;
    private String firstname;
    private String lastname;
    private String token;

    public ResponseAuthentication(AppUser user, String token) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.token = token;
    }
}
