package com.github.martinfrancois.demo;

import java.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by François Martin on 05.06.17.
 */
public class Country {

    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty population = new SimpleDoubleProperty();

    public Country(String name, double population){
        setName(name);
        setPopulation(population);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getPopulation() {
        return population.get();
    }

    public DoubleProperty populationProperty() {
        return population;
    }

    public void setPopulation(double population) {
        this.population.set(population);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(name, country.name) &&
                Objects.equals(population, country.population);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, population);
    }

    @Override
    public String toString(){
        return name.get();
    }
}
