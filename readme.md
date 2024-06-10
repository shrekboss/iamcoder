### 1. [并发编程实战](concurrency-in-action)

- [classload](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fclassload)
- [pattern](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fpattern)
- [thread](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fthread)
- [volatile_](concurrency-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fconcurrency%2Fprogramming%2Fvolatile_)

### 2. [设计模式实战](design-patterns-in-action)

- [1. oop](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_1_oop)
- [2. design_principle](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_2_design_principle)
- [3. programming_specification](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_3_programming_specification)
- [4. design_patterns](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_4_design_patterns)
- [5. code_refactoring](design-patterns-in-action%2Fsrc%2Fmain%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_5_code_refactoring)

### 管理模块版本

- 设置新版本号
  `mvn versions:set -DnewVersion=0.1.2-SNATHOST`
- 更新所有子 Module 的版本
  `mvn versions:update-child-modules`
- 更新顶级项目的parent版本
  `mvn versions:update-parent`