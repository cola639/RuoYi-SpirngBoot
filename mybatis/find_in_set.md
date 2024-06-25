  <select id="selectNormalChildrenDeptById" parameterType="Long" resultType="int">
        select count(*)
        from sys_dept
        where status = 0
          and del_flag = '0'
          and find_in_set(#{deptId}, ancestors)
    </select>

### SQL 运行场景

这个SQL查询在某个应用场景下用于获取特定部门的子部门数量。具体来说，这个查询在一个层级结构的部门系统中，
基于部门的祖先链（ancestors）来查找并统计特定部门的子部门数量。以下是这个SQL查询的详细解析和运行场景说明：

### SQL 查询解析

```sql
<
select id = "selectNormalChildrenDeptById" parameterType="Long" resultType="int">
select count(*)
from sys_dept
where status = 0
  and del_flag = '0'
  and find_in_set(#{deptId}, ancestors)
          < /
select>
```

1. **`select count(*)`**：
    - 这个部分表示查询将返回记录数。

2. **`from sys_dept`**：
    - 表示查询的数据来源于 `sys_dept` 表。

3. **`where status = 0`**：
    - 过滤条件，表示只查询状态为0的部门，通常0表示正常状态。

4. **`and del_flag = '0'`**：
    - 过滤条件，表示只查询删除标志为0的部门，通常0表示未删除。

5. **`and find_in_set(#{deptId}, ancestors)`**：
    - 这个条件使用 `find_in_set` 函数来检查 `ancestors` 列中是否包含指定的 `deptId`。`ancestors`
      通常是一个以逗号分隔的字符串，表示该部门的所有上级部门ID。

### 运行场景

这个查询的典型运行场景是一个包含部门层级关系的企业管理系统。例如，在一个公司管理系统中，部门之间有上下级关系，每个部门都有一个 `ancestors`
字段来表示其所有上级部门的ID列表。

#### 示例数据结构

假设 `sys_dept` 表的结构如下：

| dept_id | dept_name | status | del_flag | ancestors |
|---------|-----------|--------|----------|-----------|
| 1       | 总公司       | 0      | 0        |           |
| 2       | 分公司A      | 0      | 0        | 1         |
| 3       | 分公司B      | 0      | 0        | 1         |
| 4       | 分公司A部门1   | 0      | 0        | 1,2       |
| 5       | 分公司A部门2   | 0      | 0        | 1,2       |
| 6       | 分公司B部门1   | 0      | 0        | 1,3       |

#### 查询示例

假设我们想要查询 `dept_id` 为 2 的部门（分公司A）的子部门数量：

- 这个部门的 `deptId` 是 2。
- 在 `ancestors` 列中查找包含 `2` 的所有记录，并且这些记录的 `status` 为 0 且 `del_flag` 为 '0'。

SQL查询的执行如下：

```sql
select count(*)
from sys_dept
where status = 0
  and del_flag = '0'
  and find_in_set(2, ancestors)
```

#### 查询结果

基于示例数据，查询结果会是2，因为 `dept_id` 为 4 和 5 的部门的 `ancestors` 列中都包含 `2`。

| dept_id | dept_name | status | del_flag | ancestors |
|---------|-----------|--------|----------|-----------|
| 4       | 分公司A部门1   | 0      | 0        | 1,2       |
| 5       | 分公司A部门2   | 0      | 0        | 1,2       |

这两个部门都是 `dept_id` 为 2 的部门的子部门，因此结果是2。