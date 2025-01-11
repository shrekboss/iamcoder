# 第一，索引只能匹配列前缀
explain
select *
from person
where NAME like '%name123'
LIMIT 100;
explain
select *
from person
where NAME like 'name123%'
LIMIT 100;

# 第二，条件涉及函数操作无法走索引
explain
select *
from person
where length(NAME) = 7;

# 第三，联合索引只能匹配左边的列
explain
select *
from person
where SCORE > 45678;
explain
select *
from person
where SCORE > 45678
  and NAME like 'NAME45%';

