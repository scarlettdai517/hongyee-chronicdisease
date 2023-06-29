package com.jointcorp.chronicdisease.platform.controller;

import com.jointcorp.chronicdisease.data.req.resourceReq.NewResourceAddReq;
import com.jointcorp.chronicdisease.data.req.resourceReq.ResourceUpdateReq;
import com.jointcorp.chronicdisease.platform.service.ResourceService;
import com.jointcorp.parent.result.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/addResource")
    public ResultData addResource(@RequestBody @Validated NewResourceAddReq req) throws Exception {
        return resourceService.add(req);
    }

    @PostMapping("/updateResource")
    public ResultData updateResource(@RequestBody @Validated ResourceUpdateReq req) throws Exception {
        return resourceService.update(req);
    }

    /**
     * 删除资源
     * @param req  key -> value , resourceId -> resourceId
     * @return
     * @throws Exception
     */
    @PostMapping("/delResource")
    public ResultData delResource(@RequestBody @Validated Map<String,String> req) throws Exception {
        return resourceService.delResource(req.get("resourceId"));
    }
}
