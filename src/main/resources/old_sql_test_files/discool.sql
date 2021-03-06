USE discool;
DROP DATABASE discool;
CREATE DATABASE discool;
USE discool;
CREATE TABLE IF NOT EXISTS config (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,

    PRIMARY KEY(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    username VARCHAR(100) NOT NULL,
    role INT NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    website VARCHAR(100),
    firstlogin BIGINT NOT NULL,
    lastlogin BIGINT NOT NULL,
    timecreated BIGINT NOT NULL,

    PRIMARY KEY(id)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS courses (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    name VARCHAR(255) NOT NULL,
    teacherid BIGINT UNSIGNED NOT NULL,
    pathicon VARCHAR(255) NOT NULL,
    CONSTRAINT fk_teacherid
        FOREIGN KEY(teacherid) REFERENCES users(id),
    PRIMARY KEY(id)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS course_sections (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    courseid BIGINT UNSIGNED NOT NULL,
    parentid BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,

    FOREIGN KEY (parentid) REFERENCES course_sections(id),

    CONSTRAINT fk_courseid_course
        FOREIGN KEY(courseid) REFERENCES courses(id),
    PRIMARY KEY(id)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS groups (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    courseid BIGINT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    enrolmentkey VARCHAR(50),
    CONSTRAINT fk_courseid2
        FOREIGN KEY(courseid) REFERENCES courses(id),
    PRIMARY KEY(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS group_members (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    groupid BIGINT UNSIGNED NOT NULL,
    userid BIGINT UNSIGNED NOT NULL,
    timeadded BIGINT NOT NULL,

    CONSTRAINT fk_groupid
        FOREIGN KEY(groupid) REFERENCES groups(id),
    CONSTRAINT fk_userid
        FOREIGN KEY(userid) REFERENCES users(id),
    PRIMARY KEY(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS direct_messages (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    useridfrom BIGINT UNSIGNED NOT NULL,
    useridto BIGINT UNSIGNED NOT NULL,
    subject VARCHAR(255),
    parentid BIGINT UNSIGNED,
    message TEXT NOT NULL,
    timecreated BIGINT NOT NULL,
    deleted BIT NOT NULL,

    CONSTRAINT fk_parentid_direct
    FOREIGN KEY(parentid) REFERENCES direct_messages(id),
    CONSTRAINT fk_useridfrom
	FOREIGN KEY (useridfrom) REFERENCES users(id),
    CONSTRAINT fk_useridto
	FOREIGN KEY(useridto) REFERENCES users(id),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS channels (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    courseid BIGINT UNSIGNED NOT NULL,
    name VARCHAR(255),
    CONSTRAINT fk_courseid_channels
	FOREIGN KEY (courseid) REFERENCES courses(id),
    PRIMARY KEY(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    channelid BIGINT UNSIGNED NOT NULL,
    parentid BIGINT UNSIGNED,
    userid BIGINT UNSIGNED NOT NULL,
    timecreated BIGINT NOT NULL,
    message TEXT NOT NULL,
    deleted BIT NOT NULL,

    CONSTRAINT fk_parentid_posts
        FOREIGN KEY(parentid) REFERENCES posts(id),
    CONSTRAINT fk_userid_posts
        FOREIGN KEY(userid) REFERENCES users(id),
    CONSTRAINT fk_channelid
	FOREIGN KEY (channelid) REFERENCES channels(id),
    PRIMARY KEY (id)
) ENGINE=InnoDB;
