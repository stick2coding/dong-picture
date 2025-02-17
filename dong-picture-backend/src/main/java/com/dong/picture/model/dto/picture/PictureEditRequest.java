package com.dong.picture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片编辑请求，给普通用户使用，字段权限要小于update
 *
 * @author dong
 */
@Data
public class PictureEditRequest implements Serializable {


    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片介绍
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    public static final long serialVersionUID = 1L;

}
