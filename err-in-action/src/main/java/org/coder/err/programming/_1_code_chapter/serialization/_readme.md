## 序列化：一来一回你还是原来的你吗？

- 使用 RedisTemplate 来操作 Redis 进行是数据缓存，相比于 Jedis ，除了无需考虑连接池、更方便外，还可以与 Spring Cache
  等其他组件无缝整合。如果使用 Spring Boot 的话，无需任何配置就可以直接使用

- 枚举不建议定义在 DTO 中跨服务传输，因为会有版本问题，并且涉及序列化反序列化时会很复杂，容易出错。因此，我只建议在程序内部使用枚举

在调试序列化反序列化问题时，捋清楚三点：

- 是哪个组件在做序列化反序列化
- 整个过程有几次序列化反序列化
- 以及目前到底是序列化还是反序列化。

### 1. 序列化和反序列化需要确保算法一致

参考代码：[redistemplate](redistemplate)

Spring 提供的 4 种 RedisSerializer（Redis 序列化器）：

- 默认情况下，RedisTemplate 使用 JdkSerializationRedisSerializer，也就是 JDK 序列化，容易产生 Redis
  中保存了乱码的错觉。
- 通常考虑到易读性，可以设置 Key 的序列化器为 StringRedisSerializer。但直接使用 RedisSerializer.string()，相当于使用了
  UTF_8 编码的 StringRedisSerializer，需要注意字符集问题。
- 如果希望 Value 也是使用 JSON 序列化的话，可以把 Value 序列化器设置为 Jackson2JsonRedisSerializer。
  默认情况下，不会把类型信息保存在 Value 中，即使我们定义 RedisTemplate 的 Value 泛型为实际类型，查询出的 Value 也只能是
  LinkedHashMap 类型。
    - 如果希望直接获取真实的数据类型，你可以启用 Jackson ObjectMapper 的 activateDefaultTyping
      方法，把类型信息一起序列化保存在 Value 中。
    - 如果希望 Value 以 JSON 保存并带上类型信息，更简单的方式是，直接使用 RedisSerializer.json()
      快捷方法来获取序列化器。

### 2. 注意Jackson JSON反序列化对额外字段的处理

参考代码：[jsonignoreproperties](jsonignoreproperties)

> 在开发 Spring Web 应用程序时，如果自定义了 ObjectMapper，并把它注册成了 Bean，那很可能会导致 Spring Web 使用的
> ObjectMapper 也被替换，导致 Bug。

#### 自定义 ObjectMapper

> 希望修改一下 ObjectMapper 的行为，让枚举序列化为索引值而不是字符串值，比如默认情况下序列化一个
> Color 枚举中的 Color.BLUE 会得到字符串 BLUE

```java
@Bean
public ObjectMapper objectMapper(){
    ObjectMapper objectMapper=new ObjectMapper();
    objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX,true);
    return objectMapper;
}
```

##### 开启这个特性后，Color.BLUE 枚举序列化成索引值 1:

[16:11:37.382] [http-nio-45678-exec-1] [INFO ] [c.s.d.JsonIgnorePropertiesController:19 ] - color:1

##### 修改后处理枚举序列化的逻辑是满足了要求，但线上爆出了大量 400 错误，日志中也出现了很多

UnrecognizedPropertyException：

```
JSON parse error: Unrecognized field \"ver\" (class ...UserWrong), not marked as ignorable; nested 
exception is com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field 
\"version\" (class ...UserWrong), not marked as ignorable (one known property: \"name\"])\n at 
[Source: (PushbackInputStream); line: 1, column: 22] (through reference chain: UserWrong[\"ver\"])
```

从异常信息中可以看到，这是因为反序列化的时候，原始数据多了一个 version 属性。进一步分析发现，使用了 UserWrong 类型作为 Web
控制器 wrong 方法的入参，其中只有一个 name 属性

