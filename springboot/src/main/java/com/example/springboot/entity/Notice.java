package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Notice {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private Integer userid;
    private String time;
    private Boolean open;

    // This annotation indicates that this field is not in the database table and is used for business processing
    @TableField(exist = false)
    private String user;

}

