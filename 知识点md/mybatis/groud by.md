### Group By 详解笔记

---

#### 1. Group By 的基本使用

`GROUP BY` 用于根据一定的规则进行分组统计。以下是一个简单的示例：

```sql
CREATE TABLE staff
(
    id      BIGINT(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    id_card VARCHAR(20) NOT NULL COMMENT '身份证号码',
    name    VARCHAR(64) NOT NULL COMMENT '姓名',
    age     INT(4) NOT NULL COMMENT '年龄',
    city    VARCHAR(64) NOT NULL COMMENT '城市',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='员工表';


INSERT INTO staff (id_card, name, age, city)
VALUES ('123456789012345678', 'Alice', 30, 'Beijing'),
       ('223456789012345678', 'Bob', 25, 'Shanghai'),
       ('323456789012345678', 'Charlie', 35, 'Beijing'),
       ('423456789012345678', 'David', 40, 'Shenzhen'),
       ('523456789012345678', 'Eve', 28, 'Guangzhou'),
       ('623456789012345678', 'Frank', 50, 'Beijing'),
       ('723456789012345678', 'Grace', 32, 'Shanghai'),
       ('823456789012345678', 'Hank', 27, 'Shenzhen'),
       ('923456789012345678', 'Ivy', 29, 'Guangzhou'),
       ('103456789012345678', 'Jack', 33, 'Shanghai');


-- 查询每个城市的员工数量
SELECT city, COUNT(*) AS num
FROM staff
GROUP BY city;
```

---

#### 2. Group By 原理分析

使用 `EXPLAIN` 分析 SQL 的执行计划：

```sql
EXPLAIN
SELECT city, COUNT(*) AS num
FROM staff
GROUP BY city;
```

---

#### 3. Group By + Where 和 Having 的区别

- **Group By + Where 的执行流程**

```sql
-- 加索引
ALTER TABLE staff
    ADD INDEX idx_age (age);

-- 查询
SELECT city, COUNT(*) AS num
FROM staff
WHERE age > 30
GROUP BY city;
```

- **Group By + Having 的执行流程**

```sql
-- 查询每个城市的员工数量，并且员工数量不低于3的城市
SELECT city, COUNT(*) AS num
FROM staff
GROUP BY city
HAVING num >= 3;
```

- **同时使用 Where、Group By、Having 的执行顺序**

```sql
SELECT city, COUNT(*) AS num
FROM staff
WHERE age > 19
GROUP BY city
HAVING num >= 3;
```

---

#### 4. 使用 Group By 的注意事项

- **Group By 必须配合聚合函数使用吗？**

```sql
-- 示例：配合聚合函数使用
SELECT city, COUNT(*) AS num
FROM staff
GROUP BY city;

-- 示例：不配合聚合函数使用
SELECT city, id_card, age
FROM staff
GROUP BY city;
```

- **Group By 后面的字段一定要出现在 Select 中吗？**

```sql
SELECT MAX(age)
FROM staff
GROUP BY city;
```

- **Group By 导致的慢 SQL 问题**

---

#### 5. Group By 的优化方案

- **加索引**

```sql
ALTER TABLE staff
    ADD INDEX idx_age_city (age, city);

-- 查询
SELECT city, COUNT(*) AS num
FROM staff
WHERE age = 19
GROUP BY city;
```

- **避免排序**

```sql
SELECT city, COUNT(*) AS num
FROM staff
GROUP BY city
ORDER BY NULL;
```

- **只使用内存临时表**

```sql
-- 调整 tmp_table_size 参数
SET
tmp_table_size = 1024 * 1024 * 64; -- 64MB
```

- **使用 SQL_BIG_RESULT 优化**

```sql
SELECT SQL_BIG_RESULT city, COUNT(*) AS num
FROM staff
GROUP BY city;
```

---

#### 6. 一个生产慢 SQL 的优化示例

```sql
CREATE TABLE `staff`
(
    `id`            bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `id_card`       varchar(20) NOT NULL COMMENT '身份证号码',
    `name`          varchar(64) NOT NULL COMMENT '姓名',
    `status`        varchar(64) NOT NULL COMMENT 'Y-已激活 I-初始化 D-已删除 R-审核中',
    `age`           int(4) NOT NULL COMMENT '年龄',
    `city`          varchar(64) NOT NULL COMMENT '城市',
    `enterprise_no` varchar(64) NOT NULL COMMENT '企业号',
    `legal_cert_no` varchar(64) NOT NULL COMMENT '法人号码',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='员工表';


-- 原始 慢SQL
SELECT *
FROM staff
WHERE status = #{status}
GROUP BY #{legal_cert_no};

-- 优化建议：加索引
ALTER TABLE staff
    ADD INDEX idx_status_legal_cert_no (status, legal_cert_no);
```

