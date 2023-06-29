package com.jointcorp.chronicdisease.platform.mapper;

import com.jointcorp.chronicdisease.data.po.Partner;
import com.jointcorp.chronicdisease.data.resp.resourceresp.PartnerIdResp;
import com.jointcorp.chronicdisease.platform.base.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PartnerMapper extends BaseMapper<Partner> {

//    Partner selectPartner(Long partnerId);
    List<PartnerIdResp> selectAllPartnerIdList();
}
