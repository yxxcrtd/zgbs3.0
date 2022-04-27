package com.joyintech.tams.exchange.common.net.protocol;

import com.joyintech.tams.exchange.common.utils.JsonUtils;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liuyang on 12/9/15.
 */
public class ProtocolMessage implements Cloneable {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolMessage.class);

    private int fc; // function code
    private long reqId; // request id
    private int dataType; // msg type
    private byte[] body;
    private static final String charSet;//字符集

    static {
        charSet = System.getProperty("file.encoding");
    }

    public ProtocolMessage() {
        // TODO Auto-generated constructor stub
    }

    public ProtocolMessage(int fc, long reqId, int dataType, String body) {
        this.init(fc, reqId, dataType, body);
    }

    public void encode() {
        int index = 0;
        while (index < body.length) {
            int nextIndex = index + 1;
            if (nextIndex < body.length) {
                byte temp = (byte) (body[nextIndex] ^ Byte.MAX_VALUE);
                body[nextIndex] = (byte) (body[index] ^ Byte.MAX_VALUE);
                body[index] = temp;
            }
            index += 2;
        }
    }

    public void init(int fc, long reqId, int dataType, Object object) {
        this.fc = fc;
        this.reqId = reqId;
        this.dataType = dataType;
        if (body == null) {
            this.body = null;

            logger.debug("fc:{},dataType:{} ", this.fc, this.dataType);
        } else {
            try {
                this.body = JsonUtils.getJsonFromObject(object).getBytes(charSet);

                logger.debug("fc:{},dataType:{}", this.fc, this.dataType);
//                logger.debug("fc:{},dataType:{},body:{}", this.fc, this.dataType, this.body);
            } catch (Exception e) {
                logger.error("ProtocolMessage encode error:", e);
            }

        }
    }

    public void init(int fc, long reqId, int dataType, String body) {
        this.fc = fc;
        this.reqId = reqId;
        this.dataType = dataType;
        if (body == null) {
            this.body = null;
            logger.debug("fc:{},dataType:{}", this.fc, this.dataType);

        } else {
            try {
                this.body = body.getBytes(charSet);
                String string = new String(this.body);
                logger.debug("fc:{},dataType:{},body:{}", this.fc, this.dataType, string);
                encode();
            } catch (Exception e) {
                logger.error("ProtocolMessage encode error:", e);
            }

        }
    }

    public void init(int fc, long reqId, int dataType, byte[] body) {
        this.fc = fc;
        this.reqId = reqId;
        this.dataType = dataType;
        this.body = body;
        String string = new String(this.body);
        logger.debug("fc:{},dataType:{},body:{}", this.fc, this.dataType, string);
        encode();
    }

    public int getFc() {
        return fc;
    }

    public void setFc(int fc) {
        this.fc = fc;
    }

    public long getReqId() {
        return reqId;
    }

    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getBody() {
        return new String(body);
    }

    public void setBody(String body) {
        if (body == null)
            this.body = null;
        else
            this.body = body.getBytes();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] byteBody() {
        return this.body;
    }


    public void writeBuffer(ByteBuf byteBuf) {
        byteBuf.writeInt(fc);
        byteBuf.writeLong(reqId);
        byteBuf.writeInt(dataType);
        if (body == null) {
            byteBuf.writeInt(0);
        } else {
            byteBuf.writeInt(body.length);
            byteBuf.writeBytes(body);
        }
    }

    public ProtocolMessage clone() {
        try {
            return (ProtocolMessage) super.clone();
        } catch (Exception e) {
            return new ProtocolMessage();
        }
    }

}
