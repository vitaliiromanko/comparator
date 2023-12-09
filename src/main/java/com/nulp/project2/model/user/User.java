package com.nulp.project2.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user")
public class User implements UserDetails {
    private static final String SEQ_NAME = "user_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 30, message = "Кількість символів у полі має бути від 2 до 30!")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 30, message = "Кількість символів у полі має бути від 2 до 30!")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank
    @Size(min = 2, max = 30, message = "Кількість символів у полі має бути від 2 до 30!")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank
    @Size(min = 4, message = "Кількість символів у полі має бути від 4!")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Email(message = "Електронна пошта вказана некоректно!")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @OneToMany(targetEntity = ActivationToken.class, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ActivationToken> activationTokens = new HashSet<>();

    @OneToMany(targetEntity = ResetPasswordToken.class, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ResetPasswordToken> resetPasswordTokens = new HashSet<>();

    @PrePersist
    protected void prePersist() {
        registrationDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.status.equals(Status.BANNED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.status.equals(Status.NO_ACTIVATED);
    }
}
