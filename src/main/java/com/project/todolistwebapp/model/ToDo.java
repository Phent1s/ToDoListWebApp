package com.project.todolistwebapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "todos")
@Getter
@Setter
public class ToDo {
    @ToString.Include
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todos_id_seq")
    private Long id;

    @NotBlank(message = "The 'title' cannot be empty")
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @ToString.Include
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "todo", cascade = CascadeType.REMOVE)
    private List<Task> tasks;

    @ManyToMany
    @JoinTable(name = "todo_collaborator",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "collaborator_id"))
    private List<User> collaborators;

    public ToDo() {}

    public @NotBlank(message = "The 'title' cannot be empty") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "The 'title' cannot be empty") String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo toDo = (ToDo) o;
        return Objects.equals(id, toDo.id) && Objects.equals(title, toDo.title) && Objects.equals(createdAt, toDo.createdAt) && Objects.equals(owner, toDo.owner) && Objects.equals(tasks, toDo.tasks) && Objects.equals(collaborators, toDo.collaborators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, createdAt, owner, tasks, collaborators);
    }

    @Override
    public String toString() {
        return "ToDo{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
