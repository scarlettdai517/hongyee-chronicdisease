package com.jointcorp.chronicdisease.data.resp.resourceresp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import static com.jointcorp.chronicdisease.data.consts.Consts.ICON;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-24 09:01
 * 附加自定义数据类
 */

@Data
public class Meta {

    private String title;

    private String icon = ICON;

    @JsonProperty(value = "isKeepalive")
    private boolean keepAlive = false;

    @JsonProperty(value = "isAffix")
    private boolean affix = false;

    @JsonProperty(value = "isIframe")
    private boolean iframe = false;

}
