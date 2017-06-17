package businessLogic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Created by Levinger on 28/12/2016.
 */

/**
 * Relation Record POJO
 */
public class RelationRecord {

    private String fullName;
    private String name;
    private String xg;
    private String x;
    private String yg;
    private String y;
    private String zg;
    private String z;
    private String tg;
    private String t;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXg() {
        return xg;
    }

    public void setXg(String xg) {
        this.xg = xg;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getYg() {
        return yg;
    }

    public void setYg(String yg) {
        this.yg = yg;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZg() {
        return zg;
    }

    public void setZg(String zg) {
        this.zg = zg;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getTg() {
        return tg;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "RelationRecord{" +
                "Full name='" + fullName + '\'' +
                ", name='" + name + '\'' +
                ", xg='" + xg + '\'' +
                ", x='" + x + '\'' +
                ", yg='" + yg + '\'' +
                ", y='" + y + '\'' +
                ", zg='" + zg + '\'' +
                ", z='" + z + '\'' +
                ", tg='" + tg + '\'' +
                ", t='" + t + '\'' +
                '}';
    }


    /**
     * This method does string compare of all fields
     * @param relationRecords - the list for sorting
     * @return list sorted
     */
    public static List<RelationRecord> sortLists(List<RelationRecord> relationRecords) {
        Collections.sort(relationRecords, new Comparator<RelationRecord>() {
            @Override
            public int compare(RelationRecord r1, RelationRecord r2) {
                return new CompareToBuilder()
                        .append(r1.getName(), r2.getName())
                        .append(r1.getX(), r2.getX())
                        .append(r1.getY(), r2.getY())
                        .append(r1.getZ(), r2.getZ())
                        .append(r1.getT(), r2.getT())
                        .toComparison();
            }
        });
        return relationRecords;
    }

}
