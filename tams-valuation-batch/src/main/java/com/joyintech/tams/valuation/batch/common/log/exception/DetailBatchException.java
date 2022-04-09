package com.joyintech.tams.valuation.batch.common.log.exception;
/**
 * <p>Title: DetailBatchException</p>
 * <p>Description: 详细日志的异常类</p>
 *
 * @author 殷亚
 * @version 1.0
 * @date 2022/4/1 11:31
 */

import com.joyintech.tams.valuation.batch.common.log.dto.DetailBatchLogExpand;
import com.joyintech.tams.valuation.batch.common.log.dto.LogLevel;

/* @Author 殷亚
 * @Description 详细日志的异常类
 * @Date  2022/4/1 10:55
 * @Version 1.0
 * @Param
 * @Return
 **/
public class DetailBatchException extends RuntimeException{
    /**
     * 序列号
     */
    private static final long serialVersionUID = 3555238643867679421L;

    /**
     * 业务编码
     */
    public String busiId;

    public String getBusiId() {
        return busiId;
    }
    /**
     * 异常级别：INFO,DEBUG,ERROR,WRAN
     * 默认error
     */
    public String lev= LogLevel.ERROR.toString();

    /**
     * 日志扩展明细实体
     */
    public DetailBatchLogExpand detailBatchLogExpand;

    public String getLev() {
        return lev;
    }

    public DetailBatchLogExpand getDetailBatchLogExpand() {
        return detailBatchLogExpand;
    }

    /**
     *
     */
    public DetailBatchException() {
        super();
    }


    /**
     * 简单场景，输出业务编码，带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param busiId 业务编号
     * @param message 异常信息
     * @param cause 堆栈信息
     * @see
     * @since 1.0
     */
    public DetailBatchException(String busiId,String message, Throwable cause) {
        super(message, cause);
        this.busiId=busiId==null?"":busiId;
    }

    /**
     * 简单场景，输出业务编码，带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param busiId 业务编号
     * @param message 异常信息
     * @param cause 堆栈信息
     * @param level 日志级别
     * @see
     * @since 1.0
     */
    public DetailBatchException(String busiId,String message, Throwable cause,Enum level) {
        super(message, cause);
        this.busiId=busiId==null?"":busiId;
    }

    /**
     * 简单场景，输出业务编码，不带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param busiId 业务编号
     * @param message 异常信息
     * @see
     * @since 1.0
     */
    public DetailBatchException(String busiId,String message) {
        super(message);
        this.busiId=busiId==null?"":busiId;
    }


    /**
     * 简单场景，输出扩展明细，不带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param busiId 业务编号
     * @param message 异常信息
     * @param level 日志级别
     * @see
     * @since 1.0
     */
    public DetailBatchException(String busiId,String message,Enum level) {
        super(message);
        this.busiId=busiId==null?"":busiId;
        this.lev=level.toString();
    }

    /**
     * 复杂场景，输出扩展明细，带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param detailBatchLogExpand 日志扩展明细
     * @param message 异常信息
     * @param cause 堆栈信息
     * @see
     * @since 1.0
     */
    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message, Throwable cause) {
        super(message, cause);
        this.detailBatchLogExpand=detailBatchLogExpand;
    }

    /**
     * 复杂场景，输出扩展明细，带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param detailBatchLogExpand 日志扩展明细
     * @param message 异常信息
     * @param cause 堆栈信息
     * @param level 日志级别
     * @see
     * @since 1.0
     */

    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message, Throwable cause,Enum level) {
        super(message, cause);
        this.detailBatchLogExpand=detailBatchLogExpand;
        this.lev=level.toString();
    }

    /**
     * 复杂场景，输出扩展明细，不带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param detailBatchLogExpand 日志扩展明细
     * @param message 异常信息
     * @see
     * @since 1.0
     */
    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message) {
        super(message);
        this.detailBatchLogExpand=detailBatchLogExpand;
    }

    /**
     * 复杂场景，输出扩展明细，不带堆栈日志的输出
     * @author 殷亚
     * @date  2022/3/28 14:52
     * @param detailBatchLogExpand 日志扩展明细
     * @param message 异常信息
     * @param level 日志级别
     * @see
     * @since 1.0
     */
    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message,Enum level) {
        super(message);
        this.detailBatchLogExpand=detailBatchLogExpand;
        this.lev=level.toString();
    }




}
