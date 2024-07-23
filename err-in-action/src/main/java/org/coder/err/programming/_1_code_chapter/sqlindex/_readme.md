### 数据库索引：索引并不是万能药

### 1. InnoDB是如何存储数据的？

InnoDB 采用页而不是行的粒度来保存数据，即数据被分成若干页，以页为单位保存在磁盘中。InnoDB 的页大小，一般是
16KB。各个数据页组成一个双向链表，每个数据页中的记录按照主键顺序组成单向链表；每一个数据页中有一个页目录，方便按照主键查询记录。

![undefined](http://ww1.sinaimg.cn/large/002eBIeDgy1gu0ehfl2h1j61hg12sn5h02.jpg)

页目录通过槽把记录分成不同的小组，每个小组有若干条记录。记录中最前面的小方块中的数字，代表的是当前分组的记录条数，最小和最大的槽指向
2 个特殊的伪记录。有了槽之后，按照主键搜索页中记录时，就可以采用二分法快速搜索，无需从最小记录开始遍历整个页中的记录链表。

举一个例子，如果要搜索主键（PK）=15 的记录：

- 先二分得出槽中间位是 (0+6)/2=3，看到其指向的记录是 12＜15，所以需要从 #3 槽后继续搜索记录；
- 再使用二分搜索出 #3 槽和 #6 槽的中间位是 (3+6)/2=4.5 取整 4，#4 槽对应的记录是 16＞15，所以记录一定在 #3 槽中；
- 再从 #3 槽指向的 12 号记录开始向下搜索 3 次，定位到 15 号记录。

### 2. 聚簇索引和二级索引

![undefined](http://ww1.sinaimg.cn/large/002eBIeDgy1gu0et3psqej61jg0y077k02.jpg)

#### B+ 树的特点包括

- 最底层的节点叫作叶子节点，用来存放数据；
- 其他上层节点叫作非叶子节点，仅用来存放目录项，作为索引；
- 非叶子节点分为不同层次，通过分层来降低每一层的搜索量；
- 所有节点按照索引键大小排序，构成一个双向链表，加速范围查找。

#### 聚簇索引

InnoDB 使用 B+ 树，既可以保存实际数据，也可以加速数据搜索。由于数据在物理上只会保存一份，所以包含实际数据的聚簇索引只能有一个。

#### 二级索引

![undefined](http://ww1.sinaimg.cn/large/002eBIeDgy1gu0exlhukqj61i80wodji02.jpg)

- 为了实现非主键字段的快速搜索，也叫作非聚簇索引、辅助索引。二级索引，也是利用的 B+ 树的数据结构。
- 二级索引的叶子节点中保存的不是实际数据，而是主键，获得主键值后去聚簇索引中获得数据行。这个过程
  就叫作回表。

#### 联合索引

![undefined](http://ww1.sinaimg.cn/large/002eBIeDgy1gu0fax6lm6j61j415odk102.jpg)

叶子节点每一条记录的第一和第二个方块是索引列的数据，第三个方块是记录的主键。如果我们需要查询的是索引列索引或联合索引能覆盖的数据，那么查询索引本身已经“覆盖”了需要的数据，不再需要回表查询。因此，这种情况也叫作索引覆盖。

### 3. 考虑额外创建二级索引的代价

参考代码：

- [init.sql](init.sql)
- [indexcost.sql](indexcost.sql)

#### 维护代价

- 创建 N 个二级索引，就需要再创建 N 棵 B+ 树，新增数据时不仅要修改聚簇索引，还需要修改
  这 N 个二级索引。
    - 了解如何设置合理的合并阈值，来平衡页的空闲率和因为再次页分裂产生的代价。
    - https://dev.mysql.com/doc/refman/5.7/en/index-page-merge-threshold.html
    - 页中的记录都是按照索引值从小到大的顺序存放的，新增记录就需要往页中插入数据，现有的页满了就需要新创建一个页，把现有页的部分数据移过去，这就是页分裂；如果删除了许多数据使得页比较空闲，还需要进行页合并。页分裂和合并，都会有
      IO 代价，并且可能在操作过程中产生死锁。

#### 空间代价

二级索引不保存原始数据，但要保存索引列的数据，所以会占用更多的空间。

```sql
# 查看数据和索引占用的磁盘
SELECT DATA_LENGTH, INDEX_LENGTH
FROM information_schema.TABLES
WHERE TABLE_NAME = 'person'
```

#### 回表的代价

二级索引不保存原始数据，通过索引找到主键后需要再查询聚簇索引，才能得到数据。EXPLAIN#type：字段代表了访问表的方式

- ref：说明是二级索引等值匹配
- range：表示走索引扫描
- all：全表扫描

#### 关于索引开销的最佳实践

1. 无需一开始就建立索引，可以等到业务场景明确后，或者是数据量超过 1 万、查询变慢后，再针对需要查询、排序或分组的字段创建索引。创建索引后可以使用
   EXPLAIN 命令，确认查询是否可以使用索引。
2. 尽量索引轻量级的字段，比如能索引 int 字段就不要索引 varchar 字段。索引字段也可以是部分前缀，在创建的时候指定字段索引长度。针对长文本的搜索，可以考虑使用
   Elasticsearch 等专门用于文本搜索的索引
   数据库。
3. 尽量不要在 SQL 语句中 SELECT *，而是 SELECT 必要的字段，甚至可以考虑使用联合索引来包含要搜索的字段，既能实现索引加速，又可以避免回表的开销。

### 4. 不是所有针对索引列的查询都能用上索引

参考代码：[notuseindex.sql](notuseindex.sql)

#### 索引失效的情况

- 索引只能匹配列前缀
- 条件涉及函数操作无法走索引
- 联合索引只能匹配左边的列(按照从左往右的索引列进行排序，数据是按照索引第一列排序，第一列数据相
  同时才会按照第二列排序...)
- 有的时候即使可以走索引，MySQL 也不一定会选择使用索引

### 5. 数据库基于成本决定是否走索引：optimizer_trace.sql

参考代码：[optimizer_trace.sql](optimizer_trace.sql)

#### 两个结论

- MySQL 选择索引，并不是按照 WHERE 条件中列的顺序进行的；
- 即便列有索引，甚至有多个可能的索引方案，MySQL 也可能不走索引。

MySQL 在查询数据之前，会先对可能的方案做执行计划，然后依据成本决定走哪个执行计划。
成本，包括 IO 成本和 CPU 成本：

- IO 成本，是从磁盘把数据加载到内存的成本。默认情况下，读取数据页的 IO 成本常数是 1（也就是读取
  1 个页成本是 1）。

- CPU 成本，是检测数据是否满足条件和排序等 CPU 操作的成本。默认情况下，检测记录的成本是 0.2。
  `show table status like 'person';`

rows: 100009 -> cpu 成本 100009*0.2 = 20002
data_length: 4734976 -> io 成本 4734976b / 15kb = 289
权标扫描的总成本：20002 + 289

### 6. 通过 optimizer trace 来分析索引覆盖和回表的两种情况

```sql
SET optimizer_trace = "enabled=on";
select *
from person
where NAME = 'name1';
SELECT *
FROM information_schema.OPTIMIZER_TRACE;
select NAME, SCORE
from person
where NAME = 'name1';
SELECT *
FROM information_schema.OPTIMIZER_TRACE;
SET optimizer_trace = "enabled=off";
```

索引覆盖（index_only=true）的成本是 1.21：

```json
analyzing_range_alternatives": {
"range_scan_alternatives": [
{
"index": "name_score",
"ranges": [
"name1 <= name <= name1"
] /* ranges */,
"index_dives_for_eq_ranges": true,
"rowid_ordered": false,
"using_mrr": false,
"index_only": true,
"rows": 1,
"cost": 1.21,
"chosen": true
}
]
```

回表查询（index_only=false）的成本是 2.21：

```json

"range_scan_alternatives": [
{
"index": "name_score",
"ranges": [
"name1 <= name <= name1"
] /* ranges */,
"index_dives_for_eq_ranges": true,
"rowid_ordered": false,
"using_mrr": false,
"index_only": false,
"rows": 1,
"cost": 2.21,
"chosen": true
}
]
```

### 7.通过 EXPLAIN 查看索引在排序时发挥作用 && 针对排序索引会失效问题

排序使用到索引，在执行计划中的体现就是 key 这一列。如果没有用到索引，会在 Extra 中看到Using
filesort，代表使用了内存或磁盘进行排序。而具体走内存还是磁盘，是由 sort_buffer_size 和排序数据大小决定的。

排序无法使用到索引的情况有：

- 对于使用联合索引进行排序的场景，多个字段排序 ASC 和 DESC 混用；
- a+b 作为联合索引，按照 a 范围查询后按照 b 排序；
- 排序列涉及到的多个字段不属于同一个联合索引；
- 排序列使用了表达式。