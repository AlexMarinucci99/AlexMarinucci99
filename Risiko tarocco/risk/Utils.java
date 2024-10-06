package risk;

import java.nio.file.*;
import org.json.*;
import java.util.*;

public class Utils {
    public static List<Region> getRegionsList(Path path) throws Exception {
        List<Region> regions = new ArrayList<Region>();
        String myFile = Files.readString(path);
        JSONArray JSONregions = new JSONArray(myFile);
        for (int i = 0; i < JSONregions.length(); i++) {
            regions.add(new Region(JSONregions.getJSONObject(i).getString("name")));
        }
        return regions;
    }

    public static List<String> getRegionAdjStringList(Region r, Path path) throws Exception {
        int i;
        String name = r.getName();
        List<String> adjRegions = new ArrayList<String>();
        String myFile = Files.readString(path);
        JSONArray JSONregions = new JSONArray(myFile);
        for (i = 0; i < JSONregions.length(); i++) {
            if (JSONregions.getJSONObject(i).getString("name").equals(name)) {
                JSONregions = (JSONArray) JSONregions.getJSONObject(i).get("adjRegions");
                Iterator<Object> iterator = JSONregions.iterator();
                while (iterator.hasNext()) {
                    adjRegions.add(iterator.next().toString());
                }
                return adjRegions;
            }
        }
        throw new Exception("region not found");
    }

}
