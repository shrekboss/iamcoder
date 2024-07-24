## 空值处理：分不清楚的null和恼人的空指针

### 1. 修复和定位恼人的空指针问题

参考代码：[avoidnullpointerexception](avoidnullpointerexception)

[Arthas Java诊断工具](https://arthas.aliyun.com/)

[Arthas 命令列表](https://arthas.aliyun.com/doc/commands.html)

#### 最可能出现的场景归为以下 5 种

- 参数值是 Integer 等包装类型，使用时因为自动拆箱出现了空指针异常；
- 字符串比较出现空指针异常；
- 诸如 ConcurrentHashMap 这样的容器不支持 Key 和 Value 为 null，强行put null 的 Key 或 Value 会出现空指针异常；
- 对象包含了 B，在通过 A 对象的字段获得 B 之后，没有对字段判空就级联调用 B 的方法出现空指针异常；
- 方法或远程服务返回的 List 不是空而是 null，没有进行判空就直接调用 List 的方法出现空指针异常。

#### 解决方案

- 对于 Integer 的判空，可以使用 Optional.ofNullable 来构造一个 Optional，然后使用 orElse(0) 把 null 替换为默认值再进行 +1
  操作。
- 对于 String 和字面量的比较，可以把字面量放在前面，比如"OK".equals(s)，这样即使 s 是 null 也不会出现空指针异常；
- 而对于两个可能为 null 的字符串变量的 equals 比较，可以使用 Objects.equals，它会做判空处理。
- 对于 ConcurrentHashMap，既然其 Key 和 Value 都不支持 null，修复方式就是不要把 null 存进去。HashMap 的 Key 和 Value 可以存入
  null，而 ConcurrentHashMap 看似是 HashMap 的线程安全版本，却不支持 null 值的 Key 和 Value，这是容易产生误区的一个地方。
    - Hashtable 也是线程安全的，所以 Key 和 Value 不可以是 null。
    - TreeMap 是线程不安全的，但是因为需要排序，需要进行 key 的 compareTo 方法，所以 Key 不能是 null，而 Value 可以是 null。
- 对于类似 fooService.getBarService().bar().equals(“OK”) 的级联调用，需要判空的地方有很多，包括 fooService、getBarService()
  方法的返回值，以及 bar 方法返回的字符串。如果使用 if-else 来判空的话可能需要好几行代码，但使用 Optional 的话一行代码就够了。
- 对于 rightMethod 返回的 List，由于不能确认其是否为 null，所以在调用 size 方法获得列表大小之前，同样可以使用
  Optional.ofNullable 包装一下返回值，然后通过 .orElse(Collections.emptyList()) 实现在 List 为 null 的时候获得一个空的
  List，最后再调用 size 方法。

### 2. POJO中属性的null到底代表了什么？

参考代码：[pojonull](pojonull)

null 就是指针没有任何指向，而结合业务逻辑情况就复杂得多，我们需要考虑：

- DTO 中字段的 null 到底意味着什么？是客户端没有传给我们这个信息吗？
- 既然空指针问题很讨厌，那么 DTO 中的字段要设置默认值么？
- 如果数据库实体中的字段有 null，那么通过数据访问框架保存数据是否会覆盖数据库中的既有数据？

归根结底，这是如下 5 个方面的问题

- 明确 DTO 中 null 的含义。对于 JSON 到 DTO 的反序列化过程，null 的表达是有歧义的，客户端不传某个属性，或者传 null，这个属性在
  DTO 中都是 null。但，对于用户信息更新操作，不传意味着客户端不需要更新这个属性，维持数据库原先的值；传了
  null，意味着客户端希望重置这个属性。因为 Java 中的 null 就是没有这个数据，无法区分这两种表达，可以借助 Optional 来解决这个问题。
- POJO 中的字段有默认值。如果客户端不传值，就会赋值为默认值，导致创建时间也被更新到了数据库中。
- 注意字符串格式化时可能会把 null 值格式化为 null 字符串。
- DTO 和 Entity 共用了一个 POJO。对于用户昵称的设置是程序控制的，我们不应该把它们暴露在 DTO
  中，否则很容易把客户端随意设置的值更新到数据库中。此外，创建时间最好让数据库设置为当前时间，不用程序控制，可以通过在字段上设置
  columnDefinition 来实现。
- 数据库字段允许保存 null，会进一步增加出错的可能性和复杂度。

### 3. 小心数据库NULL字段的三个坑

参考代码：[dbnull](dbnull)

- MySQL 中 sum 函数没统计到任何记录时，会返回 null 而不是 0，可以使用 IFNULL 函数把 null 转换为 0；
- MySQL 中 count 字段不统计 null 值，COUNT(*) 才是统计所有记录数量的正确方式。
- MySQL 中使用诸如 =、<、> 这样的算数比较操作符比较 NULL 的结果总是 NULL，这种比较就显得没有任何意义，需要使用 IS NULL、IS
  NOT NULL 或 ISNULL() 函数来比较。

对于 Hibernate 框架可以使用 @DynamicUpdate 注解实现字段的动态更新，对于 MyBatis 框架如何实现类似的动态 SQL 功能，实现插入和修改
SQL 只包含 POJO 中的非空字段？

MyBatis 可以通过动态 SQL 实现：

```XML

<select id="findUser" resultType="User">
    SELECT * FROM USER
    WHERE 1=1
    <if test="name != null">
        AND name like #{name}
    </if>
    <if test="email != null">
        AND email = #{email}
    </if>
</select>
```

使用 MyBatisPlus 的话，实现类似的动态 SQL 功能会更方便。我们可以直接在字段上加 @TableField 注解来实现，可以设置
insertStrategy、updateStrategy、whereStrategy 属性

```JAVA

/**
 * 字段验证策略之 insert: 当insert操作时，该字段拼接insert语句时的策略
 * IGNORED: 直接拼接 insert into table_a(column) values (#{columnProperty});
 * NOT_NULL: insert into table_a(<if test="columnProperty != null">column</if>) values (<if test="columnProperty != null">#{columnProperty}</if>)
 * NOT_EMPTY: insert into table_a(<if test="columnProperty != null and columnProperty!=''">column</if>) values (<if test="columnProperty != null and columnProperty!=''">#{columnProperty}</if>)
 *
 * @since 3.1.2
 */
FieldStrategy insertStrategy() default FieldStrategy.DEFAULT;

/**
 * 字段验证策略之 update: 当更新操作时，该字段拼接set语句时的策略
 * IGNORED: 直接拼接 update table_a set column=#{columnProperty}, 属性为null/空string都会被set进去
 * NOT_NULL: update table_a set <if test="columnProperty != null">column=#{columnProperty}</if>
 * NOT_EMPTY: update table_a set <if test="columnProperty != null and columnProperty!=''">column=#{columnProperty}</if>
 *
 * @since 3.1.2
 */
FieldStrategy updateStrategy() default FieldStrategy.DEFAULT;

/**
 * 字段验证策略之 where: 表示该字段在拼接where条件时的策略
 * IGNORED: 直接拼接 column=#{columnProperty}
 * NOT_NULL: <if test="columnProperty != null">column=#{columnProperty}</if>
 * NOT_EMPTY: <if test="columnProperty != null and columnProperty!=''">column=#{columnProperty}</if>
 *
 * @since 3.1.2
 */
FieldStrategy whereStrategy() default FieldStrategy.DEFAULT;
```