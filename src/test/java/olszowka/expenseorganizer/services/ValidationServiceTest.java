package olszowka.expenseorganizer.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    ValidationService validationService;

    @BeforeEach
    public void setUp() {
        validationService = new ValidationService();
    }

    @Test
    void isValueValid_ValidDot() {
        //given
        String someValue1 = "199.99";
        String someValue2 = "300.0";
        String someValue3 = "1000";

        //when
        boolean isValid1 = validationService.isValueValid(someValue1);
        boolean isValid2 = validationService.isValueValid(someValue2);
        boolean isValid3 = validationService.isValueValid(someValue3);

        //then
        assertTrue(isValid1);
        assertTrue(isValid2);
        assertTrue(isValid3);
    }

    @Test
    void isValueValid_ValidComma() {
        //given
        String someValue1 = "199,99";
        String someValue2 = "300,0";

        //when
        boolean isValid1 = validationService.isValueValid(someValue1);
        boolean isValid2 = validationService.isValueValid(someValue2);

        //then
        assertTrue(isValid1);
        assertTrue(isValid2);
    }

    @Test
    void isValueValid_Invalid() {
        //given
        String someValue1 = "199!99";
        String someValue2 = "abcd";

        //when
        boolean isValid1 = validationService.isValueValid(someValue1);
        boolean isValid2 = validationService.isValueValid(someValue2);

        //then
        assertFalse(isValid1);
        assertFalse(isValid2);
    }

    @Test
    void isNameValid_Valid() {
        //given
        String name = "someName";

        //when
        boolean isValid = validationService.isNameValid(name);

        //then
        assertTrue(isValid);
    }

    @Test
    void isNameValid_Empty() {
        //given
        String name = "";

        //when
        boolean isValid = validationService.isNameValid(name);

        //then
        assertFalse(isValid);
    }

    @Test
    void returnFormattedValue() {
        //given
        String unformatted1 = "100";
        String unformatted2 = "100,23";
        String unformatted3 = "100.2343";

        String expectedFormat1 = "100.00";
        String expectedFormat2 = "100.23";

        //when
        String formatted1 = validationService.returnFormattedValue(unformatted1);
        String formatted2 = validationService.returnFormattedValue(unformatted2);
        String formatted3 = validationService.returnFormattedValue(unformatted3);

        //then
        assertEquals(expectedFormat1, formatted1);
        assertEquals(expectedFormat2, formatted2);
        assertEquals(expectedFormat2, formatted3);
    }
}