package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class DataService {
    private File incomesFile;
    private File outcomesFile;
    private File budgetFile;
    private File positionsDirectory;
    private File budgetDirectory;

    private final JSONParserService jsonParserService;

    public DataService(JSONParserService jsonParserService) throws IOException {
        this.jsonParserService = jsonParserService;
        initializeFiles();
    }

    public void saveIncomes(List<Income> incomes) {
        jsonParserService.saveJsonIncomes(incomes, incomesFile);
    }

    public void saveOutcomes(List<Outcome> outcomes) {
        jsonParserService.saveJsonOutcomes(outcomes, outcomesFile);
    }

    public void saveBudget(double budgetValue) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(budgetFile));
        bufferedWriter.write(String.valueOf(budgetValue));
        bufferedWriter.close();
    }

    private void initializeFiles() throws IOException {
        createDirectories();

        createPositionFiles();
    }

    private void createDirectories() {
        this.positionsDirectory = new File("src/main/resources/data/json");
        if(positionsDirectory.mkdirs()) {
            System.out.println("Data directory successfully created.");
        } else {
            System.out.println("Data directory already exists.");
        }

        this.budgetDirectory = new File("src/main/resources/data/budget");

        if(budgetDirectory.mkdirs()) {
            System.out.println("Budget directory successfully created.");
        } else {
            System.out.println("Budget directory already exists.");
        }
    }

    private void createPositionFiles() throws IOException {
        this.incomesFile = new File("src/main/resources/data/json/incomesData.json");

        if(incomesFile.createNewFile()) {
            System.out.println("IncomesFile successfully created.");
        } else {
            if(incomesFile.exists()) {
                System.out.println("IncomesFile already exists.");
            } else {
                System.err.println("Error during file creation");
            }
        }

        this.outcomesFile = new File("src/main/resources/data/json/outcomesData.json");

        if(outcomesFile.createNewFile()) {
            System.out.println("OutcomesFile successfully created.");
        } else {
            if(outcomesFile.exists()) {
                System.out.println("OutcomesFile already exists.");
            } else {
                System.err.println("Error during file creation");
            }
        }

        this.budgetFile = new File("src/main/resources/data/budget/budget.txt");

        if(budgetFile.createNewFile()) {
            System.out.println("BudgetFile successfully created.");
        } else {
            if(budgetFile.exists()) {
                System.out.println("BudgetFile already exists.");
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

    public double getBudget() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(budgetFile));
        String strBudget = bufferedReader.readLine();
        if(strBudget != null) {
            bufferedReader.close();
            return Double.parseDouble(strBudget);
        } else
            return 0;
    }
}
