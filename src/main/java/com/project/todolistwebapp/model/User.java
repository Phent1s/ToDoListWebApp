package com.project.todolistwebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name ="users")
@NoArgsConstructor
public class User implements UserDetails {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    private Long id;

    @Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email

    @Column(name = "email", nullable = false, unique = true)
    private String email;


    @Setter(AccessLevel.PRIVATE)
    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Getter
    @Setter
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<ToDo> myTodos;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name = "todo_collaborator",
            joinColumns = @JoinColumn(name = "collaborator_id"),
            inverseJoinColumns = @JoinColumn(name = "todo_id"))
    private List<ToDo> otherTodos;

    public @Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.")
    String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.") String firstName) {
        this.firstName = firstName;
    }

    public @Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.")
    String getLastName() {
        return lastName;
    }

    public void setLastName(@Pattern(regexp = "^[A-Z][a-z]+(?:[\\s-][A-Z][a-z]+)*$",
            message = "Must start with capital letter, followed by lowercase letters. May contain spaces or hyphens for multiple names.") String lastName) {
        this.lastName = lastName;
    }

    public @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}",
            message = "Must be a valid e-mail address")
    String getEmail() {
        return email;
    }

    public void setEmail(@Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}",
            message = "Must be a valid e-mail address") String email) {
        this.email = email;
    }

    @Builder(builderMethodName = "secureBuilder")
    private User(String firstName, String lastName, String email, String encodedPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = encodedPassword;
        this.role = UserRole.USER;
    }

    public static User createWithEncodedPassword(
            String firstName,
            String lastName,
            String email,
            String encodedPassword) {
        if (!encodedPassword.startsWith("$2a$")){
            throw new IllegalArgumentException("Password must be already encoded");
        }

        return User.secureBuilder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .encodedPassword(encodedPassword)
                .build();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && role == user.role && Objects.equals(myTodos, user.myTodos) && Objects.equals(otherTodos, user.otherTodos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, role, myTodos, otherTodos);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", password='" + password + '\'' +
               ", role=" + role +
               '}';
    }
}
