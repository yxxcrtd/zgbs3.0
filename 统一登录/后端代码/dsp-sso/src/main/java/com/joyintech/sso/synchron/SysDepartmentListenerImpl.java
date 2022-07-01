package com.joyintech.sso.synchron;

import com.alibaba.fastjson.JSON;
import com.joyintech.sso.config.dto.SsoPlatformDto;
import com.joyintech.sso.config.pojo.SsoPlatform;
import com.joyintech.sso.config.service.SsoPlatformService;
import com.joyintech.system.department.SysDepartmentListener;
import com.joyintech.system.department.dto.SysDepartmentDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

/**
 * @author lifangpeng
 * @date 2021/9/8
 */
@Component
public class SysDepartmentListenerImpl implements SysDepartmentListener {
    private static final Logger logger = LoggerFactory.getLogger(SysDepartmentListenerImpl.class);

    @Autowired
    private SsoPlatformService ssoPlatformService;

    @Value("${sso-sync.url.department.create}")
    private String createUrl;
    @Value("${sso-sync.url.department.modify}")
    private String modifyUrl;
    @Value("${sso-sync.url.department.delete}")
    private String deleteUrl;

    @Override
    public void onCreated(SysDepartmentDto department) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(createUrl)) {
            throw new RuntimeException("找不到同步部门数据RUL，请配置同步地址");
        }
        RestTemplate restTemplate = new RestTemplate();

        for (SsoPlatform platform : platforms) {
            URI url = URI.create(platform.getPlatformHost() + createUrl);
            try {
                restTemplate.postForEntity(url, JSON.parseObject(JSON.toJSONString(department)), String.class);
            } catch (Exception e) {
                logger.error("同步部门数据异常：{}-{}", platform.getPlatformname(), department.getDeptCode(), e);
                throw new RuntimeException("同步部门数据异常");
            }
        }
    }

    @Override
    public void onDeleted(String deptCode) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(deleteUrl)) {
            throw new RuntimeException("找不到同步部门数据RUL，请配置同步地址");
        }
        RestTemplate restTemplate = new RestTemplate();

        for (SsoPlatform platform : platforms) {
            String url = platform.getPlatformHost() + deleteUrl;
            try {
                restTemplate.delete(url, deptCode);
            } catch (Exception e) {
                logger.error("同步部门数据异常：{}-{}", platform.getPlatformname(), deptCode, e);
                throw new RuntimeException("同步部门数据异常");
            }
        }
    }

    @Override
    public void onModified(SysDepartmentDto department) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(modifyUrl)) {
            throw new RuntimeException("找不到同步部门数据RUL，请配置同步地址");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> httpEntity = new HttpEntity(department, headers);

        for (SsoPlatform platform : platforms) {
            URI url = URI.create(platform.getPlatformHost() + modifyUrl);
            try {
                restTemplate.put(url, httpEntity);
            } catch (Exception e) {
                logger.error("同步部门数据异常：{}-{}", platform.getPlatformname(), department.getDeptCode(), e);
                throw new RuntimeException("同步部门数据异常");
            }
        }
    }

    private List<SsoPlatform> getSyncSsoPlatforms() {
        SsoPlatformDto ssoPlatformDto = new SsoPlatformDto();
        ssoPlatformDto.setStatus(1);
        ssoPlatformDto.setSyncFlag(1);
        return ssoPlatformService.listSsoPlatform(ssoPlatformDto);
    }
}
