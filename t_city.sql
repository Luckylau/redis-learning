CREATE DATABASE springbootdb;
CREATE TABLE `t_city` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`province_id`  varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' ,
`city_name`  varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' ,
`description`  varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' ,
`create_time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modify_time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=1
ROW_FORMAT=DYNAMIC
;