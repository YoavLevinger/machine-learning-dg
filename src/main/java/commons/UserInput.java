/**
 *
 */
package commons;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This Class handles the users input
 *
 * @author Yoav
 */
public class UserInput {

    /**
     * This enum holds the input types.
     *
     * @author Yoav
     */
    public enum RequiredInputType {
        STRING_TYPE, INTEGER_TYPE, DOUBLE_TYPE
    }

    /**
     * General purpose user Input method (in this application)
     *
     * @param messageForUser - Display message for user on which input he should supply
     * @param requiredType   - verify the user input for a string, integer or double.
     * @return user input string
     */
    public static String getUserInput(String messageForUser, RequiredInputType requiredType, String exitString) {
        String message;
        boolean correctInput = false;

        do {
            message = null;
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            try {
                System.out.print(messageForUser + "\n");
                System.out.flush(); // empties buffer, before you input text
                message = consoleInput.readLine();
                if (message.equalsIgnoreCase(exitString.toLowerCase())) return message.trim();
                if (!checkInputForRequestedType(message, requiredType)
                        || message.trim().isEmpty()) {
                    System.out.println("Incorrect required input for input of type " + requiredType.toString());
                    System.out.println("Please try again according to instruction:");
                } else {
                    correctInput = true;
                }
            } catch (Exception e) {
                System.out.println("Error occurred on user input, error details:" + e.toString());
            }

        } while (!correctInput);
        return message.trim();
    }

    public static String getUserInput(String messageForUser, RequiredInputType requiredType, String exitString, Integer min, Integer max) {

        String message;
        boolean inRange;
        do {
            inRange = true;
            message = getUserInput(messageForUser, requiredType, exitString);
            if (message.equalsIgnoreCase("exit")) return message.trim();
            switch (requiredType) {
                case INTEGER_TYPE:
                    if (Integer.parseInt(message) < min || Integer.parseInt(message) > max) {
                        inRange = false;
                        System.out.println("Required input not in range [" + min + "] - [" + max + "]");
                    }
                    break;
                case DOUBLE_TYPE:
                    if (Double.parseDouble(message) < min || Double.parseDouble(message) > max) {
                        inRange = false;
                        System.out.println("Required input not in range [" + min + "] - [" + max + "]");
                    }
                    break;
            }

        } while (!inRange);
        return message.trim();
    }

    public static String getUserInput(String messageForUser, RequiredInputType requiredType, String exitString, String specialChars) {
        String message;
        boolean containsSpecialChars;
        do {
            containsSpecialChars = false;
            message = getUserInput(messageForUser, requiredType, exitString);
            for (char c : specialChars.toCharArray()) {
                if (message.indexOf(c) != -1) {
                    containsSpecialChars = true;
                    System.out.println("The following characters should not be used: [" + specialChars + "]");
                    break;
                }
            }

        } while (containsSpecialChars);
        return message.trim();
    }

    /**
     * Verify the user input string VS the required type.
     *
     * @param message      - the user input to check
     * @param requiredType - required type
     * @return true on correct input type and grade range
     */
    private static boolean checkInputForRequestedType(String message, RequiredInputType requiredType) {

        if (message == null) {
            return false;
        }

        switch (requiredType) {

            case STRING_TYPE:
                // Does nothing - everything goes ?
                break;

            case INTEGER_TYPE:// verify if the string is numeric or not returns false
                // if not numeric
                for (int i = 0; i < message.length(); i++) {
                    if (!Character.isDigit(message.charAt(i))
                            && message.charAt(i) != '-') {
                        return false;
                    }
                }
                try {
                    Integer.parseInt(message);
                } catch (Exception e) {
                    System.out.println("User input is incorrect for required Integer Type");
                    System.out.println("Required value must be within: [" + Integer.MIN_VALUE + "] - [" + Integer.MAX_VALUE + "]");
                    return false;
                }

                break;

            case DOUBLE_TYPE:// verify if the string is numeric or not returns false
                // if not numeric
                for (int i = 0; i < message.length(); i++) {
                    if (!Character.isDigit(message.charAt(i))
                            && message.charAt(i) != '.'
                            && message.charAt(i) != '-') {
                        return false;
                    }
                }
                try {
                    Double.parseDouble(message);
                } catch (Exception e) {
                    System.out.println("User input is incorrect for required Double Type");
                    System.out.println("Required value must be within: [" + Double.MIN_VALUE + "] - [" + Double.MAX_VALUE + "]");
                    return false;
                }

                break;
        }

        return true;
    }

}
