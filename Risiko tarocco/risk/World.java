package risk;

import java.util.*;
import java.nio.file.Path;

public class World {
    private List<Region> regions;

    public World(String folder) throws Exception {
        Path path = Path.of("risk/" + folder + "/Regions.json");
        regions = Utils.getRegionsList(path);
        for (Region r : regions) {
            List<Region> adjRegions = new ArrayList<Region>();
            for (String s : Utils.getRegionAdjStringList(r, path)) {
                adjRegions.add(this.getRegion(s));
            }
            r.setAdjRegions(adjRegions);
        }
    }

    public Region getRegion(String name) throws Exception {
        for (Region r : regions)
            if (r.getName().equals(name))
                return r;
        throw new Exception("region not present");
    }

    public List<Region> getRegions() {
        return regions;
    }

}
