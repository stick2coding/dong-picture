package com.dong.picture.manager;

import com.dong.picture.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;

@Component
public class CosManager {

    /**
     * 引入配置类
     */
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 引入客户端类
     */
    @Resource
    private COSClient cosClient;


    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
//    public PutObjectResult putObject(String key, File file) {
//        PutObjectRequest putObjectRequest =
//                new PutObjectRequest(cosClientConfig.getBucket(), key, file);
//        return cosClient.putObject(putObjectRequest);
//    }

    /**
     * 获取对象
     * @param key
     * @return
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest =
                new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传文件，并附带图片信息
     * @param key
     * @param file
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图像进行处理（新建一个图片操作对象）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 将操作对象放入请求中
        putObjectRequest.setPicOperations(picOperations);
        // 上传
        return cosClient.putObject(putObjectRequest);
    }


    // 上传文件
//    public static String uploadToCOS(MultipartFile multipartFile, String bucketName, String key) throws Exception {
//        // 创建 COS 客户端
//        COSClient cosClient = createCOSClient();
//
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            // 元信息配置
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentLength(multipartFile.getSize());
//            metadata.setContentType(multipartFile.getContentType());
//
//            // 创建上传请求
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
//
//            // 上传文件
//            cosClient.putObject(putObjectRequest);
//
//            // 生成访问链接
//            return "https://" + bucketName + ".cos." + cosClient.getClientConfig().getRegion().getRegionName()
//                    + ".myqcloud.com/" + key;
//        } finally {
//            cosClient.shutdown();
//        }
//    }



}
