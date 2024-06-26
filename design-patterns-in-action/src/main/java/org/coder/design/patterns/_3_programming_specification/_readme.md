## 代码规范与代码重构

> 编程规范主要解决的是代码的可读性问题。编码规范相对于设计原则、设计模式，更加具体、更加偏重代码细节、更加能落地。持续的小重构依赖的理论基础主
> 要就是编程规范。

- [单元测试](_1_unit_test)
- [可测试性 + Mock 切入点](_2_testability)
- [编码规范之代码实例](_3_specification)
- [案例分析](_cases)

> 在软件开发中，只要软件在不停地迭代，就没有一劳永逸的设计。随着需求的变化，代码的不停堆砌，原有的设计必定会存在这样那样的问题。针对这些问题，
> 我们就需要进行代码重构。重构是软件开发中非常重要的一个环节。持续重构是保持代码质量不下降的有效手段，能有效避免代码腐化到无可救药的地步。
>
> 而重构的工具就是前面罗列的那些面向对象设计思想、设计原则、设计模式、编码规范。实际上，设计思想、设计原则、设计模式一个最重要的应用场景就
> 是在重构的时候。我们前面讲过，虽然使用设计模式可以提高代码的可扩展性，但过度不恰当地使用，也会增加代码的复杂度，影响代码的可读性。在开发初期，
> 除非特别必须，一定不要过度设计，应用复杂的设计模式。而是当代码出现问题的时候，我们再针对问题，应用原则和模式进行重构。这样就能有效避免前
> 期的过度设计。

要不要重构，那就看重代码是否存在可读、可维护问题等

- 对于重构这部分内容，需要掌握以下几个知识点：
    - 重构的目的（why）、对象（what）、时机（when）、方法（how）；
    - 保证重构不出错的技术手段：单元测试和代码的可测试性；
    - 两种不同规模的重构：大重构（大规模高层次）和小重构（小规模低层次）。
