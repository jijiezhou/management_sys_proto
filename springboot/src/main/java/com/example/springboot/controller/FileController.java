package com.example.springboot.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.example.springboot.common.AuthAccess;
import com.example.springboot.common.HoneyLogs;
import com.example.springboot.common.LogType;
import com.example.springboot.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${ip:localhost}")
    String ip;

    @Value("${server.port}")
    String port;

    private static final String ROOT_PATH =  System.getProperty("user.dir") + File.separator + "files";  // ~\project\honey2024-master\files

    @HoneyLogs(operation = "文件", type = LogType.ADD)
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();  // original name of file
        // aaa.png
        String mainName = FileUtil.mainName(originalFilename);  // aaa -> not include postfix
        String extName = FileUtil.extName(originalFilename);// png -> postfix of file
        if (!FileUtil.exist(ROOT_PATH)) {
            FileUtil.mkdir(ROOT_PATH);  //If the parent directory of the current file does not exist, create it
        }
        //If the currently uploaded file already exists, then I will rename the file at this time using time created
        if (FileUtil.exist(ROOT_PATH + File.separator + originalFilename)) {
            originalFilename = System.currentTimeMillis() + "_" + mainName + "." + extName;
        }
        File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
        file.transferTo(saveFile);  //Store files to local disk
        //http://localhost:9090/file/download/file_name
        String url = "http://" + ip + ":" + port + "/file/download/" + originalFilename;
        //Returns the link to the file. This link is the download address of the file. This download address is provided by my backend.
        return Result.success(url);
    }

    @AuthAccess
    @GetMapping("/download/{fileName}")
    //input: fileName, response
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
//      response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));  // 附件下载
        response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(fileName, "UTF-8"));  // 预览
        String filePath = ROOT_PATH  + File.separator + fileName;
        //if file not exit
        if (!FileUtil.exist(filePath)) {
            return;
        }
        //read the file, return byte[]
        byte[] bytes = FileUtil.readBytes(filePath);
        //outputStream
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);  // array is byte array, which is the byte stream array of the file
        outputStream.flush();
        outputStream.close();
    }

    @HoneyLogs(operation = "文件", type = LogType.ADD)
    @PostMapping("/editor/upload")
    public Dict editorUpload(@RequestParam MultipartFile file, @RequestParam String type) throws IOException {
        String originalFilename = file.getOriginalFilename();  // original name of file
        // aaa.png
        String mainName = FileUtil.mainName(originalFilename);  // aaa
        String extName = FileUtil.extName(originalFilename);// png
        if (!FileUtil.exist(ROOT_PATH)) {
            FileUtil.mkdir(ROOT_PATH);  // If the parent directory of the current file does not exist, create it
        }
        //If the currently uploaded file already exists, then I will rename the file at this time using time created
        if (FileUtil.exist(ROOT_PATH + File.separator + originalFilename)) {
            originalFilename = System.currentTimeMillis() + "_" + mainName + "." + extName;
        }
        File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
        file.transferTo(saveFile);  // Store files to local disk
        String url = "http://" + ip + ":" + port + "/file/download/" + originalFilename;
        if ("img".equals(type)) {  // upload image
            return Dict.create().set("errno", 0).set("data", CollUtil.newArrayList(Dict.create().set("url", url)));
        } else if ("video".equals(type)) { //upload video
            return Dict.create().set("errno", 0).set("data", Dict.create().set("url", url));
        }
        return Dict.create().set("errno", 0);
    }

}
