package commons;

/**
 * Author YoavL on 27/11/2016.
 */
public class IdGenerator {

    public enum TrimLocation {MSB, LSB}

    private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Generate a random uuid of the specified length, and radix. Examples:
     * <ul>
     * <li>uuid(8, 2) returns "01001010" (8 character ID, base=2)
     * <li>uuid(8, 10) returns "47473046" (8 character ID, base=10)
     * <li>uuid(8, 16) returns "098F4D35" (8 character ID, base=16)
     * </ul>
     *
     * @param len   the desired number of characters
     * @param radix the number of allowable values for each character (must be <= 62)
     */
    public static String uuid(int len, int radix) {
        if (radix > CHARS.length) {
            throw new IllegalArgumentException();
        }
        char[] uuid = new char[len];
        // Compact form
        for (int i = 0; i < len; i++) {
            uuid[i] = CHARS[(int) (Math.random() * radix)];
        }
        return new String(uuid);
    }

    @Deprecated
    public static String getNextBinary(String binaryNumber, int length) {
        String nextNumber = Integer.toBinaryString(Integer.valueOf(binaryNumber, 2) + 1);
        //padding
        nextNumber = padding(nextNumber, length);
        return nextNumber;
    }


    /**
     * Trim or pas an Id composed of a Binary string
     * @param id - The id String
     * @param length - The required length
     * @param trimLocation - MSB / LSB
     * @return - The trimmed or padded string
     */
    public static String idTrimmerPadder(String id, int length, TrimLocation trimLocation) {

        if (id==null) return null;

        if (id.length() > length) {
            switch (trimLocation) {
                case MSB:
                    id = id.substring(id.length() - length, id.length());
                    break;
                case LSB:
                    id = id.substring(0, id.length() - length);
                    break;
            }
        } else {
            id = padding(id, length);
        }
        return id;
    }


    private static String padding(String id, int length) {
        while (id.length() < length) {
            id = "0" + id;
        }
        return id;
    }


}
