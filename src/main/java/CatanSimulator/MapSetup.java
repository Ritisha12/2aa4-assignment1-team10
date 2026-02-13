package CatanSimulator;

// stores the hardcoded board layout
// tile resources, number tokens, and which nodes surround each tile
public class MapSetup {

    public static final int NUM_TILES = 19;
    public static final int NUM_NODES = 54;

    // resource for each tile (0-18)
    public static final ResourceType[] TILE_RESOURCES = {
        ResourceType.WHEAT,   // 0 - center
        ResourceType.ORE,     // 1
        ResourceType.SHEEP,   // 2
        ResourceType.WOOD,    // 3
        ResourceType.BRICK,   // 4
        ResourceType.WHEAT,   // 5
        ResourceType.SHEEP,   // 6
        ResourceType.WOOD,    // 7
        ResourceType.BRICK,   // 8
        ResourceType.WHEAT,   // 9
        ResourceType.ORE,     // 10
        ResourceType.SHEEP,   // 11
        ResourceType.DESERT,  // 12
        ResourceType.WOOD,    // 13
        ResourceType.BRICK,   // 14
        ResourceType.WHEAT,   // 15
        ResourceType.ORE,     // 16
        ResourceType.SHEEP,   // 17
        ResourceType.WOOD,    // 18
    };

    // number token for each tile
    public static final int[] TILE_NUMBERS = {
        6, 5, 10, 3, 8, 4, 9,
        11, 12, 6, 5, 10, 0, 3, 8, 4, 9, 11, 2
    };

    // for each tile, the 6 surrounding node ids (clockwise)
    // worked these out from the assignment diagram
    public static final int[][] TILE_NODES = {
        { 0,  1,  2,  3,  4,  5},     // tile 0 (center)
        { 6,  7,  0,  5, 14, 13},     // tile 1
        { 8,  9,  1,  0,  7, 19},     // tile 2
        {20,  1,  9, 10, 11,  2},     // tile 3
        {21,  2, 11, 12, 22,  3},     // tile 4
        { 4,  3, 22, 23, 24, 15},     // tile 5
        { 5,  4, 15, 16, 13, 14},     // tile 6
        {29, 27,  6, 13, 28, 26},     // tile 7
        {30, 31,  8, 19,  7,  6},     // tile 8
        {32, 33, 34,  9,  8, 31},     // tile 9
        {20, 34, 35, 36, 10,  9},     // tile 10
        {11, 10, 36, 37, 38, 12},     // tile 11
        {21, 12, 38, 39, 40, 22},     // tile 12 (desert)
        {22, 40, 41, 42, 23, 43},     // tile 13
        {24, 23, 42, 44, 45, 25},     // tile 14
        {16, 15, 24, 25, 46, 47},     // tile 15
        {13, 16, 47, 48, 49, 28},     // tile 16
        {27, 50, 51, 30,  6, 26},     // tile 17
        {52, 53, 32, 31, 30, 51},     // tile 18
    };

    private MapSetup() {} // utility class, don't instantiate
}
