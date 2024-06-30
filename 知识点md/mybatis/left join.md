### 1 左连接（LEFT JOIN）是什么？

左连接（LEFT JOIN）的确是一种将多个表的数据基于某些条件组合起来的方式，但它并不是简单地将所有字段拼接成一个新的数据表。
左连接的特点是它会从左表（即LEFT JOIN关键字左边的表）中取出所有记录，并将右表中匹配的记录与之配对。
如果左表中的某条记录在右表中没有匹配项，则右表中对应的字段会以NULL值出现。

让我们用一个简化的例子来说明：

假设有两个表，一个是员工表（Employees），另一个是部门表（Departments）。

**Employees**

| EmployeeID | Name    | DepartmentID |
|------------|---------|--------------|
| 1          | Alice   | 101          |
| 2          | Bob     | 102          |
| 3          | Charlie | 103          |

**Departments**

| DepartmentID | DepartmentName |
|--------------|----------------|
| 101          | HR             |
| 102          | IT             |

如果我们对这两个表进行左连接，以`DepartmentID`作为连接条件：

```sql
select Employees.Name, Departments.DepartmentName
from Employees
         left join Departments ON Employees.DepartmentID = Departments.DepartmentID;
```

结果会是：

| Name    | DepartmentName |
|---------|----------------|
| Alice   | HR             |
| Bob     | IT             |
| Charlie | NULL           |

在这个例子中，左连接确保了所有员工（Employees表）都被选中，即使他们没有对应的部门（Departments表）。对于Charlie，因为在Departments表中没有与之对应的记录，所以DepartmentName为NULL。

总结来说，左连接是一种SQL连接类型，它会从左表中选择所有记录，并尝试从右表中找到匹配的记录。如果找不到匹配项，则右表的列会以NULL值出现。这种方式允许我们在一个查询中组合和比较来自不同表的数据。

### 2 count  limit

在SQL语句中，`select count(1) ... limit 1` 的含义是对符合条件的记录进行计数，但只返回一条结果。以下是详细解释：

### SQL语句解释

```sql
<
select id = "hasChildByDeptId" parameterType="Long" resultType="int">
select count(1)
from sys_dept
where del_flag = '0'
  and parent_id = #{deptId} limit 1
</
select>
```

### 主要部分解释

1. **`select count(1)`**：
    - `count(1)` 是一个聚合函数，用于计算满足条件的记录数。与 `count(*)` 的效果相同，`count(1)` 的作用是返回符合条件的行数。

2. **`from sys_dept`**：
    - 指定查询的表为 `sys_dept`。

3. **`where del_flag = '0' and parent_id = #{deptId}`**：
    - 过滤条件：
        - `del_flag = '0'`：表示只查询未被删除的记录（假设 `del_flag` 为'0' 表示未删除）。
        - `parent_id = #{deptId}`：表示只查询父部门ID为 `deptId` 的记录。`#{deptId}` 是MyBatis的占位符，会被传入的参数值替换。

4. **`limit 1`**：
    - `limit 1` 限制查询结果只返回一条记录。尽管 `count(1)` 只会返回一条结果，但在这种情况下，`limit 1`
      主要是为了优化查询性能，表示找到第一条符合条件的记录后立即停止进一步的搜索。

### 逻辑解释

虽然 `count(1)` 聚合函数通常会遍历整个结果集来计算符合条件的记录数，但在一些数据库实现中，结合 `limit 1`
使用可能会进行优化，快速确定是否存在至少一条符合条件的记录。具体优化取决于数据库引擎。

### 实际用例

假设我们有如下 `sys_dept` 表数据：

| dept_id | parent_id | del_flag | dept_name     |
|---------|-----------|----------|---------------|
| 1       | 0         | 0        | Headquarters  |
| 2       | 1         | 0        | HR Department |
| 3       | 1         | 1        | IT Department |
| 4       | 2         | 0        | Recruitment   |

调用 `hasChildByDeptId` 并传入 `deptId=1` 时，SQL语句如下：

```sql
select count(1)
from sys_dept
where del_flag = '0'
  and parent_id = 1 limit 1;
```

执行结果：

- `count(1)` 返回 1，因为存在至少一条记录满足条件 `del_flag = '0'` 和 `parent_id = 1`。

### 总结

在这种情况下，`select count(1) ... limit 1` 的作用是检查 `sys_dept` 表中是否存在符合条件的记录，如果存在至少一条记录则返回
1，否则返回 0。这个查询用于快速判断某个部门是否有未被删除的子部门。