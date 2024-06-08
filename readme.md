### 1. 并发编程实战

[concurrency-in-action](concurrency-in-action)

### 2. 设计模式实战

[design-patterns-in-action](design-patterns-in-action)

### 管理模块版本

- 设置新版本号
  `mvn versions:set -DnewVersion=0.1.2-SNATHOST`
- 更新所有子 Module 的版本
  `mvn versions:update-child-modules`
- 更新顶级项目的parent版本
  `mvn versions:update-parent`