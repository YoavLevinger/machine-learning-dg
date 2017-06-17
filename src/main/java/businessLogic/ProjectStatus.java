package businessLogic;

/**
 * Created by user on 30/10/2016.
 */
public enum ProjectStatus {
    CREATED, //The project configuration created and the basic tables
    POPULATED, //The project initial generation and all decedents created and added to the database
    CORRUPTED_DATA //The project data is corrupted - populated started but not finished
}
