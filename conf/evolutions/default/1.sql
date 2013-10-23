
# --- !Ups

CREATE TABLE user (
  id        BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  email     VARCHAR(255) NOT NULL,
  created   DATETIME NOT NULL
);

# --- !Downs

DROP TABLE user;