# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table map (
  image_url                 varchar(255),
  name                      varchar(255),
  gateway_url               varchar(255))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists map;

SET REFERENTIAL_INTEGRITY TRUE;

