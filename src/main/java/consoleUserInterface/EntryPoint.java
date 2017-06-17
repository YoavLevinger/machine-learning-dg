package consoleUserInterface;

import businessLogic.*;
import businessLogic.excelTemplateDataRead.Template;
import commons.UserInput;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Levinger.
 */
public class EntryPoint {

    private static String userInput;

    /**
     * Application Entry point & the main
     *
     * @param args - NOT REQUIRED
     */
    public static void main(String[] args) {
        runConsoleMenu();
    }

    /**
     * Main console menu and user options
     */
    private static void runConsoleMenu() {
        String userInput;
        do {
            printMenu();
            userInput = UserInput.getUserInput("Select option and press Enter", UserInput.RequiredInputType.STRING_TYPE, "exit");

            switch (userInput.toLowerCase()) {
                case "1": //Create new project
                    createNewProject();
                    break;
                case "2": //Display all projects
                    displayAllProjects();
                    break;
                case "3": //Delete a project
                    deleteProject();
                    break;
                case "4": //Populate a project
                    populateProject();
                    break;
                case "5": //Create new Relation
                    createNewRelation();
                    break;
                case "6": //display all relations
                    displayAllRelations();
                    break;
                case "7": //Run relation
                    runRelation();
                    break;
                case "8": //Generate relation file
                    createRelationFile();
                    break;
                case "9": //delete Relation
                    deleteRelation();
                    break;
                case "10": //delete Relation
                    displayRelation();
                    break;
                case "11": //load Template
                    loadTemplate();
                    break;
                case "12": //Display all templates
                    displayAllTemplates();
                    break;
                case "13": //Delete Template
                    deleteTemplate();
                    break;
                case "14": //Display Template by name
                    displayTemplateByName();
                    break;
                case "15": //Generate Project - Relation - Template file
                    generateProjectRelationTemplateFile();
                    break;
                case "16":
                case "6382020":
                    generateDefaultRelationsInDB();
                    break;
                case "exit": //exit
                case "99": //exit
                    System.out.println("Exit application...");
                default:
                    System.out.println("Input was incorrect, please try again.");
                    break;
            }
        } while (!userInput.equalsIgnoreCase("exit") && !userInput.equalsIgnoreCase("99"));

    }

