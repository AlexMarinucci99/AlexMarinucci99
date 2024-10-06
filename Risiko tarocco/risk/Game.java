package risk;

import java.util.Scanner;

enum State {
    ON, OFF
}

public class Game {
    private State state = State.OFF;
    private int numPlayers;

    public void gameStart() {
        Scanner scanner = new Scanner(System.in);
        state = State.ON;
        numPlayers = Integer.parseInt(scanner.nextLine());
        scanner.close();
    }
}
