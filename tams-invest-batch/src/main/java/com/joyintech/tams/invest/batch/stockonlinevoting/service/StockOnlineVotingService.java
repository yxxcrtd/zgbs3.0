package com.joyintech.tams.invest.batch.stockonlinevoting.service;

import com.jcraft.jsch.ChannelSftp;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.StMeetingInfo;
import com.joyintech.tams.common.base.entity.StMeetingProposalInfo;
import com.joyintech.tams.common.base.entity.StMeetingProposalVoteinfo;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.batch.stockonlinevoting.dao.StockOnlineVotingMapper;
import com.joyintech.tams.invest.batch.stockonlinevoting.dto.*;
import com.joyintech.tams.invest.common.util.FtpUtil;
import com.joyintech.tams.invest.common.util.SftpUtil;
import com.joyintech.tams.invest.common.util.XmlParseUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * 股票网络投票基础数据导入 Service
 */
@Service
@Scope("prototype")
public class StockOnlineVotingService extends BaseService {

    //上交所的文件前缀
    private final static String SH_FILE_PREFIX = "vtls";

    //深交所的文件前缀
    private final static String SZ_FILE_PREFIX = "evoteparams";

    @Autowired
    private StockOnlineVotingMapper stockOnlineVotingMapper;


    public StockOnlineVotingResultDto process(StockOnlineVotingDto stockOnlineVotingDto) throws Exception {
        //返回结果
        StockOnlineVotingResultDto dto = new StockOnlineVotingResultDto();
        //Ftp类型
        String ftpType = stockOnlineVotingDto.getFtpType();
        //文件目录路径
        String fileRootPath = stockOnlineVotingDto.getFilePath();

        //获取基础数据文件
        //文件集合
        List<File> files = new ArrayList<>();
        //传文件名规范
        List<String> containsFile = new ArrayList<>();
        containsFile.add(SH_FILE_PREFIX);
        containsFile.add(SZ_FILE_PREFIX);

        //FTP
        if(StringUtil.equals(ftpType,"01")) {
            //FTP
            files = FtpUtil.downXMLFile( stockOnlineVotingDto.getHost(), Integer.parseInt(stockOnlineVotingDto.getPort()), stockOnlineVotingDto.getUsername(), stockOnlineVotingDto.getPassword(), stockOnlineVotingDto.getFilePath(),
                    stockOnlineVotingDto.getLocalPath(), containsFile);
        }
        //SFTP
        else if (StringUtil.equals(ftpType,"02")) {
            Vector<?> vector = SftpUtil.getSFtpFile(stockOnlineVotingDto.getUsername(), stockOnlineVotingDto.getHost(), Integer.parseInt(stockOnlineVotingDto.getPort()), stockOnlineVotingDto.getPassword(),stockOnlineVotingDto.getFilePath());
            if (null != vector) {
                for (Object item : vector) {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)item;
                    String fileName = entry.getFilename();
                    if (containsFile == null || CollectionUtils.exists(containsFile, name -> {
                        return fileName.contains((String)name);
                    })) {
                        File lf = new File(stockOnlineVotingDto.getLocalPath() + fileName);
                        if (!lf.exists()) {
                            SftpUtil.downloadSftp(stockOnlineVotingDto.getUsername(), stockOnlineVotingDto.getHost(), Integer.parseInt(stockOnlineVotingDto.getPort()), stockOnlineVotingDto.getPassword(),stockOnlineVotingDto.getFilePath(), entry.getFilename(), stockOnlineVotingDto.getLocalPath());
                        }
                    }
                }

                File finalDir = new File(stockOnlineVotingDto.getLocalPath());
                if (finalDir.exists() && finalDir.isDirectory()) {
                    Collection<File> fileCollection = FileUtils.listFiles(finalDir,null, false);
                    files.addAll(fileCollection);
                }
            }
        }
        //本地
        else if (StringUtil.equals(ftpType,"03")) {
            File finalDir = new File(fileRootPath);
            if (finalDir.exists() && finalDir.isDirectory()) {
                Collection<File> fileCollection = FileUtils.listFiles(finalDir,null, false);
                files.addAll(fileCollection);
            }
        }

