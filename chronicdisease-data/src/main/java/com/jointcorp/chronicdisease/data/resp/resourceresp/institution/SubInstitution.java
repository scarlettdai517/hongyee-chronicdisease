package com.jointcorp.chronicdisease.data.resp.resourceresp.institution;

import com.jointcorp.chronicdisease.data.po.Institution;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 子机构信息
 * @Author: Xu-xg
 * @CreateTime: 2023-05-08 10:36
 */
@Data
public class SubInstitution {

    //机构id
    private Long institutionId;
    //父机构id
    private Long parentId;
    //属于哪个合作商
    private Long partnerId;

    public static List<SubInstitution> convert(List<Institution> subInsts) {
        List<SubInstitution> subinsts = new ArrayList<>();
        for(Institution institution : subInsts) {
            SubInstitution subinst = new SubInstitution();
            subinst.setInstitutionId(institution.getInstitutionId());
            subinst.setParentId(institution.getParentId());
            subinst.setPartnerId(institution.getPartnerId());
            subinsts.add(subinst);
        }
        return subinsts;
    }
}
