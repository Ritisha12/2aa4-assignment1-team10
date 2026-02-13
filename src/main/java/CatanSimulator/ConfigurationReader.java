package CatanSimulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// reads config from a text file
// format is "turns: <number>"
public class ConfigurationReader {

    private static final int DEFAULT_TURNS = 100;
    private static final int MAX_TURNS = 8192;

    public int readConfig(String filename) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("turns:")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        int turns = Integer.parseInt(parts[1].trim());
                        if (turns > MAX_TURNS) {
                            System.out.println("Warning: turns exceeds max (" + MAX_TURNS + "), capping.");
                            return MAX_TURNS;
                        }
                        if (turns < 1) {
                            System.out.println("Warning: turns < 1, using default.");
                            return DEFAULT_TURNS;
                        }
                        return turns;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading config: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Bad number in config: " + e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return DEFAULT_TURNS;
    }
}