        //处理基础数据文件
        if(StringUtil.isNotEmpty(files)) {

            List<StMeetingInfo> stMeetingInfoList = new ArrayList<>();
            List<StMeetingProposalInfo> stMeetingProposalInfoList = new ArrayList<>();
            List<StMeetingProposalVoteinfo> stMeetingProposalVoteinfoList = new ArrayList<>();

            for (File file : files) {
                //上交所
                if(file.getName().contains(SH_FILE_PREFIX)) {
                    XmlSHFileDto xmlSHFileDto = XmlParseUtil.readXmlToJavaBean(file.getAbsolutePath(),XmlSHFileDto.class);
                    if(StringUtil.isNotEmpty(xmlSHFileDto)) {
                        if(!StringUtil.isEmptyList(xmlSHFileDto.getCompany())) {
                            for(XmlSHCompanyDto xmlSHCompanyDto:xmlSHFileDto.getCompany()) {
                                StMeetingInfo stMeetingInfo = new StMeetingInfo();
                                BeanUtil.copyPropertiesIgnoreNull(stMeetingInfo,xmlSHCompanyDto);
                                stMeetingInfo.setMeetingdatebegin(DateUtil.parse(xmlSHCompanyDto.getMeetingdatebegin()));
                                stMeetingInfo.setMeetingdateend(DateUtil.parse(xmlSHCompanyDto.getMeetingdateend()));
                                stMeetingInfoList.add(stMeetingInfo);

                                if(!StringUtil.isEmptyList(xmlSHCompanyDto.getVolist())) {
                                    for(XmlSHVoteDto xmlSHVoteDto:xmlSHCompanyDto.getVolist()) {
                                        StMeetingProposalInfo stMeetingProposalInfo = new StMeetingProposalInfo();
                                        //股东大会编号
                                        stMeetingProposalInfo.setMeetingseq(stMeetingInfo.getMeetingseq());
                                        //投票议案组编号:if 投票议案编号为整数则 为投票议案编号，if 投票议案编号不为整数则 取投案议案编号的整数位
                                        if(xmlSHVoteDto.getVid().contains(".")) {
                                            stMeetingProposalInfo.setProposalGroupId(Double.valueOf(xmlSHVoteDto.getVid()).intValue()+"");
                                        }
                                        else {
                                            stMeetingProposalInfo.setProposalGroupId(xmlSHVoteDto.getVid());
                                        }
                                        //投票议案编号
                                        stMeetingProposalInfo.setProposalid(xmlSHVoteDto.getVid());
                                        //投票议案名称
                                        stMeetingProposalInfo.setProposalname(xmlSHVoteDto.getVinfo());
                                        //投票议案类型
                                        stMeetingProposalInfo.setVtype(xmlSHVoteDto.getVtype());
                                        //累计投票应选数
                                        stMeetingProposalInfo.setElectionnum(xmlSHVoteDto.getVleijino() != null? xmlSHVoteDto.getVleijino().longValue() : 0L);
                                        stMeetingProposalInfoList.add(stMeetingProposalInfo);

                                        for(XmlSHVroleDto xmlSHVroleDto:xmlSHVoteDto.getVrolelist()) {
                                            StMeetingProposalVoteinfo stMeetingProposalVoteinfo = new StMeetingProposalVoteinfo();
                                            stMeetingProposalVoteinfo.setMeetingseq(stMeetingInfo.getMeetingseq());
                                            stMeetingProposalVoteinfo.setProposalGroupId(stMeetingProposalInfo.getProposalGroupId());
                                            stMeetingProposalVoteinfo.setVcode(xmlSHVroleDto.getRefcode());
                                            stMeetingProposalVoteinfo.setVrole(xmlSHVroleDto.getVrole());

                                            //资产代码: 根据投票代码查询标的类金融产品表，条件是市场代码= 投票代码，找到则为金融产品代码，否则查询其他金融产品表
//                                            stMeetingProposalVoteinfo.setFinprodId(stockOnlineVotingMapper.selectFinprodId(stMeetingProposalVoteinfo.getVrole()));

                                            if(!StringUtil.isEmptyList(xmlSHCompanyDto.getVotecodelist())) {
                                                for(XmlSHVoteCodeDto xmlSHVoteCodeDto : xmlSHCompanyDto.getVotecodelist()) {
                                                    if(StringUtil.equals(xmlSHVoteCodeDto.getVotecode(),xmlSHVroleDto.getRefcode())) {
                                                        stMeetingProposalVoteinfo.setRegdate(DateUtil.parse(xmlSHVoteCodeDto.getRegdate()));
                                                    }
                                                }

                                            }
                                            stMeetingProposalVoteinfoList.add(stMeetingProposalVoteinfo);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                //深交所
                else if(file.getName().contains(SZ_FILE_PREFIX)){
                    XmlSZFileDto xmlSZFileDto = XmlParseUtil.readXmlToJavaBean(file.getAbsolutePath(),XmlSZFileDto.class);
                    if(StringUtil.isNotEmpty(xmlSZFileDto)) {
                        if(!StringUtil.isEmptyList(xmlSZFileDto.getCompany())) {
                            for(XmlSZCompanyDto xmlSZCompanyDto : xmlSZFileDto.getCompany()) {
                                StMeetingInfo stMeetingInfo = new StMeetingInfo();
                                BeanUtil.copyPropertiesIgnoreNull(stMeetingInfo,xmlSZCompanyDto);
                                stMeetingInfo.setCompanycode(xmlSZCompanyDto.getUnderlyingSecurityID().trim());
                                stMeetingInfo.setCompanyinfo(xmlSZCompanyDto.getCompanySymbol().trim());
                                stMeetingInfo.setMeetingdatebegin(DateUtil.parse(xmlSZCompanyDto.getMeetingdatebegin()));
                                stMeetingInfo.setMeetingdateend(DateUtil.parse(xmlSZCompanyDto.getMeetingdateend()));

                                stMeetingInfoList.add(stMeetingInfo);

                                if(!StringUtil.isEmptyList(xmlSZCompanyDto.getVolist())) {
                                    for(XmlSZVoteDto xmlSZVoteDto:xmlSZCompanyDto.getVolist()) {
                                        StMeetingProposalInfo stMeetingProposalInfo = new StMeetingProposalInfo();

                                        //股东大会编号
                                        stMeetingProposalInfo.setMeetingseq(stMeetingInfo.getMeetingseq());
                                        //投票议案组编号:if 投票议案编号为整数则 为投票议案编号，if 投票议案编号不为整数则 取投案议案编号的整数位
                                        if(xmlSZVoteDto.getProposalID().contains(".")) {
                                            stMeetingProposalInfo.setProposalGroupId(Double.valueOf(xmlSZVoteDto.getProposalID()).intValue()+"");
                                        }
                                        else {
                                            stMeetingProposalInfo.setProposalGroupId(xmlSZVoteDto.getProposalID());
                                        }
                                        //投票议案编号
                                        stMeetingProposalInfo.setProposalid(xmlSZVoteDto.getProposalID());
                                        //投票议案名称
                                        stMeetingProposalInfo.setProposalname(xmlSZVoteDto.getProposalName());
                                        //投票议案类型
                                        stMeetingProposalInfo.setVtype(xmlSZVoteDto.getProposalType());
                                        //累计投票应选数
                                        stMeetingProposalInfo.setElectionnum(xmlSZVoteDto.getElectionNum() != null? xmlSZVoteDto.getElectionNum().longValue() : 0L);
                                        stMeetingProposalInfoList.add(stMeetingProposalInfo);

                                        for(XmlSZVroleDto xmlSZVroleDto:xmlSZVoteDto.getVrolelist()) {
                                            StMeetingProposalVoteinfo stMeetingProposalVoteinfo = new StMeetingProposalVoteinfo();
                                            stMeetingProposalVoteinfo.setMeetingseq(stMeetingInfo.getMeetingseq());
                                            stMeetingProposalVoteinfo.setProposalGroupId(stMeetingProposalInfo.getProposalGroupId());
                                            stMeetingProposalVoteinfo.setVcode(xmlSZVroleDto.getRefcode());
                                            stMeetingProposalVoteinfo.setVrole(xmlSZVroleDto.getShareHolderRole());

                                            //资产代码: 根据投票代码查询标的类金融产品表，条件是市场代码= 投票代码，找到则为金融产品代码，否则查询其他金融产品表
//                                            stMeetingProposalVoteinfo.setFinprodId(stockOnlineVotingMapper.selectFinprodId(stMeetingProposalVoteinfo.getVrole()));

                                            if(!StringUtil.isEmptyList(xmlSZCompanyDto.getVotecodelist())) {
                                                for(XmlSZVoteCodeDto xmlSZVoteCodeDto : xmlSZCompanyDto.getVotecodelist()) {
                                                    if(StringUtil.equals(xmlSZVoteCodeDto.getSecurityID(),xmlSZVroleDto.getRefcode())) {
                                                        stMeetingProposalVoteinfo.setRegdate(DateUtil.parse(xmlSZVoteCodeDto.getRegDate()));
                                                    }
                                                }
                                            }
                                            stMeetingProposalVoteinfoList.add(stMeetingProposalVoteinfo);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                dto.setStMeetingInfoList(stMeetingInfoList);
                dto.setStMeetingProposalInfoList(stMeetingProposalInfoList);
                dto.setStMeetingProposalVoteinfoList(stMeetingProposalVoteinfoList);
            }
        }

        return dto;
    }
}
