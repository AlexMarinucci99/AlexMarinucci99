package risk;

import java.util.*;

enum Colors {
    BLUE, RED, YELLOW, BLACK, GREEN, PURPLE;

    Colors[] usedColors;
    private static final Random prc = new Random();

    public static Colors pickNewColor() {
        Colors[] colors = values();
        return colors[prc.nextInt(colors.length)];
    }
}

public class EnumPlayer {
    private String name;
    private Colors color;

    public Colors getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void play() {
        System.out.println("played");
    }
}