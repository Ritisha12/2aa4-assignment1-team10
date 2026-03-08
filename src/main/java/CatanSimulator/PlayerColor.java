package CatanSimulator;

public enum PlayerColor {
    RED,
    BLUE,
    ORANGE,
    WHITE;

    public static PlayerColor forPlayerId(int id) {
        PlayerColor[] colors = values();
        return colors[Math.floorMod(id, colors.length)];
    }
}
