package com.joyintech.tams.valuation.batch.common.log.exception;
/*
 * @ClassName DetailBatchException
 * @Description 详细日志的异常类
 * @Date 2022/3/28 13:49
 * @Author 殷亚
 * @Version 1.0
 **/

import com.joyintech.tams.valuation.batch.common.log.dto.DetailBatchLogExpand;
import com.joyintech.tams.valuation.batch.common.log.dto.DetailLogLevel;

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
    public String lev= DetailLogLevel.ERROR.toString();

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


   /* @Author 殷亚
    * @Description TODO 简单场景，输出业务编码，带堆栈日志的输出
    * @Date  2022/3/28 14:52
    * @Version 1.0
    * @Param [busiCode, message, cause]
    * @Return
    **/
    public DetailBatchException(String busiId,String message, Throwable cause) {
        super(message, cause);
        this.busiId=busiId==null?"":busiId;
    }
    public DetailBatchException(String busiId,String message, Throwable cause,Enum level) {
        super(message, cause);
        this.busiId=busiId==null?"":busiId;
    }

    /* @Author 殷亚
     * @Description TODO 简单场景，输出业务编码，不带堆栈日志的输出
     * @Date  2022/3/28 14:52
     * @Version 1.0
     * @Param [busiCode, message]
     * @Return
     **/
    public DetailBatchException(String busiId,String message) {
        super(message);
        this.busiId=busiId==null?"":busiId;
    }
    public DetailBatchException(String busiId,String message,Enum level) {
        super(message);
        this.busiId=busiId==null?"":busiId;
        this.lev=level.toString();
    }

    /* @Author 殷亚
     * @Description TODO 复杂场景，输出扩展明细，带堆栈日志的输出
     * @Date  2022/3/30 14:21
     * @Version 1.0        
     * @Param [detailBatchLogExpand, message, cause]
     * @Return 
     **/
    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message, Throwable cause) {
        super(message, cause);
        this.detailBatchLogExpand=detailBatchLogExpand;
    }

    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message, Throwable cause,Enum level) {
        super(message, cause);
        this.detailBatchLogExpand=detailBatchLogExpand;
        this.lev=level.toString();
    }
    /* @Author 殷亚
     * @Description TODO 复杂场景，输出扩展明细，不带堆栈日志的输出
     * @Date  2022/3/30 14:21
     * @Version 1.0
     * @Param [detailBatchLogExpand, message, cause]
     * @Return
     **/
    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message) {
        super(message);
        this.detailBatchLogExpand=detailBatchLogExpand;
    }

    public DetailBatchException(DetailBatchLogExpand detailBatchLogExpand,String message,Enum level) {
        super(message);
        this.detailBatchLogExpand=detailBatchLogExpand;
        this.lev=level.toString();
    }




}
