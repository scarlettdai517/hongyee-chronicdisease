package com.jointcorp.chronicdisease.data.resp.resourceresp;

import com.jointcorp.chronicdisease.data.po.Resource;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Yulin Dai
 * @CreateTime: 2023-04-24 09:08
 * 把resource转换成需要返回给前端的权限列表形式
 */
public class ResourceRespConvert {

    /**
     * 把通过用户ID得到的资源列表中的资源，转换为　前端需要的资源格式类列表
     *
     * @param resources 用户已拥有的资源
     * @param allRes 所有资源
     * @return
     */
    public static List<Object> resourceList(List<Resource> resources,List<Resource> allRes) {
        List<Res> resList = Res.convert(resources,allRes);

        List<Object> list = new ArrayList<>();
        for (Res resource:  resList){
            if("menu".equals(resource.getResourceType())) {
                if (resource.getParentId() == 0){
                    UpperMenuWithRed upperMenuWithRed = convertUMWR(resource,resList);
                    upperMenuWithRed.setShow(resource.isShow());
                    list.add(upperMenuWithRed);
                }else{
                    UpperMenu upperMenu = convertUM(resource);
                    upperMenu.setShow(resource.isShow());
                    list.add(upperMenu);
                }
            }
        }
        return list;
    }

    //没有子资源的目标类转换
    public static UpperMenu convertUM(Res resource){
        UpperMenu upperMenu = new UpperMenu();
        Meta meta = commonMeta(resource);
        upperMenu.setPath(resource.getPath());
        upperMenu.setName(resource.getName());
        if(StringUtils.isNotBlank(resource.getRedirect())){
            upperMenu.setComponent(resource.getComponent());
        }
        upperMenu.setMeta(meta);
        upperMenu.setResourceId(resource.getResourceId().toString());
        return upperMenu;
    }

    //有子资源的目标类转换
    public static UpperMenuWithRed convertUMWR(Res resource, List<Res> resources) {

        UpperMenuWithRed upperMenuWithRed = new UpperMenuWithRed();
        Meta meta = commonMeta(resource);
        upperMenuWithRed.setPath(resource.getPath());
        upperMenuWithRed.setName(resource.getName());
        if (StringUtils.isNotBlank(resource.getRedirect())) {
            upperMenuWithRed.setComponent(resource.getComponent());
        }
        if (StringUtils.isNotBlank(resource.getRedirect())) {
            upperMenuWithRed.setRedirect(resource.getRedirect());
        }
        upperMenuWithRed.setMeta(meta);
        upperMenuWithRed.setResourceId(resource.getResourceId().toString());
        List<Object> childList = findChild(resources, resource.getResourceId());

        upperMenuWithRed.setChildren(childList);
        return upperMenuWithRed;

    }

    //找到子资源列表并转化为目标类列表
    public static List<Object> findChild(List<Res> resources, long resourceId){
        Map<Long, List<Res>> resMap = resources.stream().collect(Collectors.groupingBy(Res::getParentId));

        List<Object> childList = new ArrayList<>();
        for (Res r:  resources){
            if (r.getParentId() == resourceId){
                if("menu".equals(r.getResourceType())) {
                    UpperMenuWithRedChild upperMenuWithRedChild = new UpperMenuWithRedChild();
                    Meta meta = commonMeta(r);
                    upperMenuWithRedChild.setPath(r.getPath());
                    upperMenuWithRedChild.setName(r.getName());
                    upperMenuWithRedChild.setShow(r.isShow());
                    if(StringUtils.isNotBlank(r.getComponent())){
                        upperMenuWithRedChild.setComponent(r.getComponent());
                    }
                    if(StringUtils.isNotBlank(r.getRedirect())){
                        upperMenuWithRedChild.setRedirect(r.getRedirect());
                    }
                    upperMenuWithRedChild.setMeta(meta);
                    upperMenuWithRedChild.setResourceId(r.getResourceId().toString());
                    upperMenuWithRedChild.setChildren(null);

                    List<Res> btns = resMap.get(r.getResourceId());
                    if(btns != null) {
                        List<ResourceButton> buttons = new ArrayList<>();
                        for(Res btn : btns) {
                            ResourceButton button = new ResourceButton();
                            button.setResourceId(String.valueOf(btn.getResourceId()));
                            button.setName(btn.getName());
                            button.setParentId(String.valueOf(btn.getParentId()));
                            button.setShow(btn.isShow());
                            buttons.add(button);
                            upperMenuWithRedChild.setButtons(buttons);
                        }
                    }
                    childList.add(upperMenuWithRedChild);
                }
            }
        }
        return childList;
    }

    //meta类转化公共方法
    public static Meta commonMeta (Res resource){
        Meta meta = new Meta();
        if(StringUtils.isNotBlank(resource.getIcon())){
            meta.setIcon(resource.getIcon());
        }
        meta.setTitle(resource.getTitle());
        meta.setAffix(resource.isAffix());
        meta.setIframe(resource.isIframe());
        meta.setKeepAlive(resource.isKeepAlive());
        return meta;
    }

    @Data
    static class Res {
        private Long resourceId;
        private Long parentId;
        private String path;
        private String name;
        private String component;
        private String title;
        private String icon;
        private String redirect;
        private String resourceType;
        private boolean keepAlive;
        private boolean affix;
        private boolean iframe;
        private boolean show;

        /**
         *
         * @param resourceList 已有的资源
         * @param allResources 所有的资源
         * @return
         */
        public static List<Res> convert(List<Resource> resourceList, List<Resource> allResources) {
            List<Long> resIds = resourceList.stream().map(Resource::getResourceId).collect(Collectors.toList());
            List<Long> allResIds = allResources.stream().map(Resource::getResourceId).collect(Collectors.toList());
            //没有权限的id
            allResIds.removeAll(resIds);
            List<Res> list = new ArrayList<>();
            for(Resource r : allResources) {
                boolean flag = allResIds.contains(r.getResourceId());
                Res res = new Res();
                res.setResourceId(r.getResourceId());
                res.setParentId(r.getParentId());
                res.setPath(r.getPath());
                res.setComponent(r.getComponent());
                res.setTitle(r.getTitle());
                res.setIcon(r.getIcon());
                res.setRedirect(r.getRedirect());
                res.setResourceType(r.getResourceType());
                res.setKeepAlive(r.isKeepAlive());
                res.setIframe(r.isIframe());
                res.setShow(!flag);
                res.setName(r.getName());
                list.add(res);
            }
            return list;
        }

        public static Res convert(Resource r) {
            Res res = new Res();
            res.setResourceId(r.getResourceId());
            res.setParentId(r.getParentId());
            res.setPath(r.getPath());
            res.setComponent(r.getComponent());
            res.setTitle(r.getTitle());
            res.setIcon(r.getIcon());
            res.setRedirect(r.getRedirect());
            res.setResourceType(r.getResourceType());
            res.setKeepAlive(r.isKeepAlive());
            res.setIframe(r.isIframe());
            return res;

        }
    }
}