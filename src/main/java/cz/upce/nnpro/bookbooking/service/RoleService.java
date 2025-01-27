package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.exception.CustomExceptionHandler;
import cz.upce.nnpro.bookbooking.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public Role getById(Long id) throws RuntimeException {
        return roleRepository.findById(id).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }

    public Role getByName(RoleE name) throws RuntimeException {
        return roleRepository.findByName(name).orElseThrow(CustomExceptionHandler.EntityNotFoundException::new);
    }
}
