package com.joyintech.tams.valuation.batch.common.log;
/*
 * @ClassName DetailBatchException
 * @Description 详细日志的异常类
 * @Date 2022/3/28 13:49
 * @Author 殷亚
 * @Version 1.0
 **/

public class DetailBatchException extends RuntimeException{
    /**
     * 序列号
     */
    private static final long serialVersionUID = 3555238643867679421L;

    public String busiId;

    public String getBusiId() {
        return busiId;
    }



    /**
     *
     */
    public DetailBatchException() {
        super();
    }


   /* @Author 殷亚
    * @Description 带堆栈日志的输出
    * @Date  2022/3/28 14:52
    * @Version 1.0
    * @Param [busiCode, message, cause]
    * @Return
    **/
    public DetailBatchException(String busiId,String message, Throwable cause) {
        super(message, cause);
        this.busiId=busiId==null?"":busiId;
    }

    /* @Author 殷亚
     * @Description 不带堆栈日志的输出
     * @Date  2022/3/28 14:52
     * @Version 1.0
     * @Param [busiCode, message]
     * @Return
     **/
    public DetailBatchException(String busiId,String message) {
        super(message);
        this.busiId=busiId==null?"":busiId;
    }


}
