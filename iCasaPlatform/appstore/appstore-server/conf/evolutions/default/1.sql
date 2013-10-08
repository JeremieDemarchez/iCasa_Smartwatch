SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `appstore` DEFAULT CHARACTER SET latin1 ;
USE `appstore` ;

-- -----------------------------------------------------
-- Table `appstore`.`appstores`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`appstores` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(50) NOT NULL ,
  `description` TEXT NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COMMENT = 'maintains the list of available appstores';


-- -----------------------------------------------------
-- Table `appstore`.`ProductVersion`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`ProductVersion` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `product_id` INT NOT NULL ,
  `version` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_ProductVersion_products1_idx` (`product_id` ASC) ,
  CONSTRAINT `fk_ProductVersion_products1`
    FOREIGN KEY (`product_id` )
    REFERENCES `appstore`.`products` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appstore`.`products`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`products` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NOT NULL ,
  `description` VARCHAR(255) NOT NULL ,
  `imageURL` VARCHAR(45) NULL ,
  `ProductVersion_id` INT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_products_ProductVersion1` (`ProductVersion_id` ASC) ,
  CONSTRAINT `fk_products_ProductVersion1`
    FOREIGN KEY (`ProductVersion_id` )
    REFERENCES `appstore`.`ProductVersion` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `appstore`.`users`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`users` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `userId` VARCHAR(45) NOT NULL ,
  `username` VARCHAR(80) NOT NULL ,
  `fullname` VARCHAR(50) NOT NULL ,
  `password` VARCHAR(255) NOT NULL ,
  `email` VARCHAR(50) NOT NULL ,
  `provider` VARCHAR(45) NOT NULL ,
  `authmethod` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `userId_UNIQUE` (`userId` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COMMENT = 'Table containing the information of clients';


-- -----------------------------------------------------
-- Table `appstore`.`appstore_has_users`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`appstore_has_users` (
  `user_id` INT UNSIGNED NOT NULL ,
  `appstore_id` INT UNSIGNED NOT NULL ,
  INDEX `user` (`user_id` ASC) ,
  INDEX `appstore` (`appstore_id` ASC) ,
  PRIMARY KEY (`user_id`, `appstore_id`) ,
  CONSTRAINT `appstore`
    FOREIGN KEY (`appstore_id` )
    REFERENCES `appstore`.`appstores` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `user`
    FOREIGN KEY (`user_id` )
    REFERENCES `appstore`.`users` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `appstore`.`categories`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`categories` (
  `idCategories` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL ,
  `description` VARCHAR(250) NULL ,
  PRIMARY KEY (`idCategories`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appstore`.`products_has_categories`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`products_has_categories` (
  `product_id` INT NOT NULL ,
  `category_id` INT NOT NULL ,
  PRIMARY KEY (`product_id`, `category_id`) ,
  INDEX `fk_products_has_Categories_Categories1_idx` (`category_id` ASC) ,
  INDEX `fk_products_has_Categories_products1_idx` (`product_id` ASC) ,
  CONSTRAINT `fk_products_has_Categories_products1`
    FOREIGN KEY (`product_id` )
    REFERENCES `appstore`.`products` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_products_has_Categories_Categories1`
    FOREIGN KEY (`category_id` )
    REFERENCES `appstore`.`categories` (`idCategories` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `appstore`.`product_has_price`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`product_has_price` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `appstore_id` INT UNSIGNED NOT NULL ,
  `product_id` INT NOT NULL ,
  `price` DECIMAL NULL ,
  `unit` VARCHAR(45) NULL ,
  INDEX `fk_price_appstores1_idx` (`appstore_id` ASC) ,
  INDEX `fk_price_products1_idx` (`product_id` ASC) ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_price_appstores1`
    FOREIGN KEY (`appstore_id` )
    REFERENCES `appstore`.`appstores` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_price_products1`
    FOREIGN KEY (`product_id` )
    REFERENCES `appstore`.`products` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appstore`.`services`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`services` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL ,
  `description` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appstore`.`applications`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`applications` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(45) NULL ,
  `description` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appstore`.`ServiceVersion`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`ServiceVersion` (
  `version_id` INT NOT NULL AUTO_INCREMENT ,
  `version` VARCHAR(25) NOT NULL ,
  `services_id` INT NOT NULL ,
  PRIMARY KEY (`version_id`) ,
  INDEX `fk_ServiceVersion_services1_idx` (`services_id` ASC) ,
  CONSTRAINT `fk_ServiceVersion_services1`
    FOREIGN KEY (`services_id` )
    REFERENCES `appstore`.`services` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appstore`.`products_has_services`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`products_has_services` (
  `ProductVersion_id` INT NOT NULL ,
  `serviceVersion_id` INT NOT NULL ,
  PRIMARY KEY (`ProductVersion_id`, `serviceVersion_id`) ,
  INDEX `fk_products_has_services_ProductVersion1_idx` (`ProductVersion_id` ASC) ,
  INDEX `fk_products_has_services_ServiceVersion1_idx` (`serviceVersion_id` ASC) ,
  CONSTRAINT `fk_products_has_services_ProductVersion1`
    FOREIGN KEY (`ProductVersion_id` )
    REFERENCES `appstore`.`ProductVersion` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_products_has_services_ServiceVersion1`
    FOREIGN KEY (`serviceVersion_id` )
    REFERENCES `appstore`.`ServiceVersion` (`version_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `appstore`.`applicationVersion`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`applicationVersion` (
  `version_id` INT NOT NULL AUTO_INCREMENT ,
  `applications_id` INT NOT NULL ,
  `version` VARCHAR(25) NOT NULL ,
  PRIMARY KEY (`version_id`) ,
  INDEX `fk_applicationVersion_applications1_idx` (`applications_id` ASC) ,
  CONSTRAINT `fk_applicationVersion_applications1`
    FOREIGN KEY (`applications_id` )
    REFERENCES `appstore`.`applications` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appstore`.`products_has_applications`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`products_has_applications` (
  `ProductVersion_id` INT NOT NULL ,
  `applicationVersion_id` INT NOT NULL ,
  PRIMARY KEY (`ProductVersion_id`, `applicationVersion_id`) ,
  INDEX `fk_products_has_applications_ProductVersion1_idx` (`ProductVersion_id` ASC) ,
  INDEX `fk_products_has_applications_applicationVersion1_idx` (`applicationVersion_id` ASC) ,
  CONSTRAINT `fk_products_has_applications_ProductVersion1`
    FOREIGN KEY (`ProductVersion_id` )
    REFERENCES `appstore`.`ProductVersion` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_products_has_applications_applicationVersion1`
    FOREIGN KEY (`applicationVersion_id` )
    REFERENCES `appstore`.`applicationVersion` (`version_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `appstore`.`orders`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `appstore`.`orders` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `price_id` INT NOT NULL ,
  `users_id` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `fk_orders_product_has_price1_idx` (`price_id` ASC) ,
  INDEX `fk_orders_users1_idx` (`users_id` ASC) ,
  CONSTRAINT `fk_orders_product_has_price1`
    FOREIGN KEY (`price_id` )
    REFERENCES `appstore`.`product_has_price` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_orders_users1`
    FOREIGN KEY (`users_id` )
    REFERENCES `appstore`.`users` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
