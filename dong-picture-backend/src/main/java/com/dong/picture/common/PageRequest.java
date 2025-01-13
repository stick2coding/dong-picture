package com.dong.picture.common;

import lombok.Data;

@Data
public class PageRequest {

    /**
     * 当前页，默认为1
     */
    private int current = 1;

    /**
     * 每页条数，默认为10
     */
    private int pageSize = 10;


    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式，默认为降序
     */
    private String sortOrder = "descend";

}
