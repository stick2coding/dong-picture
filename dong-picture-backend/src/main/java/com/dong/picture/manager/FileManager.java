package com.dong.picture.manager;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.dong.picture.config.CosClientConfig;
import com.dong.picture.exception.BusinessException;
import com.dong.picture.exception.ErrorCode;
import com.dong.picture.exception.ThrowUtils;
import com.dong.picture.model.dto.file.UploadPictureResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 这里是通用的文件上传服务
 *
 * 文件的校验、解析（腾讯云对象存储的数据万象）
 */
@Service
@Slf4j
public class FileManager {

    @Autowired
    private CosClientConfig cosClientConfig;

    @Autowired
    private CosManager cosManager;

    /**
     * 上传图片，并返回图片的原信息
     * @param multipartFile
     * @param uploadPathPrefix
     * @return
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix){
        // 增加文件校验
        validPicture(multipartFile);
        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFileName = multipartFile.getOriginalFilename();
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFileName));

        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadFileName, null);
            multipartFile.transferTo(file);
            // 上传
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, file);
            // 拿到图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 封装返回
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            uploadPictureResult.setPicName(FileUtil.mainName(originFileName));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);

            return uploadPictureResult;
        } catch (Exception e) {
            log.error("file upload error...filePath = " + uploadPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }


    /**
     * 校验图片
     * @param multipartFile
     */
    private void validPicture(MultipartFile multipartFile) {
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

    /**
     * 删除临时文件
     * @param file
     */
    public void deleteTempFile(File file){
        if (file == null){
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult){
            log.error("delete temp file error...filePath = " + file.getAbsolutePath());
        }
    }


}
