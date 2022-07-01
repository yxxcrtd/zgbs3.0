package com.joyintech.sso.synchron;

import com.alibaba.fastjson.JSON;
import com.joyintech.sso.config.dto.SsoPlatformDto;
import com.joyintech.sso.config.pojo.SsoPlatform;
import com.joyintech.sso.config.service.SsoPlatformService;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.system.user.SysUserListener;
import com.joyintech.system.user.service.SysUser002Service;
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
public class SysUserListenerImpl implements SysUserListener {
    private static final Logger logger = LoggerFactory.getLogger(SysInstitutionListenerImpl.class);

    @Autowired
    private SsoPlatformService ssoPlatformService;
    @Autowired
    private SysUser002Service sysUser002Service;

    @Value("${sso-sync.url.user.create}")
    private String createUrl;
    @Value("${sso-sync.url.user.modify}")
    private String modifyUrl;
    @Value("${sso-sync.url.user.delete}")
    private String deleteUrl;
    @Value("${sso-sync.url.user.lock}")
    private String lockUrl;
    @Value("${sso-sync.url.user.unlock}")
    private String unlockUrl;
    @Value("${sso-sync.url.user.reset}")
    private String resetUrl;
    @Value("${sso-sync.url.user.updatePassword}")
    private String updatePasswordUrl;

    @Override
    public void onCreated(SysUser user) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(createUrl)) {
            throw new RuntimeException("找不到同步用户数据RUL，请配置同步地址");
        }
        if (StringUtils.isBlank(user.getSsoUserId())) {
            user.setSsoUserId(user.getUserName());
        }

        RestTemplate restTemplate = new RestTemplate();
        for (SsoPlatform platform : platforms) {
            URI url = URI.create(platform.getPlatformHost() + createUrl);
            try {
                restTemplate.postForEntity(url, JSON.parseObject(JSON.toJSONString(user)), String.class);
            } catch (Exception e) {
                logger.error("同步用户数据异常：{}-{}", platform.getPlatformname(), user.getUserName(), e);
                throw new RuntimeException("同步用户数据异常");
            }
        }
    }

    @Override
    public void onDeleted(String userId) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(deleteUrl)) {
            throw new RuntimeException("找不到同步用户数据RUL，请配置同步地址");
        }

        RestTemplate restTemplate = new RestTemplate();
        for (SsoPlatform platform : platforms) {
            String url = platform.getPlatformHost() + deleteUrl;
            try {
                restTemplate.delete(url, userId);
            } catch (Exception e) {
                logger.error("同步用户数据异常：{}-{}", platform.getPlatformname(), userId, e);
                throw new RuntimeException("同步用户数据异常");
            }
        }
    }

    @Override
    public void onModified(SysUser user) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(modifyUrl)) {
            throw new RuntimeException("找不到同步用户数据RUL，请配置同步地址");
        }
        if (StringUtils.isBlank(user.getSsoUserId())) {
            user.setSsoUserId(user.getUserName());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> httpEntity = new HttpEntity(JSON.toJSONString(user), headers);

        for (SsoPlatform platform : platforms) {
            URI url = URI.create(platform.getPlatformHost() + modifyUrl);
            try {
                restTemplate.put(url, httpEntity);
            } catch (Exception e) {
                logger.error("同步用户数据异常：{}-{}", platform.getPlatformname(), user.getUserName(), e);
                throw new RuntimeException("同步用户数据异常");
            }
        }
    }

    @Override
    public void onLocked(String userId) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(lockUrl)) {
            throw new RuntimeException("找不到同步用户数据RUL，请配置同步地址");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity(headers);

        for (SsoPlatform platform : platforms) {
            String url = platform.getPlatformHost() + lockUrl;
            try {
                restTemplate.put(url, httpEntity, userId);
            } catch (Exception e) {
                logger.error("同步用户数据异常：{}-{}", platform.getPlatformname(), userId, e);
                throw new RuntimeException("同步用户数据异常");
            }
        }
    }

    @Override
    public void onUnlocked(String userId) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(unlockUrl)) {
            throw new RuntimeException("找不到同步用户数据RUL，请配置同步地址");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity(headers);

        for (SsoPlatform platform : platforms) {
            String url = platform.getPlatformHost() + unlockUrl;
            try {
                restTemplate.put(url, httpEntity, userId);
            } catch (Exception e) {
                logger.error("同步用户数据异常：{}-{}", platform.getPlatformname(), userId, e);
                throw new RuntimeException("同步用户数据异常");
            }
        }
    }

    @Override
    public void passwordReset(String userId) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(resetUrl)) {
            throw new RuntimeException("找不到同步用户数据RUL，请配置同步地址");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity(headers);

        for (SsoPlatform platform : platforms) {
            String url = platform.getPlatformHost() + resetUrl;
            try {
                restTemplate.put(url, httpEntity, userId);
            } catch (Exception e) {
                logger.error("同步用户数据异常：{}-{}", platform.getPlatformname(), userId, e);
                throw new RuntimeException("同步用户数据异常");
            }
        }
    }

    @Override
    public void updatePassword(String userId, String newPassword) {
        final List<SsoPlatform> platforms = this.getSyncSsoPlatforms();
        if (CollectionUtils.isEmpty(platforms)) {
            return;
        }
        if (StringUtils.isBlank(updatePasswordUrl)) {
            throw new RuntimeException("找不到同步用户数据RUL，请配置同步地址");
        }

        SysUser sysUser = sysUser002Service.selectByPrimaryKey(userId);
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(newPassword);
        user.setSalt(sysUser.getSalt());

        RestTemplate restTemplate = new RestTemplate();
        for (SsoPlatform platform : platforms) {
            String url = platform.getPlatformHost() + updatePasswordUrl;
            try {
                restTemplate.postForEntity(url, JSON.parseObject(JSON.toJSONString(user)), String.class, userId);
            } catch (Exception e) {
                logger.error("同步用户数据异常：{}-{}", platform.getPlatformname(), userId, e);
                throw new RuntimeException("同步用户数据异常");
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
