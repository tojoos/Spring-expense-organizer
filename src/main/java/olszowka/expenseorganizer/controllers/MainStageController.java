package olszowka.expenseorganizer.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
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
    private TableColumn<Outcome, String> outcomeNameColumn, outcomeValueColumn, outcomeCategoryColumn, outcomeDateColumn;

    @FXML
    private TableColumn<Income, String> incomeNameColumn, incomeValueColumn, incomeCategoryColumn, incomeDateColumn;

    @FXML
    private Text outcomeTotalSumText, incomeTotalSumText, outcomeSelectCategoryText, outcomeWrongValueText,
            incomeSelectCategoryText, incomeWrongValueText, incomeNameRequiredText, outcomeNameRequiredText,
            outcomeCategoryText, incomeCategoryText;

    @FXML
    private TextField outcomeNameTextField, outcomeValueTextField, incomeNameTextField, incomeValueTextField;

    @FXML
    private ComboBox<String> outcomeCategoryComboBox, incomeCategoryComboBox;

    @FXML
    private PieChart outcomePieChart, incomePieChart;

    public MainStageController(IncomeService incomeService, OutcomeService outcomeService, ValidationService validationService) {
        this.incomeService = incomeService;
        this.outcomeService = outcomeService;
        this.validationService = validationService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        outcomeCategoryComboBox.getItems().addAll(outcomeListOfCategories);
        incomeCategoryComboBox.getItems().addAll(incomeListOfCategories);

        initializeListeners();
        initializeTableViews();
        updatePieCharts();
    }

    private void updatePieCharts() {
        updatePieChart(outcomeListOfCategories, outcomeObservableList, outcomePieChart);
        updatePieChart(incomeListOfCategories, incomeObservableList, incomePieChart);
    }

    private <T extends Position> void updatePieChart(List<String> listOfCategories, ObservableList<T> observableList, PieChart pieChart) {
        ObservableList<PieChart.Data> PieDataChart = FXCollections.observableArrayList();
        double []amounts = new double[listOfCategories.size()];
        Arrays.fill(amounts,0);
        for(T o : observableList) {
            for(int i = 0 ; i < listOfCategories.size(); i++) {
                if(o.getCategory().equals(listOfCategories.get(i))) {
                    amounts[i] += Double.parseDouble(o.getValue());
                    break;
                }
            }
        }
        for(int i = 0 ; i < listOfCategories.size() ; i++) {
            if(amounts[i]>0)
                PieDataChart.add(new PieChart.Data(listOfCategories.get(i),amounts[i]));
        }
        pieChart.setData(PieDataChart);
    }

    private void initializeListeners() {
        outcomeObservableList.addListener((ListChangeListener<Outcome>) change -> {
            updateOutcomeTotalSumTextField();
            updatePieChart(outcomeListOfCategories, outcomeObservableList, outcomePieChart);
        });
        incomeObservableList.addListener((ListChangeListener<Income>) change -> {
            updateIncomeTotalSumTextField();
            updatePieChart(incomeListOfCategories, incomeObservableList, incomePieChart);
        });

        initializeListener(incomeNameTextField, incomeNameRequiredText, incomeValueTextField,
                           incomeWrongValueText, incomeCategoryComboBox, incomeSelectCategoryText, incomeCategoryText);
        initializeListener(outcomeNameTextField, outcomeNameRequiredText, outcomeValueTextField,
                           outcomeWrongValueText, outcomeCategoryComboBox, outcomeSelectCategoryText, outcomeCategoryText);
    }

    private void initializeListener(TextField NameTextField,
                                    Text NameRequiredText,
                                    TextField ValueTextField,
                                    Text WrongValueText,
                                    ComboBox<String> CategoryComboBox,
                                    Text SelectCategoryText,
                                    Text categoryText) {

        NameTextField.textProperty().addListener(e ->
                NameRequiredText.setVisible(!validationService.isNameValid(NameTextField.getText())));

        ValueTextField.textProperty().addListener(e ->
                WrongValueText.setVisible(!validationService.isValueValid(ValueTextField.getText())));

        CategoryComboBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if(CategoryComboBox.getSelectionModel().getSelectedIndex() > -1) {
                SelectCategoryText.setVisible(false);
                categoryText.setVisible(false);
            } else {
                categoryText.setVisible(true);
            }
        });
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

        initializeTableViewSort(incomeDateColumn, incomeTableView);
        initializeTableViewSort(outcomeDateColumn, outcomeTableView);
    }

    private <T extends Position> void initializeTableViewSort(TableColumn<T, String> sortedTableColumn, TableView<T> tableView) {
        sortedTableColumn.setSortType(TableColumn.SortType.DESCENDING);
        tableView.getSortOrder().add(sortedTableColumn);
    }

    private void updateOutcomeTotalSumTextField() {
        String totalValue = validationService.returnFormattedValue(outcomeService.calculateTotalAmount());
        Platform.runLater(() -> outcomeTotalSumText.setText("-" + totalValue + " zł"));
    }

    private void updateIncomeTotalSumTextField() {
        String totalValue = validationService.returnFormattedValue(incomeService.calculateTotalAmount());
        Platform.runLater(() -> incomeTotalSumText.setText("+" + totalValue + " zł"));
    }

    private boolean isCategorySelected(ComboBox<String> CategoryComboBox) {
        return CategoryComboBox.getSelectionModel().getSelectedIndex() > -1;
    }

    @FXML
    private void onOutcomeSubmitButtonClicked() {
        boolean wasSubmitted = false;
        if(validationService.isNameValid(outcomeNameTextField.getText()) &&
           validationService.isValueValid(outcomeValueTextField.getText()) &&
           isCategorySelected(outcomeCategoryComboBox)) {
                outcomeService.getAllPositions().add(
                        new Outcome(
                                outcomeNameTextField.getText(),
                                validationService.returnFormattedValue(outcomeValueTextField.getText()),
                                outcomeCategoryComboBox.getSelectionModel().getSelectedItem()));
                outcomeObservableList.clear();
                outcomeObservableList.addAll(outcomeService.getAllPositions());
                outcomeCategoryComboBox.getSelectionModel().clearSelection(); //not working properly
                outcomeNameTextField.clear();
                outcomeValueTextField.clear();

            outcomeNameRequiredText.setVisible(false);
            outcomeWrongValueText.setVisible(false);
            outcomeSelectCategoryText.setVisible(false);
            wasSubmitted = true;
        }

        if(!wasSubmitted) {
            validAllMessagesUnderFields(outcomeNameTextField, outcomeNameRequiredText, outcomeValueTextField,
                                        outcomeWrongValueText, outcomeCategoryComboBox, outcomeSelectCategoryText);
        }
    }

    private void validAllMessagesUnderFields(TextField nameTextField,
                                             Text nameRequiredText,
                                             TextField valueTextField,
                                             Text wrongValueText,
                                             ComboBox<String> categoryComboBox,
                                             Text selectCategoryText) {

        if (!validationService.isNameValid(nameTextField.getText())) {
            nameRequiredText.setVisible(true);
        }

        if (!validationService.isValueValid(valueTextField.getText())) {
            wrongValueText.setVisible(true);
        }

        if (!isCategorySelected(categoryComboBox)) {
            selectCategoryText.setVisible(true);
        }
    }

    @FXML
    private void onIncomeSubmitButtonClicked() {
        boolean wasSubmitted = false;
        if(validationService.isNameValid(incomeNameTextField.getText()) &&
           validationService.isValueValid(incomeValueTextField.getText()) &&
           isCategorySelected(incomeCategoryComboBox)) {
                incomeService.getAllPositions().add(
                        new Income(
                                incomeNameTextField.getText(),
                                validationService.returnFormattedValue(incomeValueTextField.getText()),
                                incomeCategoryComboBox.getSelectionModel().getSelectedItem()));
                incomeObservableList.clear();
                incomeObservableList.addAll(incomeService.getAllPositions());
                incomeCategoryComboBox.getSelectionModel().clearSelection(); //not working properly
                incomeNameTextField.clear();
                incomeValueTextField.clear();

            incomeNameRequiredText.setVisible(false);
            incomeWrongValueText.setVisible(false);
            incomeSelectCategoryText.setVisible(false);
            wasSubmitted = true;
        }

        if(!wasSubmitted) {
            validAllMessagesUnderFields(incomeNameTextField, incomeNameRequiredText, incomeValueTextField,
                                        incomeWrongValueText, incomeCategoryComboBox, incomeSelectCategoryText);
        }
    }
}
