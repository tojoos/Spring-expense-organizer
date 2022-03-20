package olszowka.expenseorganizer.services;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
public class ValidationService {
    public boolean isValueValid(String value) {
        return value.matches("(\\d{0,10}$)|(\\d{0,10}([,.])(\\d){0,2}$)") && !value.matches("");
    }

    public boolean isNameValid(String name) {
        return !name.matches("");
    }

    public String returnFormattedValue(String sum) {
        sum = sum.replace(",", ".");
        Double doubleSum = Double.parseDouble(sum);
        DecimalFormat myFormatter = new DecimalFormat("###.##");
        String totalValue = myFormatter.format(doubleSum);
        if(totalValue.contains(",")) {
           totalValue = totalValue.replace(",",".");
        }
        if(!totalValue.contains(".")) {
            totalValue = totalValue + ".00";
        }
        if(totalValue.substring(totalValue.indexOf(".") + 1).length() == 1) {
            totalValue = totalValue + "0";
        }
        return totalValue;
    }
}
