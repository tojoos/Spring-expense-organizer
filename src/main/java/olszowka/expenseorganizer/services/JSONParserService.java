package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class JSONParserService {
    public void saveJsonIncomes(List<Income> incomes, File incomesFile) {
        JSONArray incomesJsonList = new JSONArray();
        for(Income i : incomes)
            incomesJsonList.add(parseIncomeToJson(i));

        try (FileWriter fileWriter = new FileWriter(incomesFile)) {
            fileWriter.write(incomesJsonList.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject parseIncomeToJson(Income income) {
        JSONObject incomeAttributes = new JSONObject();
        incomeAttributes.put("name", income.getName());
        incomeAttributes.put("value", income.getValue());
        incomeAttributes.put("category", income.getCategory());
        incomeAttributes.put("date", income.getDate().toString());

        JSONObject incomeJSONObject = new JSONObject();
        incomeJSONObject.put("income", incomeAttributes);
        return incomeJSONObject;
    }

    public void saveJsonOutcomes(List<Outcome> outcomes, File outcomesFile) {
        JSONArray outcomesJsonList = new JSONArray();
        for(Outcome o : outcomes)
            outcomesJsonList.add(parseOutcomeToJson(o));

        try (FileWriter fileWriter = new FileWriter(outcomesFile)) {
            fileWriter.write(outcomesJsonList.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject parseOutcomeToJson(Outcome outcome) {
        JSONObject outcomeAttributes = new JSONObject();
        outcomeAttributes.put("name", outcome.getName());
        outcomeAttributes.put("value", outcome.getValue());
        outcomeAttributes.put("category", outcome.getCategory());
        outcomeAttributes.put("date", outcome.getDate().toString());

        JSONObject outcomeJSONObject = new JSONObject();
        outcomeJSONObject.put("outcome", outcomeAttributes);
        return outcomeJSONObject;
    }

    public List<Income> readJsonIncomes(File file) throws IOException, ParseException {
        List<Income> incomes = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(file);

        if(file.length() != 0) {
            JSONArray positions = (JSONArray) jsonParser.parse(reader);
            positions.forEach(position -> incomes.add(parseJSONToIncome((JSONObject) position)));
        }
        return incomes;
    }

    public List<Outcome> readJsonOutcomes(File file) throws IOException, ParseException {
        List<Outcome> outcomes = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(file);

        if(file.length() != 0) {
            JSONArray positions = (JSONArray) jsonParser.parse(reader);
            positions.forEach(position -> outcomes.add(parseJSONToOutcome((JSONObject) position)));
        }
        return outcomes;
    }

    private Income parseJSONToIncome(JSONObject position) {
        JSONObject incomeObject = (JSONObject) position.get("income");
        return new Income((String) incomeObject.get("name"),(String) incomeObject.get("value"),
                (String) incomeObject.get("category"), LocalDate.parse((String)incomeObject.get("date")));
    }

    private Outcome parseJSONToOutcome(JSONObject position) {
        JSONObject incomeObject = (JSONObject) position.get("outcome");
        return new Outcome((String) incomeObject.get("name"),(String) incomeObject.get("value"),
                (String) incomeObject.get("category"), LocalDate.parse((String)incomeObject.get("date")));
    }
}
