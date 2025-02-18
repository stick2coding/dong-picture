package com.dong.picture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 这个是用于通过搜索引擎搜索指定的图片，然后批量自动化导入系统中的接口参数
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 数量
     */
    private Integer count = 10;


    /**
     * 名称前缀（用于存储到数据库时批量设置图片属性）
     */
    private String namePrefix;

}
