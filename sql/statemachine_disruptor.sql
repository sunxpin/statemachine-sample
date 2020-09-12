/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50554
 Source Host           : localhost:3306
 Source Schema         : statemachine_disruptor

 Target Server Type    : MySQL
 Target Server Version : 50554
 File Encoding         : 65001

 Date: 16/07/2020 17:53:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for event
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createDate` datetime NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `entity` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `guard_spel` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sort` int(11) NULL DEFAULT NULL,
  `terminal` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modifyDate` datetime NULL DEFAULT NULL,
  `version` bigint(20) NULL DEFAULT NULL,
  `target_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of event
-- ----------------------------
INSERT INTO `event` VALUES ('1', 'doDisable', 'disable', '2020-07-14 20:03:45', NULL, 'user', NULL, '禁止', 20, 'ORG', '2020-07-14 20:03:46', 1, 2);
INSERT INTO `event` VALUES ('2', 'doEnable', 'enable', '2020-07-14 20:03:45', NULL, 'user', NULL, '有效', 10, 'ORG', '2020-07-14 20:03:46', 1, 3);
INSERT INTO `event` VALUES ('3', 'doKick', 'kick', '2020-07-14 20:03:46', NULL, 'user', NULL, '强制下线', 30, 'ORG', '2020-07-14 20:03:46', 0, 3);

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `uuid` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NULL DEFAULT NULL,
  `entity` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `entity_id` bigint(20) NULL DEFAULT NULL,
  `event` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `params` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  `version` bigint(20) NULL DEFAULT NULL,
  `department_id` bigint(20) NULL DEFAULT NULL,
  `operator_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE,
  INDEX `IDXohlt499pjjkbvhymcthigebev`(`entity`, `entity_id`) USING BTREE,
  INDEX `FK1rqiwe01f4eu65cdrxn8ywc82`(`department_id`) USING BTREE,
  INDEX `FK8vrco0c41q2iukfetnyrs7ctg`(`operator_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `uuid` bigint(20) NOT NULL AUTO_INCREMENT,
  `bought_at` datetime NULL DEFAULT NULL,
  `buy_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  `pay_period` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_state` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  `version` bigint(20) NULL DEFAULT NULL,
  `buyer_id` bigint(20) NULL DEFAULT NULL,
  `city_id` bigint(20) NULL DEFAULT NULL,
  `country_id` bigint(20) NULL DEFAULT NULL,
  `pay_channel_id` bigint(20) NULL DEFAULT NULL,
  `product_sku_id` bigint(20) NULL DEFAULT NULL,
  `province_id` bigint(20) NULL DEFAULT NULL,
  `state_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE,
  INDEX `FK4odf7l3wq1f4up72w6cc0ybqj`(`buyer_id`) USING BTREE,
  INDEX `FKmi547bmcbbjn22jqj65105yw7`(`city_id`) USING BTREE,
  INDEX `FKdvgdyffva42e85yxl22swvah0`(`country_id`) USING BTREE,
  INDEX `FK9caryl6bobyrqx5up5w5y1bjp`(`pay_channel_id`) USING BTREE,
  INDEX `FK8y0pffgtocmj39yhd5h5d13y7`(`product_sku_id`) USING BTREE,
  INDEX `FK476ra2waj9w6r5g6tw90fle09`(`province_id`) USING BTREE,
  INDEX `FKa0lyfl15wni9t4kvyic3tcuog`(`state_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for state
-- ----------------------------
DROP TABLE IF EXISTS `state`;
CREATE TABLE `state`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createDate` datetime NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `enter_action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `entity` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `exit_action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `first_guard_spel` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sort` int(11) NULL DEFAULT NULL,
  `state_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `then_guard_spel` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modifyDate` datetime NULL DEFAULT NULL,
  `version` bigint(20) NULL DEFAULT NULL,
  `first_target_id` bigint(20) NULL DEFAULT NULL,
  `last_target_id` bigint(20) NULL DEFAULT NULL,
  `then_target_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of state
-- ----------------------------
INSERT INTO `state` VALUES ('1', 'created', '2020-07-14 20:03:45', '描述初始状态', NULL, 'user', NULL, NULL, '初始状态', 10, 'BEGIN', NULL, '2020-07-14 20:03:46', 1, NULL, NULL, NULL);
INSERT INTO `state` VALUES ('2', 'disabled', '2020-07-14 20:03:45', '1描述2121', NULL, 'user', NULL, NULL, '禁止状态', 30, 'COMMON', NULL, '2020-07-14 20:03:46', 1, NULL, NULL, NULL);
INSERT INTO `state` VALUES ('3', 'enabled', '2020-07-14 20:03:45', '222描述信息22', NULL, 'user', NULL, NULL, '有效状态', 20, 'COMMON', NULL, '2020-07-14 20:03:46', 1, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for state_event
-- ----------------------------
DROP TABLE IF EXISTS `state_event`;
CREATE TABLE `state_event`  (
  `state_id` bigint(20) NOT NULL,
  `event_id` bigint(20) NOT NULL,
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createDate` datetime NULL DEFAULT NULL,
  `modifyDate` datetime NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of state_event
