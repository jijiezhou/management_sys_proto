package com.example.springboot.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {

    @TableId(type=IdType.AUTO)
    @Alias("id")
    private Integer id;
    @Alias("username")
    private String username;
    @Alias("password")
    private String password;
    @Alias("name")
    private String name;
    @Alias("phone")
    private String phone;
    @Alias("email")
    private String email;
    @Alias("address")
    private String address;
    @Alias("avatar")
    private String avatar;
    @Alias("role")
    private String role;

    @TableField(exist = false)
    private String token;

}
