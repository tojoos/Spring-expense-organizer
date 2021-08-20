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
import olszowka.expenseorganizer.model.Position;
import olszowka.expenseorganizer.services.*;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("MainStage.fxml")
public class MainStageController implements Initializable {
    private final IncomeService incomeService;
    private final OutcomeService outcomeService;
    private final ValidationService validationService;
    private final ObservableList<Outcome> outcomeObservableList = FXCollections.observableArrayList();
    private final ObservableList<Income> incomeObservableList = FXCollections.observableArrayList();
    private final List<String> outcomeListOfCategories = Arrays.asList("Food", "Entertainment", "Fitness", "Clothes", "Other");
    private final List<String> incomeListOfCategories = Arrays.asList("Primary Job", "Part Time Job", "Scholarship", "Investments");

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
    private TableColumn<Outcome, Double> outcomeCategoryColumn;
    @FXML
    private TableColumn<Income, Double> incomeCategoryColumn;

    @FXML
    private TableColumn<Outcome, Double> outcomeDateColumn;
    @FXML
    private TableColumn<Income, Double> incomeDateColumn;

    @FXML
    private Text outcomeTotalSumText, incomeTotalSumText, outcomeSelectCategoryText, outcomeWrongValueText,
            incomeSelectCategoryText, incomeWrongValueText;

    @FXML
    private TextField outcomeNameTextField, outcomeValueTextField, incomeNameTextField, incomeValueTextField;

    @FXML
    private ComboBox<String> outcomeCategoryComboBox, incomeCategoryComboBox;

    public MainStageController(IncomeService incomeService, OutcomeService outcomeService, ValidationService validationService) {
        this.incomeService = incomeService;
        this.outcomeService = outcomeService;
        this.validationService = validationService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        outcomeObservableList.addListener((ListChangeListener<Outcome>) change -> updateOutcomeTotalSumTextField());
        incomeObservableList.addListener((ListChangeListener<Income>) change -> updateIncomeTotalSumTextField());

        outcomeCategoryComboBox.getItems().addAll(outcomeListOfCategories);
        incomeCategoryComboBox.getItems().addAll(incomeListOfCategories);

        outcomeCategoryComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if(outcomeCategoryComboBox.getSelectionModel().getSelectedIndex() > -1) {
                outcomeSelectCategoryText.setVisible(false);
            }
        });
        outcomeValueTextField.textProperty().addListener(e ->
            outcomeWrongValueText.setVisible(!validationService.isValueValid(outcomeValueTextField.getText())));

        incomeCategoryComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if(incomeCategoryComboBox.getSelectionModel().getSelectedIndex() > -1) {
                incomeSelectCategoryText.setVisible(false);
            }
        });
        incomeValueTextField.textProperty().addListener(e ->
               incomeWrongValueText.setVisible(!validationService.isValueValid(incomeValueTextField.getText())));

        initializeTableViews();
    }

    private void initializeTableViews() {
        outcomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        outcomeValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        outcomeCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        outcomeDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        incomeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        incomeValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        incomeCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        incomeDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

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

    @FXML
    private void onOutcomeSubmitButtonClicked() {
        if(validationService.isValueValid(outcomeValueTextField.getText())) {
            if(isCategorySelected(outcomeCategoryComboBox)) {
                outcomeService.getAllPositions().add(new Outcome(outcomeNameTextField.getText(), Double.parseDouble(outcomeValueTextField.getText()), outcomeCategoryComboBox.getSelectionModel().getSelectedItem()));
                outcomeObservableList.clear();
                outcomeObservableList.addAll(outcomeService.getAllPositions());
                outcomeCategoryComboBox.getSelectionModel().clearSelection(); //not working properly
                outcomeNameTextField.clear();
                outcomeValueTextField.clear();
            } else {
                outcomeSelectCategoryText.setVisible(true);
            }
        }
    }

    @FXML
    private void onIncomeSubmitButtonClicked() {
        if(validationService.isValueValid(incomeValueTextField.getText())) {
            if(isCategorySelected(incomeCategoryComboBox)) {
                incomeService.getAllPositions().add(new Income(incomeNameTextField.getText(), Double.parseDouble(incomeValueTextField.getText()), incomeCategoryComboBox.getSelectionModel().getSelectedItem()));
                incomeObservableList.clear();
                incomeObservableList.addAll(incomeService.getAllPositions());
                incomeCategoryComboBox.getSelectionModel().clearSelection(); //not working properly
                incomeNameTextField.clear();
                incomeValueTextField.clear();
            } else {
                incomeSelectCategoryText.setVisible(true);
            }
        }
    }

    private boolean isCategorySelected(ComboBox<String> CategoryComboBox) {
        return CategoryComboBox.getSelectionModel().getSelectedIndex() > -1;
    }
}
