package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.model.Position;
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
            incomesJsonList.add(prepareJsonObject(i));

        try (FileWriter fileWriter = new FileWriter(incomesFile)) {
            fileWriter.write(incomesJsonList.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveJsonOutcomes(List<Outcome> outcomes, File outcomesFile) {
        JSONArray outcomesJsonList = new JSONArray();
        for(Outcome o : outcomes)
            outcomesJsonList.add(prepareJsonObject(o));

        try (FileWriter fileWriter = new FileWriter(outcomesFile)) {
            fileWriter.write(outcomesJsonList.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T extends Position> JSONObject prepareJsonObject(T position) {
        JSONObject objectAttributes = new JSONObject();
        objectAttributes.put("name", position.getName());
        objectAttributes.put("value", position.getValue());
        objectAttributes.put("category", position.getCategory());
        objectAttributes.put("date", position.getDate().toString());

        JSONObject JSONObject = new JSONObject();
        if(position instanceof Outcome) {
            JSONObject.put("outcome", objectAttributes);
        } else {
            JSONObject.put("income", objectAttributes);
        }
        return JSONObject;
    }

    public List<Income> readJsonIncomes(File file) throws IOException, ParseException {
        List<Income> incomes = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(file);

        if(file.length() != 0) {
            JSONArray positions = (JSONArray) jsonParser.parse(reader);
            positions.forEach(position -> incomes.add(parseJSONToIncome((JSONObject) position)));
        }
        reader.close();
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
        reader.close();
        return outcomes;
    }

    private Income parseJSONToIncome(JSONObject position) {
        JSONObject incomeObject = (JSONObject) position.get("income");
        return new Income((String) incomeObject.get("name"),(String) incomeObject.get("value"),
                (String) incomeObject.get("category"), LocalDate.parse((String)incomeObject.get("date")));
    }

    private Outcome parseJSONToOutcome(JSONObject position) {
        JSONObject outcomeObject = (JSONObject) position.get("outcome");
        return new Outcome((String) outcomeObject.get("name"),(String) outcomeObject.get("value"),
                (String) outcomeObject.get("category"), LocalDate.parse((String)outcomeObject.get("date")));
    }
}
