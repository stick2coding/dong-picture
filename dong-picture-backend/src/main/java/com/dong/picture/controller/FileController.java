package com.dong.picture.controller;

import com.dong.picture.annotation.AuthCheck;
import com.dong.picture.common.BaseResponse;
import com.dong.picture.common.ResultUtils;
import com.dong.picture.constant.UserConstant;
import com.dong.picture.exception.BusinessException;
import com.dong.picture.exception.ErrorCode;
import com.dong.picture.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    private CosManager cosManager;

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping(value = "/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile){
        // 获取要上传文件的名字
        String fileName = multipartFile.getOriginalFilename();
        // 拼接存储路径
        String filePath = String.format("/test/%s", fileName);
        File file = null;
        // 开始上传
        try {
            // 创建临时文件
            file = File.createTempFile(filePath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filePath, file);
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("file upload error...filePath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null){
                boolean deleted = file.delete();
                if (!deleted){
                    log.error("file delete error...filePath = " + filePath);
                }
            }
        }
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping(value = "/test/download")
    public void testDownloadFile(String filePath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInputStream = null;
        try {
            // 首先通过key获取到cos下载对象
            COSObject cosObject = cosManager.getObject(filePath);
            // 然后获取输入流
            cosObjectInputStream = cosObject.getObjectContent();
            // 处理下载到的流，转成字节数组
            byte[] bytes = IOUtils.toByteArray(cosObjectInputStream);

            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filePath);

            // 写入到http响应中
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            log.error("file download error...filePath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            // 如果流没有关闭，应该手动关闭
            if (cosObjectInputStream != null){
                cosObjectInputStream.close();
            }
        }
    }

}
