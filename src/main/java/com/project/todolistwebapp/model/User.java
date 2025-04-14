package com.project.todolistwebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name ="users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Pattern(regexp = "[A-Z][a-z]+",
    message = "Must start with a capital letter followed by one or more lowercase letters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

//    @Email
    @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}",
    message = "Must be a valid e-mail address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

//    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}",
//            message = "Must be minimum 6 characters, at least one letter and one number")
    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<ToDo> myTodos;

    @ManyToMany
    @JoinTable(name = "todo_collaborator",
            joinColumns = @JoinColumn(name = "collaborator_id"),
            inverseJoinColumns = @JoinColumn(name = "todo_id"))
    private List<ToDo> otherTodos;

    public User() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters") String firstName) {
        this.firstName = firstName;
    }

    public @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters") String getLastName() {
        return lastName;
    }

    public void setLastName(@Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters") String lastName) {
        this.lastName = lastName;
    }

    public @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}",
            message = "Must be a valid e-mail address") String getEmail() {
        return email;
    }

    public void setEmail(@Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}",
            message = "Must be a valid e-mail address") String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<ToDo> getMyTodos() {
        return myTodos;
    }

    public void setMyTodos(List<ToDo> myTodos) {
        this.myTodos = myTodos;
    }

    public List<ToDo> getOtherTodos() {
        return otherTodos;
    }

    public void setOtherTodos(List<ToDo> otherTodos) {
        this.otherTodos = otherTodos;
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
        return id == user.id && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && role == user.role && Objects.equals(myTodos, user.myTodos) && Objects.equals(otherTodos, user.otherTodos);
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
