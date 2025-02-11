package dev.ktroude.ft_hangout.utils;

/**
 * Utility class providing helper functions for formatting and manipulating data.
 * This class contains general-purpose methods that are used throughout the application
 * to simplify repetitive tasks.
 */
public class Utils {

    /**
     * Formats a phone number by inserting spaces every 3 characters.
     * <p>
     * This method is useful for displaying phone numbers in a more readable format,
     * ensuring better UX when viewing contact details.
     * </p>
     *
     * @param number The raw phone number as a {@link String}.
     * @return A formatted phone number with spaces every three digits.
     */
    public static String parseNumberToDisplay(String number) {
        StringBuilder sb = new StringBuilder(number);

        for (int i = 0; i < sb.length(); i += 3) {
            sb.insert(i, " ");
        }
        return sb.toString();
    }
}
