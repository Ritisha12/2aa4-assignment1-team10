package CatanSimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class HumanPlayer extends Player {
    private final BufferedReader input;
    private final PrintStream output;
    private final CommandParser parser;

    public HumanPlayer(int id, String name, PlayerColor color, BufferedReader input, PrintStream output) {
        super(id, name, color);
        this.input = input;
        this.output = output;
        this.parser = new CommandParser();
    }

    public Command readCommand() {
        output.print("command> ");
        output.flush();
        return parser.parse(readLine());
    }

    public void printHand() {
        output.println("Hand: " + getResources());
    }

    public void printMessage(String message) {
        output.println(message);
    }

    private String readLine() {
        try {
            String line = input.readLine();
            if (line == null) {
                throw new IllegalStateException("Input stream closed.");
            }
            return line;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read command.", e);
        }
    }
}
