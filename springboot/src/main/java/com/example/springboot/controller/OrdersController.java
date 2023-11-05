package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.LogType;
import com.example.springboot.common.Result;
import com.example.springboot.common.SysLogs;
import com.example.springboot.entity.Orders;
import com.example.springboot.entity.User;
import com.example.springboot.service.OrdersService;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    OrdersService ordersService;

    @Autowired
    UserService userService;

    /**
     * Add Orders
     */
    @SysLogs(operation = "orders", type = LogType.ADD)
    @PostMapping("/add")
    public Result add(@RequestBody Orders orders) {
        User currentUser = TokenUtils.getCurrentUser();  // get current login user info
        orders.setUserid(currentUser.getId());
        orders.setDate(DateUtil.today());  //   2023-10-08
        orders.setNo(IdUtil.fastSimpleUUID());
        ordersService.save(orders);
        return Result.success();
    }

    /**
     * Edit Orders
     */
    @SysLogs(operation = "orders", type = LogType.UPDATE)
    @PutMapping("/update")
    public Result update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return Result.success();
    }

    /**
     * Delete Orders
     */
    @SysLogs(operation = "orders", type = LogType.DELETE)
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        ordersService.removeById(id);
        return Result.success();
    }

    /**
     * Delete Orders By Batch
     */
    @SysLogs(operation = "orders", type = LogType.BATCH_DELETE)
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        ordersService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * Select All Info about Orders
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Orders> ordersList = ordersService.list(new QueryWrapper<Orders>().orderByDesc("id"));
        return Result.success(ordersList);
    }

    /**
     * Select By Order Id
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Orders orders = ordersService.getById(id);
        User user = userService.getById(orders.getUserid());
        if (user != null) {
            orders.setUser(user.getName());
        }
        return Result.success(orders);
    }


    /**
     * multi-condition query
     * pageNum current page
     * pageSize number of items each page
     */
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String name) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<Orders>().orderByDesc("id");  // default, newest on top
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        Page<Orders> page = ordersService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Orders> records = page.getRecords();
        for (Orders record : records) {
            Integer authorid = record.getUserid();
            User user = userService.getById(authorid);
            if (user != null) {
                record.setUser(user.getName());
            }
        }
        return Result.success(page);
    }
}
