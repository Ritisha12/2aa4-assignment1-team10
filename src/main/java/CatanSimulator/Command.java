package CatanSimulator;

public class Command {
    private final CommandType type;
    private final Integer firstId;
    private final Integer secondId;
    private final String rawInput;

    private Command(CommandType type, Integer firstId, Integer secondId, String rawInput) {
        this.type = type;
        this.firstId = firstId;
        this.secondId = secondId;
        this.rawInput = rawInput;
    }

    public static Command simple(CommandType type, String rawInput) {
        return new Command(type, null, null, rawInput);
    }

    public static Command withOneId(CommandType type, int firstId, String rawInput) {
        return new Command(type, firstId, null, rawInput);
    }

    public static Command withTwoIds(CommandType type, int firstId, int secondId, String rawInput) {
        return new Command(type, firstId, secondId, rawInput);
    }

    public static Command invalid(String rawInput) {
        return new Command(CommandType.INVALID, null, null, rawInput);
    }

    public CommandType getType() {
        return type;
    }

    public Integer getFirstId() {
        return firstId;
    }

    public Integer getSecondId() {
        return secondId;
    }

    public String getRawInput() {
        return rawInput;
    }
}
