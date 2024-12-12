package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.RequestUserPasswordDTO;
import cz.upce.nnpro.bookbooking.dto.UserDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.UserRepository;
import cz.upce.nnpro.bookbooking.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements ServiceInterface<AppUser> {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<AppUser> getAll() {
        return userRepository.findAll();
    }

    @Override
    public AppUser getById(Long id) throws RuntimeException {
        return userRepository.findById(id).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    @Override
    public AppUser create(AppUser user) {
        return userRepository.save(user);
    }

    @Override
    public AppUser update(AppUser user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO get(AppUser user) {
        return new UserDTO(user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole().getName());
    }

    public AppUser getByUsername(String username) throws RuntimeException {
        return userRepository.findByUsername(username).orElseThrow(CustomExceptionHandler.UsernameNotFoundException::new);
    }

    public AppUser getByEmail(String email) throws RuntimeException {
        return userRepository.findByEmail(email).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public String update(AppUser user, UserDTO data) throws RuntimeException {
        AppUser foundByEmail = getByEmail(data.getEmail());
        if (foundByEmail != null && !foundByEmail.getId().equals(user.getId())) throw new CustomExceptionHandler.EmailExistsException();

        user.setFirstname(data.getFirstname());
        user.setLastname(data.getLastname());
        user.setEmail(data.getEmail());
        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public String updatePassword(AppUser user, RequestUserPasswordDTO data) throws RuntimeException {
        if (!passwordEncoder.matches(data.getOldPassword(), user.getPassword())) throw new CustomExceptionHandler.OldPasswordIncorrectException();

        user.setPassword(passwordEncoder.encode(data.getPassword()));
        userRepository.save(user);

        return jwtService.generateToken(user);
    }
}
