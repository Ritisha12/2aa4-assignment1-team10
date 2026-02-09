import java.io.*;

public class ConfigurationReader {
    
    public int readConfig(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Parse "turns: value" format
                if (line.startsWith("turns:")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        try {
                            int turns = Integer.parseInt(parts[1].trim());
                            reader.close();
                            
                            // Enforce maximum of 8192 rounds
                            if (turns > 8192) {
                                System.out.println("Warning: turns exceeds maximum (8192). Using 8192.");
                                return 8192;
                            }
                            if (turns < 1) {
                                System.out.println("Warning: turns less than 1. Using default (100).");
                                return 100;
                            }
                            
                            return turns;
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing turns value: " + e.getMessage());
                        }
                    }
                }
            }
            
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found: " + filename);
            System.err.println("Using default value of 100 rounds.");
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
        }
        
        // Default value
        return 100;
    }
}
