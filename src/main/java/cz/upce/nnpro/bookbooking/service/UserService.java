package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.UserNameDTO;
import cz.upce.nnpro.bookbooking.dto.UserPasswordDTO;
import cz.upce.nnpro.bookbooking.entity.User;
import cz.upce.nnpro.bookbooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements ServiceInterface<User> {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User getByEmail(String email) {return userRepository.findByEmail(email).orElse(null);}

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    public User update(User user, UserNameDTO data) {
        user.setFirstname(data.getFirstname());
        user.setLastname(data.getLastname());
        user.setEmail(data.getEmail());
        return userRepository.save(user);
    }

    public User update(User user, UserPasswordDTO data) {
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
