package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class News {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String title;
    private String description;
    private String content;
    private Integer authorid;
    private String time;

    //This annotation indicates that this field is not in the database table and is used for business processing
    @TableField(exist = false)
    private String author;

}

