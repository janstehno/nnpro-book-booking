package cz.upce.nnpro.bookbooking.service;

import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getById(Long id) throws RuntimeException {
        return roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Role getByName(RoleE name) throws RuntimeException {
        return roleRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
    }
}
