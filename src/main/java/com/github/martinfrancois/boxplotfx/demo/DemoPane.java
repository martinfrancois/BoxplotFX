package com.github.martinfrancois.boxplotfx.demo;

import com.github.martinfrancois.boxplotfx.BusinessControl;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.util.List;

/**
 * @author Dieter Holz
 */
public class DemoPane extends BorderPane {
    private final static Logger LOGGER = Logger.getLogger(DemoPane.class.getName());

    private BusinessControl<Country> businessControl;

    private ObservableList<Country> countries = FXCollections.observableArrayList();

    private ObservableMap<Country, Double> map = FXCollections.observableHashMap();

    private TableView<Country> table = new TableView<>(countries);

    public DemoPane() {
        initializeControls();
        setupValueChangeListeners();
        initCountries();

        layoutControls();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        businessControl = new BusinessControl(map);

        TableColumn countryCol = new TableColumn("Country");
        TableColumn populationCol = new TableColumn("Population");

        countryCol.setCellValueFactory(
                new PropertyValueFactory<Country, String>("name")
        );
        countryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        countryCol.setOnEditCommit(
                t -> {
                    TableColumn.CellEditEvent<Country, String> cellEvent = (TableColumn.CellEditEvent<Country, String>) t;
                    Country changedCountry = cellEvent.getTableView().getItems().get(
                            cellEvent.getTablePosition().getRow());
                    String changedName = cellEvent.getNewValue();
                    changedCountry.setName(changedName);
                    map.put(changedCountry, changedCountry.getPopulation());
                }
        );

        populationCol.setCellValueFactory(
                new PropertyValueFactory<Country, String>("population")
        );
        populationCol.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        populationCol.setOnEditCommit(
                t -> {
                    TableColumn.CellEditEvent<Country, Number> cellEvent = (TableColumn.CellEditEvent<Country, Number>) t;
                    Country changedCountry = cellEvent.getTableView().getItems().get(
                            cellEvent.getTablePosition().getRow());
                    double changedPopulation = cellEvent.getNewValue().doubleValue();
                    changedCountry.setPopulation(changedPopulation);
                    map.put(changedCountry, changedPopulation);

                }
        );

        table.getColumns().addAll(countryCol, populationCol);
        table.setEditable(true);

    }

    private void initCountries() {
        // setup csvparser
        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        File csv = new File(Thread.currentThread().getContextClassLoader().getResource("countries_population_simple_outliers.csv").getFile());
        parser.parse(csv);

        // initialize test data
        List<String[]> rows = rowProcessor.getRows();
        countries.clear();
        rows.forEach(strings ->
                countries.add(
                        new Country(strings[0], Double.valueOf(strings[1]))
                )
        );
    }

    private void layoutControls() {
        setCenter(businessControl);
        setRight(table);
    }

    private void setupValueChangeListeners() {
        // setup listener to keep the map in sync with the observablelist
        countries.addListener((ListChangeListener<? super Country>) change -> {
            while (change.next()) {
                if (!(change.wasPermutated() || change.wasUpdated())) {
                    change.getRemoved().forEach(country -> {
                        map.remove(country, country.getPopulation());
                        LOGGER.info("Removed: " + country.getName() + " " + country.getPopulation());
                    });
                }
                change.getAddedSubList().forEach(country -> {
                    map.put(country, country.getPopulation());
                    LOGGER.info("Added: " + country.getName() + " " + country.getPopulation());
                });
            }
        });

        // change selection in table when clicking on an outlier
        businessControl.getBoxPlotControl().currentElementProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> table.getSelectionModel().select(newValue));
        });

        // change value inside business control upon selection in tableView
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                Country selectedCountry = (Country)newValue;
                businessControl.setValue((int)selectedCountry.getPopulation());
            }
        });
    }

    private void setupBindings() {
        // show currently selected item in table in in the boxplot
        businessControl.getBoxPlotControl().selectedElementProperty().bind(table.getSelectionModel().selectedItemProperty());
    }

}
