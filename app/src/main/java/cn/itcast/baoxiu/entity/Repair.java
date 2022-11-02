package cn.itcast.baoxiu.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author echo
 * @since 2022-04-18
 */
public class Repair implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer rId;

    private Integer uId;

    private String rPlace;

    private String rContent;

    private LocalDateTime start;

    private Integer sId;

    private LocalDateTime end;

    private Integer star;

    private String evaluation;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    public String getrPlace() {
        return rPlace;
    }

    public void setrPlace(String rPlace) {
        this.rPlace = rPlace;
    }

    public String getrContent() {
        return rContent;
    }

    public void setrContent(String rContent) {
        this.rContent = rContent;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Integer getsId() {
        return sId;
    }

    public void setsId(Integer sId) {
        this.sId = sId;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repair repair = (Repair) o;
        return Objects.equals(rId, repair.rId) && Objects.equals(uId, repair.uId) && Objects.equals(rPlace, repair.rPlace) && Objects.equals(rContent, repair.rContent) && Objects.equals(start, repair.start) && Objects.equals(sId, repair.sId) && Objects.equals(end, repair.end) && Objects.equals(star, repair.star) && Objects.equals(evaluation, repair.evaluation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rId, uId, rPlace, rContent, start, sId, end, star, evaluation);
    }

    @Override
    public String toString() {
        return "Repair{" +
                "rId=" + rId +
                ", uId=" + uId +
                ", rPlace='" + rPlace + '\'' +
                ", rContent='" + rContent + '\'' +
                ", start=" + start +
                ", sId=" + sId +
                ", end=" + end +
                ", star=" + star +
                ", evaluation='" + evaluation + '\'' +
                '}';
    }
}
