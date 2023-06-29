package com.jointcorp.chronicdisease.data.po;

import lombok.Data;

/**
 * @Author zHuH1
 * @Date 2023/5/9 13:56
 **/
@Data
public class PageRequest {

    private int page = 1;
    private int size = 10;

}
