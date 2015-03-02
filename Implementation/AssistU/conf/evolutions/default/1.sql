# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  cid                       bigint not null,
  subject                   varchar(255),
  content                   TEXT,
  date                      varchar(255),
  person_id                 bigint,
  project_id                bigint,
  is_child                  boolean,
  has_attachment            boolean,
  attachment                varchar(255),
  constraint pk_comment primary key (cid))
;

create table document_file (
  id                        bigint not null,
  name                      varchar(255),
  owntemplate               boolean,
  filepath                  varchar(255),
  project_id                bigint,
  person_id                 bigint,
  version                   bigint not null,
  constraint pk_document_file primary key (id))
;

create table event (
  id                        bigint not null,
  title                     varchar(255),
  description               varchar(255),
  all_day                   boolean,
  start_date                timestamp,
  end_date                  timestamp,
  ends_same_day             boolean,
  person_id                 bigint,
  constraint pk_event primary key (id))
;

create table linked_account (
  id                        bigint not null,
  person_id                 bigint,
  provider_user_id          varchar(255),
  provider_key              varchar(255),
  constraint pk_linked_account primary key (id))
;

create table person (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  email_validated           boolean,
  active                    boolean,
  mendeley_connected        boolean,
  constraint pk_person primary key (id))
;

create table project (
  id                        bigint not null,
  name                      varchar(255),
  folder                    varchar(255),
  description               varchar(255),
  template                  varchar(255),
  planning                  boolean,
  active                    boolean,
  date_created              timestamp,
  date_archived             timestamp,
  constraint pk_project primary key (id))
;

create table role (
  rid                       bigint not null,
  role                      varchar(255),
  date_invited              timestamp,
  date_joined               timestamp,
  accepted                  boolean,
  person_id                 bigint,
  project_id                bigint,
  constraint pk_role primary key (rid))
;

create table s3file (
  id                        varchar(40) not null,
  bucket                    varchar(255),
  name                      varchar(255),
  owntemplate               boolean,
  filepath                  varchar(255),
  project_id                bigint,
  person_id                 bigint,
  version                   bigint not null,
  constraint pk_s3file primary key (id))
;

create table task (
  id                        bigint not null,
  name                      varchar(255),
  due_date                  timestamp,
  done                      boolean,
  person_id                 bigint,
  constraint pk_task primary key (id))
;

create table token_action (
  id                        bigint not null,
  token                     varchar(255),
  target_person_id          bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_action_type check (type in ('PR','EV')),
  constraint uq_token_action_token unique (token),
  constraint pk_token_action primary key (id))
;

create sequence comment_seq;

create sequence document_file_seq;

create sequence event_seq;

create sequence linked_account_seq;

create sequence person_seq;

create sequence project_seq;

create sequence role_seq;

create sequence task_seq;

create sequence token_action_seq;

alter table comment add constraint fk_comment_person_1 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_comment_person_1 on comment (person_id);
alter table comment add constraint fk_comment_project_2 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_comment_project_2 on comment (project_id);
alter table document_file add constraint fk_document_file_project_3 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_document_file_project_3 on document_file (project_id);
alter table document_file add constraint fk_document_file_person_4 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_document_file_person_4 on document_file (person_id);
alter table event add constraint fk_event_person_5 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_event_person_5 on event (person_id);
alter table linked_account add constraint fk_linked_account_person_6 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_linked_account_person_6 on linked_account (person_id);
alter table role add constraint fk_role_person_7 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_role_person_7 on role (person_id);
alter table role add constraint fk_role_project_8 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_role_project_8 on role (project_id);
alter table s3file add constraint fk_s3file_project_9 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_s3file_project_9 on s3file (project_id);
alter table s3file add constraint fk_s3file_person_10 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_s3file_person_10 on s3file (person_id);
alter table task add constraint fk_task_person_11 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_task_person_11 on task (person_id);
alter table token_action add constraint fk_token_action_targetPerson_12 foreign key (target_person_id) references person (id) on delete restrict on update restrict;
create index ix_token_action_targetPerson_12 on token_action (target_person_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists comment;

drop table if exists document_file;

drop table if exists event;

drop table if exists linked_account;

drop table if exists person;

drop table if exists project;

drop table if exists role;

drop table if exists s3file;

drop table if exists task;

drop table if exists token_action;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists comment_seq;

drop sequence if exists document_file_seq;

drop sequence if exists event_seq;

drop sequence if exists linked_account_seq;

drop sequence if exists person_seq;

drop sequence if exists project_seq;

drop sequence if exists role_seq;

drop sequence if exists task_seq;

drop sequence if exists token_action_seq;

