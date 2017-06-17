package businessLogic;

/**
 * Created by Levinger on 29/12/2016.
 * The required sorting order for the generated relation to file
 */
public enum RequiredOrder {
    RANDOM, //scramble all records
    SORTED, // sort all records
    NATURAL //Do nothing - Get the records as it was retried from the database

}
