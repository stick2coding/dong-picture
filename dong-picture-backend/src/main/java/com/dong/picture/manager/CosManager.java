package com.dong.picture.manager;

import cn.hutool.core.io.FileUtil;
import com.dong.picture.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 上传图片，增加规则处理
     * 1、图片压缩，格式转为webp
     * @param key
     * @param file
     * @return
     */
    public PutObjectResult putPictureObject(String key, File file){

        PutObjectRequest putObjectRequest =
                new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图像进行处理（新建一个图片操作对象）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);

        // 定义规则（数据万象的能力，可以查看官方sdk）
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片压缩，转成webp格式
        String webpKey = FileUtil.mainName(key) + ".webp";
        // 定义压缩规则
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        // 添加到规则列表中
        rules.add(compressRule);
        // 定义缩略图规则 但有个比较坑的情况，如果上传的图片本身就比较小，缩略图反而比压缩图更大，还不如不缩略！
        // 仅对 > 20 KB 的图片生成缩略图
        if (file.length() > 2 * 1024){
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            // 缩放规则
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s", 128, 128));
            rules.add(thumbnailRule);
        }

        // 将规则放入操作中
        picOperations.setRules(rules);
        // 将操作对象放入请求中
        putObjectRequest.setPicOperations(picOperations);
        // 上传
        return cosClient.putObject(putObjectRequest);


    }


    /**
     * 删除对象
     *
     * @param key 文件 key
     */
    public void deleteObject(String key) throws CosClientException {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
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
