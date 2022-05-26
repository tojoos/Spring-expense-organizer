package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Outcome;
import org.apache.commons.io.FileUtils;
import olszowka.expenseorganizer.model.Income;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class JSONParserServiceTest {

    JSONParserService jsonParserService;

    File positionFile;
    Path path;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        jsonParserService = new JSONParserService();

        try {
            path = tempDir.resolve("positionFile.json");
        } catch (InvalidPathException invalidPathException) {
            System.err.println("Error creating temp test file " + this.getClass().getSimpleName());
        }

        positionFile = path.toFile();
    }

    @Test
    void saveJsonIncomes() throws IOException {
        //given
        Income income1 = new Income("name1", "100.00", "Salary" , LocalDate.of(2002,2,2));
        Income income2 = new Income("name2", "200.00", "Salary" , LocalDate.of(2001,1,1));
        Income income3 = new Income("name3", "300.00", "Salary" , LocalDate.of(2003,3,3));

        List<Income> incomeList = new ArrayList<>();
        incomeList.add(income1);
        incomeList.add(income2);
        incomeList.add(income3);

        //when
        jsonParserService.saveJsonIncomes(incomeList, positionFile);

        //then
        String savedDate = FileUtils.readFileToString(positionFile, "UTF-8");
        assertTrue(savedDate.contains("date"));
        assertEquals(incomeList.size(), savedDate.split("date").length - 1);
    }

    @Test
    void saveJsonOutcomes() throws IOException {
        //given
        Outcome outcome1 = new Outcome("name1", "100.00", "Food" , LocalDate.of(2000,2,2));
        Outcome outcome2 = new Outcome("name2", "200.00", "Fitness" , LocalDate.of(2000,1,1));
        Outcome outcome3 = new Outcome("name3", "300.00", "Fitness" , LocalDate.of(2000,3,3));

        List<Outcome> outcomeList = new ArrayList<>();
        outcomeList.add(outcome1);
        outcomeList.add(outcome2);
        outcomeList.add(outcome3);

        //when
        jsonParserService.saveJsonOutcomes(outcomeList, positionFile);

        //then
        String savedDate = FileUtils.readFileToString(positionFile, "UTF-8");
        assertTrue(savedDate.contains("date"));
        assertEquals(outcomeList.size(), savedDate.split("date").length - 1);
    }

    @Test
    void readJsonOutcomes() throws IOException, ParseException {
        //given
        String category1 = "Food";
        String name3 = "name3";
        Outcome outcome1 = new Outcome("name1", "100.00", category1 , LocalDate.of(2000,2,2));
        Outcome outcome2 = new Outcome("name2", "200.00", "Fitness" , LocalDate.of(2000,1,1));
        Outcome outcome3 = new Outcome(name3, "300.00", "Fitness" , LocalDate.of(2000,3,3));

        List<Outcome> outcomeList = new ArrayList<>();
        outcomeList.add(outcome1);
        outcomeList.add(outcome2);
        outcomeList.add(outcome3);

        jsonParserService.saveJsonOutcomes(outcomeList, positionFile);

        //when
        List<Outcome> readOutcomes = jsonParserService.readJsonOutcomes(positionFile);

        //then
        assertEquals(readOutcomes.size(), outcomeList.size());
        assertEquals(category1, readOutcomes.get(0).getCategory());
        assertEquals(name3, readOutcomes.get(2).getName());
    }

    @Test
    void readJsonIncomes() throws IOException, ParseException {
        //given
        String category2 = "Food";
        String name3 = "name3";
        Income income1 = new Income("name1", "100.00", "Food" , LocalDate.of(2000,2,2));
        Income income2 = new Income("name2", "200.00", category2 , LocalDate.of(2000,1,1));
        Income income3 = new Income(name3, "300.00", "Fitness" , LocalDate.of(2000,3,3));

        List<Income> incomeList = new ArrayList<>();
        incomeList.add(income1);
        incomeList.add(income2);
        incomeList.add(income3);

        jsonParserService.saveJsonIncomes(incomeList, positionFile);

        //when
        List<Income> readOutcomes = jsonParserService.readJsonIncomes(positionFile);

        //then
        assertEquals(readOutcomes.size(), incomeList.size());
        assertEquals(category2, readOutcomes.get(1).getCategory());
        assertEquals(name3, readOutcomes.get(2).getName());
    }

}