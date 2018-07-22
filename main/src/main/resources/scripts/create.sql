CREATE TABLE  IF NOT EXISTS User
(
  id IDENTITY primary key auto_increment not null,
  login  varchar(50),
  password varchar(50),
  balance DECIMAL(20, 2)
);


CREATE UNIQUE INDEX IF NOT EXISTS login_index ON User(login);


