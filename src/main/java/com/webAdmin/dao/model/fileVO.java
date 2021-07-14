package com.webAdmin.dao.model;


/**
 * Created by Leo.
 * User: notho
 * Date: 2020-12-20, Time: 오후 10:48
 */
public class fileVO<T>  {

    public byte[] BLOB1;
    public String ATTC_FIL_NM;

    public byte[] getBLOB1() {
        return BLOB1;
    }
    public void setBLOB1(byte[] BLOB1) {
        this.BLOB1 = BLOB1;
    }
    public String getATTC_FIL_NM() {
        return ATTC_FIL_NM;
    }
    public void setATTC_FIL_NM(String ATTC_FIL_NM) {
        this.ATTC_FIL_NM = ATTC_FIL_NM;
    }

}
