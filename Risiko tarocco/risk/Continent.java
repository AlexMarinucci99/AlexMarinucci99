package risk;

import java.util.*;

// i nomi dei continenti sono Maiuscoli
public class Continent {
    private List<Region> regions;
    private int bonus;
    private Player propertyOf;
    private String name;

    public Continent(int bonus, String name, Region... regions) {
        this.propertyOf = null;
        this.bonus = bonus;
        this.name = name;
        this.regions = new ArrayList<Region>();
        for (Region r : regions)
            this.regions.add(r);
    }

    public int getBonus() {
        return bonus;
    }

    public void setPropertyOf() {
        Player property0 = regions.get(0).getPropertyOf();
        for (Region r : regions)
            if (r.getPropertyOf() != property0) {
                propertyOf = null;
                return;
            }
        propertyOf = property0;
    }

    // returns null if is not unique
    public Player getPropertyOf() {
        return propertyOf;
    }

    public String getName() {
        return name;
    }
}
