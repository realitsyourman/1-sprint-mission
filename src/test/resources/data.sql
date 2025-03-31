-- 유저 초기화
INSERT INTO users (id, created_at, username, email, password)
VALUES ('3a2c1f0d-6b9e-4e8a-a7c5-d4f2e9b8c1a0', CURRENT_TIMESTAMP, 'user1', 'user1@mail.com',
        'password123');

INSERT INTO users (id, created_at, username, email, password)
VALUES ('4b3d2e1f-7c0a-5f9b-b8d6-e5c3f2d1e0b9', CURRENT_TIMESTAMP, 'user2', 'user2@mail.com',
        'password123');

INSERT INTO users (id, created_at, username, email, password)
VALUES ('5c4e3f2d-8d1b-6a0c-c9e7-f6d4e2c0b9a8', CURRENT_TIMESTAMP, 'user3', 'user3@mail.com',
        'password123');


-- 채널 초기화
INSERT INTO channels (id, created_at, updated_at, name, description, type)
VALUES ('6f17a8d1-77d7-437d-811e-d98db3bd30bc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        'ch1',
        'channel 1', 'PUBLIC');

INSERT INTO channels (id, created_at, updated_at, name, description, type)
VALUES ('1f17a8d1-77d7-437d-811e-d98db3bd30bc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        'ch2',
        'channel 2', 'PUBLIC');


-- 유저의 read status 초기화
INSERT INTO read_statuses (id, created_at, updated_at, last_read_at, user_id, channel_id)
VALUES ('5e8cf92c-f1de-4a66-b364-6e9379df1476', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        '3a2c1f0d-6b9e-4e8a-a7c5-d4f2e9b8c1a0', '6f17a8d1-77d7-437d-811e-d98db3bd30bc');

INSERT INTO read_statuses (id, created_at, updated_at, last_read_at, user_id, channel_id)
VALUES ('4e7cf92c-f1de-4a66-b364-6e9379df1476', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        '4b3d2e1f-7c0a-5f9b-b8d6-e5c3f2d1e0b9', '6f17a8d1-77d7-437d-811e-d98db3bd30bc');

INSERT INTO read_statuses (id, created_at, updated_at, last_read_at, user_id, channel_id)
VALUES ('2e7cf92c-f1de-4a66-b364-6e9379df1476', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        '4b3d2e1f-7c0a-5f9b-b8d6-e5c3f2d1e0b9', '1f17a8d1-77d7-437d-811e-d98db3bd30bc');