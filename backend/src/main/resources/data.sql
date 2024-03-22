-- Insert seed data into 'users' table
INSERT INTO users (username, email)
VALUES ('johndoe', 'john@example.com');
INSERT INTO users (username, email)
VALUES ('janedoe', 'jane@example.com');

-- Insert seed data into 'posts' table
INSERT INTO posts (user_id, title, content)
VALUES (1, 'First Post', 'This is the content of the first post.');
INSERT INTO posts (user_id, title, content)
VALUES (2, 'Second Post', 'This is the content of the second post.');
