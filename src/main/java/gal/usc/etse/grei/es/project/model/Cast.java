package gal.usc.etse.grei.es.project.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cast extends Person {
    private String character;

    public Cast() {
    }

    public Cast(String id, String name, String country, String picture, String biography, Date birthday, Date deathday, String character) {
        super(id, name, country, picture, biography, birthday, deathday);
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    public Cast setCharacter(String character) {
        this.character = character;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cast cast = (Cast) o;
        return Objects.equals(character, cast.character);
    }

    @Override
    public int hashCode() {
        return Objects.hash(character);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Cast.class.getSimpleName() + "[", "]")
                .add("character='" + character + "'")
                .toString();
    }
}
