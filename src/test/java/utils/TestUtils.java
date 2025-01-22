package utils;

import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;

public class TestUtils {
    public static AppUser testAppUser = AppUser.builder()
                                               .id(1L)
                                               .firstname("John")
                                               .lastname("Doe")
                                               .email("john.doe@example.com")
                                               .username("john_doe")
                                               .password("hashedPassword")
                                               .role(new Role(RoleE.USER))
                                               .build();

    public static RegisterRequest testRegisterRequest =
            new RegisterRequest(testAppUser.getFirstname(),
                                testAppUser.getLastname(),
                                testAppUser.getEmail(),
                                testAppUser.getUsername(),
                                testAppUser.getPassword());
}
