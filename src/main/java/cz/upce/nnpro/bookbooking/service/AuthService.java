package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.*;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.entity.ResetToken;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    private final UserService userService;

    private final RoleService roleService;

    private final ResetTokenService resetTokenService;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> register(RegisterRequest registerRequest) throws RuntimeException {
        if (userService.emailExists(registerRequest.getEmail())) throw new CustomExceptionHandler.EmailExistsException();
        if (userService.usernameExists(registerRequest.getUsername())) throw new CustomExceptionHandler.UsernameExistsException();

        final AppUser user = new AppUser(registerRequest.getFirstname(),
                                         registerRequest.getLastname(),
                                         registerRequest.getEmail(),
                                         registerRequest.getUsername(),
                                         passwordEncoder.encode(registerRequest.getPassword()),
                                         roleService.getByName(RoleE.USER));

        AppUser registered = userService.create(user);
        return returnAuthenticatedUser(registered);
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) throws RuntimeException {
        final AppUser found = userService.getByUsername(loginRequest.getUsername());
        if (passwordEncoder.matches(loginRequest.getPassword(), found.getPassword())) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            } catch (AuthenticationException e) {
                throw new CustomExceptionHandler.PasswordNotCorrectException();
            }
            return returnAuthenticatedUser(found);
        } else {
            throw new CustomExceptionHandler.PasswordNotCorrectException();
        }
    }

    public ResponseEntity<?> passwordMail(PasswordResetRequest passwordResetRequest, HttpServletRequest httpRequest) {
        AppUser user = userService.getByUsername(passwordResetRequest.getUsername());

        if (user != null) {
            final String token = jwtService.generateResetToken(user);
            ResetToken resetToken = new ResetToken(token, user, LocalDateTime.now().plusMinutes(15));
            resetTokenService.create(resetToken);

            String appUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":" + httpRequest.getServerPort();
            String resetUrl = appUrl + "/password/reset?token=" + token;

            mailService.sendEmailAboutPasswordReset(user.getEmail(), resetUrl);
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> passwordReset(PasswordResetDTO passwordResetDTO) throws RuntimeException {
        if (passwordResetDTO.getNewPassword().equals(passwordResetDTO.getConfirmPassword())) {
            String token = passwordResetDTO.getToken();

            String username;
            AppUser user;

            try {
                username = jwtService.extractUsername(token);
                user = userService.getByUsername(username);
            } catch (Exception e) {
                throw new CustomExceptionHandler.InvalidTokenException();
            }

            if (user != null) {
                if (username == null || !jwtService.isTokenValid(token, user)) {
                    throw new CustomExceptionHandler.InvalidTokenException();
                }

                String purpose = jwtService.extractClaim(token, claims -> (String) claims.get("usedFor"));
                if (!purpose.equals("PASSWORD_RESET")) {
                    throw new CustomExceptionHandler.InvalidTokenException();
                }

                user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
                userService.update(user);

                ResetToken resetToken = resetTokenService.getByToken(token);
                if (resetToken != null) resetTokenService.deleteById(resetToken.getId());

                return ResponseEntity.ok().build();
            }
        }

        throw new CustomExceptionHandler.PasswordNotCorrectException();
    }

    private ResponseEntity<?> returnAuthenticatedUser(AppUser user) {
        final String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new ResponseAuthentication(user, token));
    }
}
