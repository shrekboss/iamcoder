DROP TABLE IF EXISTS `category_info_varchar_50`;
CREATE TABLE `category_info_varchar_50`
(
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(50) NOT NULL COMMENT '分类名称',
    `is_show`     tinyint(4)  NOT NULL DEFAULT '0' COMMENT '是否展示：0禁用，1启用',
    `sort`        int(11)     NOT NULL DEFAULT '0' COMMENT '序号',
    `deleted`     tinyint(1)           DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime    NOT NULL COMMENT '创建时间',
    `update_time` datetime    NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_name` (`name`) USING BTREE COMMENT '名称索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='分类';

DROP TABLE IF EXISTS `category_info_varchar_500`;
CREATE TABLE `category_info_varchar_500`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(500) NOT NULL COMMENT '分类名称',
    `is_show`     tinyint(4)   NOT NULL DEFAULT '0' COMMENT '是否展示：0 禁用，1启用',
    `sort`        int(11)      NOT NULL DEFAULT '0' COMMENT '序号',
    `deleted`     tinyint(1)            DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime     NOT NULL COMMENT '创建时间',
    `update_time` datetime     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_name` (`name`) USING BTREE COMMENT '名称索引'
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  DEFAULT CHARSET = utf8mb4 COMMENT ='分类';


# 初始化数据，给每张表插入相同的数据,为了凸显不同,插入100万条数据
DELIMITER $$
CREATE PROCEDURE batchInsertData(IN total INT)
BEGIN
    DECLARE start_idx INT DEFAULT 1;
    DECLARE end_idx INT;
    DECLARE batch_size INT DEFAULT 500;
    DECLARE insert_values TEXT;

    SET end_idx = LEAST(total, start_idx + batch_size - 1);

    WHILE start_idx <= total
        DO
            SET insert_values = '';
            WHILE start_idx <= end_idx
                DO
                    SET insert_values =
                            CONCAT(insert_values, CONCAT('(\'name', start_idx, '\', 0, 0, 0, NOW(), NOW()),'));
                    SET start_idx = start_idx + 1;
                END WHILE;
            SET insert_values = LEFT(insert_values, LENGTH(insert_values) - 1); -- Remove the trailing comma
            SET @sql = CONCAT(
                    'INSERT INTO category_info_varchar_50 (name, is_show, sort, deleted, create_time, update_time) VALUES ',
                    insert_values, ';');

            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            SET @sql = CONCAT(
                    'INSERT INTO category_info_varchar_500 (name, is_show, sort, deleted, create_time, update_time) VALUES ',
                    insert_values, ';');
            PREPARE stmt FROM @sql;
            EXECUTE stmt;

            SET end_idx = LEAST(total, start_idx + batch_size - 1);
        END WHILE;
END$$
DELIMITER ;

CALL batchInsertData(1000000);


select count(*)
from category_info_varchar_50;
select count(*)
from category_info_varchar_500;
