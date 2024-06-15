### 组合模式

将一组对象组织（Compose）成树形结构，以表示一种“部分 - 整体”的层次结构。组合让客户端（在很多设计
模式书籍中，“客户端”代指代码的使用者。）可以统一单个对象和组合对象的处理逻辑。

组合模式，将一组对象组织成树形结构，将单个对象和组合对象都看做树中的节点，以统一处理逻辑，并且它
利用树形结构的特点，递归地处理每个子树，依次简化代码实现。使用组合模式的前提在于，你的业务场景必
须能够表示成树形结构。所以，组合模式的应用场景也比较局限，它并不是一种很常用的设计模式。

### 文件目录

将一组对象（文件和目录）组织成树形结构，以表示一种‘部分 - 整体’的层次结构（目录与子目录的嵌套结构）。组合模式让客户端可以统一单个对象（文件）和组合对象（目录）的处理逻辑（递归遍历）。

参考代码：

- [FileSystemNode.java](refactor%2Fsample01%2FFileSystemNode.java)
- [Directory.java](refactor%2Fsample01%2FDirectory.java)
- [File.java](refactor%2Fsample01%2FFile.java)
- [Demo.java](refactor%2Fsample01%2FDemo.java)

### 组织结构

将一组对象（员工和部门）组织成树形结构，以表示一种‘部分 - 整体’的层次结构（部门与子部门的嵌套结构）。组合模式让客户端可以统一单个对象（员工）和组合对象（部门）的处理逻辑（递归遍历）。

参考代码：

- [HumanResource.java](refactor%2Fsamplep02%2FHumanResource.java)
- [Department.java](refactor%2Fsamplep02%2FDepartment.java)
- [Employee.java](refactor%2Fsamplep02%2FEmployee.java)
- [DepartmentRepo.java](refactor%2Fsamplep02%2FDepartmentRepo.java)
- [EmployeeRepo.java](refactor%2Fsamplep02%2FEmployeeRepo.java)
- [Demo.java](refactor%2Fsamplep02%2FDemo.java)
