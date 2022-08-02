create database quarkussocial;

create table users(
	id bigint not null primary key auto_increment,
	name varchar(100) not null,
    agr integer not null
);

create table posts(
	id bigint not null primary key auto_increment,
	post_text varchar(150) not null,
    date_time timestamp not null,
    user_id bigint not null references users(id)
);