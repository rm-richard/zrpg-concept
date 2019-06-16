package rmrichard.learn;

public class Constants {
    public static final float ZOOM = 0.5f;
    public static final int VIEW_WIDTH = (int)(800 * ZOOM);
    public static final int VIEW_HEIGHT = (int)(600 * ZOOM);

    public static final int TILE_SIZE = 32;
    public static final int TILE_COUNT_WIDTH = 200;
    public static final int TILE_COUNT_HEIGHT = 200;

    public static final int MAP_WIDTH = TILE_SIZE * TILE_COUNT_WIDTH;
    public static final int MAP_HEIGHT = TILE_SIZE * TILE_COUNT_HEIGHT;

    public static final int[] BACKGROUND_LAYERS = new int[] {0, 1, 2, 3};
    public static final int[] FOREGROUND_LAYERS = new int[] {4};
}
