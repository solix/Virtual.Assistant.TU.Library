# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table project (
  id                        bigint not null,
  folder                    varchar(255),
  constraint pk_project primary key (id))
;

create table user (
  email                     varchar(255) not null,
  enabled                   boolean,
  constraint pk_user primary key (email))
;

create sequence project_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists project;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

