package gal.usc.etse.grei.es.project.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Producer {
    private String name;
    private String logo;
    private String country;

    public Producer() {
    }

    public Producer(String name, String logo, String country) {
        this.name = name;
        this.logo = logo;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public Producer setName(String name) {
        this.name = name;
        return this;
    }

    public String getLogo() {
        return logo;
    }

    public Producer setLogo(String logo) {
        this.logo = logo;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Producer setCountry(String country) {
        this.country = country;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producer producer = (Producer) o;
        return Objects.equals(name, producer.name) && Objects.equals(logo, producer.logo) && Objects.equals(country, producer.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, logo, country);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Producer.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("logo='" + logo + "'")
                .add("country='" + country + "'")
                .toString();
    }
}
