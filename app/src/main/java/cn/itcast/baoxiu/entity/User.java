package cn.itcast.baoxiu.entity;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer uId;

    private String uName;

    private String uPassword;

    /**
     * 用户性别0:male,1:female
     */
    private Integer uSex;

    private String uTel;

    /**
     * 用户类型0:teacher,1:student
     */
    private Integer uType;

    /**
     * 是否毕业/离职0：否，1：是
     */
    private Integer isDelete;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuPassword() {
        return uPassword;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public Integer getuSex() {
        return uSex;
    }

    public void setuSex(Integer uSex) {
        this.uSex = uSex;
    }

    public String getuTel() {
        return uTel;
    }

    public void setuTel(String uTel) {
        this.uTel = uTel;
    }

    public Integer getuType() {
        return uType;
    }

    public void setuType(Integer uType) {
        this.uType = uType;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uId, user.uId) && Objects.equals(uName, user.uName) && Objects.equals(uPassword, user.uPassword) && Objects.equals(uSex, user.uSex) && Objects.equals(uTel, user.uTel) && Objects.equals(uType, user.uType) && Objects.equals(isDelete, user.isDelete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uId, uName, uPassword, uSex, uTel, uType, isDelete);
    }

    @Override
    public String toString() {
        return "User{" +
                "uId=" + uId +
                ", uName='" + uName + '\'' +
                ", uPassword='" + uPassword + '\'' +
                ", uSex=" + uSex +
                ", uTel='" + uTel + '\'' +
                ", uType=" + uType +
                ", isDelete=" + isDelete +
                '}';
    }
}
