///*
// * 文件名：SysDataDictService.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
// * 描述：中信银行资管系统 修改人：何阳阳 修改时间：2016年10月18日 修改内容：新建 系统名称：中信银行资管系统
// */
//
//package com.joyintech.tams.report.common.service;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Service;
//
//import com.joyintech.framework.base.BaseService;
//import com.joyintech.framework.constant.GlobalConstant;
//import com.joyintech.framework.util.StringUtil;
//import com.joyintech.tams.common.base.entity.ChbSysDatadictListLink;
//import com.joyintech.tams.common.base.entity.ChbSysPageDictlistKey;
//import com.joyintech.system.dict.dto.SysDataDictDto;
//import com.joyintech.system.dict.dto.SysDataDictItem;
//
///**
// * 数据字典操作服务
// *
// * @author 何阳阳
// * @version 1.0 2016年10月18日
// * @see SysDataDictService
// * @since 1.0
// */
//@Service
//public class SysDataDictService extends BaseService<SysDataDictDto> {
////
////    /**
////     * SysDataDictMapper
////     */
////    @Autowired
////    private SysDataDictExtMapper mapper;
//
//    /**
//     * 获取系统所有数据字典 数据字典中包含有数据字典的明细
//     *
//     * @return 数据字典列表
//     * @see
//     * @since 1.0
//     */
////    public List<SysDataDictDto> findAllDataDict() {
////        List<ChbSysDatadict> dictList = mapper.findSysDataDictList();
////        List<SysDataDictItem> dictItemList = mapper.findSysDataDictItemList();
////        List<ChbSysDatadictListLink> dictLinkList = mapper.findSysDataDictListLink();
////        List<ChbSysPageDictlistKey> pageDictLinkList = mapper.findSysPageDictList();
////        if (CollectionUtils.isNotEmpty(dictList)) {
////            List<SysDataDictDto> result = new ArrayList<>(dictList.size());
////            for (ChbSysDatadict dict : dictList) {
////                SysDataDictDto dto = new SysDataDictDto();
////                dto.setDictCode(dict.getDictCode());
////                dto.setDictCnname(dict.getDictCnname());
////                dto.setDictEnname(dict.getDictEnname());
////                dto.setUpdateFlg(dict.getUpdateFlg());
////                // 从全部的画面字典关联信息中过滤出当前字典代码的所有关联信息
////                List<ChbSysPageDictlistKey> subPageDictLinkList = pageDictLinkList.stream().filter(
////                    item -> StringUtil.equals(item.getDictId(), dict.getDictCode())).collect(
////                        Collectors.toList());
////                dto.setItems(
////                    getItems(dict.getDictCode(), dictItemList, dictLinkList, subPageDictLinkList));
////                // 设置当前字典的关联画面信息
////                Set<String> pageIds = new HashSet<>();
////                subPageDictLinkList.forEach(link -> {
////                    pageIds.add(link.getPageId());
////                });
////                dto.setPageIds(pageIds);
////                result.add(dto);
////            }
////            return result;
////        }
////        return Collections.emptyList();
////    }
//
//    /**
//     * 获取某个字典下的所有明细信息
//     *
//     * @param dictCode 字典代码
//     * @param dictItemList 所有字典明细列表
//     * @param dictLinkList 数据字典关联表数据
//     * @param pageDictLink 画面和数据字典关联关系
//     * @return 字典明显列表
//     * @see
//     * @since 1.0
//     */
//    private List<SysDataDictItem> getItems(String dictCode, List<SysDataDictItem> dictItemList,
//                                           List<ChbSysDatadictListLink> dictLinkList,
//                                           List<ChbSysPageDictlistKey> pageDictLink) {
//        List<SysDataDictItem> result = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(dictItemList)) {
//            dictItemList.stream().filter(item -> item.getDictCode().equals(dictCode)).forEach(
//                item -> {
//                    Set<String> pageIds = new HashSet<>();
//                    pageDictLink.stream().filter(link -> StringUtil.equals(link.getDictItemVal(),
//                        item.getItemCode())).forEach(link -> {
//                            pageIds.add(link.getPageId());
//                        });
//                    item.setPageIds(pageIds);
//                    result.add(item);
//                });
//        }
//        if (CollectionUtils.isNotEmpty(dictLinkList)) {
//            for (SysDataDictItem item : result) {
//                for (ChbSysDatadictListLink dictLink : dictLinkList) {
//                    if (item.getDictCode().equals(dictLink.getDictCode())
//                        && item.getItemCode().equals(dictLink.getItemCode())) {
//                        String linkedDictCode = dictLink.getLinkedDictCode() + GlobalConstant.POINT;
//                        if (StringUtils.isEmpty(item.getParentId())) {
//                            item.setParentId(linkedDictCode + dictLink.getLinkedItemCode());
//                        }
//                        else {
//                            item.setParentId(item.getParentId() + GlobalConstant.COMMA
//                                             + linkedDictCode + dictLink.getLinkedItemCode());
//                        }
//                    }
//                }
//            }
//        }
//        
//        List<SysDataDictItem> tmpList = new ArrayList<>();
//        // 去重
//        for(SysDataDictItem item : result) {
//            if(!tmpList.contains(item)) {
//                tmpList.add(item);
//            }
//        }
//        return tmpList;
//    }
//}
