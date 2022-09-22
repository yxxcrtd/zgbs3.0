package com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.controller;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.dto.RskSupervisionIndicatorParam003Dto;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.dto.RskSupervisionIndicatorParamDto;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.service.RskSupervisionIndicatorParam001Service;
import com.joyintech.tams.invest.riskredis.quota.rsksupervisionindicatorparam.service.RskSupervisionIndicatorParam003Service;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.dto.RskSupervisionIndicator002Dto;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.service.RskSupervisionIndicator001Service;
import com.joyintech.tams.invest.riskredis.quota.superviseindicator.service.RskSupervisionIndicator002Service;
import com.joyintech.tams.position.pojo.BaseMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RequestMapping("/generatorSQL")
@RestController
public class GeneratorSQLController {
    @Autowired
    private RskSupervisionIndicatorParam001Service rskSupervisionIndicatorParamService;
    @Autowired
    private RskSupervisionIndicatorParam003Service rskSupervisionIndicatorParam003Service;
    @Autowired
    private RskSupervisionIndicator001Service rskSupervisionIndicator001Service;

    @Autowired(required = false)
    private RskSupervisionIndicator002Service rskSupervisionIndicator002Service;


    @GetMapping("/sql")
    public void generatorSQL() throws Exception {
        Properties initProp = new Properties();
        initProp.setProperty("resource.loader", "class");

        initProp.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        initProp.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        initProp.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        Velocity.init(initProp);
        //处理指标参数
        Template paramTpl = Velocity.getTemplate("tpl/rsk/RSK_ISUP_INDICATOR_PARA.vm", "UTF-8");
        BaseMap param = BaseMap.newMap().put("page", 1).put("length", 100000);
        Page<RskSupervisionIndicatorParamDto> paramPage = rskSupervisionIndicatorParamService.selectForPage(param);
        Map<String, String> paramMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(paramPage.getRoot())) {
            for (RskSupervisionIndicatorParamDto paramDto : paramPage.getRoot()) {
                if (StringUtil.equals(paramDto.getParaType(), "FIX")) continue;
                VelocityContext context = new VelocityContext();
                if (StringUtil.isNotEmpty(paramDto.getForbidEnum())) {
                    paramDto.setForbidEnum(paramDto.getForbidEnum().replace("'", "''"));
                }
                context.put("param", paramDto);
                List<RskSupervisionIndicatorParamDto> list = rskSupervisionIndicatorParamService.selectInfo(paramDto.getParaCode());
                if (!CollectionUtils.isEmpty(list)) {
                    for (RskSupervisionIndicatorParamDto entity : list) {
                        entity.setContent(entity.getContent().replace("'", "''"));
                        if (StringUtil.equals(entity.getParaGranularity(), BussinessDictCodeListRds.参数粒度_买入.getValue())) {
                            context.put("param" + BussinessDictCodeListRds.参数粒度_买入.getValue(), entity);
                        } else if (StringUtil.equals(entity.getParaGranularity(), BussinessDictCodeListRds.参数粒度_卖出.getValue())) {
                            context.put("param" + BussinessDictCodeListRds.参数粒度_卖出.getValue(), entity);
                        } else if (StringUtil.equals(entity.getParaGranularity(), BussinessDictCodeListRds.参数粒度_实时.getValue())) {
                            context.put("param" + BussinessDictCodeListRds.参数粒度_实时.getValue(), entity);
                        } else if (StringUtil.equals(entity.getParaGranularity(), BussinessDictCodeListRds.参数粒度_事后_日终.getValue())) {
                            context.put("param" + BussinessDictCodeListRds.参数粒度_事后_日终.getValue(), entity);
                        } else if (StringUtil.equals(entity.getParaGranularity(), BussinessDictCodeListRds.参数粒度_事后_日间.getValue())) {
                            context.put("param" + BussinessDictCodeListRds.参数粒度_事后_日间.getValue(), entity);
                        }
                    }
                    StringWriter paramWriter = new StringWriter();
                    paramTpl.merge(context, paramWriter);
                    paramMap.put(paramDto.getParaName(), paramWriter.toString());
                }
            }
        }
        //处理指标参数固定
        Template paramFixTpl = Velocity.getTemplate("tpl/rsk/RSK_ISUP_FIX_RECODE.vm", "UTF-8");
        param.put("paraType", "FIX");
        Page<RskSupervisionIndicatorParamDto> fixPage = rskSupervisionIndicatorParamService.selectForPage(param);
        Map<String, String> fixMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(fixPage.getRoot())) {
            for (RskSupervisionIndicatorParamDto paramDto : fixPage.getRoot()) {
                VelocityContext context = new VelocityContext();
                context.put("param", paramDto);
                List<RskSupervisionIndicatorParam003Dto> list = rskSupervisionIndicatorParam003Service.selectFixRecode(paramDto.getParaCode());
                if (!CollectionUtils.isEmpty(list)) {
                    context.put("paramFix", list.get(0));
                    StringWriter fixWriter = new StringWriter();
                    paramFixTpl.merge(context, fixWriter);
                    fixMap.put(paramDto.getParaName(), fixWriter.toString());
                }
            }
        }
        //处理指标
        Template indicatorTpl = Velocity.getTemplate("tpl/rsk/RSK_ISUP_INDICATOR.vm", "UTF-8");
        Map<String, String> indicatorMap = new HashMap<>();
        Page<RskSupervisionIndicator002Dto> indicatorPage = rskSupervisionIndicator001Service.selectForPage(param);
        if (!CollectionUtils.isEmpty(indicatorPage.getRoot())) {
            for (RskSupervisionIndicator002Dto paramDto : indicatorPage.getRoot()) {
                VelocityContext context = new VelocityContext();
                context.put("ind", rskSupervisionIndicator002Service.getBussinessEntityByIndId(paramDto.getIndId()));
                StringWriter indicatorWriter = new StringWriter();
                indicatorTpl.merge(context, indicatorWriter);
                indicatorMap.put(paramDto.getIndName(), indicatorWriter.toString());
            }
        }
        //处理数据写入文件
        String paramPath = "D:/sql/指标参数/";
        paramMap.entrySet().forEach(e -> {
            try {
                FileUtils.write(new File(String.format("%s/%s.sql",paramPath,e.getKey())),e.getValue(),"UTF-8");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        fixMap.entrySet().forEach(e -> {
            try {
                FileUtils.write(new File(String.format("%s/%s.sql",paramPath,e.getKey())),e.getValue(),"UTF-8");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        String indPath = "D:/sql/指标/";
        indicatorMap.entrySet().forEach(e -> {
            try {
                FileUtils.write(new File(String.format("%s/%s.sql",indPath,e.getKey().replace("/",""))),e.getValue(),"UTF-8");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }





}
