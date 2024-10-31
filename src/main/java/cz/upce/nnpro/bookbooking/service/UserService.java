package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.User;
import cz.upce.nnpro.bookbooking.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class UserService implements ServiceInterface<User> {

    private final UserRepository userRepository;

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
        user.setCreation_date(LocalDateTime.now());
        user.setUpdate_date(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        user.setUpdate_date(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
