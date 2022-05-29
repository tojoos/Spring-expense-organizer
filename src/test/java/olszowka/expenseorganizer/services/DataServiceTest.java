package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class DataServiceTest {

    DataService dataService;

    @Mock
    JSONParserService jsonParserService;

    File positionFile;
    Path path;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        dataService = new DataService(jsonParserService);

        try {
            path = tempDir.resolve("positionFile.json");
        } catch (InvalidPathException invalidPathException) {
            System.err.println("Error creating temp test file " + this.getClass().getSimpleName());
        }

        positionFile = path.toFile();
    }

    @Test
    void saveIncomes() {
        //given
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income("Refund1", "100.50", "Cashback", LocalDate.now()));
        incomes.add(new Income("Refund2", "200.50", "Cashback", LocalDate.now()));

        //when
        dataService.saveIncomes(incomes);

        verify(jsonParserService,times(1)).saveJsonIncomes(anyList(), any());
    }

    @Test
    void saveOutcomes() {
        //given
        List<Outcome> outcomes = new ArrayList<>();
        outcomes.add(new Outcome("Dinner1", "70.99", "Food", LocalDate.now()));
        outcomes.add(new Outcome("Dinner2", "100.99", "Food", LocalDate.now()));

        //when
        dataService.saveOutcomes(outcomes);

        verify(jsonParserService,times(1)).saveJsonOutcomes(anyList(), any());
    }

    @Test
    void getIncomes() throws IOException, ParseException {
        //given
        List<Income> incomes = new ArrayList<>();
        incomes.add(new Income("Refund1", "100.50", "Cashback", LocalDate.now()));
        incomes.add(new Income("Refund2", "200.50", "Cashback", LocalDate.now()));

        //when
        when(jsonParserService.readJsonIncomes(any())).thenReturn(incomes);
        List<Income> returnedIncomes = dataService.getIncomes();

        verify(jsonParserService,times(1)).readJsonIncomes(any());
        assertEquals(incomes.size(), returnedIncomes.size());
    }

    @Test
    void getOutcomes() throws IOException, ParseException {
        //given
        List<Outcome> outcomes = new ArrayList<>();
        outcomes.add(new Outcome("Dinner1", "70.99", "Food", LocalDate.now()));
        outcomes.add(new Outcome("Dinner2", "100.99", "Food", LocalDate.now()));

        //when
        when(jsonParserService.readJsonOutcomes(any())).thenReturn(outcomes);
        List<Outcome> returnedOutcomes = dataService.getOutcomes();

        verify(jsonParserService,times(1)).readJsonOutcomes(any());
        assertEquals(outcomes.size(), returnedOutcomes.size());
    }
}