package com.jointcorp.chronicdisease.platform.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author Xiao
 * @create 2017-07-05 10:16
 **/
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
    //TODO
    //FIXME 特别注意，该接口不能被扫描到，否则会出错

    /**
     *   1.表名默认使用类名,驼峰转下划线(只对大写字母进行处理),如UserInfo默认对应的表名为user_info。
         2.表名可以使用@Table(name = "tableName")进行指定,对不符合第一条默认规则的可以通过这种方式指定表名.
         3.字段默认和@Column一样,都会作为表字段,表字段默认为Java对象的Field名字驼峰转下划线形式.
         4.可以使用@Column(name = "fieldName")指定不符合第3条规则的字段名
         5.使用@Transient(javax.persistence.Transient)注解可以忽略字段,添加该注解的字段不会作为表字段使用.
         6.建议一定是有一个@Id注解作为主键的字段,可以有多个@Id注解的字段作为联合主键.
         7.默认情况下,实体类中如果不存在包含@Id注解的字段,所有的字段都会作为主键字段进行使用(这种效率极低).
         8.实体类可以继承使用,可以参考测试代码中的tk.mybatis.mapper.model.UserLogin2类.
         9.由于基本类型,如int作为实体类字段时会有默认值0,而且无法消除,所以实体类中建议不要使用基本类型.
         10.@NameStyle注解，用来配置对象名/字段和表名/字段之间的转换方式，该注解优先于全局配置style，可选值：

         -- normal:使用实体类名/属性名作为表名/字段名
         -- camelhump:这是默认值，驼峰转换为下划线形式
         -- uppercase:转换为大写
         -- lowercase:转换为小写

        更多 ： http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/3.Use.md
        通用方法 ： http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/5.Mappers.md
     *
     */
}
