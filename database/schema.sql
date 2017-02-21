CREATE TABLE room( room_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, room_name varchar(255), room_board_id varchar(255)) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE temperature( temperature_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, temperature_value  varchar(255), timestamp TIMESTAMP, board_id INT, FOREIGN KEY (board_id) REFERENCES room(room_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE humidity ( humidity_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, humidity_value varchar(255), timestamp TIMESTAMP, board_id INT, FOREIGN KEY (board_id) REFERENCES room(room_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE led( led_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, led_usage varchar(255), timestamp TIMESTAMP, board_id INT, FOREIGN KEY (board_id) REFERENCES room(room_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE user( user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_name varchar(255), user_username varchar(255), email varchar(255), password varchar(255), isadmin boolean, main_room INT, temp_threshold varchar(255), humid_threshold varchar(255), led_threshold varchar(255), city varchar(255), address varchar(255), camera varchar(255), FOREIGN KEY (main_room) REFERENCES room(room_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE user_room( user_room_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT, room_id INT, FOREIGN KEY (user_id) REFERENCES user(user_id), FOREIGN KEY (room_id) REFERENCES room(room_id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;
