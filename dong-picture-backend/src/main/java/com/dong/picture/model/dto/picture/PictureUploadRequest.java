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

    private static final long serialVersionUID = 1L;

}
