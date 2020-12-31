package managers;

import objects.Bench;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchManager implements Manager, Search, Serializable {

    // use rPath as the key, as it is unique to each Bench
    Map<String, Bench> benchMap = new HashMap<>(5000);

    // used to track changes between local version and online database:
    Map<String, Bench> addMap = new HashMap<>(1000);
    Map<String, Bench> removeMap = new HashMap<>(1000);

    // addMap is used to track new and edited benches
    // removeMap is used to track removed benches (relative to last serialized/synchronization state)

    public void addBench(Bench bench) {
        benchMap.put(bench.getRPath(), bench);
        addMap.put(bench.getRPath(), bench);
    }

    public void removeBench(String rPath) {
        removeMap.put(rPath, benchMap.get(rPath));
        benchMap.remove(rPath);
    }

    // getters

    public String getName(String rPath) {
        return benchMap.get(rPath).getName();
    }

    public List<String> getAllRecipes(String rPath) {
        return benchMap.get(rPath).getAllRecipes();
    }

    public Map<String[], List<String>> getAllRecipesByCategory(String rPath) {
        return benchMap.get(rPath).getAllRecipesByCategory();
    }

    /**
     * Returns the associated profession name for the specified bench, if applicable.
     * Returns null otherwise.
     *
     * @param rPath the relative path of the bench
     * @return the associated profession name
     */
    public String getProfessionName(String rPath) {
        if (benchMap.get(rPath).isProfession()) {
            return benchMap.get(rPath).getProfessionName();
        }
        return null;
    }

    // setters

    /**
     * Sets the profession name identifier of the specified bench.
     *
     * @param rPath the relative path of the bench
     * @param name the identifier of the name (in a LangFile)
     */
    public void setProfessionName(String rPath, String name) {
        benchMap.get(rPath).setProfessionName(name);
    }
}
