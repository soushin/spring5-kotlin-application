DROP SCHEMA IF EXISTS todo;
CREATE DATABASE todo DEFAULT CHARACTER SET utf8mb4;
USE todo;
SET AUTOCOMMIT=0;

DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `task_id`     INT(10)       UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'タスクの識別子',
  `title`       VARCHAR(255)           NOT NULL                COMMENT 'タスクのタイトル',
  `finished_at` DATETIME                                       COMMENT 'タスクの完了日時',
  `created_at`  DATETIME               NOT NULL,
  `updated_at`  DATETIME               NOT NULL,
  PRIMARY KEY (`task_id`));
