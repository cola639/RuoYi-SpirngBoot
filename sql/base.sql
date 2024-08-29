# 弹出sys_user表 加入新字段 source varchar(50)
alter table `sys_user`
    add column `source` VARCHAR(50) null comment '来源';