    private static void displayRelation() {

        RelationHandler relationHandler = new RelationHandler();
        Relation relation;
        userInput = UserInput.getUserInput("Please insert the Relation Name to display", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        if (!relationHandler.checkIfRelationExist(userInput)) {
            System.out.println("Relation by this name does not exist, diverted back to menu");
            return;
        }
        relation = relationHandler.getRelationByName(userInput);

        System.out.println("The required relation information:");
        System.out.println(relation.toStringFileDisplay());
    }


    private static void generateDefaultRelationsInDB() {
        RelationHandler relationHandler = new RelationHandler();
        Relation grandparent = new Relation();
        Relation parent = new Relation();
        Relation sibling = new Relation();
        Relation uncle = new Relation();

        grandparent.setName("grandparent");
        grandparent.setPrefixName("gp");
        grandparent.setQuery("SELECT  childParent.id as child, childParent.father_id as parent, parentgp.father_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id where childParent.generation>1 UNION SELECT childParent.id as child, childParent.father_id as parent, parentgp.mother_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id where childParent.generation>1 UNION SELECT childParent.id as child, childParent.mother_id as parent, parentgp.father_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id where childParent.generation>1 UNION SELECT childParent.id as child, childParent.mother_id as parent, parentgp.mother_id as gp FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id where childParent.generation>1");
        grandparent.setDescription("Grandparent relation: child-parent-grandparent");
        grandparent.setViewHeaders("child,parent,gp");

        parent.setName("parent");
        parent.setPrefixName("prt");
        parent.setQuery("select id as child, father_id as parent from individual where generation>0 UNION select id as child, mother_id as parent from individual where generation>0 order by child");
        parent.setDescription("Parent relation: child-parent");
        parent.setViewHeaders("child,parent");

        sibling.setName("sibling");
        sibling.setPrefixName("sib");
        sibling.setQuery("SELECT childparent.id as sibling1, childparent.father_id as parent, brother.id as sibling2 FROM individual as childparent INNER JOIN individual as brother ON childparent.father_id = brother.father_id where childparent.generation>1 AND childparent.id <> brother.id union SELECT childparent.id as sibiling1, childparent.mother_id as parent, brother.id as sibiling2 FROM individual as childparent INNER JOIN individual as brother  ON childparent.mother_id = brother.mother_id where childparent.generation>1 AND childparent.id <> brother.id order by parent");
        sibling.setDescription("Sibling relation -> child, parent, brother");
        sibling.setViewHeaders("sibling1,parent,sibling2");

        uncle.setName("uncle");
        uncle.setPrefixName("uncl");
        uncle.setQuery("SELECT  childParent.id as child, childParent.father_id as parent, parentgp.father_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id INNER JOIN individual as brother ON parentgp.father_id = brother.father_id where childparent.generation>1 AND childparent.father_id <> brother.id union SELECT  childParent.id as child, childParent.mother_id as parent, parentgp.father_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id INNER JOIN individual as brother ON parentgp.father_id = brother.father_id where childparent.generation>1 AND childparent.mother_id <> brother.id union SELECT  childParent.id as child, childParent.mother_id as parent, parentgp.mother_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.mother_id=parentgp.id INNER JOIN individual as brother ON parentgp.mother_id = brother.mother_id where childparent.generation>1 AND childparent.mother_id <> brother.id union SELECT  childParent.id as child, childParent.father_id as parent, parentgp.mother_id as gp, brother.id as uncle FROM individual as childParent INNER JOIN individual as parentgp ON childParent.father_id=parentgp.id INNER JOIN individual as brother ON parentgp.mother_id = brother.mother_id where childparent.generation>1 AND childparent.father_id <> brother.id");
        uncle.setDescription("Uncle relation - child,parent,gp,uncle");
        uncle.setViewHeaders("child,parent,gp,uncle");

        if (!relationHandler.checkIfRelationExist(parent.getName())) {
            relationHandler.createNewRelation(parent);
        }
        if (!relationHandler.checkIfRelationExist(grandparent.getName())) {
            relationHandler.createNewRelation(grandparent);
        }
        if (!relationHandler.checkIfRelationExist(sibling.getName())) {
            relationHandler.createNewRelation(sibling);
        }
        if (!relationHandler.checkIfRelationExist(uncle.getName())) {
            relationHandler.createNewRelation(uncle);
        }
    }

    private static void generateProjectRelationTemplateFile() {
        RelationHandler relationHandler = new RelationHandler();
        TemplateHandler templateHandler = new TemplateHandler();
        ProjectHandler projectHandler = new ProjectHandler();
        String relations;

        String projectName = UserInput.getUserInput("Please insert the project name", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (projectName.equalsIgnoreCase("exit")) return;
        if (!projectHandler.checkIfProjectExist(projectName)) {
            System.out.println("Project by this name does NOT exist, diverted back to menu");
            return;
        }

        do {
            relations = UserInput.getUserInput("Please insert the relation(s) name(s) [delimiter = ',']", UserInput.RequiredInputType.STRING_TYPE, "exit");
            if (relations.equalsIgnoreCase("exit")) return;
            for (String relationName : Arrays.asList(relations.split(","))) {
                if (!relationHandler.checkIfRelationExist(relationName)) {
                    System.out.println("Relation by this name does not exist: [" + relationName + "], please insert relation list again");
                    relations = "RELATION DOES NOT EXIST";
                    break;
                }
            }
        } while (relations.equalsIgnoreCase("RELATION DOES NOT EXIST"));

        String template;
        do {
            template = UserInput.getUserInput("Please insert the template name", UserInput.RequiredInputType.STRING_TYPE, "exit");
            if (template.equalsIgnoreCase("exit")) return;
            if (!templateHandler.checkIfTemplateExist(template)) {
                System.out.println("Template by this name does NOT exist, please try again");
            }
        } while (!templateHandler.checkIfTemplateExist(template));

        String randomOrSortedOrNative;
        do {
            randomOrSortedOrNative = UserInput.getUserInput("Please choose sorting order: random / sorted / natural", UserInput.RequiredInputType.STRING_TYPE, "exit");
            if (randomOrSortedOrNative.equalsIgnoreCase("exit")) return;
            if (!randomOrSortedOrNative.equalsIgnoreCase("random") &&
                    !randomOrSortedOrNative.equalsIgnoreCase("sorted") &&
                    !randomOrSortedOrNative.equalsIgnoreCase("natural")) {
                System.out.println("Sorting value can be only one of: random / sorted / natural, please try again");
            }

        } while (!randomOrSortedOrNative.equalsIgnoreCase("random") &&
                !randomOrSortedOrNative.equalsIgnoreCase("sorted") &&
                !randomOrSortedOrNative.equalsIgnoreCase("natural"));

        userInput = UserInput.getUserInput("Please select the number of Id bits", UserInput.RequiredInputType.INTEGER_TYPE, "exit", 2, 50);
        if (userInput.equalsIgnoreCase("exit")) return;
        int numberOfIdBits = Integer.parseInt(userInput);

        //run all relations to generate relations views in DB
        relationHandler.runSelectedRelations(relations, projectName);

        //collect all relations to lists + write to file + bits (padding if needed)
        relationHandler.createRelationFile(projectHandler.getProjectByName(projectName), relations, templateHandler.getTemplate(template), getEnumValueFromString(randomOrSortedOrNative), numberOfIdBits);
    }

    private static RequiredOrder getEnumValueFromString(String userInput) {
        if (userInput.toLowerCase().contains("nat")) {
            return RequiredOrder.NATURAL;
        }
        if (userInput.toLowerCase().contains("ran")) {
            return RequiredOrder.RANDOM;
        }
        if (userInput.toLowerCase().contains("sor")) {
            return RequiredOrder.SORTED;
        }
        return null;
    }

    private static void displayTemplateByName() {
        TemplateHandler templateHandler = new TemplateHandler();
        userInput = UserInput.getUserInput("Please insert the Template Name to display", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        if (!templateHandler.checkIfTemplateExist(userInput)) {
            System.out.println("Template by this name does NOT exist, diverted back to menu");
            return;
        }
        System.out.println(templateHandler.getTemplate(userInput));
    }

    private static void deleteTemplate() {
        TemplateHandler templateHandler = new TemplateHandler();
        userInput = UserInput.getUserInput("Please insert the Template Name to delete", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        if (!templateHandler.checkIfTemplateExist(userInput)) {
            System.out.println("Template by this name does NOT exist, diverted back to menu");
            return;
        }
        templateHandler.deleteTemplate(userInput);
    }

    private static void displayAllTemplates() {
        TemplateHandler templateHandler = new TemplateHandler();
        List<Template> templates = templateHandler.getAllTemplates();

        for (Template template : templates) {
            System.out.println(template);
        }
    }

    private static void loadTemplate() {

        TemplateHandler templateHandler = new TemplateHandler();
        ProjectHandler projectHandler = new ProjectHandler();
        Template template = new Template();
        boolean alreadyExist;
        File f, f1, f2, f3, f4, f5;

        String projectName;

        do {
            projectName = UserInput.getUserInput("Please insert the Project Name to load Template from its folder or \"NONE\" to specify full path of excel ", UserInput.RequiredInputType.STRING_TYPE, "exit", "~!@#$%^&*()+|-*/<>,=[]{}:.\\?");
            if (projectName.equalsIgnoreCase("exit")) return;

            if (!projectName.equalsIgnoreCase("none") && !projectHandler.checkIfProjectExist(projectName)) {
                System.out.println("Project by this name does not exist, please select another project name or \"NONE\" ");
            }

        } while (!projectName.equalsIgnoreCase("none") && !projectHandler.checkIfProjectExist(projectName));


        do {
            alreadyExist = false;
            userInput = UserInput.getUserInput("Please insert the Template Name to create", UserInput.RequiredInputType.STRING_TYPE, "exit", "~!@#$%^&*()+|-*/<>,=[]{}:.\\?");
            if (userInput.equalsIgnoreCase("exit")) return;
            if (templateHandler.checkIfTemplateExist(userInput)) {
                System.out.println("Template by this name already exist, please try again");
                alreadyExist = true;
            }
        } while (alreadyExist);

        template.setName(userInput);


        do {
            userInput = UserInput.getUserInput("Please insert the Template Excel file path", UserInput.RequiredInputType.STRING_TYPE, "exit", "#<$+%>!`&*“|{?”=}@");
            if (userInput.equalsIgnoreCase("exit")) return;
            f = new File(userInput);
            f1 = new File(projectName + "/" + userInput);
            f2 = new File(projectName + "/" + userInput + ".xlsx");
            f3 = new File(projectName + "/" + userInput + ".xls");
            f4 = new File(userInput + ".xlsx");
            f5 = new File(userInput + ".xls");
            if (!f.exists() && !f1.exists() && !f2.exists() && !f3.exists() && !f4.exists() && !f5.exists()) {
                System.out.println("File does not exist, please check and try again");
            }
        } while (!f.exists() && !f1.exists() && !f2.exists() && !f3.exists() && !f4.exists() && !f5.exists());

        if (f.exists()) {
            template.setExcelRows(templateHandler.readTemplateFromExcelFile(userInput));
        } else if (f1.exists()) {
            template.setExcelRows(templateHandler.readTemplateFromExcelFile(projectName + "/" + userInput));
        } else if (f2.exists()) {
            template.setExcelRows(templateHandler.readTemplateFromExcelFile(projectName + "/" + userInput + ".xlsx"));
        } else if (f3.exists()) {
            template.setExcelRows(templateHandler.readTemplateFromExcelFile(projectName + "/" + userInput + ".xls"));
        } else if (f4.exists()) {
            template.setExcelRows(templateHandler.readTemplateFromExcelFile(userInput + ".xlsx"));
        } else if (f5.exists()) {
            template.setExcelRows(templateHandler.readTemplateFromExcelFile(userInput + ".xls"));
        }

        if (template.getExcelRows() != null) {
            templateHandler.createNewTemplate(template);
        } else {
            System.out.println("Error loading excel file");
        }
    }


    private static void deleteRelation() {
        RelationHandler relationHandler = new RelationHandler();
        userInput = UserInput.getUserInput("Please insert the Relation Name for deletion", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        if (!relationHandler.checkIfRelationExist(userInput)) {
            System.out.println("Relation by this name does not exist, diverted back to menu");
            return;
        }
        relationHandler.deleteRelation(userInput);
    }

    private static void createNewRelation() {
        Relation relation = new Relation();
        RelationHandler relationHandler = new RelationHandler();

        userInput = UserInput.getUserInput("Please insert the Relation Name", UserInput.RequiredInputType.STRING_TYPE, "exit", "~!@#$%^&*()+|-*/<>,=[]{}:.\\?");
        if (userInput.equalsIgnoreCase("exit")) return;
        if (relationHandler.checkIfRelationExist(userInput)) {
            System.out.println("Relation by this name already exist, diverting back to menu");
            return;
        }
        relation.setName(userInput);

        userInput = UserInput.getUserInput("Please insert the relation file prefix Name", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        relation.setPrefixName(userInput);

        userInput = UserInput.getUserInput("Please insert the relation query [as a single line!]", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        relation.setQuery(userInput);

        userInput = UserInput.getUserInput("Please insert the relation description", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        relation.setDescription(userInput);

        userInput = UserInput.getUserInput("Please insert the relation view result headers", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        relation.setViewHeaders(userInput);

        relationHandler.createNewRelation(relation);
    }

    private static void populateProject() {
        ProjectHandler projectHandler = new ProjectHandler();
        userInput = UserInput.getUserInput("Please insert the Project Name to populate", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;

        if (!projectHandler.checkIfProjectExist(userInput)) {
            System.out.println("Project by this name does NOT exist, diverted back to menu");
            return;
        }

        if (projectHandler.checkIfProjectStatus(userInput, ProjectStatus.POPULATED)) {
            System.out.println("Project by this name already populated, diverted back to menu");
            return;
        }

        if (projectHandler.checkIfProjectStatus(userInput, ProjectStatus.CORRUPTED_DATA)) {
            System.out.println("Project by this name already corrupted data, this might occur if population was interrupted \n" +
                    "diverted back to menu");
            return;
        }

        projectHandler.populateProject(userInput);
    }

    private static void deleteProject() {
        ProjectHandler projectHandler = new ProjectHandler();
        userInput = UserInput.getUserInput("Please insert the Project Name for deletion", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return;
        if (!projectHandler.checkIfProjectExist(userInput)) {
            System.out.println("Project by this name does NOT exist, diverted back to menu");
            return;
        }
        projectHandler.deleteProject(userInput);
    }

    private static void printMenu() {
        System.out.println("\n\t== Data Generation for Learning Inferred Relations System ==");
        System.out.println("\t1>  Create a new project ");
        System.out.println("\t2>  Display all projects ");
        System.out.println("\t3>  Delete a project ");
        System.out.println("\t4>  Populate a project ");
        System.out.println("\t5>  Create New Relation ");
        System.out.println("\t6>  Display all relations ");
        System.out.println("\t7>  Populate a relation ");
        System.out.println("\t8>  Generate relation file ");
        System.out.println("\t9>  Delete relation ");
        System.out.println("\t10> Display Relation by Name");
        System.out.println("\t11> Load Template ");
        System.out.println("\t12> Display all templates ");
        System.out.println("\t13> Delete Template ");
        System.out.println("\t14> Display Template by name ");
        System.out.println("\t15> Generate Project - Relation - Template file ");
        System.out.println("\t16> Create default relations in DB (Should only run once) ");
        System.out.println("\t99> Exit program");
        System.out.println("\t== == == == == == == == == == == == == == == == == == == == ==");
    }

    private static void createNewProject() {
        ProjectHandler projectHandler = new ProjectHandler();
        Project project = new Project("TEMP");

        project.setId(UUID.randomUUID().toString());

        userInput = UserInput.getUserInput("Please insert the Project Name", UserInput.RequiredInputType.STRING_TYPE, "exit", "~!@#$%^&*()+|-*/<>,=[]{}:.\\?");
        if (userInput.equalsIgnoreCase("exit")) return;
        if (projectHandler.checkIfProjectExist(userInput)) {
            System.out.println("Project by this name already exist, please select another project to create");
            return;
        }
        project.setName(userInput);

        userInput = UserInput.getUserInput("Please insert initial number of individuals", UserInput.RequiredInputType.INTEGER_TYPE, "exit", 2, Integer.MAX_VALUE);
        if (userInput.equalsIgnoreCase("exit")) return;
        project.setInitialIndividuals(userInput);

        userInput = UserInput.getUserInput("Please insert the number of generations", UserInput.RequiredInputType.INTEGER_TYPE, "exit", 1, Integer.MAX_VALUE);
        if (userInput.equalsIgnoreCase("exit")) return;
        project.setNumberOfGenerations(Integer.parseInt(userInput));

        userInput = UserInput.getUserInput("Please insert the average child per family", UserInput.RequiredInputType.DOUBLE_TYPE, "exit", 1, Integer.MAX_VALUE);
        if (userInput.equalsIgnoreCase("exit")) return;
        project.setChildAverage(Float.parseFloat(userInput));

        userInput = UserInput.getUserInput("Please insert the normal Child Distribution Deviation", UserInput.RequiredInputType.DOUBLE_TYPE, "exit", 0, Integer.MAX_VALUE);
        if (userInput.equalsIgnoreCase("exit")) return;
        project.setNormalChildDistributionDeviation(Float.parseFloat(userInput));

        do {
            userInput = UserInput.getUserInput("Please insert the Max Member number", UserInput.RequiredInputType.INTEGER_TYPE, "exit");
            if (userInput.equalsIgnoreCase("exit")) return;
            project.setMaxMemberNumber(Integer.parseInt(userInput));
            if (project.getInitialIndividualsNumber() > project.getMaxMemberNumber()) {
                System.out.println("Max members number is smaller than the initial individual number");
                System.out.println("Max members number=[" + project.getMaxMemberNumber() + "], Initial individual number=[" + project.getInitialIndividualsNumber() + "]");
            }
        } while (project.getInitialIndividualsNumber() > project.getMaxMemberNumber());

        do {
            userInput = UserInput.getUserInput("Please insert Individual Id Length [Database id value only]\nThe minimal id length required for the MAX members value = [" + (log(project.getMaxMemberNumber(), 2) + 1) + "]"
                    , UserInput.RequiredInputType.INTEGER_TYPE, "exit");
            if (userInput.equalsIgnoreCase("exit")) return;
            project.setIndividualIdLength(Integer.parseInt(userInput));
            if (project.getMaxMemberNumber() > (Math.pow(2, project.getIndividualIdLength()) - 1)) {
                System.out.println("Max Members requires longer id length");
                System.out.println("Max Members= [" + project.getMaxMemberNumber() + "] id length(2^IndividualIdLength-1) enables only [" + (Math.pow(2, project.getIndividualIdLength()) - 1) + "] members in binary");
            }
        } while (project.getMaxMemberNumber() > (Math.pow(2, project.getIndividualIdLength()) - 1));

        project.setProjectStatus(ProjectStatus.CREATED);

        try {
            if (projectHandler.createNewProject(project).equalsIgnoreCase(project.getName())) {
                System.out.println("The project was created, The next step should be to \"POPULATE\" a project");
            }

            File directory = new File(project.getName());
            if (!directory.exists()) {
                boolean result = directory.mkdir();
                if (!result) System.out.println("Error creating project folder");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was an error creating the project, please try again or contact administrator");
        }


    }

    private static int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    private static void displayAllProjects() {
        ProjectHandler projectHandler = new ProjectHandler();
        List<Project> projects = projectHandler.getAllProjects();

        if (projects.isEmpty()) {
            System.out.println("***** No projects found *****\n\n\n");
        }
        for (Project project : projects) {
            System.out.println(project.toStringConsoleDisplay());
            System.out.println("Total Individuals in project [" + project.getName() + "]:[" + projectHandler.getTableCount(project.getName(), projectHandler.INDIVIDUALS_TABLE_NAME) + "]");
        }
    }

    private static void displayAllRelations() {
        RelationHandler relationHandler = new RelationHandler();
        List<Relation> relations = relationHandler.getAllRelations();

        if (relations.isEmpty()) {
            System.out.println("***** No relations found *****\n\n\n");
        }
        for (Relation relation : relations) {
            System.out.println(relation.toStringConsoleDisplay());
        }
    }

    private static void runRelation() {
        RelationHandler relationHandler = new RelationHandler();

        Project project = getProject();
        Relation relation = null;

        if (project != null) {
            relation = getRelation();
        }


        if (project != null && relation != null) {
            System.out.println("\nRunning relation: [ " + relation.getName() + "] on Project [" + project.getName() + "].");
            relationHandler.writeRelationInProjectDatabase(relation, project);
            System.out.println("Relation was created, number of relation records: [" +
                    relationHandler.getTableCount(project.getName(), relation.getName()) + "]");
            System.out.println("The relation data was stored in the project database, you can now generate a relation file");
        }
    }

    private static void createRelationFile() {
        RelationHandler relationHandler = new RelationHandler();

        Project project = getProject();
        Relation relation = null;
        if (project != null) {
            relation = getRelation();
        }

        if (project != null && relation != null) {
            relationHandler.runSelectedRelations(relation.getName(), project.getName());
            System.out.println("\nCreating relation file from relation: [ " + relation.getName() + "] in Project [" + project.getName() + "].");
            relationHandler.createRelationFile(relation, project);
        }
    }

    private static Project getProject() {
        ProjectHandler projectHandler = new ProjectHandler();
        Project project;
        do {
            userInput = UserInput.getUserInput("Please insert the Project Name", UserInput.RequiredInputType.STRING_TYPE, "exit");
            if (userInput.equalsIgnoreCase("exit")) return null;
            if (!projectHandler.checkIfProjectExist(userInput)) {
                System.out.println("Project by this name does NOT exist, please try again");
            }
        } while (!projectHandler.checkIfProjectExist(userInput));

        project = projectHandler.getProjectByName(userInput);

        return project;
    }

    private static Relation getRelation() {
        RelationHandler relationHandler = new RelationHandler();
        Relation relation;

        userInput = UserInput.getUserInput("Please insert the Relation Name", UserInput.RequiredInputType.STRING_TYPE, "exit");
        if (userInput.equalsIgnoreCase("exit")) return null;
        if (!relationHandler.checkIfRelationExist(userInput)) {
            System.out.println("Relation by this name does not exist, diverting back to menu");
            return null;
        }

        relation = relationHandler.getRelationByName(userInput);

        return relation;
    }

}
