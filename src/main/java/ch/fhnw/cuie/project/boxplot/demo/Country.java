package ch.fhnw.cuie.project.boxplot.demo;

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
}
