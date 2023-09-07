-- test.edge_namespace definition

CREATE TABLE `edge_namespace` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `description` text COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='命名空间';


-- test.edge_user definition

CREATE TABLE `edge_user` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(256) DEFAULT NULL COMMENT '密码',
  `ns_id` varchar(32) NOT NULL COMMENT '命名空间id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `edge_user_UN` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';


-- test.edge_app definition

CREATE TABLE `edge_app` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '应用名称',
  `description` text COMMENT '描述',
  `ns_id` varchar(32) NOT NULL COMMENT '命名空间id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用信息';


-- test.edge_app_version definition

CREATE TABLE `edge_app_version` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `app_id` varchar(32) NOT NULL COMMENT '应用id',
  `version_num` varchar(128) NOT NULL COMMENT '版本号',
  `image` varchar(256) DEFAULT NULL COMMENT '镜像名称',
  `description` text COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用版本';


-- test.edge_node definition

CREATE TABLE `edge_node` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `ns_id` varchar(32) NOT NULL COMMENT '命名空间id',
  `name` varchar(128) DEFAULT NULL COMMENT '节点名称',
  `ip` varchar(32) DEFAULT NULL COMMENT '节点ip',
  `token` varchar(32) DEFAULT NULL COMMENT '节点token',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点信息';


-- test.edge_node_app definition

CREATE TABLE `edge_node_app` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `app_id` varchar(32) NOT NULL COMMENT '应用id',
  `version_id` varchar(32) NOT NULL COMMENT '应用版本id',
  `node_id` varchar(32) NOT NULL COMMENT '节点id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点应用关联表';