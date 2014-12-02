# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table person (
  email                     varchar(255) not null,
  name                      varchar(255),
  last_name                 varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  constraint pk_person primary key (email))
;

create table user (
  uid                       varchar(255) not null,
  created_at                timestamp,
  last_login                timestamp,
  constraint pk_user primary key (uid))
;

create sequence person_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists person;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists person_seq;

drop sequence if exists user_seq;

