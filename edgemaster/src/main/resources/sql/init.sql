CREATE TABLE `edge_namespace` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `description` text COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='命名空间';


CREATE TABLE `edge_user` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '用户名',
  `password` varchar(256) DEFAULT NULL COMMENT '密码',
  `ns_id` varchar(32) NOT NULL COMMENT '命名空间id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `edge_user_UN` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

CREATE TABLE `edge_app` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '应用名称',
  `description` text COMMENT '描述',
  `ns_id` varchar(32) NOT NULL COMMENT '命名空间id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用信息';

CREATE TABLE `edge_app_version` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `app_id` varchar(32) NOT NULL COMMENT '应用id',
  `version_num` varchar(128) NOT NULL COMMENT '版本号',
  `image` varchar(256) DEFAULT NULL COMMENT '镜像名称',
  `running_config` text CHARACTER SET utf8mb4 NOT NULL COMMENT 'json格式容器运行配置，用于启动docker容器。\r\nenv，环境变量，格式["a=1", "b=2"]\r\nportMappings，端口映射，格式["8080:80", "6443:443"]\r\nvolumeMappings，卷映射，格式["/path/on/host:/path/on/container"]',
  `description` text COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用版本';

CREATE TABLE `edge_node` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `ns_id` varchar(32) NOT NULL COMMENT '命名空间id',
  `name` varchar(128) DEFAULT NULL COMMENT '节点名称',
  `ip` varchar(32) DEFAULT NULL COMMENT '节点ip',
  `token` varchar(32) DEFAULT NULL COMMENT '节点token',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点信息';


CREATE TABLE `edge_node_app` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `app_id` varchar(32) NOT NULL COMMENT '应用id',
  `version_id` varchar(32) NOT NULL COMMENT '应用版本id',
  `node_id` varchar(32) NOT NULL COMMENT '节点id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `enabled` tinyint DEFAULT 1 COMMENT '是否可用，逻辑删除标志字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点应用关联表';

CREATE TABLE `edge_app_task_config` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `task_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '运维任务id',
  `app_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '应用id',
  `app_version_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '目标应用版本id',
  `order` int DEFAULT NULL COMMENT '应用运维任务执行顺序',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用操作任务配置信息';

CREATE TABLE `edge_app_task_detail` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `node_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '节点id',
  `app_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '应用id',
  `version_id` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '应用版本id',
  `task_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '运维任务id',
  `accepted` tinyint DEFAULT NULL COMMENT '用户是否已接受，仅升级版本任务使用',
  `status` int NOT NULL COMMENT '当前运维状态，0-待升级；1-升级中；2-升级结束；3-还原中；4-还原结束；5-删除中；6-删除结束；7-部署中；8-部署结束；',
  `result` int DEFAULT NULL COMMENT '运维结果，0-成功，1-失败',
  `reason` text CHARACTER SET utf8mb4 COMMENT '运维结果原因',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='运维任务明细信息';


CREATE TABLE `edge_app_task_info` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `summary` text CHARACTER SET utf8mb4 COMMENT '任务概述',
  `type` int NOT NULL COMMENT '任务类型：0-重启；1-关闭；2-升级',
  `status` int NOT NULL COMMENT '任务状态，0-待执行；1-执行中；2-执行结束',
  `result` int DEFAULT NULL COMMENT '升级结果，0-成功，1-失败',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='节点应用运维任务信息';

CREATE TABLE microedge.edge_docker_registry (
	id varchar(32) NOT NULL,
	url varchar(256) NOT NULL COMMENT 'docker镜像仓库地址',
	user_email varchar(128) NULL COMMENT '用户email',
	user_name varchar(128) NULL COMMENT '用户名',
	password varchar(128) NULL COMMENT '密码',
	is_public tinyint(1) DEFAULT 1 NULL COMMENT '是否为公共仓库（公共仓库可以只通过url访问，不需要账号密码）',
	create_time datetime NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='docker镜像仓库';
