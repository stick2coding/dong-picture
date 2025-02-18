package com.dong.picture.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员执行审核操作时，传入的参数
 * 这里不需要传审核人和审核时间字段，这两个是由系统自动填充
 */
@Data
public class PictureReviewRequest implements Serializable {

    /**
     * id(这里是图片的ID)
     */
    private Long id;

    /**
     * 审核状态
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;

}
