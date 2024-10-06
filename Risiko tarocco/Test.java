import java.nio.file.Files;
import java.nio.file.Path;

public class Test {
    public static void main(String[] args) throws Exception {
        String myFile = Files.readString(Path.of("risk/json/Regions.json"));
        System.out.println(myFile);
    }
}
