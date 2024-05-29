create database if not exists api;
use api;

drop table if exists api_key;
CREATE TABLE `api_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `apply_no` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请编号',
  `information` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请信息',
  `remark` varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '申请备注',
  `status` tinyint(1) DEFAULT NULL COMMENT '申请状态（0待申请；1待审核 2已通过 3未通过 4已吊销）',
  `api_key` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'api-key',
  `api_secret` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'api-secret',
  `user_id` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id',
  `create_time` BigInt DEFAULT NULL COMMENT '创建时间',
  `update_time` BigInt DEFAULT NULL COMMENT '更新时间',
  `init_tag` tinyint(1) DEFAULT '0' COMMENT '是否已初始化（0-否；1-是)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='跨链apikey记录表';