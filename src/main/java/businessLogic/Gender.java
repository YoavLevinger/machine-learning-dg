package businessLogic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by user on 21/11/2016.
 */
public enum Gender {
    FEMALE, MALE;

    public static Gender getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
