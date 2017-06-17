package businessLogic;

import dataAccessLayer.rdb.*;

import java.util.Calendar;

/**
 * Created by user on 17/12/2016.
 */

/**
 * This class is the handlers parent, holds the database connection string and run joint DB tasks.
 */
public class Handlers {

    final String PROJECT_MANAGEMENT_SCHEMA_NAME = "project_management";

    DatabaseQueries databaseQueries = new DatabaseQueries();    
    DataSourceDetails dataSourceDetails = new DataSourceDetails(DatabaseType.MYSQL, "root", "password", "127.0.0.1", "3306", null, null);
    final int DATABASE_TIMEOUT = 10;
    final int DATABASE_TIMEOUT_LONG = 3600;
    String query = null;

    void setDataSource() {
        databaseQueries.setDataSource(dataSourceDetails);
    }


    /**
     * Get the table count - number of rows in the defined table.
     *
     * @param schema    - the database/schema required.
     * @param tableName - the table to query for number of rows.
     * @return - number of rows in table.
     */
    public int getTableCount(String schema, String tableName) {
        dataSourceDetails.setSchema(schema);
        databaseQueries.setDataSource(dataSourceDetails);
        int result = 0;
//        System.out.println("["+(Calendar.getInstance()).getTime() + "] Getting table/view count for: [" + schema + "]:[" + tableName + "]");
        query = "SELECT count(*) as count FROM " + tableName;

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {

            System.out.println("Error getting table/view count for: [" + schema + "]:[" + tableName + "]");

        } else {

            try {
                if (queryResult.getData().get("count") != null) {
//                    System.out.println("["+(Calendar.getInstance()).getTime() + "] Table/view count for: [" + schema + "]:[" + tableName + "] = [ " + Integer.parseInt(queryResult.getData().get("count").get(0)) + "]");
                    result = Integer.parseInt(queryResult.getData().get("count").get(0));
                }
            } catch (Exception e) {
                System.out.println("["+(Calendar.getInstance()).getTime() + "] Error getting table/view count for: [" + schema + "]:[" + tableName + "] A");
            }
        }


        return result;
    }

    //create project management DB if not exist
    void createProjectManagementDB() {
        setDataSource();
        String query = "CREATE SCHEMA IF NOT EXISTS " + PROJECT_MANAGEMENT_SCHEMA_NAME;
        QueryResult queryResult = databaseQueries.query(query, QueryType.DDL, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error creating project management schema...");
        }
    }

}
