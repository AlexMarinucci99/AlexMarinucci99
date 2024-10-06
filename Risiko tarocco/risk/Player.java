package risk;

class Colors {
    private static String[] colors = new String[] { "BLUE", "RED", "YELLOW", "BLACK", "GREEN", "PURPLE" };
    private static int i = 0;

    protected static String getNextColor() throws Exception {
        if (i < 6)
            return colors[i++];
        else
            throw new Exception("Too many players");
    }

    protected static void reset() {
        i = 0;
    }
}

public class Player {
    private String name;
    private String color;

    Player(String name) {
        try {
            color = Colors.getNextColor();
        } catch (Exception e) {
        }
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void play() {
        System.out.println("played");
    }
}
