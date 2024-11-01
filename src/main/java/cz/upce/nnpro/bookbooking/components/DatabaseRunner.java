package cz.upce.nnpro.bookbooking.components;

import cz.upce.nnpro.bookbooking.entity.Role;
import cz.upce.nnpro.bookbooking.entity.enums.RoleE;
import cz.upce.nnpro.bookbooking.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DatabaseRunner {

    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        roleRepository.save(Role.builder().name(RoleE.ADMIN).build());
        roleRepository.save(Role.builder().name(RoleE.USER).build());
    }
}

