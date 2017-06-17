package businessLogic;

import businessLogic.excelTemplateDataRead.ExcelRow;
import businessLogic.excelTemplateDataRead.Template;
import commons.IdGenerator;
import dataAccessLayer.rdb.DataSourceDetails;
import dataAccessLayer.rdb.QueryResult;
import dataAccessLayer.rdb.QueryType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Levinger on 17/12/2016.
 */
public class RelationHandler extends Handlers {


    private final String RELATIONS_TABLE_NAME = "relations";

    /**
     * create relations table if not exist
     */
    private void createRelationsTable() {
        //create configuration table with data
        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);

        String query = "CREATE TABLE IF NOT EXISTS \n" + RELATIONS_TABLE_NAME +
                "(\n" +
                "\trelation_name VARCHAR(500),\n" +
                "\trelation_prefix_Name VARCHAR(100),\n" +
                "\trelation_query TEXT,\n" +
                "\trelation_description TEXT,\n" +
                "\trelation_view_headers VARCHAR(200)\n" +
                ");";

        QueryResult queryResult = databaseQueries.query(query, QueryType.DDL, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error creating relation table...");
        }
    }

    /**
     * create new relation
     *
     * @param relation - the relation to create (Save in database)
     */
    public void createNewRelation(Relation relation) {
        createProjectManagementDB();
        createRelationsTable();
        saveRelation(relation);
    }

    /**
     * Save Relation in database
     *
     * @param relation - the relation to save
     */
    private void saveRelation(Relation relation) {
        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);

        String query = "INSERT INTO " + RELATIONS_TABLE_NAME +
                " VALUES ('"
                + relation.getName() + "','"
                + relation.getPrefixName() + "','"
                + relation.getQuery() + "','"
                + relation.getDescription() + "','"
                + relation.getViewHeadersString()
                + "');";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error saving relation...");
        }
    }

    /**
     * Retrieve all relations
     *
     * @return list of relations
     */
    public List<Relation> getAllRelations() {
        createProjectManagementDB();
        createRelationsTable();
        List<Relation> relations = new ArrayList<>();
        Relation relation;
        int numberOfRelations;

        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);

        query = "SELECT * FROM " + RELATIONS_TABLE_NAME;

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);

        numberOfRelations = getTableCount(PROJECT_MANAGEMENT_SCHEMA_NAME, RELATIONS_TABLE_NAME);

        for (int i = 0; i < numberOfRelations; i++) {
            try {
                relation = new Relation();
                relation.setName(queryResult.getData().get("relation_name").get(i));
                relation.setPrefixName(queryResult.getData().get("relation_prefix_Name").get(i));
                relation.setQuery(queryResult.getData().get("relation_query").get(i));
                relation.setDescription(queryResult.getData().get("relation_description").get(i));
                relation.setViewHeaders(queryResult.getData().get("relation_view_headers").get(i));
                relations.add(relation);
            } catch (Exception e) {
                System.out.println("Error getting relation information..");
            }
        }
        return relations;
    }

    /**
     * Runs the selected relations
     *
     * @param relationsNames , separated relations name list
     * @param projectName    - the project to refer
     */
    public void runSelectedRelations(String relationsNames, String projectName) {
        List<String> relationsNamesList = Arrays.asList(relationsNames.split(","));
        runSelectedRelations(relationsNamesList, projectName);
    }


    /**
     * Runs the relations in the list
     *
     * @param relationsNames - relations' list
     * @param projectName    - the project to refer
     */
    private void runSelectedRelations(List<String> relationsNames, String projectName) {
        Relation relation;
        ProjectHandler projectHandler = new ProjectHandler();
        Project project = projectHandler.getProjectByName(projectName);
        for (String relationName : relationsNames) {
            System.out.println("[" + (Calendar.getInstance()).getTime() + "] Running relation query to database view: [" + relationName + "]");
            System.out.println("Note: for large amount of individuals / generations /complex relations it may take several minutes...");
            relation = getRelationByName(relationName);
            if (relation != null) {
                writeRelationInProjectDatabase(relation, project);
            }
        }
    }

    //run relation on project

    /**
     * Write relation in project database
     *
     * @param relation - the relation to create
     * @param project  - The project to refer
     */
    public void writeRelationInProjectDatabase(Relation relation, Project project) {
        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);

        String query = "CREATE OR REPLACE VIEW " + relation.getName() + " AS " + relation.getQuery();

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT_LONG);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error creating relation view...");
        } else {
            System.out.println("Query for relation " + relation.getName() + " was sent successfully to the database ");
            System.out.println("Relation details: ");
            System.out.println(relation.toStringConsoleDisplay());
        }
    }


    /**
     * Retrieve relation from database by its name
     *
     * @param relationName - The relation name
     * @return - Relation object
     */
    public Relation getRelationByName(String relationName) {

        Relation relation = new Relation();

        relation.setName(relationName);

        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);
        query = "SELECT * FROM " + RELATIONS_TABLE_NAME + " WHERE relation_name=\'" + relationName + "\'";

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);

        if (queryResult.getErrors().isEmpty()) {
            try {
                relation.setPrefixName(queryResult.getData().get("relation_prefix_Name").get(0));
                relation.setQuery(queryResult.getData().get("relation_query").get(0));
                relation.setDescription(queryResult.getData().get("relation_description").get(0));
                relation.setViewHeaders(queryResult.getData().get("relation_view_headers").get(0));
            } catch (Exception e) {
                System.out.println("Error retrieving relation " + relationName + " information from database");
                return null;
            }
        } else return null;

        return relation;
    }


    /**
     * Write relation to file
     *
     * @param relation - the relation to write
     * @param project  - The project to refer
     */
    public void createRelationFile(Relation relation, Project project) {

        StringBuilder relationString = new StringBuilder();

        //read relation table
        List<Individual[]> relationRecordsList = readRelationTable(relation, project);

        //build the string for the file
        for (Individual[] individualsRelationRecord : relationRecordsList) {

            relationString.append(relation.getPrefixName());
            try {
                for (int i = 0; i < individualsRelationRecord.length; i++) {
                    relationString.append(",").append(individualsRelationRecord[i].getGenderCapital()).append(",").append(individualsRelationRecord[i].getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            relationString.append("\n");
        }

        //save to file
        File directory = new File(project.getName());
        try {
            if (!directory.exists()) {
                boolean result = directory.mkdir();
                if (!result) System.out.println("Error creating project folder");
            }
            FileUtils.writeStringToFile(new File(project.getName() + "/" + project.getName() + "-" + relation.getName() + ".txt"), relationString.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Save project configuration to file as well...
        //save to file
        String configurationString = project.toStringFileDisplay() + "\n" + relation.toStringFileDisplay();
        try {
            FileUtils.writeStringToFile(new File(project.getName() + "/" + project.getName() + "-" + relation.getName() + "-log" + ".txt"), configurationString, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write relation to file -Given PROJECT, RELATION(S) & TEMPLATE
     *
     * @param project        - The project to refer
     * @param relationsNames - The relation(s) (, separated list)
     * @param template       - The Template to use
     * @param requiredOrder  - The required output order (RANDOM,SORTED,NATURAL;)
     * @param idLength       - The length of each individual id length as it would be written to file
     */
    public void createRelationFile(Project project, String relationsNames, Template template, RequiredOrder requiredOrder, int idLength) {

        List<Individual[]> relationRecordsList;
        Map<String, String> projectStatistics = new HashMap<>();
        List<RelationRecord> relationRecords = new ArrayList<>();
        List<String> relationsNamesList = Arrays.asList(relationsNames.split(","));
        Relation relation;
        ProjectHandler projectHandler = new ProjectHandler();

        StringBuilder relationString = new StringBuilder();

        //read relation tables
        for (String relationName : relationsNamesList) {
            relation = getRelationByName(relationName);
            System.out.println("[" + (Calendar.getInstance()).getTime() + "] Reading relation [" + relationName + "] records from database");
            relationRecordsList = readRelationTable(relation, project);
            projectStatistics.put(relationName + " records", relationRecordsList.size() + "");
            System.out.println("[" + (Calendar.getInstance()).getTime() + "] Relation [" + relationName + "] was read from database");

            //Individual records to Relation record
            relationRecords.addAll(individualsRecordsToRelationRecords(relationRecordsList, relation));
        }

        //sort order
        switch (requiredOrder) {
            case NATURAL:
                //do nothing - this is natural...
                break;
            case SORTED://sorted: by relation name,id,id,id,id
                relationRecords = RelationRecord.sortLists(relationRecords);
                break;
            case RANDOM://shuffled
                long seed = System.nanoTime();
                Collections.shuffle(relationRecords, new Random(seed));
                break;
        }

        //matcher
        relationRecords = matchTemplateToRelationRecords(relationRecords, template, idLength);

        //build the string for the file
        for (RelationRecord relationRecord : relationRecords) {

            if (relationRecord != null) {
                relationString.append(relationRecord.getName()).append(",");
                relationString.append(relationRecord.getXg()).append(",").append(relationRecord.getX());
                if (relationRecord.getY() != null) {
                    relationString.append(",").append(relationRecord.getYg()).append(",").append(relationRecord.getY());
                }
                if (relationRecord.getZ() != null) {
                    relationString.append(",").append(relationRecord.getZg()).append(",").append(relationRecord.getZ());
                }
                if (relationRecord.getT() != null) {
                    relationString.append(",").append(relationRecord.getTg()).append(",").append(relationRecord.getT());
                }
                relationString.append("\n");
            }
        }
        File directory = new File(project.getName());
        //save to file
        try {
            if (!directory.exists()) {
                boolean result = directory.mkdir();
                if (!result) System.out.println("Error creating project folder");
            }
            FileUtils.writeStringToFile(new File(project.getName() + "/" + project.getName() + "-" + template.getName() + ".txt"), relationString.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Save project configuration to file as well...
        //save to file
        String configurationString = project.toStringFileDisplay() + "\n" + template.toString() + "\n\n";

        for (Map.Entry<String, String> entry : projectStatistics.entrySet()) {
            configurationString += "[" + entry.getKey() + "] : [" + entry.getValue() + "]\n";
        }

        //insert project statistics
        configurationString += projectHandler.displayProjectStatistics(project);

        try {
            FileUtils.writeStringToFile(new File(project.getName() + "/" + project.getName() + "-" + template.getName() + "-log" + ".txt"), configurationString, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Match Template to Relation records
     *
     * @param relationRecords - List of relation record to use
     * @param template        - The template to use
     * @param idLength        - The IdLength to use
     * @return - List of Relation records after the Template was applied
     */
    private List<RelationRecord> matchTemplateToRelationRecords(List<RelationRecord> relationRecords, Template template, int idLength) {
        RelationRecord relationRecord;

        //trimming / padding of Ids:
        System.out.println("Trimming / padding  Ids...");
        for (int j = 0; j < relationRecords.size(); j++) {
            relationRecord = new RelationRecord();
            relationRecord.setName(relationRecords.get(j).getName());
            relationRecord.setXg(relationRecords.get(j).getXg());
            relationRecord.setX(IdGenerator.idTrimmerPadder(relationRecords.get(j).getX(), idLength, IdGenerator.TrimLocation.MSB));
            relationRecord.setYg(relationRecords.get(j).getYg());
            relationRecord.setY(IdGenerator.idTrimmerPadder(relationRecords.get(j).getY(), idLength, IdGenerator.TrimLocation.MSB));
            relationRecord.setZg(relationRecords.get(j).getZg());
            relationRecord.setZ(IdGenerator.idTrimmerPadder(relationRecords.get(j).getZ(), idLength, IdGenerator.TrimLocation.MSB));
            relationRecord.setTg(relationRecords.get(j).getTg());
            relationRecord.setT(IdGenerator.idTrimmerPadder(relationRecords.get(j).getT(), idLength, IdGenerator.TrimLocation.MSB));
            relationRecord.setFullName(relationRecords.get(j).getFullName());
            relationRecords.set(j, relationRecord);
        }

        //match template for each record
        for (int j = 0; j < relationRecords.size(); j++) {
            relationRecord = applyTemplateOnRelationRecord(relationRecords.get(j), template, idLength);
            relationRecords.set(j, relationRecord);
        }

        return relationRecords;
    }

    /**
     * Apply Template on a single relation record randomly according to percentage
     *
     * @param relationRecordSource - The relationRecord to handle
     * @param template             - The Template to use
     * @param idLength             - The idLength (Required for ? marks calculations)
     * @return RelationRecord after the template was applied
     */
    private RelationRecord applyTemplateOnRelationRecord(RelationRecord relationRecordSource, Template template, int idLength) {
        RelationRecord relationRecord = null;
        ExcelRow excelRow;
        List<Integer> locations;
        List<ExcelRow> excelRows = new ArrayList<>();

        //with the following method, if there would be more than 100 % in the excel template, the ratio will remain.
        for (ExcelRow row : template.getExcelRows()) {
            if (row.getRelation() != null) {
                if (row.getPercentage() > 0 && row.getRelation().equalsIgnoreCase(relationRecordSource.getFullName().toLowerCase())) {
                    for (int i = 0; i < row.getPercentage(); i++) {
                        excelRows.add(row);
                    }
                }
            }
        }

        if (excelRows.size() == 0) {
            return null;
        }

        Random randomizer = new Random();


        excelRow = excelRows.get(randomizer.nextInt(excelRows.size()));

        if (!excelRow.isRandom()) {
            relationRecord = new RelationRecord();
            relationRecord.setName((excelRow.isRelationPrefixName() ? relationRecordSource.getName() : (relationRecordSource.getName() != null ? "?" + relationRecordSource.getName() : null)));
            relationRecord.setXg((excelRow.isxGender() ? relationRecordSource.getXg() : (relationRecordSource.getXg() != null ? "?" + relationRecordSource.getXg() : null)));
            relationRecord.setX((excelRow.isxId() ? relationRecordSource.getX() : (relationRecordSource.getX() != null ? getAllStringWithQMarks(relationRecordSource.getX()) : null)));
            relationRecord.setYg((excelRow.isyGender() ? relationRecordSource.getYg() : (relationRecordSource.getYg() != null ? "?" + relationRecordSource.getYg() : null)));
            relationRecord.setY((excelRow.isyId() ? relationRecordSource.getY() : (relationRecordSource.getY() != null ? getAllStringWithQMarks(relationRecordSource.getY()) : null)));
            relationRecord.setZg((excelRow.iszGender() ? relationRecordSource.getZg() : (relationRecordSource.getZg() != null ? "?" + relationRecordSource.getZg() : null)));
            relationRecord.setZ((excelRow.iszId() ? relationRecordSource.getZ() : (relationRecordSource.getZ() != null ? getAllStringWithQMarks(relationRecordSource.getZ()) : null)));
            relationRecord.setTg((excelRow.istGender() ? relationRecordSource.getTg() : (relationRecordSource.getTg() != null ? "?" + relationRecordSource.getTg() : null)));
            relationRecord.setT((excelRow.istId() ? relationRecordSource.getT() : (relationRecordSource.getT() != null ? getAllStringWithQMarks(relationRecordSource.getT()) : null)));
        }

        if (excelRow.isRandom()) {
            int totalOptionsForQMarks;
            totalOptionsForQMarks = 0;
            //name = 1, each Gender = 1, each id = length | if Id/gender != null.
            totalOptionsForQMarks += 1; //relation name
            totalOptionsForQMarks += idLength + 1; //x, xg
            totalOptionsForQMarks += (relationRecordSource.getY() != null ? idLength + 1 : 0); //y, yg
            totalOptionsForQMarks += (relationRecordSource.getZ() != null ? idLength + 1 : 0); //z, zg
            totalOptionsForQMarks += (relationRecordSource.getT() != null ? idLength + 1 : 0); //t, tg

            locations = getRandomLocations(totalOptionsForQMarks, excelRow.getRandomBits());

            relationRecord = new RelationRecord();
            relationRecord.setName((locations.contains(1) ? "?" + relationRecordSource.getName() : relationRecordSource.getName())); //location = 1
            relationRecord.setXg((locations.contains(2) ? "?" + relationRecordSource.getXg() : relationRecordSource.getXg())); //location = 2
            relationRecord.setX(getGeneratedStringWithQMarks(relationRecordSource.getX(), locations, 3, idLength + 3)); //location = 3 -> idLength + 3
            relationRecord.setYg((locations.contains(idLength + 3) ? "?" + relationRecordSource.getYg() : relationRecordSource.getYg())); //location =  idLength + 3
            relationRecord.setY(getGeneratedStringWithQMarks(relationRecordSource.getY(), locations, idLength + 4, 2 * idLength + 4)); //location = idLength + 4 -> 2*idLength + 4
            relationRecord.setZg((locations.contains(2 * idLength + 4) ? "?" + relationRecordSource.getZg() : relationRecordSource.getZg())); //location = 2*idLength + 4
            relationRecord.setZ(getGeneratedStringWithQMarks(relationRecordSource.getZ(), locations, 2 * idLength + 5, 3 * idLength + 5)); //location = 2*idLength + 5 -> 3*idLength + 5
            relationRecord.setTg((locations.contains(3 * idLength + 5) ? "?" + relationRecordSource.getTg() : relationRecordSource.getTg())); //location = 3*idLength + 5
            relationRecord.setT(getGeneratedStringWithQMarks(relationRecordSource.getT(), locations, 3 * idLength + 6, 4 * idLength + 6)); //location = 3*idLength + 6 -> 4*idLength + 6
        }

        return relationRecord;
    }


    /**
     * Get a list of required random locations within a given length
     *
     * @param length                  - The length represents the boundary max, starting with 0.
     * @param requiredRandomLocations - The required random locations within the given boundery length
     * @return - List of locations generated randomly.
     */
    private List<Integer> getRandomLocations(int length, int requiredRandomLocations) {
        Random rand = new Random();
        List<Integer> locations = new ArrayList<>();
        int tempRand;
        if (requiredRandomLocations >= length) {
            for (int i = 1; i <= length; i++) {
                locations.add(i);
            }
        } else {
            for (int i = 0; i < requiredRandomLocations; i++) {
                do {
                    tempRand = rand.nextInt(length) + 1;
                } while (locations.contains(tempRand));
                locations.add(tempRand);
            }
        }
        return locations;
    }

    /**
     * Replace specified chars with '?' question marks
     *
     * @param id         - The sent id String
     * @param locations  - The locations to replace
     * @param rangeStart - The starting point of the relevant is string sent within the total record
     * @param rangeEnd   - The end point of the relevant is string sent within the total record
     * @return - the id string with replaced (if any) chars
     */
    private String getGeneratedStringWithQMarks(String id, List<Integer> locations, int rangeStart, int rangeEnd) {
        if (id == null) return null;

        int insertCounter = 0;
        StringBuilder result = new StringBuilder(id);

        Collections.sort(locations);

        for (Integer location : locations) {
            try {
                if (location >= rangeStart && location < rangeEnd) {
                    result.insert(location - rangeStart + insertCounter, '?');
                    insertCounter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }


    /**
     * Get a String and return it with question marks for each char
     *
     * @param id - The received String EG: 010110101101
     * @return 010110101101 -> ?0?1?0?1?1?0?1?0?1?1?0?1
     */
    private String getAllStringWithQMarks(String id) {
        if (id == null) return null;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < id.length(); i++) {
            result.append("?").append(id.charAt(i));
        }
        return result.toString();
    }

    /**
     * Transforms a list of Individuals to a list of Relation records
     *
     * @param individuals The Individuals list
     * @param relation    - The relation to use
     * @return - A  list of Relation records
     */
    private List<RelationRecord> individualsRecordsToRelationRecords(List<Individual[]> individuals, Relation relation) {
        List<RelationRecord> relationRecords = new ArrayList<>();
        RelationRecord relationRecord;
        try {
            //Individual records to Relation record
            for (Individual[] individualsRelationRecord : individuals) {

                relationRecord = new RelationRecord();

                relationRecord.setName(relation.getPrefixName());
                relationRecord.setFullName(relation.getName());

                if (individualsRelationRecord.length > 0) { //x

                    relationRecord.setXg(individualsRelationRecord[0].getGenderCapital());
                    relationRecord.setX(individualsRelationRecord[0].getId());

                    if (individualsRelationRecord.length > 1) { //y
                        relationRecord.setYg(individualsRelationRecord[1].getGenderCapital());
                        relationRecord.setY(individualsRelationRecord[1].getId());
                    }
                    if (individualsRelationRecord.length > 2) { //z
                        relationRecord.setZg(individualsRelationRecord[2].getGenderCapital());
                        relationRecord.setZ(individualsRelationRecord[2].getId());
                    }
                    if (individualsRelationRecord.length > 3) { //t
                        relationRecord.setTg(individualsRelationRecord[3].getGenderCapital());
                        relationRecord.setT(individualsRelationRecord[3].getId());
                    }
                }
                relationRecords.add(relationRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relationRecords;
    }


    /**
     * Read a relation table/view and return a list of all individuals within a list of individuals array
     *
     * @param relation - The relation to use
     * @param project  - The project to use
     * @return - a list of all individuals within a list of individuals array
     */
    private List<Individual[]> readRelationTable(Relation relation, Project project) {

        List<Individual[]> relationRecordsList = new ArrayList<>();
        Individual[] record = new Individual[relation.getViewHeaders().length];

        ProjectHandler projectHandler = new ProjectHandler();

        Map<String, Individual> individualMap = projectHandler.getIndividualsMap(project);

        dataSourceDetails.setSchema(project.getName());
        databaseQueries.setDataSource(dataSourceDetails);
//todo:
        query = "SELECT * FROM " + relation.getName();

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT_LONG);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error Reading relation [" + relation.getName() + "] from database");
        }

        System.out.println("Relation number of records for relation ["+relation.getName()+"]:["+queryResult.getData().get(relation.getViewHeaders()[0]).size()+"]");

        for (int i = 0; i < queryResult.getData().get(relation.getViewHeaders()[0]).size(); i++) {
            for (int j = 0; j < relation.getViewHeaders().length; j++) {
                try {
                    record[j] = individualMap.get(queryResult.getData().get(relation.getViewHeaders()[j]).get(i));
                } catch (Exception e) {
                    System.out.println("Error reading relation data from database....");
                }
            }
            relationRecordsList.add(record);
            record = new Individual[relation.getViewHeaders().length];
        }

        return relationRecordsList;
    }

    /**
     * Delete a Relation from database given its name
     *
     * @param relationName - The required relation name
     */
    public void deleteRelation(String relationName) {
        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);
        query = "DELETE FROM " + RELATIONS_TABLE_NAME + " WHERE relation_name=\'" + relationName + "\'";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error deleting a relation from the database");
        }
    }

    public boolean checkIfRelationExist(String relationName) {
        List<Relation> relations = this.getAllRelations();

        for (Relation relation : relations) {
            if (relation.getName().equalsIgnoreCase(relationName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
