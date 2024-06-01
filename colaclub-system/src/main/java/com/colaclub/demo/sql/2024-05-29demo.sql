-- ----------------------------
--  表结构
-- ----------------------------
drop table if exists ;

create table 
(
    id          bigint(20) not null auto_increment comment ' ID',
    remarks     varchar(255) comment '备注',
    create_by   varchar(64) default '' comment '创建者',
    update_by   varchar(64) default '' comment '更新者',
    create_time datetime    default current_timestamp comment '创建时间',
    update_time datetime    default current_timestamp on update current_timestamp comment '更新时间',
    primary key (id)
) engine=innodb auto_increment=1 default charset=utf8 collate=utf8_general_ci comment = '表';

