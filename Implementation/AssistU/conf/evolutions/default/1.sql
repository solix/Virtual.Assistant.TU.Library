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
  folder                    varchar(255),
  name                      varchar(255),
  active                    boolean,
  constraint pk_project primary key (id))
;

create table user (
  email                     varchar(255) not null,
  password                  varchar(255),
  constraint pk_user primary key (email))
;

create table user_role (
  name                      varchar(255) not null,
  constraint pk_user_role primary key (name))
;


create table project_user (
  project_id                     bigint not null,
  user_email                     varchar(255) not null,
  constraint pk_project_user primary key (project_id, user_email))
;
create sequence document_file_seq;

create sequence project_seq;

create sequence user_seq;

create sequence user_role_seq;




alter table project_user add constraint fk_project_user_project_01 foreign key (project_id) references project (id) on delete restrict on update restrict;

alter table project_user add constraint fk_project_user_user_02 foreign key (user_email) references user (email) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists document_file;

drop table if exists project;

drop table if exists project_user;

drop table if exists user;

drop table if exists user_role;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists document_file_seq;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

drop sequence if exists user_role_seq;

