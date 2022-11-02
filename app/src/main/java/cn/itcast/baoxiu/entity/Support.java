package cn.itcast.baoxiu.entity;

import java.io.Serializable;
import java.util.Objects;

public class Support implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer sId;

    private String sName;

    private String sPassword;

    /**
     * 后勤人员性别0:male,1:female
     */
    private Integer sSex;

    private String sTel;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getsId() {
        return sId;
    }

    public void setsId(Integer sId) {
        this.sId = sId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsPassword() {
        return sPassword;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }

    public Integer getsSex() {
        return sSex;
    }

    public void setsSex(Integer sSex) {
        this.sSex = sSex;
    }

    public String getsTel() {
        return sTel;
    }

    public void setsTel(String sTel) {
        this.sTel = sTel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Support support = (Support) o;
        return Objects.equals(sId, support.sId) && Objects.equals(sName, support.sName) && Objects.equals(sPassword, support.sPassword) && Objects.equals(sSex, support.sSex) && Objects.equals(sTel, support.sTel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sId, sName, sPassword, sSex, sTel);
    }

    @Override
    public String toString() {
        return "Support{" +
                "sId=" + sId +
                ", sName='" + sName + '\'' +
                ", sPassword='" + sPassword + '\'' +
                ", sSex=" + sSex +
                ", sTel='" + sTel + '\'' +
                '}';
    }
}
