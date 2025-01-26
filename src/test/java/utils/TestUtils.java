package utils;

import cz.upce.nnpro.bookbooking.dto.RegisterRequest;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;

public class TestUtils {
    public static AppUser testAppUser = new AppUser(1L,
                                                    "John",
                                                    "Doe",
                                                    "john.doe@example.com",
                                                    "john_doe",
                                                    "hashedPassword",
                                                    null,
                                                    null,
                                                    new Role(RoleE.USER));

    public static RegisterRequest testRegisterRequest =
            new RegisterRequest(testAppUser.getFirstname(),
                                testAppUser.getLastname(),
                                testAppUser.getEmail(),
                                testAppUser.getUsername(),
                                testAppUser.getPassword());
}
