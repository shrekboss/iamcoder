### 1. [并发编程实战](concurrency-in-action)

- [classload](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fclassload)
- [juc](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fjuc)
- [pattern](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fpattern)
- [thread](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fthread)
- [volatile_](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fvolatile_)

### 2. [设计模式实战](design-patterns-in-action)

- [oop](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_1_oop)
- [design_principle](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_2_design_principle)
- [programming_specification](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_3_programming_specification)
- [design_patterns](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_4_design_patterns)
- [open_source_practices](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_5_open_source_practices)
- [project_practices](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_6_project_practices)

### 管理模块版本

- 设置新版本号
  `mvn versions:set -DnewVersion=0.1.2-SNATHOST`
- 更新所有子 Module 的版本
  `mvn versions:update-child-modules`
- 更新顶级项目的parent版本
  `mvn versions:update-parent`