
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (1, 'Alex', 'Fisher', 'alex@mail.com', '$2a$12$n7JxG2mB4x2QFv5oDu.X1.T8dUxqCHmIyZ6vsRgyS/eFvjlT44VwS', 'ADMIN');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (2, 'Bill', 'Black', 'bill@mail.com', '$2a$12$M8ejDjJliggKWUodFncD9.sZyMrhC7PpsmaYXb5i7iPamZ6/eytGa', 'ADMIN');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (3, 'Harley', 'Almond', 'harley@mail.com', '$2a$12$hBxKD42i90mKap1S3oa.D.eLE3RNWwW5ieXrEV3C8X22ANCmGBJte', 'USER');


INSERT INTO states (id, name) VALUES (1, 'New');
INSERT INTO states (id, name) VALUES (2, 'Doing');
INSERT INTO states (id, name) VALUES (3, 'Verify');
INSERT INTO states (id, name) VALUES (4, 'Done');

INSERT INTO todos (id, title, created_at, owner_id) VALUES (1, 'Alex''s To-Do #1', '2024-10-06 15:00:04.114253', 1);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (2, 'Alex''s To-Do #2', '2024-10-06 15:00:11.585311', 1);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (3, 'Alex''s To-Do #3', '2024-10-06 15:00:16.952515', 1);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (4, 'Bill''s To-Do #1', '2024-10-06 15:14:54.532337', 2);
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
