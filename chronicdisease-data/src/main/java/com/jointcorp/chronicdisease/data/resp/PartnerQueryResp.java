package com.jointcorp.chronicdisease.data.resp;

import com.jointcorp.chronicdisease.data.po.Partner;
import lombok.Data;

import java.util.List;

@Data
public class PartnerQueryResp {
    private Partner partner;
    //private List<ResourceResp> partnerResources;
}
