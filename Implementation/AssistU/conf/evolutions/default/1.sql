# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table document_file (
  id                        bigint not null,
  name                      varchar(255),
  filepath                  varchar(255),
  constraint pk_document_file primary key (id))
;

create table project (
  id                        bigint not null,
  tabname                   varchar(255),
  name                      varchar(255),
  description               varchar(255),
  constraint pk_project primary key (id))
;

create table user (
  email                     varchar(255) not null,
  password                  varchar(255),
  titles                    varchar(255),
  firstname                 varchar(255),
  lastname                  varchar(255),
  institution               varchar(255),
  contactemail              varchar(255),
  avatar                    varchar(255),
  constraint pk_user primary key (email))
;

create sequence document_file_seq;

create sequence project_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists document_file;

drop table if exists project;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists document_file_seq;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

