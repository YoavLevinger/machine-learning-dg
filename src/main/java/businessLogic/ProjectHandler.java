package businessLogic;

import commons.IdGenerator;
import dataAccessLayer.rdb.DataSourceDetails;
import dataAccessLayer.rdb.QueryResult;
import dataAccessLayer.rdb.QueryType;

import java.util.*;

/**
 * Created by Levinger on 29/10/2016.
 */
public class ProjectHandler extends Handlers{

    private final String PROJECT_CONFIGURATION_TABLE_NAME = "project_configuration";
    public final String INDIVIDUALS_TABLE_NAME = "individual";
    private static Gender lastGender = Gender.FEMALE;
    private static HashSet<String> idCollection = new HashSet<>();

    /**
     * @return list of all existing project in database
     */
    public List<Project> getAllProjects() {
        setDataSource();
        List<String> schemas = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        query = "SHOW DATABASES";
        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);

        //get all schemas:
        if (queryResult.getErrors().isEmpty()) {
            for (String schema : queryResult.getData().get("SCHEMA_NAME")) {
                schemas.add(schema);
            }
        } else {
            System.out.println("Query for projects configuration failed");
        }

        //Check if schema has the specific project configuration table
        for (String schema : schemas) {
            DataSourceDetails dataSourceDetails = this.dataSourceDetails;
            dataSourceDetails.setSchema(schema);
            databaseQueries.setDataSource(dataSourceDetails);
            query = "SELECT 1 FROM " + PROJECT_CONFIGURATION_TABLE_NAME + " LIMIT 1";
            queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);
            if (queryResult.getErrors().isEmpty()) {
                projects.add(getProjectConfiguration(schema));
            }
        }
        return projects;
    }

    /**
     * Get specific project configuration
     * @param projectName - the project name for configuration retrieval
     * @return - project configuration object
     */
    private Project getProjectConfiguration(String projectName) {
        Project project = new Project(projectName);
        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(projectName);
        databaseQueries.setDataSource(dataSourceDetails);
        query = "SELECT * FROM " + PROJECT_CONFIGURATION_TABLE_NAME;
        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);

        if (queryResult.getErrors().isEmpty()) {
            try {
                project.setNormalChildDistributionDeviation(queryResult.getData().get("Normal_child_distribution_deviation").get(0));
                project.setProjectStatus(queryResult.getData().get("project_status").get(0));
                project.setInitialIndividuals(queryResult.getData().get("first_generation_number_of_individuals").get(0));
                project.setMaxMemberNumber(queryResult.getData().get("max_members").get(0));
                project.setChildAverage(queryResult.getData().get("average_number_of_kids").get(0));
                project.setId(queryResult.getData().get("id").get(0));
                project.setNumberOfGenerations(queryResult.getData().get("number_of_generations").get(0));
                project.setIndividualIdLength(queryResult.getData().get("individual_id_length").get(0));
            } catch (Exception e) {
                System.out.println("Error occurred in getting project ["+ projectName +"] configuration.");
            }
        } else return null;

        return project;
    }



    /**
     * new project creator
     * @param project - the project to create
     * @return - created project name
     */
    public String createNewProject(Project project) {
        String createdProjectName = "NOT CREATED";
        setDataSource();
        //verify project name valid for schema creation.
        project.setName(project.getName().replaceAll(" ","_"));

        //create schema if not exist.
        query = "CREATE SCHEMA IF NOT EXISTS " + project.getName();
        QueryResult queryResult = databaseQueries.query(query, QueryType.DDL, DATABASE_TIMEOUT);

        if (queryResult.getErrors().isEmpty()) {
            createdProjectName = project.getName();
        } else return createdProjectName; //  "NOT CREATED";

        //create configuration table with data
        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "CREATE TABLE IF NOT EXISTS \n" + PROJECT_CONFIGURATION_TABLE_NAME +
                "(\n" +
                "\tid VARCHAR(50) NOT NULL," +
                "\tPRIMARY KEY(Id),\n" +
                "\tproject_name VARCHAR(32),\n" +
                "\tproject_status VARCHAR(32),\n" +
                "\tfirst_generation_number_of_individuals INT,\n" +
                "\tnumber_of_generations INT,\n" +
                "\taverage_number_of_kids float,\n" +
                "\tNormal_child_distribution_deviation float,\n" +
                "\tmax_members int,\n" +
                "\tindividual_id_length int\n" +
                ");";

        queryResult = databaseQueries.query(query, QueryType.DDL, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error creating project configuration table...");
        }

        databaseQueries.setDataSource(dataSourceDetails);

        query = "CREATE TABLE IF NOT EXISTS  " + INDIVIDUALS_TABLE_NAME +
                "(\n" +
                "\tid VARCHAR(50) NOT NULL,\n" +
                "\tPRIMARY KEY(id),\n" +
//                "\tfirst_name VARCHAR(50),\n" +
//                "\tlast_name VARCHAR(50),\n" +
                "\tgender bit,\n" +
                "\tfather_id VARCHAR(50),\n" +
                "\tmother_id VARCHAR(50),\n" +
                "\tgeneration int,\n" +
                "\tpartner_id VARCHAR(50)\n" +
                ");";

        queryResult = databaseQueries.query(query, QueryType.DDL, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error creating individuals table...");
        }

        saveProjectConfiguration(project);

        return createdProjectName;
    }

    /**
     * Save the project configuration to the database
     * @param project - project configuration data object
     * @return success true / false
     */
    private boolean saveProjectConfiguration(Project project) {

        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "INSERT INTO " + PROJECT_CONFIGURATION_TABLE_NAME +
                " VALUES ('"
                + project.getId() + "','"
                + project.getName() + "','"
                + project.getProjectStatus() + "','"
                + project.getInitialIndividuals() + "','"
                + project.getNumberOfGenerations() + "','"
                + project.getChildAverage() + "','"
                + project.getNormalChildDistributionDeviation() + "','"
                + project.getMaxMemberNumber() + "','"
                + project.getIndividualIdLength()
                + "');";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error saving project configuration in database...");
            return false;
        }
        return true;
    }

    /**
     * Updating a project configuration by its name.
     * @param project - project new data, all configuration wil be replaced but its name..
     * @return - updated true /false.
     */
    private boolean updateProjectConfiguration(Project project) {

        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "UPDATE " + PROJECT_CONFIGURATION_TABLE_NAME +
                " SET "
                + "project_name='" + project.getName() + "',"
                + "project_status='" + project.getProjectStatus() + "',"
                + "first_generation_number_of_individuals='" + project.getInitialIndividuals() + "',"
                + "number_of_generations='" + project.getNumberOfGenerations() + "',"
                + "average_number_of_kids='" + project.getChildAverage() + "',"
                + "Normal_child_distribution_deviation='" + project.getNormalChildDistributionDeviation() + "',"
                + "max_members='" + project.getMaxMemberNumber() + "', "
                + "individual_id_length='" + project.getIndividualIdLength() + "' "
                + "WHERE id='" + project.getId() + "';";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error updating project configuration...");
            return false;
        }
        return true;
    }

    /**
     * Delete project by name
     * @param projectName - the project for deletion name
     */
    public void deleteProject(String projectName) {
        dataSourceDetails.setSchema(null);
        databaseQueries.setDataSource(dataSourceDetails);

        query = "DROP DATABASE " + projectName;

        QueryResult queryResult = databaseQueries.query(query, QueryType.DDL, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error deleting project name: ["+ projectName +"]. check if this project exist");
        } else {
            System.out.println("Deleting project name: ["+ projectName +"] successful.");

        }

    }

    /**
     * populate project - generate family data according to project configuration
     * @param projectName - The project to populate
     */
    public void populateProject(String projectName) {
        Gender gender;
        //read project config - project object
        Project project = getProjectConfiguration(projectName);
        Integer individualCounter = 0;

        //Change project status to corrupted data - this will change once population completed
        if (project != null) {
            project.setProjectStatus(ProjectStatus.CORRUPTED_DATA);
            updateProjectConfiguration(project);
        }

        System.out.println("Creating initial individuals");

        //create initial individuals
        if (project != null) {
            for (int i = 1; i <= project.getInitialIndividualsNumber(); i++) {
                if (i % 2 == 0) {
                    gender = Gender.MALE;
                } else {
                    gender = Gender.FEMALE;
                }
                insertIndividual(project, createInitialIndividual(gender, project.getIndividualIdLength()));
                individualCounter++;
                if (i%30 == 0) System.out.println();
            }
        }
        //run generations
        System.out.println("\nCreating all generations: ");
        if (project != null) {
            for (int generation = 1; generation <= project.getNumberOfGenerations(); generation++) {
                if (getTableCount(project.getName(), INDIVIDUALS_TABLE_NAME) < project.getMaxMemberNumber()) {
                    createGeneration(project, generation);
                } else {
                    System.out.println("WARNING: Generation number ["+ generation +"] was not created as it will exceed the max member number");
                }
            }
        }

        //Change project status to populated
        if (project != null) {
            project.setProjectStatus(ProjectStatus.POPULATED);
        }
        updateProjectConfiguration(project);
        displayProjectStatistics(project);
    }

    /**
     * Get all individuals as Hash map of individuals for fast individual retrieval (O(1) !)
     * @param project - the project to refer
     * @return Hash map of individuals
     */
    Map<String, Individual> getIndividualsMap(Project project) {

        List<Individual> individuals = getAllIndividuals(project);

        Map<String, Individual> individualsMap = new HashMap<>();

        for (Individual individual : individuals) {
            individualsMap.put(individual.getId(), individual);
        }
        return individualsMap;
    }


    /**
     * Create initial individual, setting generation = 0, getting unique Id
     * @param gender - The required Gender to create
     * @param idLength - the required Id Length
     * @return - individual object
     */
    private Individual createInitialIndividual(Gender gender, int idLength) {
        Individual individual = new Individual();

        String id;

        do {
            id = IdGenerator.uuid(idLength, 2);
        } while (idCollection.contains(id));

        idCollection.add(id);
        individual.setId(id);
//        individual.setfName(UUID.randomUUID().toString());
//        individual.setlName(UUID.randomUUID().toString());
        individual.setGender(gender);
        individual.setGeneration("0");
        individual.setFatherId(null);
        individual.setMotherId(null);
        individual.setPartnerId(null);

        return individual;
    }

    /**
     * Insert individual to the database
     * @param project - The project (which schema to use).
     * @param individual - The individual to insert.
     */
    private void insertIndividual(Project project, Individual individual) {
        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "INSERT INTO " + INDIVIDUALS_TABLE_NAME +
                " VALUES ('"
                + individual.getId() + "',"
//                + individual.getfName() + "','"
//                + individual.getlName() + "',"
                + (individual.getGender().equals(Gender.FEMALE) ? "0" : "1") + ",'"
                + individual.getFatherId() + "','"
                + individual.getMotherId() + "','"
                + individual.getGeneration() + "','"
                + individual.getPartnerId()
                + "');";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error inserting an individual to the database...");
        } else {
            System.out.print(".");
        }
    }

    /**
     * Create another generation
     * @param project - The project to use.
     * @param generationNumber - which generation to create.
     */
    private Integer createGeneration(Project project, int generationNumber) {
        System.out.println("\nCreating generation # [" + generationNumber +"]" );
        List<Individual> individuals;
        //get all individuals
        individuals = getAllIndividuals(project);
        //mate couples & create childes
        return mateCouples(project, individuals);
    }

    /**
     * Get a list of all individuals.
     * @param project - The project to use.
     * @return - list of all individuals.
     */
    private List<Individual> getAllIndividuals(Project project) {

        List<Individual> individuals = new ArrayList<>();
        Individual individual;
        int numberOfIndividuals;

        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "SELECT * FROM " + INDIVIDUALS_TABLE_NAME;

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT_LONG);

        //query errors handler...
        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error getting all individuals from project: ["+ project.getName() +"]");
        }

        numberOfIndividuals = getTableCount(project.getName(), INDIVIDUALS_TABLE_NAME);

        for (int i = 0; i < numberOfIndividuals; i++) {
            individual = new Individual();
            try {
                individual.setId(queryResult.getData().get("id").get(i));
                individual.setFatherId(queryResult.getData().get("father_id").get(i));
                individual.setGeneration(queryResult.getData().get("generation").get(i));
//            individual.setfName(queryResult.getData().get("first_name").get(i));
//            individual.setlName(queryResult.getData().get("last_name").get(i));
                individual.setMotherId(queryResult.getData().get("mother_id").get(i));
                individual.setPartnerId(queryResult.getData().get("partner_id").get(i));
                individual.setGender(queryResult.getData().get("gender").get(i));
            } catch (Exception e) {
                System.out.println("Error reading Individual from the database");
            }
            individuals.add(individual);
        }
        return individuals;
    }

    /**
     * Mating/matching couples and "creating childes" according to configuration
     * @param project - the project
     * @param individuals - all the individuals
     */
    private Integer mateCouples(Project project, List<Individual> individuals) {
        List<Individual> males = new ArrayList<>();
        List<Individual> females = new ArrayList<>();
        int dotsCounter = 0;
        Random r = new Random();

        Integer currentMembersCounter = getTableCount(project.getName(), INDIVIDUALS_TABLE_NAME);

        for (Individual individual : individuals) {
            if (individual.getGender().equals(Gender.FEMALE) && individual.getPartnerId().equals("null")) {
                females.add(individual);
            } else if (individual.getGender().equals(Gender.MALE) && individual.getPartnerId().equals("null")) {
                males.add(individual);
            }
        }
        if (males.size() != females.size()) {
            System.out.println("unequal number of males [" + males.size() + "] and females[" + females.size() + "]");
        } else {
            System.out.println("Number of males [" + males.size() + "] and females[" + females.size() + "]");
        }


        for (int i = 0; i < (females.size() > males.size() ? males.size() : females.size()); i++) {
            males.get(i).setPartnerId(females.get(i).getId());
            females.get(i).setPartnerId(males.get(i).getId());
            updateIndividual(project, males.get(i));
            updateIndividual(project, females.get(i));
            int numberOfChildes = (int) (Math.round(r.nextGaussian() * project.getNormalChildDistributionDeviation() + project.getChildAverage()));
            for (int j = 1; j <= numberOfChildes; j++) {
                if (currentMembersCounter < project.getMaxMemberNumber()) {
                    createChild(project, males.get(i), females.get(i));
                    currentMembersCounter++;
                    dotsCounter++;
                    if (dotsCounter % 80 == 0) System.out.println();
                } else {
                    System.out.println("Max member reached, stopping child creation...");
                    return currentMembersCounter;
                }
            }
        }
        return currentMembersCounter;
    }

    /**
     * Create a child given mother & father
     * @param project - the project
     * @param father - father individual object
     * @param mother - mother individual object
     */
    private void createChild(Project project, Individual father, Individual mother) {
        lastGender = (lastGender.equals(Gender.FEMALE) ? Gender.MALE : Gender.FEMALE);
        int lastGeneration = Math.max(Integer.parseInt(father.getGeneration()), Integer.parseInt(mother.getGeneration()));
        Individual individual = createInitialIndividual(lastGender, project.getIndividualIdLength());
        individual.setGeneration((lastGeneration + 1) + "");
        individual.setFatherId(father.getId());
        individual.setMotherId(mother.getId());
        insertIndividual(project, individual);
    }

    /**
     * Updating an individual (all but id)
     * @param project - the project
     * @param individual - the individual new details (all but id)
     */
    private void updateIndividual(Project project, Individual individual) {

        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "UPDATE " + INDIVIDUALS_TABLE_NAME +
                " SET father_id='" + individual.getFatherId() + "', " +
                " generation='" + individual.getGeneration() + "', " +
//                " first_name='" + individual.getfName() + "', " +
//                " last_name='" + individual.getlName() + "', " +
                " mother_id='" + individual.getMotherId() + "', " +
                " partner_id='" + individual.getPartnerId() + "', " +
                " gender=" + (individual.getGender().equals(Gender.FEMALE) ? "0" : "1") + " " +
                "WHERE id='" + individual.getId() + "';";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error updating an individual in the database...");
        }

    }

    /**
     * Get a project object
     * @param projectName - the project name to retrieve
     * @return a project object
     */
    public Project getProjectByName(String projectName) {
        return getProjectConfiguration(projectName);
    }


    /**
     * Display project statistics - count by generation & total individuals
     * @param project - the project to refer
     */
    public String displayProjectStatistics(Project project){
        StringBuilder projectStatistics = new StringBuilder();
        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "SELECT count(generation) as count,generation FROM "+ INDIVIDUALS_TABLE_NAME +" group by generation;";
        int totalIndividuals = 0;

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);

        System.out.println("\nproject statistics:\n" + project.toStringFileDisplay());
        projectStatistics.append("\nproject statistics:\n");
        System.out.println("Generations individuals count:");
        projectStatistics.append("Generations individuals count:\n");
        for (int i = 0; i <= getMaxGeneration(project); i++) {
            System.out.println("Generation = ["+ queryResult.getData().get("generation").get(i) + "] Individuals Count = ["+ queryResult.getData().get("count").get(i) + "]");
            projectStatistics.append("Generation = ["+ queryResult.getData().get("generation").get(i) + "] Individuals Count = ["+ queryResult.getData().get("count").get(i) + "]\n");
            totalIndividuals += Integer.parseInt(queryResult.getData().get("count").get(i));
        }
        System.out.println("Total Individuals = [" + totalIndividuals + "]");
        projectStatistics.append("Total Individuals = [" + totalIndividuals + "]\n");

        return projectStatistics.toString();
    }



    /**
     * This method retrieves the last created generation number
     * @param project - the project to refer
     * @return - the number of the last generation
     */
    private int getMaxGeneration(Project project){
        int maxGeneration = -1;
        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        query = "select max(generation) maxgeneration from " + INDIVIDUALS_TABLE_NAME;

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);
        try {
            maxGeneration = Integer.parseInt(queryResult.getData().get("maxgeneration").get(0));
        } catch (Exception e) {
            System.out.println("Error getting the max generation number, using error -1 as max generation number");
        }

        return maxGeneration;
    }

    /**
     * Check if a project with the same name already exist
     * @param projectName - The project name to check
     * @return True if found, false if not.
     */
    public boolean checkIfProjectExist(String projectName) {
        List<Project> projects = this.getAllProjects();
        for (Project project: projects) {
            if (project.getName().equalsIgnoreCase(projectName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIfProjectStatus(String projectName, ProjectStatus projectStatus) {
        List<Project> projects = this.getAllProjects();
        for (Project project: projects) {
            if (project.getName().equalsIgnoreCase(projectName.toLowerCase()) &&
                    project.getProjectStatus().equals(projectStatus)) {
                return true;
            }
        }
        return false;
    }
}
