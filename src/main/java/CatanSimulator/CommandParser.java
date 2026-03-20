package CatanSimulator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    private static final Pattern ROLL_PATTERN =
        Pattern.compile("^\\s*roll\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern GO_PATTERN =
        Pattern.compile("^\\s*go\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIST_PATTERN =
        Pattern.compile("^\\s*list\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_SETTLEMENT_PATTERN =
        Pattern.compile("^\\s*build\\s+settlement\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_CITY_PATTERN =
        Pattern.compile("^\\s*build\\s+city\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BUILD_ROAD_PATTERN =
        Pattern.compile("^\\s*build\\s+road\\s+(\\d+)\\s*,\\s*(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    // NEW: undo and redo patterns
    private static final Pattern UNDO_PATTERN =
        Pattern.compile("^\\s*undo\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern REDO_PATTERN =
        Pattern.compile("^\\s*redo\\s*$", Pattern.CASE_INSENSITIVE);

    public Command parse(String input) {
        String rawInput = input == null ? "" : input;

        if (ROLL_PATTERN.matcher(rawInput).matches()) {
            return Command.simple(CommandType.ROLL, rawInput);
        }
        if (GO_PATTERN.matcher(rawInput).matches()) {
            return Command.simple(CommandType.GO, rawInput);
        }
        if (LIST_PATTERN.matcher(rawInput).matches()) {
            return Command.simple(CommandType.LIST, rawInput);
        }
        if (UNDO_PATTERN.matcher(rawInput).matches()) {
            return Command.simple(CommandType.UNDO, rawInput);
        }
        if (REDO_PATTERN.matcher(rawInput).matches()) {
            return Command.simple(CommandType.REDO, rawInput);
        }

        Matcher settlementMatcher = BUILD_SETTLEMENT_PATTERN.matcher(rawInput);
        if (settlementMatcher.matches()) {
            return Command.withOneId(
                CommandType.BUILD_SETTLEMENT,
                Integer.parseInt(settlementMatcher.group(1)),
                rawInput
            );
        }

        Matcher cityMatcher = BUILD_CITY_PATTERN.matcher(rawInput);
        if (cityMatcher.matches()) {
            return Command.withOneId(
                CommandType.BUILD_CITY,
                Integer.parseInt(cityMatcher.group(1)),
                rawInput
            );
        }

        Matcher roadMatcher = BUILD_ROAD_PATTERN.matcher(rawInput);
        if (roadMatcher.matches()) {
            return Command.withTwoIds(
                CommandType.BUILD_ROAD,
                Integer.parseInt(roadMatcher.group(1)),
                Integer.parseInt(roadMatcher.group(2)),
                rawInput
            );
        }

        return Command.invalid(rawInput);
    }

    public boolean validateSyntax(String input) {
        return !parse(input).getType().equals(CommandType.INVALID);
    }
}
