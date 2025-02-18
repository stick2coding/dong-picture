package com.dong.picture.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.dong.picture.config.CosClientConfig;
import com.dong.picture.exception.BusinessException;
import com.dong.picture.exception.ErrorCode;
import com.dong.picture.manager.CosManager;
import com.dong.picture.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 定义模板方法，通过输入内容来进行图片上传
     * @param inputSource
     * @param uploadPathPrefix
     * @return
     */
    public final UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 第一步：文件校验
        //validPicture(multipartFile);
        validPicture(inputSource);
        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        // 获取图片名称
        String originFileName = getOriginFileName(inputSource);
        // 拼接图片上传名称
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFileName));

        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadFileName, null);
            // 处理文件
            processFile(inputSource, file);

            // 第四：上传
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, file);
            // 拿到图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();


            // 第五步：封装返回
            return buildResult(originFileName, file, uploadPath, imageInfo);

        } catch (Exception e) {
            log.error("file upload error...filePath = " + uploadPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 文件上传，这里增加了图片压缩的功能
     * @param inputSource
     * @param uploadPathPrefix
     * @return
     */
    public final UploadPictureResult uploadPictureWithRule(Object inputSource, String uploadPathPrefix) {
        // 第一步：文件校验
        //validPicture(multipartFile);
        validPicture(inputSource);
        // 图片上传地址
        String uuid = RandomUtil.randomString(16);
        // 获取图片名称
        String originFileName = getOriginFileName(inputSource);
        // 拼接图片上传名称
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFileName));

        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadFileName, null);
            // 处理文件
            processFile(inputSource, file);

            // 第四：上传（更换使用图片压缩的方法）
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 拿到图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 拿到压缩处理后的内容
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            // 获取列表
            List<CIObject> ciObjectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(ciObjectList)){
                CIObject compressdCiObject = ciObjectList.get(0);
                // 获取规则2处理后的对象，因为有些图片是没有缩略图的，如果没有，那么默认缩略图就是原图
                CIObject thumbnailCiObject = compressdCiObject;
                if (ciObjectList.size() > 1){
                    thumbnailCiObject = ciObjectList.get(1);
                }
                // 封装压缩后的结果
                //return buildResult(originFileName, compressdCiObject);
                return buildResult(originFileName, compressdCiObject, thumbnailCiObject);
            }


            // 第五步：封装返回
            return buildResult(originFileName, file, uploadPath, imageInfo);

        } catch (Exception e) {
            log.error("file upload error...filePath = " + uploadPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }




    /**
     * 文件校验，这里需要根据不同的输入源进行不同的校验，所以是抽象方法
     * 可以是文件本身
     * 可以是URL
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);


    /**
     * 获取原始文件名，这里需要根据不同的输入源进行不同的获取，所以是抽象方法
     * 可以是文件本身
     * 可以是URL
     * @param inputSource
     * @return
     */
    protected abstract String getOriginFileName(Object inputSource);

    /**
     * 处理文件，这里需要根据不同的输入源进行不同的处理，所以是抽象方法
     * 如果是文件本身，就传递给临时文件
     * 如果是URL，就需要下载
     * @param inputSource
     * @param file
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;


    /**
     * 封装上传结果，这个方法是统一的，不需要进行区分，所以不需要抽象
     * @param originFileName
     * @param file
     * @param uploadPath
     * @param imageInfo
     * @return
     */
    private UploadPictureResult buildResult(String originFileName, File file, String uploadPath, ImageInfo imageInfo) {
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
    }

    /**
     * 这里封装返回的是压缩后的数据
     * @param originFileName
     * @param compressedCiObject
     * @return
     */
    private UploadPictureResult buildResult(String originFileName, CIObject compressedCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFileName));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        // 设置图片为压缩后的地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        return uploadPictureResult;
    }

    /**
     * 封装压缩后、带缩略图的数据
     * @param originFileName
     * @param compressedCiObject
     * @param thumbnailCiObject
     * @return
     */
    private UploadPictureResult buildResult(String originFileName, CIObject compressedCiObject, CIObject thumbnailCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFileName));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        // 设置图片为压缩后的地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());

        // 设置缩略图url
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        return uploadPictureResult;
    }


    /**
     * 删除临时文件，这个方法是统一的，不需要进行区分，所以不需要抽象
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
