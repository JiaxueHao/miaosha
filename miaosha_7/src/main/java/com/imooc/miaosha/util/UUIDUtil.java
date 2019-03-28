package com.imooc.miaosha.util;

import java.util.UUID;
//生成全局唯一ID
//UUID是通用唯一识别码 (Universally Unique Identifier)，在其他语言中也叫GUID，可以生成一个长度32位的全局唯一识别码。
//缺点：占用长度长，且无序，入库性能差，原因 B+树索引的分裂，会 导致一些中间节点产生分裂，也会白白创造出很多不饱和的节点，这样大大降低了数据库插入的性能。
//代替方案一：snowflake[64bit]（保留位[1]+时间戳[31]+机器ID[10]+序列号ID[12]）
//代替方案二：数据库自增ID，但对数据库产生严重依赖，一旦数据库挂掉，服务不可用
public class UUIDUtil {
	public static String uuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
