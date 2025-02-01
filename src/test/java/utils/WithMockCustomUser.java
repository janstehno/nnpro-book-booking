package utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "user";

    RoleE role() default RoleE.USER;

    long id() default 1L;
}
