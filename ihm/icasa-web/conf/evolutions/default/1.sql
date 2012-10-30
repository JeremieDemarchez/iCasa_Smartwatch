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

-- SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
-- SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
-- SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- CREATE SCHEMA IF NOT EXISTS `iCasaDB`;

-- -----------------------------------------------------
-- Table `iCasaDB`.`DEVICE`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `DEVICE` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `deviceId` VARCHAR(255) NULL ,
  `name` VARCHAR(150) NULL ,
  PRIMARY KEY (`id`))

# --- !Downs

DROP TABLE 'DEVICE';