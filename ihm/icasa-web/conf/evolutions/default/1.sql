# Devices schema

# --- !Ups

CREATE SEQUENCE device_id_seq;
CREATE TABLE device (
    id integer NOT NULL DEFAULT nextval('device_id_seq'),
    name varchar(255)
);

# --- !Downs

DROP TABLE device;
DROP SEQUENCE device_id_seq;