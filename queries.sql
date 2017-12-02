-- check if reservations and rooms exist--if no error table exists:
SELECT 1 FROM reservations LIMIT 1;
SELECT 1 FROM rooms LIMIT 1;

-- admin: show status
-- TODO: database full, empty, or no database
SELECT COUNT(*) FROM reservations;
SELECT COUNT(*) FROM rooms;

-- OWNER:

-- R-1
-- show list of rooms
SELECT RoomName from rooms;
