create database quarkussocial;

create table users(
	id bigint not null primary key auto_increment,
	name varchar(100) not null,
    agr integer not null
);