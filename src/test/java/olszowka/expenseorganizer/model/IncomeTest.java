package olszowka.expenseorganizer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IncomeTest {

    Income income;

    String category = "Salary";
    LocalDate someDate = LocalDate.of(1999, 12, 12);

    @BeforeEach
    void setUp() {
        income = new Income("name", "100.00", category, someDate);
    }

    @Test
    void testGettersAndSetters() {
        String newName = "some name";
        String newValue = "some value";
        LocalDate newDate = LocalDate.now();

        assertEquals(category, income.getCategory());
        assertEquals(someDate, income.getDate());
        income.setValue(newValue);
        income.setName(newName);
        income.setDate(newDate);
        assertEquals(newDate, income.getDate());
        assertEquals(newName, income.getName());
        assertEquals(newValue, income.getValue());
    }
}