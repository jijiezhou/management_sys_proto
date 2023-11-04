package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.LogType;
import com.example.springboot.common.Result;
import com.example.springboot.common.SysLogs;
import com.example.springboot.entity.Notice;
import com.example.springboot.entity.User;
import com.example.springboot.service.NoticeService;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    NoticeService noticeService;

    @Autowired
    UserService userService;

    /**
     * Add Notice
     */
    @SysLogs(operation = "notice", type = LogType.ADD)
    @PostMapping("/add")
    public Result add(@RequestBody Notice notice) {
        //Get the currently logged in user information
        User currentUser = TokenUtils.getCurrentUser();
        notice.setUserid(currentUser.getId());
        notice.setTime(DateUtil.now());  //   2023-09-12 21:09:12
        noticeService.save(notice);
        return Result.success();
    }

    /**
     * Edit Notice
     */
    @SysLogs(operation = "notice", type = LogType.UPDATE)
    @PutMapping("/update")
    public Result update(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return Result.success();
    }

    /**
     * Delete Notice
     */
    @SysLogs(operation = "notice", type = LogType.DELETE)
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        noticeService.removeById(id);
        return Result.success();
    }


    /**
     * Delete Notice By Batch
     */
    @SysLogs(operation = "notice", type = LogType.BATCH_DELETE)
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        noticeService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * Select All
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Notice> noticeList = noticeService.list(new QueryWrapper<Notice>().orderByDesc("id"));
        return Result.success(noticeList);
    }

    /**
     * select User Notice
     * @return User Notice
     */
    @GetMapping("/selectUserData")
    public Result selectUserData() {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<Notice>().orderByDesc("id");
        //user can only see isOpen=true
        queryWrapper.eq("open", 1);
        List<Notice> userList = noticeService.list(queryWrapper);
        return Result.success(userList);
    }

    /**
     * select user by Id
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Notice notice = noticeService.getById(id);
        User user = userService.getById(notice.getUserid());
        if (user != null) {
            notice.setUser(user.getName());
        }
        return Result.success(notice);
    }


    /**
     * multi-condition query
     * pageNum current page
     * pageSize number of items per page
     */
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String title) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<Notice>().orderByDesc("id");  // default: newest on the top
        queryWrapper.like(StrUtil.isNotBlank(title), "title", title);
        Page<Notice> page = noticeService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Notice> records = page.getRecords();
//        List<User> list = userService.list();
        for (Notice record : records) {
            Integer authorid = record.getUserid();
            User user = userService.getById(authorid);
//            String author = list.stream().filter(u -> u.getId().equals(authorid)).findFirst().map(User::getName).orElse("");
            if (user != null) {
                record.setUser(user.getName());
            }
        }
        return Result.success(page);
    }


}