##### 客户端实际传过来的数据多了一个 version 属性。那，为什么之前没这个问题呢？

问题就出在:

1. 自定义 ObjectMapper 启用 WRITE_ENUMS_USING_INDEX 序列化功能特性时，覆盖了 Spring Boot 自
   动创建的 ObjectMapper；
2. 而这个自动创建的 ObjectMapper 设置过 FAIL_ON_UNKNOWN_PROPERTIES 反序列化特性为 false，
   以确保出现未知字段时不要抛出异常。源码如下：

```java
public MappingJackson2HttpMessageConverter() {
  this(Jackson2ObjectMapperBuilder.json().build());
}

public class Jackson2ObjectMapperBuilder {
    ...
  private void customizeDefaultFeatures(ObjectMapper objectMapper) {
    if (!this.features.containsKey(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
      configureFeature(objectMapper, MapperFeature.DEFAULT_VIEW_INCLUSION, false);
    }
    if (!this.features.containsKey(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
      configureFeature(objectMapper, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
  }
}
```

##### 修复这个问题，有三种方式：

- 第一种，同样禁用自定义的 ObjectMapper 的 FAIL_ON_UNKNOWN_PROPERTIES：
  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
- 第二种，设置自定义类型，加上 @JsonIgnoreProperties 注解，开启 ignoreUnknown 属性，以实现反序列
  化时忽略额外的数据：

```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRight {
    private String name;
}
```

- 第三种，不要自定义 ObjectMapper，而是直接在配置文件设置相关参数，来修改 Spring 默认的
  ObjectMapper 的功能。比如，直接在配置文件启用把枚举序列化为索引号：
  spring.jackson.serialization.write_enums_using_index=true
  或者可以直接定义 Jackson2ObjectMapperBuilderCustomizer Bean 来启用新特性：

```java
@Bean
public Jackson2ObjectMapperBuilderCustomizer customizer(){
    return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
}
```

##### 总结

- Jackson 针对序列化和反序列化有大量的细节功能特性，可以参考 Jackson 官方文档来了解这些特性，详见
  [SerializationFeature](https://fasterxml.github.io/jackson-databind/javadoc/2.10/com/fasterxml/jackson/databind/SerializationFeature.html)
  [DeserializationFeature](https://fasterxml.github.io/jackson-databind/javadoc/2.10/com/fasterxml/jackson/databind/DeserializationFeature.html)
  [MapperFeature](https://fasterxml.github.io/jackson-databind/javadoc/2.10/com/fasterxml/jackson/databind/MapperFeature.html)
- 忽略多余字段，是写业务代码时最容易遇到的一个配置项。Spring Boot 在自动配置时贴心地做了全局设置。如果需要设置更多的特性，可以直接修改配置文件
  spring.jackson. 或设置 Jackson2ObjectMapperBuilderCustomizer 回调接口，来启用更多设置，无需重新定义 ObjectMapper Bean。

### 3. 反序列化时要小心类的构造方法

参考代码：[deserializationconstructor](deserializationconstructor)

默认情况下，在反序列化的时候，Jackson 框架只会调用无参构造方法创建对象。如果走自定义的构造方法创建对象，需要通过
@JsonCreator 来指定构造方法，并通过 @JsonProperty 设置构造方法中参数对应的 JSON 属性名

### 4. 枚举作为API接口参数或返回值的两个大坑

参考代码：[enumusedinapi](enumusedinapi)

枚举不建议定义在 DTO 中跨服务传输，因为会有版本问题，并且涉及序列化反序列化时会很复杂，容易出错。因此，只建议在程序内部使用枚举

### 5. 序列化版本号问题

参考代码：[serialversionissue](serialversionissue)

实现java.io.Serializable这个接口是为序列化,serialVersionUID 用来表明实现序列化类的不同版本间的兼容性。如果你修改了此类,
要修改此值。否则以前用老版本的类序列化的类恢复时会出错。