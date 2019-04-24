create table miaosha_user(
id bigint(20) not null,
nickname varchar(255) not null,
password varchar(32) default null,
salt varchar(10) default null,
head varchar(128) default null,
register_date datetime default null,
last_login_date datetime default null,
login_count int(11) default 0,
primary key(id)
)engine=innodb default charset=utf8mb4;

insert into miaosha_user (id,nickname,password,salt,register_date,last_login_date,login_count) values(13119197508,'Iris','b7797cce01b4b131b433b6acf4add449','1a2b3c4d',now(),now(),1);

CREATE TABLE goods(
id bigint(20) not null auto_increment,
goods_name varchar(16) DEFAULT NULL,
goods_title varchar(64) DEFAULT NULL,
goods_img varchar(64) DEFAULT NULL,
goods_detail longtext,
goods_price decimal(10,2) DEFAULT 0.00,
goods_stock int(11) DEFAULT 0,
PRIMARY KEY(id)
)engine=innodb auto_increment=3 default charset=utf8mb4;

INSERT INTO goods VALUES(1,'iphoneX','Apple iphone X(A1865) 64G 银色 移动联通电信4G手机','/img/iphonex.png','Apple iphone X(A1865) 64G 银色 移动联通电信4G手机',8765.00,10000);
INSERT INTO goods VALUES(2,'华为mate9','华为 mate 9 4G+32G 月光 移动联通电信4G手机双卡双待','/img/meta10.png','华为 mate 9 4G+32G 月光 移动联通电信4G手机双卡双待',3212.00,-1);


CREATE TABLE miaosha_goods(
id bigint(20) not null auto_increment,
goods_id bigint(20) DEFAULT NULL,
miaosha_price decimal(10,2) DEFAULT 0.00,
stock_count int(11) DEFAULT NULL,
start_date datetime DEFAULT NULL,
end_date datetime DEFAULT NULL,
PRIMARY KEY(id)
)engine=innodb auto_increment=3 default charset=utf8mb4;

INSERT INTO miaosha_goods VALUES(1,1,0.01,4,'2019-03-15 18:00:00','2019-04-15 18:00:00');
INSERT INTO miaosha_goods VALUES(2,2,0.01,9,'2019-03-16 18:00:00','2019-04-15 18:00:00');

CREATE TABLE order_info(
id bigint(20)not null auto_increment,
user_id bigint(20) DEFAULT NULL,
goods_id bigint(20) DEFAULT NULL,
delivery_addr_id bigint(20) DEFAULT NULL,
goods_name varchar(16) DEFAULT NULL,
goods_count int(11) DEFAULT 0,
goods_price decimal(10,2) DEFAULT 0.00,
order_channel tinyint(4) DEFAULT 0,
status tinyint(4) DEFAULT 0 comment '订单状态 0新建未支付 1已支付 2已发货 3已收货 4已退款 5已完成',
create_date datetime DEFAULT NULL,
pay_date datetime DEFAULT NULL,
PRIMARY KEY(id)
)engine=innodb auto_increment=12 default charset=utf8mb4;

CREATE TABLE miaosha_order(
id bigint(20) NOT NULL AUTO_INCREMENT,
user_id bigint(20) DEFAULT NULL,
order_id bigint(20) DEFAULT NULL,
goods_id bigint(20) DEFAULT NULL,
PRIMARY KEY(id)
)engine=innodb auto_increment=3 default charset=utf8mb4;


