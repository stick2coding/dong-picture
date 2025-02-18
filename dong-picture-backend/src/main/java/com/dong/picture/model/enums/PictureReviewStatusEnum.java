package com.dong.picture.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum PictureReviewStatusEnum {

    REVIEWING("审核中", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);


    private final String test;

    private final Integer value;

    PictureReviewStatusEnum(String test, Integer value) {
        this.test = test;
        this.value = value;
    }


    /**
     * 根据value获取对应的枚举
     */
    public static PictureReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureReviewStatusEnum anEnum : PictureReviewStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
