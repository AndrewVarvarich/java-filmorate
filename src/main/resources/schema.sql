-- PUBLIC.USERS определение

CREATE TABLE IF NOT EXISTS USERS (
    USER_ID BIGINT NOT NULL AUTO_INCREMENT,
    EMAIL VARCHAR(40) NOT NULL UNIQUE,
    LOGIN VARCHAR(40) NOT NULL UNIQUE,
    USER_NAME VARCHAR(40),
    BIRTHDAY DATE,
    PRIMARY KEY (USER_ID)
);

ALTER TABLE USERS ADD CONSTRAINT USERS_EMAIL_UQ UNIQUE (EMAIL);
ALTER TABLE USERS ADD CONSTRAINT USERS_LOGIN_UQ UNIQUE (LOGIN);

-- PUBLIC.FRIENDSHIP определение

CREATE TABLE IF NOT EXISTS FRIENDSHIP (
	USER_ID BIGINT,
	FRIEND_ID BIGINT,
	CONSTRAINT FRIENDSHIP_USERS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID),
	CONSTRAINT FRIENDSHIP_USERS_FK_1 FOREIGN KEY (FRIEND_ID) REFERENCES PUBLIC.USERS(USER_ID)
);

-- PUBLIC.MPA определение

CREATE TABLE IF NOT EXISTS MPA (
	MPA_ID INTEGER NOT NULL,
	MPA_NAME VARCHAR_IGNORECASE(5) NOT NULL,
	CONSTRAINT MPA_PK PRIMARY KEY (MPA_ID)
);

-- PUBLIC.GENRES определение

CREATE TABLE IF NOT EXISTS GENRES (
	GENRE_ID INTEGER NOT NULL,
	GENRE_NAME VARCHAR_IGNORECASE(14) NOT NULL,
	CONSTRAINT GENRES_PK PRIMARY KEY (GENRE_ID)
);

-- PUBLIC.FILMS определение

CREATE TABLE IF NOT EXISTS FILMS (
	FILM_ID BIGINT NOT NULL AUTO_INCREMENT,
	FILM_NAME VARCHAR_IGNORECASE(40) NOT NULL,
	DESCRIPTION VARCHAR_IGNORECASE(200),
	RELEASE_DATE DATE,
	DURATION BIGINT,
	MPA_ID INTEGER,
	CONSTRAINT FILMS_PK PRIMARY KEY (FILM_ID),
	CONSTRAINT FILMS_MPA_FK FOREIGN KEY (MPA_ID) REFERENCES PUBLIC.MPA(MPA_ID)
);

-- PUBLIC.FILMS_GENRES определение

CREATE TABLE IF NOT EXISTS FILMS_GENRES (
	FILM_ID BIGINT,
	GENRE_ID INTEGER,
	CONSTRAINT FILMS_GENRES_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID),
	CONSTRAINT FILMS_GENRES_GENRES_FK FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRES(GENRE_ID)
);

-- PUBLIC.LIKES определение

CREATE TABLE IF NOT EXISTS LIKES (
	FILM_ID BIGINT,
	USER_ID BIGINT,
	CONSTRAINT LIKES_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID),
	CONSTRAINT LIKES_USERS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID)
);