-- ----------------------------
INSERT INTO `state_event` VALUES (1, 1, '1', '2020-07-16 14:27:00', '2020-07-16 14:27:00');
INSERT INTO `state_event` VALUES (3, 1, '2', '2020-07-16 14:27:00', '2020-07-16 14:27:00');
INSERT INTO `state_event` VALUES (1, 2, '3', '2020-07-16 14:27:00', '2020-07-16 14:27:00');
INSERT INTO `state_event` VALUES (2, 2, '4', '2020-07-16 14:27:00', '2020-07-16 14:27:00');
INSERT INTO `state_event` VALUES (3, 3, '5', '2020-07-16 14:27:00', '2020-07-16 14:27:00');

-- ----------------------------
-- Table structure for timer
-- ----------------------------
DROP TABLE IF EXISTS `timer`;
CREATE TABLE `timer`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createDate` datetime NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `entity` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `time_interval` int(11) NULL DEFAULT NULL,
  `time_once` int(11) NULL DEFAULT NULL,
  `modifyDate` datetime NULL DEFAULT NULL,
  `version` bigint(20) NULL DEFAULT NULL,
  `source_state_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of timer
-- ----------------------------
INSERT INTO `timer` VALUES ('1', 'doSpeak', 'speak', '2020-07-14 20:03:45', NULL, 'user', '说话定时器', NULL, 20, '2020-07-14 20:03:45', 0, 3);
INSERT INTO `timer` VALUES ('2', 'doKick', 'kick', '2020-07-15 08:47:02', NULL, 'user', '被踢定时器', NULL, 20, '2020-07-15 08:47:31', 0, 3);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `createDate` datetime NULL DEFAULT NULL,
  `enabled` bit(1) NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `headimgurl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `position` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modifyDate` datetime NULL DEFAULT NULL,
  `version` bigint(20) NULL DEFAULT NULL,
  `city_id` bigint(20) NULL DEFAULT NULL,
  `country_id` bigint(20) NULL DEFAULT NULL,
  `department_id` bigint(20) NULL DEFAULT NULL,
  `org_id` bigint(20) NULL DEFAULT NULL,
  `province_id` bigint(20) NULL DEFAULT NULL,
  `role_id` bigint(20) NULL DEFAULT NULL,
  `state` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `userinfo_id` bigint(20) NULL DEFAULT NULL,
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '2020-07-14 20:01:40', b'1', NULL, 'https://www.gravatar.com/avatar/21232f297a57a5a743894a0e4a801fc3?d=robohash&s=256', '用户fzhxe5', NULL, '3f336966ad4631b5c323a8aeb0ac97c7', '2020-07-14 20:01:41', 1, NULL, NULL, NULL, NULL, NULL, 1, '3', 1, '13610116072', '123456');
INSERT INTO `user` VALUES ('2', '2020-07-15 08:39:28', b'1', NULL, 'https://www.gravatar.com/avatar/7d8f820d5aa80aca5b8b0ef78da478e9?d=robohash&s=256', '用户gativs', NULL, 'bc3c37697aa75cc9dd23f43bf1a477ec', '2020-07-15 08:39:29', 1, NULL, NULL, NULL, NULL, NULL, 2, '3', 2, '13610116072', '123456');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `createDate` datetime NULL DEFAULT NULL,
  `mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `modifyDate` datetime NULL DEFAULT NULL,
  `version` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1', '2020-07-14 20:01:41', 'admin', '123456', '2020-07-14 20:01:41', 0);
INSERT INTO `user_info` VALUES ('2', '2020-07-15 08:39:29', '13610116072', '123456', '2020-07-15 08:39:29', 0);

SET FOREIGN_KEY_CHECKS = 1;
