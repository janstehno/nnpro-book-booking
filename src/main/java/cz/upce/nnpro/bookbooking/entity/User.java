package cz.upce.nnpro.bookbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
@Entity
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @NotNull
    @Length(max = 30)
    private String firstname;

    @Column
    @NotNull
    @Length(max = 30)
    private String lastname;

    @Column
    @NotNull
    @Email
    private String email;

    @Column
    @NotNull
    @Length(max = 20)
    private String username;

    @Column
    @NotNull
    private String password;

    @Column(updatable = false) private LocalDateTime creation_date;

    @Column private LocalDateTime update_date;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getName().name()));
    }
}
