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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import net.rgielen.fxweaver.core.FxmlView;
import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.model.Position;
import olszowka.expenseorganizer.model.Timeframe;
import olszowka.expenseorganizer.services.*;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("MainStage.fxml")
public class MainStageController implements Initializable {
    private final IncomeService incomeService;
    private final OutcomeService outcomeService;
    private final ValidationService validationService;
    private final DataService dataService;
    private final CalculationService calculationService;
    private final ObservableList<Outcome> outcomeObservableList = FXCollections.observableArrayList();
    private final ObservableList<Income> incomeObservableList = FXCollections.observableArrayList();
    private final ObservableList<Position> summaryObservableList = FXCollections.observableArrayList();
    private final List<String> outcomeListOfCategories = Arrays.asList("Food", "Entertainment", "Fitness", "Clothes", "Traveling", "Education", "Other");
    private final List<String> incomeListOfCategories = Arrays.asList("Primary Job", "Part Time Job", "Scholarship", "Investments", "Cashback");
    private final List<String> listOfPeriods = Arrays.asList("Day", "Week", "Month", "All");
    private static final double DEFAULT_BUDGET = 5000.0;

    @FXML
    private AnchorPane budgetPane;

    private double xOffset;
    private double yOffset;
    private double budgetValue = DEFAULT_BUDGET;

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
            summaryTotalSumText, summarySubmittedPromptText, summaryWrongBudgetValuePromptText, summaryBudgetText,
            budgetProgressionText, outcomePeriodStringText, incomePeriodStringText,outcomeCurrentSumText,
            outcomePreviousSumText, outcomeChangeSumText, incomeCurrentSumText, incomePreviousSumText, incomeChangeSumText;

    @FXML
    private TextField outcomeNameTextField, outcomeValueTextField, incomeNameTextField, incomeValueTextField,
            summaryBudgetTextField;

    @FXML
    private ComboBox<String> outcomeCategoryComboBox, incomeCategoryComboBox, periodComboBox;

    @FXML
    private PieChart outcomePieChart, incomePieChart, summaryPieChart;

    @FXML
    private ProgressBar budgetProgressionBar;

    public MainStageController(IncomeService incomeService, OutcomeService outcomeService, ValidationService validationService, DataService dataService, CalculationService calculationService) {
        this.incomeService = incomeService;
        this.outcomeService = outcomeService;
        this.validationService = validationService;
        this.dataService = dataService;
        this.calculationService = calculationService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        outcomeCategoryComboBox.getItems().addAll(outcomeListOfCategories);
        incomeCategoryComboBox.getItems().addAll(incomeListOfCategories);
        periodComboBox.getItems().addAll(listOfPeriods);

        try {
            updateIncomesDataFiles();
            updateOutcomesDataFiles();
            updateBudget();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        initializeListeners();
        initializeTableViews();
        updatePieCharts();
        preparePeriodString();
        prepareComparedWithPreviousPeriod();

        initializeSummaryBudgetPane();
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
        summarySubmittedPromptText.setVisible(false);
    }

    @FXML
    private void summaryBudgetSubmitButtonClicked() throws IOException {
        if(validationService.isValueValid(summaryBudgetTextField.getText())) {
            budgetValue = Double.parseDouble(summaryBudgetTextField.getText());
            saveBudgetFiles();
            updateBudgetProgressionBar();
            summaryBudgetText.setText(outcomeService.calculateTotalAmount() + "/" + budgetValue + " z??");
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
        dataService.saveIncomes(incomeService.getAllPositions());
    }

    private void saveOutcomesDataFiles() {
        dataService.saveOutcomes(outcomeService.getAllPositions());
    }

    private void saveBudgetFiles() throws IOException {
        dataService.saveBudget(budgetValue);
    }

    private void updateIncomesDataFiles() throws IOException, ParseException {
        incomeService.clearAllPositions();
        dataService.getIncomes().forEach(incomeService::addPosition);
    }

    private void updateOutcomesDataFiles() throws IOException, ParseException {
        outcomeService.clearAllPositions();
        dataService.getOutcomes().forEach(outcomeService::addPosition);
    }

    private void updateBudget() throws IOException {
        double newBudget = dataService.getBudget();
        if(newBudget != 0)
            budgetValue = newBudget;
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
            prepareComparedWithPreviousPeriod();
        });
        incomeObservableList.addListener((ListChangeListener<Income>) change -> {
            updateIncomeTotalSumTextField();
            updatePieChart(incomeListOfCategories, incomeObservableList, incomePieChart);
            updateSummaryPieChart();
            prepareComparedWithPreviousPeriod();
        });
        summaryObservableList.addListener((ListChangeListener<Position>) change -> {
            updateSummaryTotalSumTextField();
            updateBudgetProgressionBar();
            summaryBudgetText.setText(outcomeService.calculateTotalAmount() + "/" + budgetValue + " z??");
        });

        initializeTextFieldsListeners(incomeNameTextField, incomeNameRequiredText, incomeValueTextField,
                           incomeWrongValueText, incomeCategoryComboBox, incomeSelectCategoryText, incomeCategoryText);
        initializeTextFieldsListeners(outcomeNameTextField, outcomeNameRequiredText, outcomeValueTextField,
                           outcomeWrongValueText, outcomeCategoryComboBox, outcomeSelectCategoryText, outcomeCategoryText);
    }

