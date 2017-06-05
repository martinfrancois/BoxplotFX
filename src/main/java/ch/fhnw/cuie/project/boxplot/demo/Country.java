package ch.fhnw.cuie.project.boxplot.demo;

import java.util.Objects;

/**
 * Created by François Martin on 05.06.17.
 */
public class Country {

    private final String name;
    private final double population;

    public Country(String name, double population){
        this.name = name;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public double getPopulation() {
        return population;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Double.compare(country.population, population) == 0 &&
                Objects.equals(name, country.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, population);
    }
}
