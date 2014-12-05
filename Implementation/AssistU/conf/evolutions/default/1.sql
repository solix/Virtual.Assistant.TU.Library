# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table document (
  id                        bigint not null,
  file                      varchar(255),
  constraint pk_document primary key (id))
;

create table project (
  id                        bigint not null,
  folder                    varchar(255),
  constraint pk_project primary key (id))
;

create table user (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (email))
;

create sequence document_seq;

create sequence project_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists document;

drop table if exists project;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists document_seq;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

