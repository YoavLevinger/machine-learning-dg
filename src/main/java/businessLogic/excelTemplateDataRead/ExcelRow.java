package businessLogic.excelTemplateDataRead;

/**
 * Created by user on 26/12/2016.
 */

/**
 * Excel Row POJO, holding excel input one row data: percentage, present the relationPrefixName, x,y,z,t and their Gender
 */
public class ExcelRow {
    private int percentage;
    private boolean random;
    private int randomBits;
    private boolean relationPrefixName;
    private boolean xGender;
    private boolean xId;
    private boolean yGender;

    private boolean yId;
    private boolean zGender;
    private boolean zId;
    private boolean tGender;
    private boolean tId;
    private String relation;

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public int getRandomBits() {
        return randomBits;
    }

    public void setRandomBits(int randomBits) {
        this.randomBits = randomBits;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isRelationPrefixName() {
        return relationPrefixName;
    }

    public void setRelationPrefixName(boolean relationPrefixName) {
        this.relationPrefixName = relationPrefixName;
    }

    public boolean isxGender() {
        return xGender;
    }

    public void setxGender(boolean xGender) {
        this.xGender = xGender;
    }

    public boolean isxId() {
        return xId;
    }

    public void setxId(boolean xId) {
        this.xId = xId;
    }

    public boolean isyGender() {
        return yGender;
    }

    public void setyGender(boolean yGender) {
        this.yGender = yGender;
    }

    public boolean isyId() {
        return yId;
    }

    public void setyId(boolean yId) {
        this.yId = yId;
    }

    public boolean iszGender() {
        return zGender;
    }

    public void setzGender(boolean zGender) {
        this.zGender = zGender;
    }

    public boolean iszId() {
        return zId;
    }

    public void setzId(boolean zId) {
        this.zId = zId;
    }

    public boolean istGender() {
        return tGender;
    }

    public void settGender(boolean tGender) {
        this.tGender = tGender;
    }

    public boolean istId() {
        return tId;
    }

    public void settId(boolean tId) {
        this.tId = tId;
    }

    @Override
    public String toString() {
        if (percentage>0) {
        return "ExcelRow{" +
                "percentage=" + percentage +
                ", random=" + random +
                ", random bits=" + randomBits +
                ", R=" + relationPrefixName +
                ", XG=" + xGender +
                ", X=" + xId +
                ", YG=" + yGender +
                ", Y=" + yId +
                ", ZG=" + zGender +
                ", Z=" + zId +
                ", TG=" + tGender +
                ", T=" + tId +
                ", Relation=" + relation +
                "}\n";
        }
        return "";
    }
}
