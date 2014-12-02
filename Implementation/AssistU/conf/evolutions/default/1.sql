# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table profile (
  email                     varchar(255) not null,
  name                      varchar(255),
  last_name                 varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  constraint pk_profile primary key (email))
;

create table user (
  uid                       varchar(255) not null,
  user_profile_email        varchar(255),
  created_at                timestamp,
  last_login                timestamp,
  enabled                   boolean,
  constraint pk_user primary key (uid))
;

create sequence profile_seq;

create sequence user_seq;

alter table user add constraint fk_user_userProfile_1 foreign key (user_profile_email) references profile (email) on delete restrict on update restrict;
create index ix_user_userProfile_1 on user (user_profile_email);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists profile;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists profile_seq;

drop sequence if exists user_seq;

