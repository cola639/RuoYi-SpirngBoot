-- ----------------------------
-- 1 用户聊天表
-- ----------------------------
drop table if exists user_chat;
create table user_chat (
                           id                bigint(20)      not null auto_increment    comment '聊天记录ID',
                           user_id           bigint(20)      not null                   comment '用户ID',
                           create_time       datetime                                   comment '创建时间',
                           update_time       datetime                                   comment '更新时间',
                           message           LONGTEXT                                   comment '聊天',
                           message_type      varchar(20)     default 'text'             comment '消息类型',
                           primary key (id),
                           index idx_user_id (user_id)
) engine=innodb  comment = '用户聊天表';
-- ----------------------------
-- 初始化-聊天表数据
-- ----------------------------
insert into user_chat values(100,  0,   sysdate(),    null,   '测试聊天信息1', 'text');
insert into user_chat values(101,   1,   sysdate(),    null,   '测试聊天信息2', 'text');
insert into user_chat values(102,    2,   sysdate(),    null,   '测试聊天信息3', 'text');
insert into user_chat values(103,    3,   sysdate(),    null,   '测试聊天信息4', 'text');
insert into user_chat values(104,    4,   sysdate(),    null,   '测试聊天信息5', 'text');
insert into user_chat values(105, 5,   sysdate(),    null,   '测试聊天信息6', 'text');

