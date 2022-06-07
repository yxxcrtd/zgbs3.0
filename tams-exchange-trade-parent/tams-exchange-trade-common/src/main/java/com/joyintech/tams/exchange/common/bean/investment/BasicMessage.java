package com.joyintech.tams.exchange.common.bean.investment;

import java.io.Serializable;

/**
 * @author lifangpeng
 * @date 2022/4/12 14:45
 */
public class BasicMessage implements Cloneable, Serializable {
    private static final long serialVersionUID = 8020449611941603235L;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
