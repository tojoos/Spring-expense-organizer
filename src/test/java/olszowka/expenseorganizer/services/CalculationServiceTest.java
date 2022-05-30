package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.model.Position;
import olszowka.expenseorganizer.model.Timeframe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculationServiceTest {

    CalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationService();
    }

    @Test
    void getPeriodicPositionsDay() {
        //given
        List<Position> positions = new ArrayList<>();
        positions.add(new Income("income1", "100", "Other", LocalDate.now()));
        positions.add(new Income("income2", "200", "Other", LocalDate.now()));
        positions.add(new Income("income3", "1250", "Other", LocalDate.now().minusDays(1)));
        positions.add(new Outcome("outcome1", "75", "Other", LocalDate.now().minusDays(2)));
        positions.add(new Outcome("outcome2", "175", "Other", LocalDate.now().minusDays(6)));

        Timeframe timeframe = Timeframe.DAY;

        //when
        List<Position> positionReturned = calculationService.getPeriodicPositions(positions, timeframe);

        //then
        assertEquals(positions.size()-2, positionReturned.size());
    }

    @Test
    void getPeriodicPositionsMonth() {
        //given
        List<Position> positions = new ArrayList<>();
        positions.add(new Outcome("outcome1", "100", "Other", LocalDate.now()));
        positions.add(new Income("income2", "100", "Other", LocalDate.now().minusWeeks(2)));
        positions.add(new Outcome("outcome2", "100", "Other", LocalDate.now().minusWeeks(3)));
        positions.add(new Income("income4", "100", "Other", LocalDate.now().minusMonths(1)));
        positions.add(new Income("income5", "500", "Other", LocalDate.now().minusMonths(2)));

        Timeframe timeframe = Timeframe.MONTH;

        //when
        List<Position> positionReturned = calculationService.getPeriodicPositions(positions, timeframe);

        //then
        assertEquals(positions.size()-1, positionReturned.size());
    }

    @Test
    void getPeriodicPositionsAll() {
        //given
        List<Position> positions = new ArrayList<>();
        positions.add(new Income("income1", "100", "Other", LocalDate.now()));
        positions.add(new Income("income2", "100", "Other", LocalDate.now().minusMonths(7)));
        positions.add(new Outcome("income3", "100", "Other", LocalDate.now().minusYears(2)));
        positions.add(new Outcome("income4", "100", "Other", LocalDate.now().minusYears(10)));

        Timeframe timeframe = Timeframe.ALL;

        //when
        List<Position> positionReturned = calculationService.getPeriodicPositions(positions, timeframe);

        //then
        assertEquals(positions.size(), positionReturned.size());
    }

    @Test
    void getPeriodicPositionsRangeDay() {
        //given
        List<Position> positions = new ArrayList<>();
        positions.add(new Outcome("outcome1", "100", "Other", LocalDate.now()));
        positions.add(new Income("income2", "100", "Other", LocalDate.now()));
        positions.add(new Outcome("outcome2", "100", "Other", LocalDate.now().minusDays(1)));
        positions.add(new Outcome("outcome3", "200", "Other", LocalDate.now().minusDays(2)));
        positions.add(new Outcome("outcome4", "2100", "Other", LocalDate.now().minusDays(2)));
        positions.add(new Income("income4", "100", "Other", LocalDate.now().minusDays(3)));
        positions.add(new Income("income5", "500", "Other", LocalDate.now().minusDays(4)));

        Timeframe timeframe = Timeframe.DAY;

        //when
        List<Position> positionReturned = calculationService.getPeriodicPositionsRange(positions, timeframe);

        //then
        //2 - only positions within range <The day before yesterday, Yesterday) are taken
        assertEquals(2, positionReturned.size());
    }

    @Test
    void getPeriodicPositionsRangeWeek() {
        //given
        List<Position> positions = new ArrayList<>();
        positions.add(new Outcome("outcome1", "100", "Other", LocalDate.now()));
        positions.add(new Income("income2", "100", "Other", LocalDate.now().minusDays(2)));
        positions.add(new Income("income3", "1100", "Other", LocalDate.now().minusDays(6)));
        positions.add(new Outcome("outcome2", "100", "Other", LocalDate.now().minusWeeks(1)));
        positions.add(new Income("income4", "100", "Other", LocalDate.now().minusWeeks(1).minusDays(2)));
        positions.add(new Income("income5", "300", "Other", LocalDate.now().minusWeeks(1).minusDays(3)));
        positions.add(new Income("income6", "400", "Other", LocalDate.now().minusWeeks(1).minusDays(4)));
        positions.add(new Income("income7", "500", "Other", LocalDate.now().minusWeeks(2)));

        Timeframe timeframe = Timeframe.WEEK;

        //when
        List<Position> positionReturned = calculationService.getPeriodicPositionsRange(positions, timeframe);

        //then
        //2 - only positions within range <The week before last week, Last week) are taken
        assertEquals(4, positionReturned.size());
    }

    @Test
    void returnTimeframeStringWeek() {
        //given
        Timeframe timeframe = Timeframe.WEEK;

        //when
        String stringTimeframe = calculationService.returnTimeframeString(timeframe);

        //then
        String expectedString = LocalDate.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                + " - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertEquals(expectedString, stringTimeframe);
    }

    @Test
    void returnTimeframeStringAll() {
        //given
        Timeframe timeframe = Timeframe.ALL;

        //when
        String stringTimeframe = calculationService.returnTimeframeString(timeframe);

        //then
        String expectedString = "Full history";
        assertEquals(expectedString, stringTimeframe);
    }
}