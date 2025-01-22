package cz.upce.nnpro.bookbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class AppUser implements UserDetails {

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

    @Column(updatable = false) private LocalDateTime creationDate;

    @Column private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull
    private Role role;

    public AppUser(String firstname, String lastname, String email, String username, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getName().name()));
    }

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}