    private void updateBudgetProgressionBar() {
        if(Double.parseDouble(outcomeService.calculateTotalAmount()) >= budgetValue) {
            budgetProgressionBar.setProgress(100);
            budgetProgressionText.setText("100%");
        } else {
            double progression = Double.parseDouble(outcomeService.calculateTotalAmount())/budgetValue;
            budgetProgressionBar.setProgress(progression);
            budgetProgressionText.setText(Math.round(progression*100) + "%");
        }
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
                    setStyle("-fx-text-background-color: #66CCCCFF");
                } else if(position instanceof Income) {
                    setStyle("-fx-text-background-color: #036d20");
                } else {
                    setStyle("-fx-text-background-color: #e53636");
                }
            }
        });

        incomeObservableList.addAll(incomeService.getAllPositions());
        outcomeObservableList.addAll(outcomeService.getAllPositions());
        summaryObservableList.addAll(incomeService.getAllPositions());
        summaryObservableList.addAll(outcomeService.getAllPositions());

        initializeTableViewSort(incomeDateColumn, incomeTableView);
        initializeTableViewSort(outcomeDateColumn, outcomeTableView);
        initializeTableViewSort(summaryDateColumn, summaryTableView);

        outcomeTableView.setItems(outcomeObservableList);
        incomeTableView.setItems(incomeObservableList);
        summaryTableView.setItems(summaryObservableList);
    }

    private <T extends Position> void initializeTableViewSort(TableColumn<T, String> sortedTableColumn, TableView<T> tableView) {
        sortedTableColumn.setSortType(TableColumn.SortType.DESCENDING);
        tableView.getSortOrder().add(sortedTableColumn);
        sortTableView(tableView);
    }

    private <T extends  Position> void sortTableView(TableView<T> tableView) {
        tableView.sort();
    }

    private void updateSummaryTotalSumTextField() {
        Timeframe selectedTimeframe = getTimeFrameBasedOnIndex(periodComboBox.getSelectionModel().getSelectedIndex());
        List<Income> currentIncomeList = calculationService.getPeriodicPositions(incomeService.getAllPositions(), selectedTimeframe);
        List<Outcome> currentOutcomeList = calculationService.getPeriodicPositions(outcomeService.getAllPositions(), selectedTimeframe);

        double totalSum = Double.parseDouble(incomeService.calculateTotalAmountForPositions(currentIncomeList)) - Double.parseDouble(outcomeService.calculateTotalAmountForPositions(currentOutcomeList));
        if(totalSum >= 0) {
            summaryTotalSumText.setFill(Paint.valueOf("#10b244"));
        } else {
            summaryTotalSumText.setFill(Paint.valueOf("#ff1a00"));
        }
        summaryTotalSumText.setText(new DecimalFormat("0.00").format(totalSum).replace(",", ".") + " z??");
    }

    private void updateOutcomeTotalSumTextField() {
        Timeframe selectedTimeframe = getTimeFrameBasedOnIndex(periodComboBox.getSelectionModel().getSelectedIndex());
        List<Outcome> currentOutcomeList = calculationService.getPeriodicPositions(outcomeService.getAllPositions(), selectedTimeframe);
        String totalValue = validationService.returnFormattedValue(outcomeService.calculateTotalAmountForPositions(currentOutcomeList));
        Platform.runLater(() -> outcomeTotalSumText.setText("-" + totalValue + " z??"));
    }

    private void updateIncomeTotalSumTextField() {
        Timeframe selectedTimeframe = getTimeFrameBasedOnIndex(periodComboBox.getSelectionModel().getSelectedIndex());
        List<Income> currentIncomeList = calculationService.getPeriodicPositions(incomeService.getAllPositions(), selectedTimeframe);
        String totalValue = validationService.returnFormattedValue(incomeService.calculateTotalAmountForPositions(currentIncomeList));
        Platform.runLater(() -> incomeTotalSumText.setText("+" + totalValue + " z??"));
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

           clearPositionAfterSubmitted(outcomeObservableList, outcomeService, outcomeTableView, outcomeCategoryComboBox,
                                       outcomeNameTextField, outcomeValueTextField, outcomeNameRequiredText, outcomeWrongValueText, outcomeSelectCategoryText);

            preparePositionsBasedOnTimeframe();
            prepareComparedWithPreviousPeriod();

            wasSubmitted = true;
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

           clearPositionAfterSubmitted(incomeObservableList, incomeService, incomeTableView, incomeCategoryComboBox,
                                       incomeNameTextField, incomeValueTextField, incomeNameRequiredText, incomeWrongValueText, incomeSelectCategoryText);

            preparePositionsBasedOnTimeframe();
            prepareComparedWithPreviousPeriod();

            wasSubmitted = true;
        }

        if(!wasSubmitted) {
            validAllMessagesUnderFields(incomeNameTextField, incomeNameRequiredText, incomeValueTextField,
                                        incomeWrongValueText, incomeCategoryComboBox, incomeSelectCategoryText);
        }
    }

    @FXML
    private void onOutcomeDeleteButtonClicked() {
        onPositionDeleteButtonClicked(outcomeTableView, outcomeService,
                outcomeObservableList, outcomeSelectPositionText);
    }

    @FXML
    private void onIncomeDeleteButtonClicked() {
        onPositionDeleteButtonClicked(incomeTableView, incomeService,
                incomeObservableList, incomeSelectPositionText);
    }

    private <T extends Position> void onPositionDeleteButtonClicked(TableView<T> positionTableView,
                                                                    CrudService<T> positionService,
                                                                    ObservableList<T> positionObservableList,
                                                                    Text positionSelectPositionText) {
        if(positionTableView.getSelectionModel().getSelectedIndex() > -1) {
            positionService.getAllPositions().remove(positionTableView.getSelectionModel().getSelectedItem());
            positionObservableList.remove(positionTableView.getSelectionModel().getSelectedItem());
            summaryObservableList.clear();
            summaryObservableList.addAll(outcomeObservableList);
            summaryObservableList.addAll(incomeObservableList);
            positionSelectPositionText.setVisible(false);
        } else {
            positionSelectPositionText.setVisible(true);
        }
    }

    private <T extends Position> void clearPositionAfterSubmitted(ObservableList<T> positionObservableList,
                                                                  CrudService<T> positionService,
                                                                  TableView<T> positionTableView,
                                                                  ComboBox<String> positionCategoryComboBox,
                                                                  TextField positionNameTextField,
                                                                  TextField positionValueTextField,
                                                                  Text positionNameRequiredText,
                                                                  Text positionWrongValueText,
                                                                  Text positionSelectCategoryText) {
        positionObservableList.clear();
        positionObservableList.addAll(positionService.getAllPositions());
        positionCategoryComboBox.getSelectionModel().clearSelection();
        positionNameTextField.clear();
        positionValueTextField.clear();

        summaryObservableList.clear();
        summaryObservableList.addAll(outcomeObservableList);
        summaryObservableList.addAll(incomeObservableList);

        positionNameRequiredText.setVisible(false);
        positionWrongValueText.setVisible(false);
        positionSelectCategoryText.setVisible(false);
        positionTableView.sort();
    }

    @FXML
    private void onPeriodComboBoxButtonClicked() {
        preparePositionsBasedOnTimeframe();
        preparePeriodString();
        prepareComparedWithPreviousPeriod();
    }

    private void preparePositionsBasedOnTimeframe() {
        Timeframe selectedTimeframe = getTimeFrameBasedOnIndex(periodComboBox.getSelectionModel().getSelectedIndex());

        List<Income> allIncomes = incomeService.getAllPositions();
        List<Outcome> allOutcomes = outcomeService.getAllPositions();
        incomeObservableList.clear();
        outcomeObservableList.clear();
        summaryObservableList.clear();
        List<Income> filteredIncomes = calculationService.getPeriodicPositions(allIncomes, selectedTimeframe);
        List<Outcome> filteredOutcomes = calculationService.getPeriodicPositions(allOutcomes, selectedTimeframe);
        incomeObservableList.addAll(filteredIncomes);
        outcomeObservableList.addAll(filteredOutcomes);
        summaryObservableList.addAll(filteredIncomes);
        summaryObservableList.addAll(filteredOutcomes);
    }

    private void preparePeriodString() {
        Timeframe selectedTimeframe = getTimeFrameBasedOnIndex(periodComboBox.getSelectionModel().getSelectedIndex());

        incomePeriodStringText.setText(
                calculationService.returnTimeframeString(selectedTimeframe));
        outcomePeriodStringText.setText(
                calculationService.returnTimeframeString(selectedTimeframe));
    }

    private Timeframe getTimeFrameBasedOnIndex(int indexOfItem) {
        switch (indexOfItem) {
            case 0:
                return Timeframe.DAY;
            case 1:
                return Timeframe.WEEK;
            case 2:
                return Timeframe.MONTH;
            default:
                return Timeframe.ALL;
        }
    }

    private void prepareComparedWithPreviousPeriod() {
        Timeframe selectedTimeframe = getTimeFrameBasedOnIndex(periodComboBox.getSelectionModel().getSelectedIndex());

        List<Income> filteredIncomesPrevious = calculationService.getPeriodicPositionsRange(incomeService.getAllPositions(), selectedTimeframe);
        List<Income> filteredIncomesCurrent = calculationService.getPeriodicPositions(incomeService.getAllPositions(), selectedTimeframe);
        List<Outcome> filteredOutcomesPrevious = calculationService.getPeriodicPositionsRange(outcomeService.getAllPositions(), selectedTimeframe);
        List<Outcome> filteredOutcomesCurrent = calculationService.getPeriodicPositions(outcomeService.getAllPositions(), selectedTimeframe);

        String currentIncomes, previousIncomes, changeIncomes;
        String currentOutcomes, previousOutcomes, changeOutcomes;

        currentIncomes = incomeService.calculateTotalAmountForPositions(filteredIncomesCurrent);
        previousIncomes = incomeService.calculateTotalAmountForPositions(filteredIncomesPrevious);
        changeIncomes = String.valueOf(Double.parseDouble(currentIncomes) - Double.parseDouble(previousIncomes));

        currentOutcomes = outcomeService.calculateTotalAmountForPositions(filteredOutcomesCurrent);
        previousOutcomes = outcomeService.calculateTotalAmountForPositions(filteredOutcomesPrevious);
        changeOutcomes = String.valueOf(Double.parseDouble(currentOutcomes) - Double.parseDouble(previousOutcomes));

        incomeCurrentSumText.setText(validationService.returnFormattedValue(currentIncomes) + " z??");
        incomePreviousSumText.setText(validationService.returnFormattedValue(previousIncomes) + " z??");
        incomeChangeSumText.setText(validationService.returnFormattedValue(changeIncomes) + " z??");

        outcomeCurrentSumText.setText(validationService.returnFormattedValue(currentOutcomes) + " z??");
        outcomePreviousSumText.setText(validationService.returnFormattedValue(previousOutcomes) + " z??");
        outcomeChangeSumText.setText(validationService.returnFormattedValue(changeOutcomes) + " z??");
    }

    @FXML
    private void onDTimeButtonClicked() {
        periodComboBox.getSelectionModel().select(Timeframe.DAY.getIdx());
    }

    @FXML
    private void onWTimeButtonClicked() {
        periodComboBox.getSelectionModel().select(Timeframe.WEEK.getIdx());
    }

    @FXML
    private void onMTimeButtonClicked() {
        periodComboBox.getSelectionModel().select(Timeframe.MONTH.getIdx());
    }

    @FXML
    private void onAllTimeButtonClicked() {
        periodComboBox.getSelectionModel().select(Timeframe.ALL.getIdx());
    }
}
