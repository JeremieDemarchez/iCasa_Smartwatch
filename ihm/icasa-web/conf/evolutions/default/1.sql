--
--
--   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
--   Licensed under the Apache License, Version 2.0 (the "License");
--   you may not use this file except in compliance with the License.
--   You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
--   Unless required by applicable law or agreed to in writing, software
--   distributed under the License is distributed on an "AS IS" BASIS,
--   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--   See the License for the specific language governing permissions and
--   limitations under the License.
--
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