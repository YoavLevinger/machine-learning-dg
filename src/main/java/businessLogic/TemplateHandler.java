package businessLogic;

import businessLogic.excelTemplateDataRead.ExcelRow;
import businessLogic.excelTemplateDataRead.Template;
import commons.ExcelOperations;
import commons.JsonObjectConverter;
import dataAccessLayer.rdb.DataSourceDetails;
import dataAccessLayer.rdb.QueryResult;
import dataAccessLayer.rdb.QueryType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Levinger on 26/12/2016.
 */
public class TemplateHandler extends Handlers {

    private final static String TEMPLATES_TABLE_NAME = "templates";

    /**
     * Read the Template from the Excel file
     * @param path - The path to the excel file (file name included)
     * @return - Excel Template object containing all the data that was retrieved from the excel file.
     */
    public List<ExcelRow> readTemplateFromExcelFile(String path) {
        return ExcelOperations.getTemplateFromExcelFile(path);
    }

    /**
     * Create new template
     * @param template - A template object
     */
    public void createNewTemplate(Template template) {
        createNewTemplate(template.getExcelRows(), template.getName());
    }

    /**
     * Create new template
     * @param excelRows - The Excel Rows data
     * @param templateName - The template name
     */
    private void createNewTemplate(List<ExcelRow> excelRows, String templateName) {
        this.createProjectManagementDB();
        createTemplatesTable();
        storeTemplateInDatabase(excelRows, templateName);
    }

    /**
     * Creates the Template table within the project management schema
     */
    private void createTemplatesTable() {

        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);

        String query = "CREATE TABLE IF NOT EXISTS \n" + TEMPLATES_TABLE_NAME +
                "(\n" +
                "\ttemplate_name VARCHAR(100),\n" +
                "\ttemplate_json TEXT\n" +
                ");";

        QueryResult queryResult = databaseQueries.query(query, QueryType.DDL, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error creating template table...");
        }

    }

    /**
     * Save a template within the project management Template's table
     * @param excelRows - The Excel Rows data
     * @param templateName - The Template name
     */
    private void storeTemplateInDatabase(List<ExcelRow> excelRows, String templateName) {
        String templateAsJson = JsonObjectConverter.objectToJson(excelRows);

        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);

        String query = "INSERT INTO " + TEMPLATES_TABLE_NAME +
                " VALUES ('"
                + templateName + "','"
                + templateAsJson
                + "');";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()) {
            System.out.println("Error saving Template...");
        }
    }

    /**
     * Retrieve all Templates from database
     * @return - List of Template objects
     */
    public List<Template> getAllTemplates() {
        createProjectManagementDB();
        createTemplatesTable();

        List<Template> templates = new ArrayList<>();
        Template template;
        int numberOfTemplates;

        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);

        query = "SELECT * FROM " + TEMPLATES_TABLE_NAME;

        QueryResult queryResult = databaseQueries.query(query, QueryType.SELECT, DATABASE_TIMEOUT);

        numberOfTemplates = getTableCount(PROJECT_MANAGEMENT_SCHEMA_NAME, TEMPLATES_TABLE_NAME);

        for (int i = 0; i < numberOfTemplates; i++) {
            template = new Template();
            try {
            template.setName(queryResult.getData().get("template_name").get(i));
            template.setExcelRows(queryResult.getData().get("template_json").get(i));

            templates.add(template);
            } catch (Exception e) {
                System.out.println("Error retrieving a template from database");
            }
        }
        return templates;
    }

    /**
     * Retrieve a Single Template object from database given the template name
     * @param templateName - The required Template's name
     * @return - The Required Template object
     */
    public Template getTemplate(String templateName) {
        List<Template> templates = getAllTemplates();

        for (Template template : templates) {
            if (template.getName().equalsIgnoreCase(templateName)) {
                return template;
            }
        }
        return null;
    }

    /**
     * Delete a Template from database given its name.
     * @param templateName - The Template's name.
     */
    public void deleteTemplate(String templateName) {
        DataSourceDetails dataSourceDetails = this.dataSourceDetails;
        dataSourceDetails.setSchema(PROJECT_MANAGEMENT_SCHEMA_NAME);
        databaseQueries.setDataSource(dataSourceDetails);
        query = "DELETE FROM " + TEMPLATES_TABLE_NAME + " WHERE template_name=\'" + templateName + "\'";

        QueryResult queryResult = databaseQueries.query(query, QueryType.TRANSACTIONAL_MODIFY, DATABASE_TIMEOUT);

        if (!queryResult.getErrors().isEmpty()){
            System.out.println("Error deleting a template from the database");
        }
    }

    public boolean checkIfTemplateExist(String templateName) {
        List<Template> templates = getAllTemplates();

        for (Template template: templates) {
            if (template.getName().equalsIgnoreCase(templateName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
