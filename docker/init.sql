
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS states(
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS todos(
    id BIGINT NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    owner_id BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasks(
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    priority VARCHAR(255),
    todo_id BIGINT,
    state_id BIGINT,
    FOREIGN KEY (todo_id) REFERENCES todos(id) ON DELETE CASCADE,
    FOREIGN KEY (state_id) REFERENCES states(id)
);

CREATE TABLE IF NOT EXISTS todo_collaborator(
    todo_id BIGINT NOT NULL,
    collaborator_id BIGINT NOT NULL,
    FOREIGN KEY (todo_id) REFERENCES todos(id),
    FOREIGN KEY (collaborator_id) REFERENCES users(id)
);

INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (1, 'Alex', 'Fisher', 'alex@mail.com', '$2a$12$n7JxG2mB4x2QFv5oDu.X1.T8dUxqCHmIyZ6vsRgyS/eFvjlT44VwS', 'ADMIN');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (2, 'Bill', 'Black', 'bill@mail.com', '$2a$12$M8ejDjJliggKWUodFncD9.sZyMrhC7PpsmaYXb5i7iPamZ6/eytGa', 'ADMIN');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (3, 'Harley', 'Almond', 'harley@mail.com', '$2a$12$hBxKD42i90mKap1S3oa.D.eLE3RNWwW5ieXrEV3C8X22ANCmGBJte', 'USER');


INSERT INTO states (id, name) VALUES (1, 'New');
INSERT INTO states (id, name) VALUES (2, 'Doing');
INSERT INTO states (id, name) VALUES (3, 'Verify');
INSERT INTO states (id, name) VALUES (4, 'Done');


INSERT INTO todos (id, title, created_at, owner_id) VALUES (1, 'Alex''s To-Do #1', '2024-10-06 15:00:04.114253', 1);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (2, 'Alex''s To-Do #2', '2024-10-06 15:00:11.585311', 1);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (3, 'Alex''s To-Do #3', '2024-10-06 15:05:16.952515', 1);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (4, 'Bill''s To-Do #1', '2024-10-06 15:03:54.532337', 2);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (5, 'Bill''s To-Do #2', '2024-10-06 15:15:04.707176', 2);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (6, 'Harley''s To-Do #1', '2024-10-06 15:15:32.464391', 3);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (7, 'Harley''s To-Do #2', '2024-10-06 15:15:39.16246', 3);

INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (2, 'Task #2', 'LOW', 1, 1);
INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (1, 'Task #1', 'HIGH', 1, 4);
INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (3, 'Task #3', 'MEDIUM', 1, 2);

INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (1, 2);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (1, 3);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (4, 3);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (4, 1);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (5, 2);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (5, 1);

create sequence state_id_seq
    increment by 50;
alter sequence state_id_seq owner to admin;

create sequence users_id_seq;
alter sequence users_id_seq owner to admin;
alter sequence users_id_seq owned by users.id;

create sequence tasks_id_seq;
alter sequence tasks_id_seq owner to admin;
alter sequence tasks_id_seq owned by tasks.id;

create sequence todos_id_seq;
alter sequence todos_id_seq owner to admin;
alter sequence todos_id_seq owned by todos.id;


SELECT setval('state_id_seq', (SELECT MAX(id) FROM states));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('tasks_id_seq', (SELECT MAX(id) FROM tasks));
SELECT setval('todos_id_seq', (SELECT MAX(id) FROM todos));
