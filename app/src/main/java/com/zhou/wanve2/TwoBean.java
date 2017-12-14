package com.zhou.wanve2;

/**
 * Created by zhou on 2017/12/13.
 */

public class TwoBean {
    private String WEBSESSION;
    private String CODE;
    private String RESP_DESC;

    public String getWEBSESSION() {
        return WEBSESSION;
    }

    public void setWEBSESSION(String WEBSESSION) {
        this.WEBSESSION = WEBSESSION;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getRESP_DESC() {
        return RESP_DESC;
    }

    public void setRESP_DESC(String RESP_DESC) {
        this.RESP_DESC = RESP_DESC;
    }

    @Override
    public String toString() {
        return "TwoBean{" +
                "WEBSESSION='" + WEBSESSION + '\'' +
                ", CODE='" + CODE + '\'' +
                ", RESP_DESC='" + RESP_DESC + '\'' +
                '}';
    }
}
