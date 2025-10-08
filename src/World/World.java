package World;

import Materials.Cell;
import Materials.Material;

public class World {
    public final int width;
    public final int height;
    public Cell[][] grid;

    public World(int w, int h) {
        width = w;
        height = h;
        grid = new Cell[h][w];

        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++) {
                grid[y][x] = new Cell(Material.AIR);
            }
        }
    }

    public void set(int x, int y, Material mat) {
        if(isBounds(x, y))
            grid[y][x].material = mat;
    }

    public boolean isBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
