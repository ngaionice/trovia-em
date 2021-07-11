package datamodel.objects;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.*;

public class Bench extends Observable implements Article, ArticleTable {

    StringProperty name; // the string identifier of the bench's name
    StringProperty rPath;
    MapProperty<List<String>, ObservableList<String>> categories; // key: [category path, category #]; value: list of recipe file names

    /**
     * The name property of the profession. The value wrapped inside is nullable.
     */
    StringProperty professionName; // the value in the property can be null

    public Bench(String name, String rPath, Map<String[], List<String>> categories, String professionName) {
        this.name = new SimpleStringProperty(name);
        this.rPath = new SimpleStringProperty(rPath);
        this.professionName = new SimpleStringProperty(professionName);

        ObservableMap<List<String>, ObservableList<String>> tempMap = FXCollections.observableHashMap();
        categories.forEach((key, value) -> tempMap.put(Arrays.asList(key), FXCollections.observableArrayList(value)));
        this.categories = new SimpleMapProperty<>(tempMap);
    }

    public final void setName(String name) {
        this.name.set(name);
        notifyObservers();
    }

    public final void setRPath(String rPath) {
        this.rPath.set(rPath);
        notifyObservers();
    }

    public final void setProfessionName(String professionName) {
        this.professionName.set(professionName);
        notifyObservers();
    }

    public final void setCategories(ObservableMap<List<String>, ObservableList<String>> categories) {
        this.categories.set(categories);
        notifyObservers();
    }

    public final void updateCategory(List<String> key, ObservableList<String> value) {
        categories.getValue().put(key, value);
        notifyObservers();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty rPathProperty() {
        return rPath;
    }

    public MapProperty<List<String>, ObservableList<String>> categoriesProperty() {
        return categories;
    }

    public StringProperty professionNameProperty() {
        return professionName;
    }

    public String getName() {
        return name.get();
    }

    public String getRPath() {
        return rPath.get();
    }

    public ObservableMap<List<String>, ObservableList<String>> getCategories() {
        return categories.get();
    }

    public String getProfessionName() {
        return professionName.get();
    }
}