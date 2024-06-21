-- ----------------------------
-- tldraw    表结构
-- ----------------------------
drop table if exists tldraw;

create table tldraw
(
    id          bigint(20) not null auto_increment comment 'ID',
    user_id     bigint(20) not null auto_increment comment '用户ID',
    title         varchar(500) default '' comment '标题',
    roomId    varchar(500) default '' comment '房间ID',
    userId      varchar(500) default '' comment '用户ID',
    type         varchar(500) default '' comment '类型 文件夹或白板文件',
    cover       varchar(500) default '' comment '封面',
    isOpen      varchar(500) default '' comment '是否公开',
    remarks     varchar(255) comment '备注',
    create_by   varchar(64)  default '' comment '创建者',
    update_by   varchar(64)  default '' comment '更新者',
    create_time datetime     default current_timestamp comment '创建时间',
    update_time datetime     default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id)
) engine=innodb auto_increment=1 default charset=utf8 collate=utf8_general_ci comment = 'tldraw表';

