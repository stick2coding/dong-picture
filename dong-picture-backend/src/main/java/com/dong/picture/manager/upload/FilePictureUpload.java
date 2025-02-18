package com.dong.picture.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.dong.picture.exception.ErrorCode;
import com.dong.picture.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * 本地图片上传(fileManager中的上传方法)
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate{
    @Override
    protected void validPicture(Object inputSource) {
        // 先转换成对应的类型
        MultipartFile multipartFile = (MultipartFile) inputSource;
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "上传文件不能为空");

        // 复用原来的校验
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        // 文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");

        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final List<String> ALLOW_FROMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "bmp");
        ThrowUtils.throwIf(!ALLOW_FROMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "不支持的文件类型");

    }

    @Override
    protected String getOriginFileName(Object inputSource) {
        // 先转换成对应的类型
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        // 先转换成对应的类型
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }
}
