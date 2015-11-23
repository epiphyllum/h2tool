-- 默认的没行都是所有表都支持的
-- 后者按数据库类型， 建立不同的migration目录？


drop table if exists contact;

create table contact(
    id int(11) not null auto_increment,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    phone varchar(30) not null,
    memo varchar(255),
    primary key(id)
)
--mysql-- engine = InnoDB
--mysql-- default charset = utf8
--mysql-- auto_increment = 1
;