/*
Navicat MySQL Data Transfer

Source Server         : 学校双核
Source Server Version : 50729
Source Host           : 39.100.138.71:3306
Source Database       : shadow

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2020-11-02 12:38:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for am_device
-- ----------------------------
DROP TABLE IF EXISTS `am_device`;
CREATE TABLE `am_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_tag` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标识符',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` int(255) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of am_device
-- ----------------------------
INSERT INTO `am_device` VALUES ('3', '水表', 'waterMeter', '回表', '2020-09-27 14:39:33', '2020-09-27 14:39:33', '0');
INSERT INTO `am_device` VALUES ('5', '测试', 'Good', '大师傅似的', '2020-09-27 15:17:28', '2020-09-27 15:17:28', '0');
INSERT INTO `am_device` VALUES ('6', '冰箱', 'Icebox', '冰箱', '2020-10-04 22:21:46', '2020-09-29 21:39:39', '0');
INSERT INTO `am_device` VALUES ('7', '测试', 'Test', null, '2020-10-05 14:10:44', '2020-10-05 14:10:44', '0');
INSERT INTO `am_device` VALUES ('8', '速度', '速度', null, '2020-10-05 14:58:19', '2020-10-05 14:58:19', '0');
INSERT INTO `am_device` VALUES ('9', '山东神', '等待', null, '2020-10-10 22:29:56', '2020-10-10 22:29:56', '0');

-- ----------------------------
-- Table structure for am_device_prop_array
-- ----------------------------
DROP TABLE IF EXISTS `am_device_prop_array`;
CREATE TABLE `am_device_prop_array` (
  `id` int(11) NOT NULL,
  `prop_id` int(11) DEFAULT NULL,
  `construction` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of am_device_prop_array
-- ----------------------------

-- ----------------------------
-- Table structure for am_device_prop_child
-- ----------------------------
DROP TABLE IF EXISTS `am_device_prop_child`;
CREATE TABLE `am_device_prop_child` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `prop_id` int(11) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tag` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `construction` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of am_device_prop_child
-- ----------------------------
INSERT INTO `am_device_prop_child` VALUES ('1', '8', '序号', 'serial', 'int', '2020-09-29 21:42:28', '2020-09-29 21:42:31', '0');
INSERT INTO `am_device_prop_child` VALUES ('3', '17', 'qqwe', 'qqwe', 'text', '2020-10-05 13:29:21', '2020-10-05 13:29:21', null);
INSERT INTO `am_device_prop_child` VALUES ('4', '18', 'state', 'state', 'int', '2020-10-05 14:11:22', '2020-10-05 14:11:22', null);

-- ----------------------------
-- Table structure for am_device_property
-- ----------------------------
DROP TABLE IF EXISTS `am_device_property`;
CREATE TABLE `am_device_property` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` int(11) DEFAULT NULL COMMENT 'device id',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备属性名称',
  `struct_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标识符',
  `construction` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据类型',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_tim` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of am_device_property
-- ----------------------------
INSERT INTO `am_device_property` VALUES ('2', '3', 'gddsg', 'dsfgsf', 'float', 'dfsf', '2020-09-27 21:00:35', '2020-09-27 21:21:34', '0');
INSERT INTO `am_device_property` VALUES ('3', '3', 'dfsf', 'sdfsdf', 'double', null, '2020-09-27 21:00:44', '2020-09-27 21:21:35', '0');
INSERT INTO `am_device_property` VALUES ('4', '3', '地方', 'sfdf', 'float', '速度', '2020-09-27 22:50:49', '2020-09-27 22:51:39', '0');
INSERT INTO `am_device_property` VALUES ('6', '3', '士大夫', 'sfdf', 'int', '士大夫', '2020-09-27 22:52:35', '2020-09-27 22:52:35', '0');
INSERT INTO `am_device_property` VALUES ('7', '6', '名称', 'name', 'String', '名称', '2020-09-28 15:58:47', '2020-09-29 21:41:35', '0');
INSERT INTO `am_device_property` VALUES ('8', '6', '灯泡', 'light', 'struct', '灯泡', '2020-09-28 16:10:09', '2020-10-04 22:22:21', '0');
INSERT INTO `am_device_property` VALUES ('12', '3', 'www', 'www', 'struct', null, '2020-10-01 17:42:51', '2020-10-01 17:42:51', '0');
INSERT INTO `am_device_property` VALUES ('13', '3', 'wwq', 'wwq', 'struct', null, '2020-10-01 17:45:51', '2020-10-01 17:45:51', '0');
INSERT INTO `am_device_property` VALUES ('14', '3', '深圳测试', 'sfdf', 'struct', null, '2020-10-02 21:35:01', '2020-10-02 21:35:01', '0');
INSERT INTO `am_device_property` VALUES ('16', '4', '深圳测试', 'www', 'struct', null, '2020-10-02 21:42:06', '2020-10-02 21:42:06', '0');
INSERT INTO `am_device_property` VALUES ('17', '3', 'qqw', 'qqw', 'struct', null, '2020-10-05 13:29:21', '2020-10-05 13:29:21', '0');
INSERT INTO `am_device_property` VALUES ('18', '7', 'qqw', 'qqw', 'struct', null, '2020-10-05 14:11:22', '2020-10-05 14:11:22', '0');

-- ----------------------------
-- Table structure for am_product
-- ----------------------------
DROP TABLE IF EXISTS `am_product`;
CREATE TABLE `am_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_id` int(255) DEFAULT NULL,
  `encryption` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'none' COMMENT '加密方式',
  `operate_system` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'android' COMMENT '操作系统',
  `protocol_size` int(11) DEFAULT NULL,
  `server_size` int(11) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated` int(1) DEFAULT '1',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of am_product
-- ----------------------------
INSERT INTO `am_product` VALUES ('4', 'watermeter', '/root/model//1600064085020.xml', '5', 'none', 'android', '1', '1', '', '2020-10-12 12:40:00', '2020-10-12 12:40:00', '0');
INSERT INTO `am_product` VALUES ('5', 'Icebox', '/root/model/1601821523794.xml', '6', 'none', 'android', '1', '1', '', '2020-10-12 12:40:04', '2020-10-12 12:40:04', '0');
INSERT INTO `am_product` VALUES ('10', 'qqw', '/root/model\\1602340165623.xml', '3', 'none', 'android', '1', '1', '', '2020-10-12 12:40:04', '2020-10-12 12:40:04', '0');
INSERT INTO `am_product` VALUES ('13', 'sss', null, '3', 'rsa', 'Linux', '1', '1', '', '2020-10-12 12:40:17', '2020-10-12 12:40:17', '0');

-- ----------------------------
-- Table structure for am_product_protocol
-- ----------------------------
DROP TABLE IF EXISTS `am_product_protocol`;
CREATE TABLE `am_product_protocol` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL,
  `protocol` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of am_product_protocol
-- ----------------------------
INSERT INTO `am_product_protocol` VALUES ('16', '10', 'mqtt', null, '2020-10-05 14:56:16', '0');
INSERT INTO `am_product_protocol` VALUES ('17', null, 'mqtt', null, '2020-10-12 12:14:49', '0');
INSERT INTO `am_product_protocol` VALUES ('18', null, 'mqtt', null, '2020-10-12 12:31:07', '0');
INSERT INTO `am_product_protocol` VALUES ('19', null, 'mqtt', null, '2020-10-12 12:32:59', '0');

-- ----------------------------
-- Table structure for am_product_server
-- ----------------------------
DROP TABLE IF EXISTS `am_product_server`;
CREATE TABLE `am_product_server` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(255) DEFAULT NULL,
  `server` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of am_product_server
-- ----------------------------
INSERT INTO `am_product_server` VALUES ('23', '10', 'undefined', null, '2020-10-05 14:56:16', '0');
INSERT INTO `am_product_server` VALUES ('24', null, 'undefined', null, '2020-10-12 12:14:49', '0');
INSERT INTO `am_product_server` VALUES ('25', null, 'undefined', null, '2020-10-12 12:31:07', '0');
INSERT INTO `am_product_server` VALUES ('26', null, 'undefined', null, '2020-10-12 12:32:59', '0');

-- ----------------------------
-- Table structure for cm_permission
-- ----------------------------
DROP TABLE IF EXISTS `cm_permission`;
CREATE TABLE `cm_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(60) NOT NULL COMMENT '权限名称',
  `permission_desc` varchar(255) DEFAULT NULL COMMENT '权限描述',
  `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否系统数据',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDXU_CM_PERMISION_NAME` (`permission_name`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
-- Records of cm_permission
-- ----------------------------

-- ----------------------------
-- Table structure for cm_role
-- ----------------------------
DROP TABLE IF EXISTS `cm_role`;
CREATE TABLE `cm_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(60) NOT NULL COMMENT '角色名称',
  `role_desc` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否系统数据',
  `co_level` int(1) NOT NULL COMMENT '所属公司等级',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDXU_CM_ROLE_ROLENAME` (`role_name`,`deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of cm_role
-- ----------------------------
INSERT INTO `cm_role` VALUES ('1', 'am_admin_super', '后台超级管理员', '1', '0', '2018-05-24 16:21:07.277', '2018-07-18 10:13:33.255', '0');

-- ----------------------------
-- Table structure for cm_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `cm_role_permission`;
CREATE TABLE `cm_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色权限ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `permission_id` int(11) NOT NULL COMMENT '权限ID',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `IDX_CM_ROLE_PERMISSION_ROLE_ID` (`role_id`),
  KEY `FK_CM_ROLE_PERMISSION_PERMISSION_ID` (`permission_id`),
  CONSTRAINT `FK_CM_ROLE_PERMISSION_PERMISSION_ID` FOREIGN KEY (`permission_id`) REFERENCES `cm_permission` (`id`),
  CONSTRAINT `IDX_CM_ROLE_PERMISSION_ROLE_ID` FOREIGN KEY (`role_id`) REFERENCES `cm_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8364 DEFAULT CHARSET=utf8 COMMENT='角色权限表';

-- ----------------------------
-- Records of cm_role_permission
-- ----------------------------

-- ----------------------------
-- Table structure for cm_user
-- ----------------------------
DROP TABLE IF EXISTS `cm_user`;
CREATE TABLE `cm_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(60) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `name` char(20) DEFAULT NULL COMMENT '姓名',
  `telephone` varchar(255) DEFAULT NULL COMMENT '联系电话',
  `last_logout_time` datetime DEFAULT NULL COMMENT '上次登出时间',
  `state` int(11) DEFAULT '1' COMMENT '用户状态',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDXU_CM_USER_LOGIN_NAME` (`username`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of cm_user
-- ----------------------------
INSERT INTO `cm_user` VALUES ('1', 'admin', '$2a$10$ylEorWnmL4q4fuYH0vnI7.OTaaiN39jrbVVcD9d.s4MoLc9t7SfV2', '管理员', '15600000000', '2020-09-14 14:06:15', '0', '2020-06-13 10:22:00.864', '2020-09-14 14:06:15.245', '0');

-- ----------------------------
-- Table structure for cm_user_role
-- ----------------------------
DROP TABLE IF EXISTS `cm_user_role`;
CREATE TABLE `cm_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户角色ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) DEFAULT NULL COMMENT '角色ID',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `deleted` int(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `IDX_CM_USER_ROLE_USER_ID` (`user_id`),
  KEY `IDX_CM_USER_ROLE_ROLE_ID` (`role_id`),
  CONSTRAINT `IDX_CM_USER_ROLE_ROLE_ID` FOREIGN KEY (`role_id`) REFERENCES `cm_role` (`id`),
  CONSTRAINT `IDX_CM_USER_ROLE_USER_ID` FOREIGN KEY (`user_id`) REFERENCES `cm_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='用户角色表';

-- ----------------------------
-- Records of cm_user_role
-- ----------------------------
INSERT INTO `cm_user_role` VALUES ('1', '1', '1', '2018-12-25 09:29:31.219', '2018-12-25 09:29:31.219', '0');

-- ----------------------------
-- Table structure for am_product_version
-- ----------------------------
CREATE TABLE `am_product_version` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id` int(11) NOT NULL COMMENT '产品id',
    `version_no` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本号',
    `file_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'xml文件地址',
    `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` int(1) DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品版本表';