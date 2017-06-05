package ch.fhnw.cuie.project.boxplot.demo;

import ch.fhnw.cuie.project.boxplot.BusinessControl;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.util.List;

/**
 * @author Dieter Holz
 */
public class DemoPane extends BorderPane {
    private BusinessControl<Country> businessControl;

    private ObservableList<Country> countries = FXCollections.observableArrayList();

    private ObservableMap<Country, Double> map = FXCollections.observableHashMap();

    private TableView<Country> table = new TableView<>(countries);

    public DemoPane() {
        setupValueChangeListeners();
        initCountries();

        initializeControls();
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

        populationCol.setCellValueFactory(
                new PropertyValueFactory<Country, String>("population")
        );

        table.getColumns().addAll(countryCol, populationCol);

    }

    private void initCountries() {
        // setup csvparser
        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        File csv = new File(Thread.currentThread().getContextClassLoader().getResource("countries_population_simple.csv").getFile());
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
                    change.getRemoved().forEach(country -> map.remove(country, country.getPopulation()));
                }
                change.getAddedSubList().forEach(country -> map.put(country, country.getPopulation()));
            }
        });
    }

    private void setupBindings() {

    }

}
