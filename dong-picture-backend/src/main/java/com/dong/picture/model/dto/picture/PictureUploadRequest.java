package com.dong.picture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传请求，生成URL
 * 另外由于要将URL信息更新到数据库，因此还需要当前图片的ID信息
 */
@Data
public class PictureUploadRequest implements Serializable {

    /**
     * 图片ID
     */
    private Long id;

    /**
     * 文件地址，如果是通过URL导入，就需要填
     */
    private String fileUrl;

    /**
     * 图片名称
     */
    private String picName;

    private static final long serialVersionUID = 1L;

}
