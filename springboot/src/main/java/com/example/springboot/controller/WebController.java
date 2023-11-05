package com.example.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.AuthAccess;
import com.example.springboot.common.LogType;
import com.example.springboot.common.Result;
import com.example.springboot.common.SysLogs;
import com.example.springboot.entity.Orders;
import com.example.springboot.entity.User;
import com.example.springboot.service.OrdersService;
import com.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * func：provide interface, return data
 * author：jijiez
 * date：2023/10/29
 */
@RestController
public class WebController {

    @Resource
    UserService userService;

    @Resource
    OrdersService ordersService;

    @AuthAccess
    @GetMapping("/")
    public Result hello() {
        return Result.success("success");
    }

    @SysLogs(operation = "user", type = LogType.LOGIN)
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            return Result.error("Data input illegal");
        }
        user = userService.login(user);
        return Result.success(user);
    }

    /**
     * register
     *
     * @param user
     * @return
     */
    @SysLogs(operation = "user", type = LogType.REGISTER)
    @AuthAccess
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword()) || StrUtil.isBlank(user.getRole())) {
            return Result.error("Data input illegal");
        }
        if (user.getUsername().length() > 10 || user.getPassword().length() > 20) {
            return Result.error("Data input illegal");
        }
        user = userService.register(user);
        return Result.success(user);
    }

    /**
     * Reset Password
     */
    @SysLogs(operation = "user", type = LogType.UPDATE)
    @AuthAccess
    @PutMapping("/password")
    public Result password(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPhone())) {
            return Result.error("Input data illegal");
        }
        userService.resetPassword(user);
        return Result.success();
    }

    /**
     * Get Dynamic Echarts
     *
     * @return Dynamic Echart
     */
    @GetMapping("/charts")
    public Result charts() {
        // Wrapping data for a line chart
        List<Orders> list = ordersService.list(); //list of Orders
        Set<String> dates = list.stream().map(Orders::getDate).collect(Collectors.toSet()); //set of dates(no duplicate)
        List<String> dateList = CollUtil.newArrayList(dates); //list of dates
        dateList.sort(Comparator.naturalOrder()); //ascending
        List<Dict> lineList = new ArrayList<>(); //map

        for (String date : dateList) {
            // Count the total sum of total money of all orders in the same date
            // 1. first filter orders with same date
            // 2. then get their money
            // 3. sum
            BigDecimal sum = list.stream().filter(orders -> orders.getDate().equals(date)).map(Orders::getMoney)
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            Dict dict = Dict.create(); //map
            Dict line = dict.set("date", date).set("value", sum);
            lineList.add(line);
        }

        // barCharts
        List<Dict> barList = new ArrayList<>();
        Set<String> categories = list.stream().map(Orders::getCategory).collect(Collectors.toSet());
        for (String cate : categories) {
            //Count the total sum of all amounts on the current date
            BigDecimal sum = list.stream().filter(orders -> orders.getCategory().equals(cate)).map(Orders::getMoney)
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            Dict dict = Dict.create();
            Dict bar = dict.set("name", cate).set("value", sum);
            barList.add(bar);
        }

        // wrapping all data
        Dict res = Dict.create().set("line", lineList).set("bar", barList);
        return Result.success(res);
    }

}
