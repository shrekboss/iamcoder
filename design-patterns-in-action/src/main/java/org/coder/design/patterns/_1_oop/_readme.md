> 面向对象编程中有两个非常重要、非常基础的概念，那就是类（class）和对象（object）。 这两个概念最早出现在 1960 年，在 Simula
> 这种编程语言中第一次使用。而面向对象编程这个概念第一次 被使用是在 Smalltalk 这种编程语言中。Smalltalk
> 被认为是第一个真正意义上的面向对象编程语言。

## 面相对象编程(OOP)

> 现在，主流的编程范式或者是编程风格有三种，它们分别是面向过程、面向对象和函数式编程。面向对象这种编程风格又是这其中最主流的。现在比较流行的编
> 程语言大部分都是面向对象编程语言。大部分项目也都是基于面向对象编程风格开发的。面向对象编程因为其具有丰富的特性（封装、抽象、继承、多态），可
> 以实现很多复杂的设计思路，是很多设计原则、设计模式编码实现的基础。

1. 面向对象的四大特性：封装、抽象、继承、多态
   - 面向对象编程是一种编程范式或编程风格。它以类或对象作为组织代码的基本单元，并将封装、抽象、继承、多态四个特性，作为代码设计和实现的基石。
   - 面向对象编程语言是支持类或对象的语法机制，并有现成的语法机制，能方便地实现面向对象编程四大特性（封装、抽象、继承、多态）的编程语言。
   - 封装也叫作信息隐藏或者数据访问保护。类通过暴露有限的访问接口，授权外部仅能通过类提供的方式（或者叫函数）来访问内部信息或者数据。
   - 抽象讲的是如何隐藏方法的具体实现，让调用者只需要关心方法提供了哪些功能，并不需要知道这些功能是如何实现的。
   - 继承是用来表示类之间的 is-a 关系，作用就是为了复用代码。
   - 多态是指子类可以替换父类，在实际的代码运行过程中，调用子类的方法实现。多态特性能提高代码的可扩展性和复用性。
2. 面向对象编程(oop)与面向过程编程(pop)的区别和联系
3. 面向对象分析、面向对象设计、面向对象编程
4. 接口和抽象类的区别以及各自的应用场景
   - 相对于抽象类的 is-a 关系来说，接口表示一种 has-a 关系，表示具有某些功能。对于接口，有一个更加形象的叫法，那就是协议(contract)。
   - 抽象类更多的是为了代码复用，而接口就更侧重于解耦。
5. 基于接口而非实现编程的设计思想
   - 条原则的设计初衷是，将接口和实现相分离，封装不稳定的实现，暴露稳定的接口。
6. 多用组合少用继承的设计思想
   - 如果类之间的继承结构稳定（不会轻易改变），继承层次比较浅（比如，最多有两层继承关系），继承关系不复杂，我们就可以大胆地使用继承。反之，系统
     越不稳定，继承层次很深，继承关系复杂，我们就尽量使用组合来替代继承。
   - 除此之外，还有一些设计模式会固定使用继承或者组合。比如，装饰者模式（decorator pattern）、策略模式（strategy pattern）、组合模式等都
     使用了组合关系，而模板模式（template pattern）使用了继承关系。
   - 如果你不能改变一个函数的入参类型，而入参又非接口，为了支持多态，只能采用继承来实现。
7. 面向过程的贫血模型和面向对象的充血模型
   - 只包含数据，不包含业务逻辑的类，就叫作贫血模型（Anemic Domain Model）。
   - 充血模型（Rich Domain Model）正好相反，数据和对应的业务逻辑被封装到同一个类中。
   - 即便你对领域驱动搞得再清楚，但是对业务不熟悉，也并不一定能做出合理的领域设计。所以，不要把领域驱动设计当银弹，不要花太多的时间去过度地研
     究它。

### 面向对象的四大特性

#### [封装 - Encapsulation](_1_encapsulation)

之所以这样设计，是因为从业务的角度来说，id、createTime 在创建钱包的时候就确定好了，之后不应该再被改动，所以，我们并没有在
Wallet 类中，暴露 id、createTime 这两个属性的任何修改方法，比如 set 方法。而且，这两个属性的初始化设置，对于 Wallet
类的调用者来说，也应该是透明的，所以，我们在 Wallet 类的构造函数内部将其初始化设置好，而不是通过构造函数的参数来外部赋值。

对于钱包余额 balance 这个属性，从业务的角度来说，只能增或者减，不会被重新设置。所以，我们在 Wallet 类中，只暴露了
increaseBalance() 和 decreaseBalance() 方法，并没有暴露 set 方法。对于 balanceLastModifiedTime 这个属性，它完全是跟 balance
这个属性的修改操作绑定在一起的。只有在 balance 修改的时候，这个属性才会被修改。所以，我们把 balanceLastModifiedTime
这个属性的修改操作完全封装在了 increaseBalance() 和 decreaseBalance() 两个方法中，不对外暴露任何修改这个属性的方法和业务细节。这样也可以保证
balance 和 balanceLastModifiedTime 两个数据的一致性。

- [Wallet.java](_1_encapsulation%2FWallet.java)

[抽象 - Abstraction](_2_abstraction)
  - [IPictureStorage.java](_2_abstraction%2FIPictureStorage.java)
  - [PictureStorage.java](_2_abstraction%2FPictureStorage.java)

[继承 - Inheritance](_3_inheritance)
  - case 1:
    - [ImageStore.java](_3_inheritance%2Fimagestore%2FImageStore.java)
    - [AliyunImageStore.java](_3_inheritance%2Fimagestore%2FAliyunImageStore.java)
    - [PrivateImageStore.java](_3_inheritance%2Fimagestore%2FPrivateImageStore.java)- [IPictureStorage.java](_3_inheritance)
    - [ImageProcessingJob.java](_3_inheritance%2Fimagestore%2FImageProcessingJob.java)
  - case 2: 【接口 + 组合 + 委派】 --替换--> 【继承关系】
    - [Flyable.java](_3_inheritance%2Fbird%2FFlyable.java)
    - [Tweetable.java](_3_inheritance%2Fbird%2FTweetable.java)
    - [EggLayable.java](_3_inheritance%2Fbird%2FEggLayable.java)
    - [FlyAbility.java](_3_inheritance%2Fbird%2FFlyAbility.java)
    - [TweetAbility.java](_3_inheritance%2Fbird%2FTweetAbility.java)
    - [EggLayAbility.java](_3_inheritance%2Fbird%2FEggLayAbility.java)
    - [Ostrich.java](_3_inheritance%2Fbird%2FOstrich.java)
    - [Sparrow.java](_3_inheritance%2Fbird%2FSparrow.java)

[多态 - Polymorphism](_4_polymorphism)
  - case 1:
    - [SortedDynamicArray.java](_4_polymorphism%2FSortedDynamicArray.java)
    - [SortedDynamicArrayTest.java](_4_polymorphism%2FSortedDynamicArrayTest.java)
  - case 2:
    - [Iterator.java](_4_polymorphism%2FIterator.java)
    - [Array.java](_4_polymorphism%2FArray.java)
    - [LinkedList.java](_4_polymorphism%2FLinkedList.java)
    - [IteratorTest.java](_4_polymorphism%2FIteratorTest.java)

[案例分析](_cases)
  - [case 1: 虚拟钱包设计](_cases%2Fvirtualwallet)
    - [_readme.md](_cases%2Fvirtualwallet%2F_readme.md)
  - [case 2: 接口鉴权设计](_cases%2Fauthentication)
    - [_readme.md](_cases%2Fauthentication%2F_readme.md)