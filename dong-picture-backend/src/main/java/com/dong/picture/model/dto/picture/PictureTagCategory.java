package com.dong.picture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PictureTagCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签
     */
    private List<String> tagList;

    /**
     * 分类
     */
    private List<String> categoryList;



}
