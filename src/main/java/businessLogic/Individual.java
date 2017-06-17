package businessLogic;

/**
 * Created by user on 21/11/2016.
 */
public class Individual {

    private String id;
//    private String fName;
//    private String lName;
    private Gender gender;
    private String fatherId;
    private String motherId;
    private String partnerId;
    private String generation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//      public String getfName() {
//        return fName;
//    }
//
//    public void setfName(String fName) {
//        this.fName = fName;
//    }
//
//    public String getlName() {
//        return lName;
//    }
//
//    public void setlName(String lName) {
//        this.lName = lName;
//    }

    public Gender getGender() {
        return gender;
    }

    public String getGenderCapital() {
        switch (gender) {
            case FEMALE:
                return "F";
            case MALE :
                return "M";
        }
        return "Error";
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setGender(String gender) {

        switch (gender){
            case "0":
                this.gender = Gender.FEMALE;
                break;
            case "1":
                this.gender = Gender.MALE;
                break;
        }
    }


    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    @Override
    public String toString() {
        return "Individual{" +
                "id='" + id + '\'' +
//                ", fName='" + fName + '\'' +
//                ", lName='" + lName + '\'' +
                ", gender=" + gender +
                ", fatherId='" + fatherId + '\'' +
                ", motherId='" + motherId + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", generation='" + generation + '\'' +
                '}';
    }


}
