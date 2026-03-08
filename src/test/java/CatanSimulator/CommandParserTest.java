package CatanSimulator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    @DisplayName("Parses roll command")
    void parsesRollCommand() {
        Command command = parser.parse("roll");
        assertEquals(CommandType.ROLL, command.getType());
    }

    @Test
    @DisplayName("Parses go command")
    void parsesGoCommand() {
        Command command = parser.parse("go");
        assertEquals(CommandType.GO, command.getType());
    }

    @Test
    @DisplayName("Parses list command")
    void parsesListCommand() {
        Command command = parser.parse("list");
        assertEquals(CommandType.LIST, command.getType());
    }

    @Test
    @DisplayName("Parses build settlement command")
    void parsesBuildSettlementCommand() {
        Command command = parser.parse("build settlement 12");
        assertEquals(CommandType.BUILD_SETTLEMENT, command.getType());
        assertEquals(12, command.getFirstId());
    }

    @Test
    @DisplayName("Parses build city command")
    void parsesBuildCityCommand() {
        Command command = parser.parse("build city 19");
        assertEquals(CommandType.BUILD_CITY, command.getType());
        assertEquals(19, command.getFirstId());
    }

    @Test
    @DisplayName("Parses build road command")
    void parsesBuildRoadCommand() {
        Command command = parser.parse("build road 3, 17");
        assertEquals(CommandType.BUILD_ROAD, command.getType());
        assertEquals(3, command.getFirstId());
        assertEquals(17, command.getSecondId());
    }

    @Test
    @DisplayName("Unknown commands are invalid")
    void rejectsUnknownCommand() {
        Command command = parser.parse("dance");
        assertEquals(CommandType.INVALID, command.getType());
    }

    @Test
    @DisplayName("Malformed build command is invalid")
    void rejectsMalformedBuildCommand() {
        Command command = parser.parse("build road 3 17");
        assertEquals(CommandType.INVALID, command.getType());
    }
}
