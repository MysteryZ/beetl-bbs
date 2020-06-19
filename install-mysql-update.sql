

-- ----------------------------
-- 某些模板只读，20190428
-- ----------------------------
ALTER TABLE `bbs`.`bbs_module`
ADD COLUMN `readonly` INT NULL DEFAULT 0 COMMENT '' AFTER `turn`,
ADD COLUMN `admin_list` VARCHAR(120) NULL COMMENT '' AFTER `readonly`;


-- ----------------------------
-- 用户注册校验，发帖xss校验，验证码
-- ----------------------------
ALTER TABLE `bbs`.`bbs_user`
ADD COLUMN `ip` VARCHAR(45) NULL COMMENT '' AFTER `corp`,
ADD COLUMN `register_time` DATETIME NULL COMMENT '' AFTER `ip`;
