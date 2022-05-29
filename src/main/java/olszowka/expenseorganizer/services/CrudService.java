package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Position;

import java.util.List;

public interface CrudService<T extends Position> {
    List<T> getAllPositions();

    void addPosition(T position);

    void addAllPositions(List<T> positions);

    void deletePosition(T position);

    void clearAllPositions();

    String calculateTotalAmount();

    String calculateTotalAmountForPositions(List<T> positions);
}
