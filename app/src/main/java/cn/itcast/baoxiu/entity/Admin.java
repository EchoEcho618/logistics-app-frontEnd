package cn.itcast.baoxiu.entity;

import java.io.Serializable;
import java.util.Objects;

public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer aId;

    private String aName;

    private String aPassword;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getaId() {
        return aId;
    }

    public void setaId(Integer aId) {
        this.aId = aId;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaPassword() {
        return aPassword;
    }

    public void setaPassword(String aPassword) {
        this.aPassword = aPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return aId.equals(admin.aId) && aName.equals(admin.aName) && aPassword.equals(admin.aPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aId, aName, aPassword);
    }
}
