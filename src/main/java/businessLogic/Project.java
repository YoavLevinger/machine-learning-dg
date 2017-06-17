package businessLogic;

/**
 * Created by user on 30/10/2016.
 */

/**
 * Project object data holder.
 */
public class Project {

    private String id;
    private String name;
    private String initialIndividuals;
    private int numberOfGenerations;
    private float childAverage;
    private float normalChildDistributionDeviation;
    private int maxMemberNumber;
    private ProjectStatus projectStatus;
    private int individualIdLength;

    public Project(String name) {
        this.name = name;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        switch (projectStatus.toUpperCase()){
            case "CREATED":
                    this.projectStatus = ProjectStatus.CREATED;
                break;
            case "POPULATED":
                    this.projectStatus = ProjectStatus.POPULATED;
                break;
            case "CORRUPTED_DATA":
                this.projectStatus = ProjectStatus.CORRUPTED_DATA;
                break;
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitialIndividuals() {
        return initialIndividuals;
    }

    public int getInitialIndividualsNumber() {
        int initialNumber = 0;

        try{
           initialNumber =  Integer.parseInt(initialIndividuals);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initialNumber;
    }

    public void setInitialIndividuals(String initialIndividuals) {
        this.initialIndividuals = initialIndividuals;
    }

    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public void setNumberOfGenerations(String numberOfGenerations) {
        this.numberOfGenerations = Integer.parseInt(numberOfGenerations);
    }

    public float getChildAverage() {
        return childAverage;
    }

    public void setChildAverage(float childAverage) {
        this.childAverage = childAverage;
    }

    public void setChildAverage(String childAverage) {
        this.childAverage = Float.parseFloat(childAverage);
    }

    public float getNormalChildDistributionDeviation() {
        return normalChildDistributionDeviation;
    }

    public void setNormalChildDistributionDeviation(float normalChildDistributionDeviation) {
        this.normalChildDistributionDeviation = normalChildDistributionDeviation;
    }

    public void setNormalChildDistributionDeviation(String normalChildDistributionDeviation) {
        this.normalChildDistributionDeviation = Float.parseFloat(normalChildDistributionDeviation);
    }

    public int getMaxMemberNumber() {
        return maxMemberNumber;
    }

    public void setMaxMemberNumber(int maxMemberNumber) {
        this.maxMemberNumber = maxMemberNumber;
    }

    public void setMaxMemberNumber(String maxMemberNumber) {
        this.maxMemberNumber = Integer.parseInt(maxMemberNumber);
    }

    public int getIndividualIdLength() {
        return individualIdLength;
    }

    public void setIndividualIdLength(int individualIdLength) {
        this.individualIdLength = individualIdLength;
    }

    public void setIndividualIdLength(String individualIdLength) {
        this.individualIdLength =  Integer.parseInt(individualIdLength);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", initialIndividuals='" + initialIndividuals + '\'' +
                ", numberOfGenerations=" + numberOfGenerations +
                ", childAverage=" + childAverage +
                ", normalChildDistributionDeviation=" + normalChildDistributionDeviation +
                ", maxMemberNumber=" + maxMemberNumber +
                ", projectStatus=" + projectStatus +
                ", individualIdLength=" + individualIdLength +
                '}';
    }

    public String toStringConsoleDisplay() {
        return "Project{" +
                " name='" + name + '\'' +
                ", initialIndividuals='" + initialIndividuals + '\'' +
                ", numberOfGenerations=" + numberOfGenerations +
                ", childAverage=" + childAverage +
                ", normalChildDistributionDeviation=" + normalChildDistributionDeviation +
                ", maxMemberNumber=" + maxMemberNumber +
                ", projectStatus=" + projectStatus +
                ", individualIdLength=" + individualIdLength +
                '}';
    }


    public String toStringFileDisplay() {
        return "Project{" +
                "\n id='" + id + '\'' +
                ",\n name='" + name + '\'' +
                ",\n initialIndividuals='" + initialIndividuals + '\'' +
                ",\n numberOfGenerations=" + numberOfGenerations +
                ",\n childAverage=" + childAverage +
                ",\n normalChildDistributionDeviation=" + normalChildDistributionDeviation +
                ",\n maxMemberNumber=" + maxMemberNumber +
                ",\n projectStatus=" + projectStatus +
                ",\n individualIdLength=" + individualIdLength +
                "\n}";
    }
}
