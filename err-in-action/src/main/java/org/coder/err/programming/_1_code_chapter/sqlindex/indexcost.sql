create
    DEFINER = `crayzer`@`` PROCEDURE `insert_person`()
begin
    declare c_id integer default 1;
    while c_id <= 100000
        do
            insert into person values (c_id, concat('name', c_id), c_id + 100, date_sub(NOW(), interval c_id second));
            -- 需要注意，因为使用的是now()，所以对于后续的例子，使用文中的SQL你需要自己调整条件，否则可能看不到文中的效果
            set c_id = c_id + 1;
        end while;
end;
CALL insert_person();

# select user, host from mysql.user;
# CREATE USER 'crayzer'@'%' IDENTIFIED BY '123qwe';
# GRANT ALL PRIVILEGES ON *.* TO 'crayzer'@'%' WITH GRANT OPTION;
# FLUSH PRIVILEGES;

select count(*)
from person;

# 查看数据和索引占用的磁盘：
select DATA_LENGTH, INDEX_LENGTH
from information_schema.TABLES
where TABLE_NAME = 'person';

explain
select *
from person
where NAME = 'name1';

explain
select NAME, SCORE
from person
where NAME = 'name1';
