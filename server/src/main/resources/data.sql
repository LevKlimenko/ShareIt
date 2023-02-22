DELETE
FROM comments;
DELETE
FROM bookings;
DELETE
FROM items;
DELETE
FROM users;
DELETE
FROM requests;

ALTER TABLE comments
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE bookings
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE items
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;
Alter TABLE requests
    Alter column id restart with 1;