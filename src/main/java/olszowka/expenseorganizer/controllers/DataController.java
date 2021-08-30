package olszowka.expenseorganizer.controllers;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.services.JSONParserService;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class DataController {
    private File incomesFile;
    private File outcomesFile;
    private File directory;

    private final List<Income> incomes;
    private final List<Outcome> outcomes;

    private final JSONParserService jsonParserService;

    public DataController(JSONParserService jsonParserService) throws IOException, ParseException {
        this.jsonParserService = jsonParserService;
        initializeFiles();
        incomes = jsonParserService.readJsonIncomes(incomesFile);
        outcomes = jsonParserService.readJsonOutcomes(outcomesFile);
    }

    public void saveIncomes(List<Income> incomes) {
        jsonParserService.saveJsonIncomes(incomes, incomesFile);
    }

    public void saveOutcomes(List<Outcome> outcomes) {
        jsonParserService.saveJsonOutcomes(outcomes, outcomesFile);
    }

    private void initializeFiles() throws IOException {
        this.directory = new File("src/main/resources/data/json");
        if(directory.mkdirs()) {
            System.out.println("Data directory successfully created.");
        } else {
            System.out.println("Data directory already exists.");
        }

        this.incomesFile = new File("src/main/resources/data/json/incomesData.json");

        if(incomesFile.createNewFile()) {
            System.out.println("IncomesFile successfully created.");
        } else {
            if(incomesFile.exists()) {
                System.out.println("File already exists.");
            } else {
                System.err.println("Error during file creation");
            }
        }

        this.outcomesFile = new File("src/main/resources/data/json/outcomesData.json");

        if(outcomesFile.createNewFile()) {
            System.out.println("IncomesFile successfully created.");
        } else {
            if(outcomesFile.exists()) {
                System.out.println("File already exists.");
            } else {
                System.err.println("Error during file creation");
            }
        }
    }

    public List<Income> getIncomes() throws IOException, ParseException {
        return jsonParserService.readJsonIncomes(incomesFile);
    }

    public List<Outcome> getOutcomes() throws IOException, ParseException {
        return jsonParserService.readJsonOutcomes(outcomesFile);
    }
}
