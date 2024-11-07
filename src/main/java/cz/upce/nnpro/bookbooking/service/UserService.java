package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.dto.UserNameDTO;
import cz.upce.nnpro.bookbooking.dto.UserPasswordDTO;
import cz.upce.nnpro.bookbooking.entity.AppUser;
import cz.upce.nnpro.bookbooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements ServiceInterface<AppUser> {

    private final UserRepository userRepository;

    @Override
    public List<AppUser> getAll() {
        return userRepository.findAll();
    }

    @Override
    public AppUser getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public AppUser getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public AppUser getByEmail(String email) {return userRepository.findByEmail(email).orElse(null);}

    @Override
    public AppUser create(AppUser user) {
        return userRepository.save(user);
    }

    @Override
    public AppUser update(AppUser user) {
        return userRepository.save(user);
    }

    public AppUser update(AppUser user, UserNameDTO data) {
        user.setFirstname(data.getFirstname());
        user.setLastname(data.getLastname());
        user.setEmail(data.getEmail());
        return userRepository.save(user);
    }

    public AppUser update(AppUser user, UserPasswordDTO data, PasswordEncoder passwordEncoder) {
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
