package ch.fhnw.cuie.project.boxplot.demo;

import ch.fhnw.cuie.project.boxplot.BusinessControl;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private BusinessControl boxPlotControl;

    private ObservableList<Country> countries = FXCollections.observableArrayList();

    private TableView<Country> table = new TableView<>(countries);

    public DemoPane() {
        initCountries();
        initializeControls();
        layoutControls();
        setupValueChangeListeners();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        boxPlotControl = new BusinessControl();

        TableColumn countryCol = new TableColumn("Country");
        TableColumn populationCol = new TableColumn("Population");

        countryCol.setCellValueFactory(
                new PropertyValueFactory<Country,String>("name")
        );

        populationCol.setCellValueFactory(
                new PropertyValueFactory<Country,String>("population")
        );

        table.getColumns().addAll(countryCol, populationCol);

    }

    private void initCountries(){
        // setup csvparser
        RowListProcessor rowProcessor = new RowListProcessor();
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        File csv = new File(Thread.currentThread().getContextClassLoader().getResource("countries_population.csv").getFile());
        parser.parse(csv);

        // initialize test data
        List<String[]> rows = rowProcessor.getRows();
        countries.clear();
        rows.forEach(strings ->
                countries.add(
                        new Country(strings[0], Double.valueOf(strings[1]) )
                )
        );
    }

    private void layoutControls() {
        setCenter(boxPlotControl);
        setRight(table);
    }

    private void setupValueChangeListeners() {
    }

    private void setupBindings() {

    }

}
