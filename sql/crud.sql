-- ----------------------------
-- 1、crud表
-- ----------------------------
create table base_crud
(
    id          bigint(20) not null auto_increment comment 'ID',
    uuid        varchar(64)  not null comment '唯一标识',
    user_id     bigint(20) not null comment '创建用户ID',
    title       varchar(255) not null comment '房间标题',
    room_id     varchar(255) not null comment '房间ID',
    members     varchar(255) not null comment '用户群组',
    type        varchar(50)  default '' comment '类型 (0文件夹 1白板)',
    cover       varchar(255) default '' comment '封面',
    status      char(1)      default '0' comment '状态（0私有 1公开）',
    remarks     varchar(255) comment '备注',
    create_by   varchar(64)  default '' comment '创建者',
    update_by   varchar(64)  default '' comment '更新者',
    create_time datetime     default current_timestamp comment '创建时间',
    update_time datetime     default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id)
) engine=innodb
  auto_increment = 1
  comment = 'crud表';