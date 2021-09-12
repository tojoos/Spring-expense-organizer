package olszowka.expenseorganizer.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import net.rgielen.fxweaver.core.FxmlView;
import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.model.Position;
import olszowka.expenseorganizer.services.*;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private final ObservableList<Position> summaryObservableList = FXCollections.observableArrayList();
    private final List<String> outcomeListOfCategories = Arrays.asList("Food", "Entertainment", "Fitness", "Clothes", "Traveling", "Other");
    private final List<String> incomeListOfCategories = Arrays.asList("Primary Job", "Part Time Job", "Scholarship", "Investments", "Cashback");

    private final DataController dataController;

    @FXML
    private AnchorPane budgetPane;

    private double xOffset;
    private double yOffset;
    private double budgetValue;

    @FXML
    private TableView<Outcome> outcomeTableView;
    @FXML
    private TableView<Income> incomeTableView;
    @FXML
    private TableView<Position> summaryTableView;

    @FXML
    private TableColumn<Outcome, String> outcomeNameColumn, outcomeValueColumn, outcomeCategoryColumn, outcomeDateColumn;

    @FXML
    private TableColumn<Income, String> incomeNameColumn, incomeValueColumn, incomeCategoryColumn, incomeDateColumn;

    @FXML
    private TableColumn<Position, String> summaryNameColumn, summaryValueColumn, summaryCategoryColumn, summaryDateColumn;

    @FXML
    private Text outcomeTotalSumText, incomeTotalSumText, outcomeSelectCategoryText, outcomeWrongValueText,
            incomeSelectCategoryText, incomeWrongValueText, incomeNameRequiredText, outcomeNameRequiredText,
            outcomeCategoryText, incomeCategoryText, outcomeSelectPositionText, incomeSelectPositionText,
            summaryTotalSumText, summarySubmittedPromptText, summaryWrongBudgetValuePromptText, summaryBudgetText;

    @FXML
    private TextField outcomeNameTextField, outcomeValueTextField, incomeNameTextField, incomeValueTextField,
            summaryBudgetTextField;

    @FXML
    private ComboBox<String> outcomeCategoryComboBox, incomeCategoryComboBox;

    @FXML
    private PieChart outcomePieChart, incomePieChart, summaryPieChart;

    public MainStageController(IncomeService incomeService, OutcomeService outcomeService, ValidationService validationService, JSONParserService jsonParserService) throws IOException, ParseException {
        this.incomeService = incomeService;
        this.outcomeService = outcomeService;
        this.validationService = validationService;
        this.dataController = new DataController(jsonParserService);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        outcomeCategoryComboBox.getItems().addAll(outcomeListOfCategories);
        incomeCategoryComboBox.getItems().addAll(incomeListOfCategories);

        try {
            updateIncomesDataFiles();
            updateOutcomesDataFiles();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        initializeListeners();
        initializeTableViews();
        updatePieCharts();

        initializeSummaryBudgetPane();

        try {
            summaryPieChart.getStylesheets().add("/css/summary-pie-chart-styles.css");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeSummaryBudgetPane() {
            budgetPane.setOnMousePressed(mouseEvent -> Platform.runLater(() -> {
                xOffset = mouseEvent.getX();
                yOffset = mouseEvent.getY() + 28;
            }));

            budgetPane.setOnMouseDragged(mouseEvent -> Platform.runLater(() -> {
                budgetPane.setLayoutX(mouseEvent.getSceneX() - xOffset);
                budgetPane.setLayoutY(mouseEvent.getSceneY() - yOffset);
            }));
    }

    @FXML
    private void onChangeBudgetLabelClicked() {
        budgetPane.setDisable(false);
        budgetPane.setVisible(true);
    }

    @FXML
    private void summaryBudgetSubmitButtonClicked() {
        if(validationService.isValueValid(summaryBudgetTextField.getText())) {
            budgetValue = Double.parseDouble(summaryBudgetTextField.getText());
            summaryBudgetText.setText(budgetValue + " zł");
            summaryWrongBudgetValuePromptText.setVisible(false);
            summarySubmittedPromptText.setVisible(true);
        } else {
            summarySubmittedPromptText.setVisible(false);
            summaryWrongBudgetValuePromptText.setVisible(true);
        }
    }

    @FXML
    private void onBudgetPaneCloseLabelClicked() {
        budgetPane.setVisible(false);
        budgetPane.setDisable(true);
    }

    private void saveIncomesDataFiles() {
        dataController.saveIncomes(incomeService.getAllPositions());
    }

    private void saveOutcomesDataFiles() {
        dataController.saveOutcomes(outcomeService.getAllPositions());
    }

    private void updateIncomesDataFiles() throws IOException, ParseException {
        incomeService.clearAllPositions();
        dataController.getIncomes().forEach(incomeService::addPosition);
    }

    private void updateOutcomesDataFiles() throws IOException, ParseException {
        outcomeService.clearAllPositions();
        dataController.getOutcomes().forEach(outcomeService::addPosition);
    }

    private void updatePieCharts() {
        updatePieChart(outcomeListOfCategories, outcomeObservableList, outcomePieChart);
        updatePieChart(incomeListOfCategories, incomeObservableList, incomePieChart);
        updateSummaryPieChart();
    }

    private void updateSummaryPieChart() {
        double totalOutcomes;
        double totalIncomes;
        ObservableList<PieChart.Data> pieDataChart = FXCollections.observableArrayList();

        totalOutcomes = Double.parseDouble(outcomeService.calculateTotalAmount());
        totalIncomes = Double.parseDouble(incomeService.calculateTotalAmount());

        PieChart.Data pieChartOutcomeData = new PieChart.Data("Outcomes", totalOutcomes);
        PieChart.Data pieChartIncomeData = new PieChart.Data("Incomes", totalIncomes);

        pieDataChart.add(pieChartOutcomeData);
        pieDataChart.add(pieChartIncomeData);

        summaryPieChart.setData(pieDataChart);
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
                PieDataChart.add(new PieChart.Data(listOfCategories.get(i), amounts[i]));
        }
        pieChart.setData(PieDataChart);
    }

    private void initializeListeners() {
        Platform.runLater(() -> outcomeNameRequiredText.getScene().getWindow().setOnCloseRequest(e -> {
            saveOutcomesDataFiles();
            saveIncomesDataFiles();
            Platform.exit();
            System.exit(0);
        }));

        outcomeObservableList.addListener((ListChangeListener<Outcome>) change -> {
            updateOutcomeTotalSumTextField();
            updatePieChart(outcomeListOfCategories, outcomeObservableList, outcomePieChart);
            updateSummaryPieChart();
        });
        incomeObservableList.addListener((ListChangeListener<Income>) change -> {
            updateIncomeTotalSumTextField();
            updatePieChart(incomeListOfCategories, incomeObservableList, incomePieChart);
            updateSummaryPieChart();
        });
        summaryObservableList.addListener((ListChangeListener<Position>) change -> {
            updateSummaryTotalSumText();
        });

        initializeTextFieldsListeners(incomeNameTextField, incomeNameRequiredText, incomeValueTextField,
                           incomeWrongValueText, incomeCategoryComboBox, incomeSelectCategoryText, incomeCategoryText);
        initializeTextFieldsListeners(outcomeNameTextField, outcomeNameRequiredText, outcomeValueTextField,
                           outcomeWrongValueText, outcomeCategoryComboBox, outcomeSelectCategoryText, outcomeCategoryText);
    }

    private void updateSummaryTotalSumText() {
        double totalSum = Double.parseDouble(incomeService.calculateTotalAmount()) - Double.parseDouble(outcomeService.calculateTotalAmount());
        if(totalSum >= 0) {
            summaryTotalSumText.setFill(Paint.valueOf("#10b244"));
        } else {
            summaryTotalSumText.setFill(Paint.valueOf("#ff1a00"));
        }
        summaryTotalSumText.setText(totalSum + " zł");
    }

    private void initializeTextFieldsListeners(TextField NameTextField,
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

        summaryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        summaryValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        summaryCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        summaryDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        summaryTableView.setRowFactory(tr -> new TableRow<>() {
            @Override
            protected void updateItem(Position position, boolean empty) {
                super.updateItem(position, empty);
                if(position == null) {
                    setStyle("-fx-background-color: #66CCCCFF");
                } else if(position instanceof Income) {
                    setStyle("-fx-background-color: #44c265");
                } else {
                    setStyle("-fx-background-color: #ed6d6d");
                }
            }
        });

        incomeObservableList.addAll(incomeService.getAllPositions());
        outcomeObservableList.addAll(outcomeService.getAllPositions());
        summaryObservableList.addAll(incomeService.getAllPositions());
        summaryObservableList.addAll(outcomeService.getAllPositions());

        outcomeTableView.setItems(outcomeObservableList);
        incomeTableView.setItems(incomeObservableList);
        summaryTableView.setItems(summaryObservableList);

        initializeTableViewSort(incomeDateColumn, incomeTableView);
        initializeTableViewSort(outcomeDateColumn, outcomeTableView);
        initializeTableViewSort(summaryDateColumn, summaryTableView);
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
                outcomeCategoryComboBox.getSelectionModel().clearSelection();
                outcomeNameTextField.clear();
                outcomeValueTextField.clear();

                summaryObservableList.clear();
                summaryObservableList.addAll(outcomeObservableList);
                summaryObservableList.addAll(incomeObservableList);

            outcomeNameRequiredText.setVisible(false);
            outcomeWrongValueText.setVisible(false);
            outcomeSelectCategoryText.setVisible(false);
            wasSubmitted = true;
            outcomeTableView.sort();
        }

        if(!wasSubmitted) {
            validAllMessagesUnderFields(outcomeNameTextField, outcomeNameRequiredText, outcomeValueTextField,
                                        outcomeWrongValueText, outcomeCategoryComboBox, outcomeSelectCategoryText);
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
                incomeCategoryComboBox.getSelectionModel().clearSelection();
                incomeNameTextField.clear();
                incomeValueTextField.clear();

                summaryObservableList.clear();
                summaryObservableList.addAll(outcomeObservableList);
                summaryObservableList.addAll(incomeObservableList);

            incomeNameRequiredText.setVisible(false);
            incomeWrongValueText.setVisible(false);
            incomeSelectCategoryText.setVisible(false);
            wasSubmitted = true;
            incomeTableView.sort();
        }

        if(!wasSubmitted) {
            validAllMessagesUnderFields(incomeNameTextField, incomeNameRequiredText, incomeValueTextField,
                                        incomeWrongValueText, incomeCategoryComboBox, incomeSelectCategoryText);
        }
    }

    @FXML
    private void onOutcomeDeleteButtonClicked() {
        if(outcomeTableView.getSelectionModel().getSelectedIndex() > -1) {
            outcomeService.getAllPositions().remove(outcomeTableView.getSelectionModel().getSelectedItem());
            outcomeObservableList.remove(outcomeTableView.getSelectionModel().getSelectedItem());
            summaryObservableList.clear();
            summaryObservableList.addAll(outcomeObservableList);
            summaryObservableList.addAll(incomeObservableList);
            outcomeSelectPositionText.setVisible(false);
        } else {
            outcomeSelectPositionText.setVisible(true);
        }
    }

    @FXML
    private void onIncomeDeleteButtonClicked() {
        if(incomeTableView.getSelectionModel().getSelectedIndex() > -1) {
            incomeService.getAllPositions().remove(incomeTableView.getSelectionModel().getSelectedItem());
            incomeObservableList.remove(incomeTableView.getSelectionModel().getSelectedItem());
            summaryObservableList.clear();
            summaryObservableList.addAll(outcomeObservableList);
            summaryObservableList.addAll(incomeObservableList);
            incomeSelectPositionText.setVisible(false);
        } else {
            incomeSelectPositionText.setVisible(true);
        }
    }
}
