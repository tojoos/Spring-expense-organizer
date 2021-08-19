package olszowka.expenseorganizer.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import net.rgielen.fxweaver.core.FxmlView;
import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.services.IncomeService;
import olszowka.expenseorganizer.services.OutcomeService;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("MainStage.fxml")
public class MainStageController implements Initializable {
    private final IncomeService incomeService;
    private final OutcomeService outcomeService;
    private final ObservableList<Outcome> outcomeObservableList = FXCollections.observableArrayList();
    private final ObservableList<Income> incomeObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Outcome> outcomeTableView;
    @FXML
    private TableView<Income> incomeTableView;

    @FXML
    private TableColumn<Outcome, String> outcomeNameColumn;
    @FXML
    private TableColumn<Income, String> incomeNameColumn;

    @FXML
    private TableColumn<Outcome, Double> outcomeValueColumn;
    @FXML
    private TableColumn<Income, Double> incomeValueColumn;

    @FXML
    private Text outcomeTotalSumText, incomeTotalSumText;

    public MainStageController(IncomeService incomeService, OutcomeService outcomeService) {
        this.incomeService = incomeService;
        this.outcomeService = outcomeService;
        outcomeObservableList.addListener((ListChangeListener<Outcome>) change -> updateOutcomeTotalSumTextField());
        incomeObservableList.addListener((ListChangeListener<Income>) change -> updateIncomeTotalSumTextField());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTableViews();
    }

    private void initializeTableViews() {
        outcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        outcomeValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        incomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        incomeValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        incomeObservableList.addAll(incomeService.getAllPositions());
        outcomeObservableList.addAll(outcomeService.getAllPositions());

        outcomeTableView.setItems(outcomeObservableList);
        incomeTableView.setItems(incomeObservableList);

    }

    private void updateOutcomeTotalSumTextField() {
        outcomeTotalSumText.setText("-" + outcomeService.calculateTotalAmount() + " zł");
    }

    private void updateIncomeTotalSumTextField() {
        incomeTotalSumText.setText("+" + incomeService.calculateTotalAmount() + " zł");
    }
}
