package com.ziro.espresso.formatters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class NameCaseConverterTest {

    @Test
    void canConvertStringsToProperCase() {
        // Single character out of proper case
        assertThat(NameCaseConverter.toNameCase("jEan")).isEqualTo("Jean");
        assertThat(NameCaseConverter.toNameCase("tRemblay")).isEqualTo("Tremblay");

        // Multiple Characters out of proper case
        assertThat(NameCaseConverter.toNameCase("BENoît")).isEqualTo("Benoît");
        assertThat(NameCaseConverter.toNameCase("dESNOYERS")).isEqualTo("Desnoyers");

        // Irish types
        assertThat(NameCaseConverter.toNameCase("Macdonald")).isEqualTo("MacDonald");

        // Hyphenated names
        assertThat(NameCaseConverter.toNameCase("marie-josée LEBLANC")).isEqualTo("Marie-Josée Leblanc");

        // Spanish conjunctions (y, e, i)
        assertThat(NameCaseConverter.toNameCase("JUAN Y MARIA")).isEqualTo("Juan y Maria");

        // Particles at the start (e.g., "van", "von", "de")
        assertThat(NameCaseConverter.toNameCase("van der sar")).isEqualTo("van der Sar");

        // Lowercase particles (e.g., "della", "de la")
        assertThat(NameCaseConverter.toNameCase("GABRIELLA DELLA VALLE")).isEqualTo("Gabriella della Valle");

        assertThat(NameCaseConverter.toNameCase("GABRIELLA DE LA VALLE")).isEqualTo("Gabriella de la Valle");

        // Apostrophe in name
        assertThat(NameCaseConverter.toNameCase("D'ARTAGNAN")).isEqualTo("D'Artagnan");

        // Roman numerals
        assertThat(NameCaseConverter.toNameCase("LOUIS XVI")).isEqualTo("Louis XVI");

        // Single-letter initials
        assertThat(NameCaseConverter.toNameCase("J. R. R. TOLKIEN")).isEqualTo("J. R. R. Tolkien");

        // Non-Latin characters (Unicode letters)
        assertThat(NameCaseConverter.toNameCase("BJÖRK GUÐMUNDSDÓTTIR")).isEqualTo("Björk Guðmundsdóttir");

        // Mac/Mc exceptions (e.g., "Macias", "Macquarie")
        assertThat(NameCaseConverter.toNameCase("MACIAS")).isEqualTo("Macias");

        assertThat(NameCaseConverter.toNameCase("MACQUARIE")).isEqualTo("Macquarie");

        // Mixed case input
        assertThat(NameCaseConverter.toNameCase("mARY aNN sMITH")).isEqualTo("Mary Ann Smith");

        // All-caps input
        assertThat(NameCaseConverter.toNameCase("JOHN DOE")).isEqualTo("John Doe");

        // Names with "St." (Saint)
        assertThat(NameCaseConverter.toNameCase("st. clair")).isEqualTo("St. Clair");
    }
}
