-- ----------------------------
-- tldraw    白板表结构
-- ----------------------------
drop table if exists tldraw;

create table tldraw
(
    id          bigint(20)   not null auto_increment comment 'ID',
    user_id     bigint(20)   not null comment '创建用户ID',
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
    primary key (id),
    index idx_user_id (user_id),
    index idx_roomid (room_id)
) engine = InnoDB
  auto_increment = 1
  default charset = utf8
  collate = utf8_general_ci comment = 'tldraw表';

-- 插入模拟数据
insert into tldraw (user_id, title, room_id, members, type, cover, status, remarks, create_by, update_by)
values (1, '项目A白板', 'room001', '1,2,3', '1', '', '0', '这是项目A的白板文件', 'admin', 'admin'),
       (2, '项目B文件夹', 'room002', '2,3,4', '0', '', '1', '这是项目B的文件夹', 'admin', 'admin'),
       (3, '项目C白板', 'room003', '1,3,5', '1', '', '0', '这是项目C的白板文件', 'admin', 'admin'),
       (4, '项目D文件夹', 'room004', '1,2,4', '0', '', '1', '这是项目D的文件夹', 'admin', 'admin'),
       (5, '项目E白板', 'room005', '2,4,5', '1', '', '0', '这是项目E的白板文件', 'admin', 'admin');
