-- 创建 Employees 表
CREATE TABLE Employees
(
    EmployeeID   INT PRIMARY KEY,
    Name         VARCHAR(50),
    DepartmentID INT
);

-- 插入数据到 Employees 表
INSERT INTO Employees (EmployeeID, Name, DepartmentID)
VALUES (1, 'Alice', 101),
       (2, 'Bob', 102),
       (3, 'Charlie', 103);

-- 创建 Departments 表
CREATE TABLE Departments
(
    DepartmentID   INT PRIMARY KEY,
    DepartmentName VARCHAR(50)
);

-- 插入数据到 Departments 表
INSERT INTO Departments (DepartmentID, DepartmentName)
VALUES (101, 'HR'),
       (102, 'IT');

-- 进行左连接查询
SELECT Employees.Name, Departments.DepartmentName
FROM Employees
         LEFT JOIN Departments ON Employees.DepartmentID = Departments.DepartmentID;
