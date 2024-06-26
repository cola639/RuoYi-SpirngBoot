-- ----------------------------
-- tldraw    白板表结构
-- ----------------------------
DROP TABLE IF EXISTS tldraw;

CREATE TABLE tldraw
(
    id          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id     bigint(20)   NOT NULL COMMENT '创建用户ID',
    title       varchar(255) NOT NULL COMMENT '标题',
    room_id     varchar(255) NOT NULL COMMENT '房间ID',
    members     varchar(255) NOT NULL COMMENT '用户群组',
    type        varchar(50)  DEFAULT '' COMMENT '类型 (0文件夹 1白板)',
    cover       varchar(255) DEFAULT '' COMMENT '封面',
    status      char(1)      default '0' comment '状态（0私有 1公开）',
    remarks     varchar(255) COMMENT '备注',
    create_by   varchar(64)  DEFAULT '' COMMENT '创建者',
    update_by   varchar(64)  DEFAULT '' COMMENT '更新者',
    create_time datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_roomid (room_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  COLLATE = utf8_general_ci COMMENT = 'tldraw表';

-- 插入模拟数据
INSERT INTO tldraw (user_id, title, room_id, members, type, cover, status, remarks, create_by, update_by)
VALUES (1, '项目A白板', 'room001', '1,2,3', '1', '', '0', '这是项目A的白板文件', 'admin', 'admin'),
       (2, '项目B文件夹', 'room002', '2,3,4', '0', '', '1', '这是项目B的文件夹', 'admin', 'admin'),
       (3, '项目C白板', 'room003', '1,3,5', '1', '', '0', '这是项目C的白板文件', 'admin', 'admin'),
       (4, '项目D文件夹', 'room004', '1,2,4', '0', '', '1', '这是项目D的文件夹', 'admin', 'admin'),
       (5, '项目E白板', 'room005', '2,4,5', '1', '', '0', '这是项目E的白板文件', 'admin', 'admin');
