package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.LogType;
import com.example.springboot.common.Result;
import com.example.springboot.common.SysLogs;
import com.example.springboot.entity.News;
import com.example.springboot.entity.User;
import com.example.springboot.service.NewsService;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    /**
     * Add News
     */
    @SysLogs(operation = "news", type = LogType.ADD)
    @PostMapping("/add")
    public Result add(@RequestBody News news) {
        User currentUser = TokenUtils.getCurrentUser();  // Get the currently logged in user information
        news.setAuthorid(currentUser.getId());
        news.setTime(DateUtil.now());  //   2023-09-12 21:09:12
        newsService.save(news);
        return Result.success();
    }

    /**
     * Edit News
     */
    @SysLogs(operation = "news", type = LogType.UPDATE)
    @PutMapping("/update")
    public Result update(@RequestBody News news) {
        newsService.updateById(news);
        return Result.success();
    }

    /**
     * Delete News
     */
    @SysLogs(operation = "news", type = LogType.DELETE)
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        newsService.removeById(id);
        return Result.success();
    }

    /**
     * Delete News Batch
     */
    @SysLogs(operation = "news", type = LogType.BATCH_DELETE)
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        newsService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * Query all info
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<News> userList = newsService.list(new QueryWrapper<News>().orderByDesc("id"));
        return Result.success(userList);
    }

    /**
     * Select by Id
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        News news = newsService.getById(id);
        User user = userService.getById(news.getAuthorid());
        if (user != null) {
            news.setAuthor(user.getName());
        }
        return Result.success(news);
    }


    /**
     * Muti-condition query
     * pageNum current page
     * pageSize number of items in one page
     */
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String title) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<News>().orderByDesc("id");  // default: newest on the top
        queryWrapper.like(StrUtil.isNotBlank(title), "title", title);
        Page<News> page = newsService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<News> records = page.getRecords();
//        List<User> list = userService.list();
        for (News record : records) {
            Integer authorid = record.getAuthorid();
            User user = userService.getById(authorid);
//            String author = list.stream().filter(u -> u.getId().equals(authorid)).findFirst().map(User::getName).orElse("");
            if (user != null) {
                record.setAuthor(user.getName());
            }
        }
        return Result.success(page);
    }


}
