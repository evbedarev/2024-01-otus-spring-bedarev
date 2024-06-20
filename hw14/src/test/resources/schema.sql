create table tmp_author (
    id bigserial,
    full_name varchar(255),
    mongo_id varchar(40),
    primary key (id)
);

create table authors (
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

create table tmp_genre (
    id bigserial,
    name varchar(255),
    mongo_id varchar(40),
    primary key (id)
);

create table genres (
    id bigserial,
    name varchar(255),
    primary key (id)
);

create table tmp (
    id bigserial,
    title varchar(255),
    book_mongo_id varchar(40),
    author_mongo_id varchar(40),
    genre_mongo_id varchar(40),
    primary key (id)
);

create table books (
    id bigserial,
    title varchar(255),
    author_id bigserial,
    genre_id bigserial,
    primary key (id)
);
