package com.ziro.espresso.formatters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.base.Strings;

/**
 * A utility class for converting strings to Name Case/Proper Case.
 * Based on the Ruby NameCase gem: https://github.com/tenderlove/namecase
 */
public final class NameCaseConverter {
    private static final String TENS = "(?:[Xx]{1,3}|[Xx][Ll]|[Ll][Xx]{0,3})?";
    private static final String ONES = "(?:[Ii]{1,3}|[Ii][VvXx]|[Vv][Ii]{0,3})?";

    // Pre-compile patterns for performance
    private static final Pattern APOSTROPHE_S_END_PATTERN = Pattern.compile("'(\\w)\\b");
    private static final Pattern MAC_MC_CHECK_PATTERN = Pattern.compile("\\bMac[A-Za-z]{2,}[^aciozj]\\b|\\bMc");
    private static final Pattern MAC_MC_REPLACE_PATTERN = Pattern.compile("\\b(Ma?c)([A-Za-z]+)");
    private static final Pattern ROMAN_NUMERAL_PATTERN = Pattern.compile("\\b(" + TENS + ONES + ")\\b");

    // Patterns for "son/daughter of" rules (case-insensitive matching needed after initial lowercasing/casing)
    private static final Pattern AL_PATTERN = Pattern.compile("\\bAl(?=\\s+\\w)");
    private static final Pattern BIN_BINTI_BINTE_PATTERN = Pattern.compile("\\b(Bin|Binti|Binte)\\b");
    private static final Pattern AP_PATTERN = Pattern.compile("\\bAp\\b");
    private static final Pattern BEN_PATTERN = Pattern.compile("\\bBen(?=\\s+\\w)");
    private static final Pattern DELLA_DELLE_PATTERN = Pattern.compile("\\bDell([ae])\\b");
    private static final Pattern D_VOWEL_PATTERN = Pattern.compile("\\bD([aeiou])\\b");
    private static final Pattern D_AS_OS_PATTERN = Pattern.compile("\\bD([ao]s)\\b");
    private static final Pattern DE_LR_PATTERN = Pattern.compile("\\bDe([lr])\\b");
    private static final Pattern EL_PATTERN = Pattern.compile("\\bEl\\b");
    private static final Pattern LA_PATTERN = Pattern.compile("\\bLa\\b");
    private static final Pattern L_EO_PATTERN = Pattern.compile("\\bL([eo])\\b");
    private static final Pattern VAN_PATTERN = Pattern.compile("\\bVan(?=\\s+\\w)");
    private static final Pattern VON_PATTERN = Pattern.compile("\\bVon\\b");

    private NameCaseConverter() {}

    public static String toNameCase(String input) {
        if (Strings.isNullOrEmpty(input)) {
            return input;
        }

        // Initial capitalization
        String[] words = input.split("\\b");
        StringBuilder workingBuilder = new StringBuilder();
        for (String word : words) {
            if (word.matches("\\p{L}+")) {
                workingBuilder.append(capitalizeFirst(word));
            } else {
                workingBuilder.append(word);
            }
        }
        String workingString = workingBuilder.toString();

        // Apply specific formatting rules
        workingString = handleApostropheS(workingString);
        workingString = handleIrishNames(workingString);
        workingString = handleSonOfParticles(workingString);
        workingString = handleRomanNumerals(workingString);
        workingString = handleSpanishConjunctions(workingString);

        return workingString;
    }

    private static String capitalizeFirst(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    private static String handleApostropheS(String input) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = APOSTROPHE_S_END_PATTERN.matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement("'" + matcher.group(1).toLowerCase()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String handleIrishNames(String input) {
        String workingString = input;
        if (MAC_MC_CHECK_PATTERN.matcher(workingString).find()) {
            StringBuilder sbIrish = new StringBuilder();
            Matcher irishMatcher = MAC_MC_REPLACE_PATTERN.matcher(workingString);
            while (irishMatcher.find()) {
                String prefix = irishMatcher.group(1);
                String namePart = irishMatcher.group(2);
                irishMatcher.appendReplacement(sbIrish, Matcher.quoteReplacement(prefix + capitalizeFirst(namePart)));
            }
            irishMatcher.appendTail(sbIrish);
            workingString = sbIrish.toString();

            // Apply specific Mac exceptions
            workingString = workingString.replace("MacEdo", "Macedo");
            workingString = workingString.replace("MacEvicius", "Macevicius");
            workingString = workingString.replace("MacHado", "Machado");
            workingString = workingString.replace("MacHar", "Machar");
            workingString = workingString.replace("MacHin", "Machin");
            workingString = workingString.replace("MacHlin", "Machlin");
            workingString = workingString.replace("MacIas", "Macias");
            workingString = workingString.replace("MacIulis", "Maciulis");
            workingString = workingString.replace("MacKie", "Mackie");
            workingString = workingString.replace("MacKle", "Mackle");
            workingString = workingString.replace("MacKlin", "Macklin");
            workingString = workingString.replace("MacKmin", "Mackmin");
            workingString = workingString.replace("MacQuarie", "Macquarie");
            workingString = workingString.replace("Macmurdo", "MacMurdo");
        }
        return workingString;
    }

    private static String handleSonOfParticles(String input) {
        String workingString = input;
        workingString = AL_PATTERN.matcher(workingString).replaceAll("al");
        workingString = BIN_BINTI_BINTE_PATTERN.matcher(workingString).replaceAll(mr -> mr.group(1).toLowerCase());
        workingString = AP_PATTERN.matcher(workingString).replaceAll("ap");
        workingString = BEN_PATTERN.matcher(workingString).replaceAll("ben");
        workingString = DELLA_DELLE_PATTERN.matcher(workingString).replaceAll("dell$1");
        workingString = D_VOWEL_PATTERN.matcher(workingString).replaceAll("d$1");
        workingString = D_AS_OS_PATTERN.matcher(workingString).replaceAll("d$1");
        workingString = DE_LR_PATTERN.matcher(workingString).replaceAll("de$1");
        workingString = EL_PATTERN.matcher(workingString).replaceAll("el");
        workingString = LA_PATTERN.matcher(workingString).replaceAll("la");
        workingString = L_EO_PATTERN.matcher(workingString).replaceAll("l$1");
        workingString = VAN_PATTERN.matcher(workingString).replaceAll("van");
        workingString = VON_PATTERN.matcher(workingString).replaceAll("von");
        return workingString;
    }

    private static String handleRomanNumerals(String input) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = ROMAN_NUMERAL_PATTERN.matcher(input);
        while (matcher.find()) {
            String match = matcher.group(1);
            if (!Strings.isNullOrEmpty(match)) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(match.toUpperCase()));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String handleSpanishConjunctions(String input) {
        String workingString = input;
        for (String conj : new String[] {"Y", "E", "I"}) {
            Pattern conjPattern = Pattern.compile("\\b" + conj + "\\b", Pattern.CASE_INSENSITIVE);
            workingString = conjPattern.matcher(workingString).replaceAll(conj.toLowerCase());
        }
        return workingString;
    }
}