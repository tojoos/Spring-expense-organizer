package olszowka.expenseorganizer.services;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    public boolean isValueValid(String value) {
        return value.matches("(\\d{0,10}$)|(\\d{0,10}\\.(\\d){0,2}$)");
    }
}
