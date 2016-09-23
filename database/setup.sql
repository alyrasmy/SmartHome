CREATE DATABASE SmartHome;
USE SmartHome;
CREATE USER ‘team’@‘localhost' IDENTIFIED BY 'password';
GRANT ALL ON SmartHome.* TO 'team'@'localhost';

source /Users/Rasmy/Documents/forthProject/database/schema.sql;
