-- ----------------------------
--  商品
-- ----------------------------
create table demo_goods
(
    id         BIGINT UNSIGNED not null AUTO_INCREMENT COMMENT '主键ID',
    name       VARCHAR(128)   not null COMMENT '商品名称',
    price      DECIMAL(10, 2) not null default 0.00 COMMENT '商品价格',
    stock      INT UNSIGNED not null default 0 COMMENT '库存数量',
    remarks    VARCHAR(512)            default null COMMENT '备注',
    created_by VARCHAR(64)             default '' COMMENT '创建者',
    updated_by VARCHAR(64)             default '' COMMENT '更新者',
    created_at DATETIME                default current_timestamp COMMENT '创建时间',
    updated_at DATETIME                default current_timestamp on update current_timestamp COMMENT '更新时间',
    status     TINYINT(1) default 0 COMMENT '逻辑删除标识',
    primary key (id),
    INDEX      idx_name ( name)
) ENGINE=InnoDB AUTO_INCREMENT=1 default CHARSET=utf8mb4 COMMENT='商品表';

