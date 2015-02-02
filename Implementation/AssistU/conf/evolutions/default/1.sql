# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  cid                       bigint not null,
  subject                   varchar(255),
  content                   TEXT,
  date                      varchar(255),
  user_id                   bigint,
  project_id                bigint,
  is_child                  boolean,
  constraint pk_comment primary key (cid))
;

create table document_file (
  id                        bigint not null,
  name                      varchar(255),
  filepath                  varchar(255),
  project_id                bigint,
  version                   bigint not null,
  constraint pk_document_file primary key (id))
;

create table event (
  id                        bigint not null,
  title                     varchar(255),
  all_day                   boolean,
  start                     timestamp,
  end                       timestamp,
  ends_same_day             boolean,
  user_id                   bigint,
  constraint pk_event primary key (id))
;

create table linked_account (
  id                        bigint not null,
  user_id                   bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table project (
  id                        bigint not null,
  name                      varchar(255),
  folder                    varchar(255),
  description               varchar(255),
  active                    boolean,
  date_created              timestamp,
  constraint pk_project primary key (id))
;

create table task (
  id                        bigint not null,
  name                      varchar(255),
  due_date                  timestamp,
  done                      boolean,
  user_id                   bigint,
  constraint pk_task primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  email_validated           boolean,
  active                    boolean,
  mendeley_connected        boolean,
  constraint pk_user primary key (id))
;

create table user_role (
  id                        bigint not null,
  tag                       varchar(255),
  description               varchar(255),
  constraint pk_user_role primary key (id))
;


create table user_user_role (
  user_id                        bigint not null,
  user_role_id                   bigint not null,
  constraint pk_user_user_role primary key (user_id, user_role_id))
;

create table user_project (
  user_id                        bigint not null,
  project_id                     bigint not null,
  constraint pk_user_project primary key (user_id, project_id))
;
create sequence comment_seq;

create sequence document_file_seq;

create sequence event_seq;

create sequence linked_account_seq;

create sequence project_seq;

create sequence task_seq;

create sequence user_seq;

create sequence user_role_seq;

<<<<<<< HEAD
alter table comment add constraint fk_comment_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_comment_user_1 on comment (user_id);
alter table comment add constraint fk_comment_project_2 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_comment_project_2 on comment (project_id);
alter table document_file add constraint fk_document_file_project_3 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_document_file_project_3 on document_file (project_id);
alter table linked_account add constraint fk_linked_account_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_linked_account_user_4 on linked_account (user_id);
alter table task add constraint fk_task_user_5 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_task_user_5 on task (user_id);
=======
alter table document_file add constraint fk_document_file_project_1 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_document_file_project_1 on document_file (project_id);
alter table event add constraint fk_event_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_event_user_2 on event (user_id);
alter table linked_account add constraint fk_linked_account_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_linked_account_user_3 on linked_account (user_id);
alter table task add constraint fk_task_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_task_user_4 on task (user_id);
>>>>>>> calendar



alter table user_user_role add constraint fk_user_user_role_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_user_role add constraint fk_user_user_role_user_role_02 foreign key (user_role_id) references user_role (id) on delete restrict on update restrict;

alter table user_project add constraint fk_user_project_user_01 foreign key (user_id) references user (id) on delete restrict on update restrict;

alter table user_project add constraint fk_user_project_project_02 foreign key (project_id) references project (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists comment;

drop table if exists document_file;

drop table if exists event;

drop table if exists linked_account;

drop table if exists project;

drop table if exists user_project;

drop table if exists task;

drop table if exists user;

drop table if exists user_user_role;

drop table if exists user_role;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists comment_seq;

drop sequence if exists document_file_seq;

drop sequence if exists event_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists project_seq;

drop sequence if exists task_seq;

drop sequence if exists user_seq;

drop sequence if exists user_role_seq;

