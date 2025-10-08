package Simulator;

import Materials.Cell;
import Materials.Material;
import World.World;

public class Simulator {
    private final World world;

    public Simulator(World w) {
        this.world = w;
    }

    public void tick() {
        for(int y = world.height - 1; y >= 0; y--) {
            for(int x = 0; x < world.width; x++) {
                Cell cell = world.grid[y][x];

                if(cell.updated) continue;
                cell.updated = true;

                switch(cell.material) {
                    case SAND -> updateSand(x, y);
                    case WATER -> updateWater(x, y);
                    case FIRE -> updateFire(x, y);
                    case OIL -> updateOil(x, y);
                }
            }
        }

        for(int y = 0; y < world.height; y++){
            for(int x = 0; x < world.width; x++) {
                world.grid[y][x].updated = false;
            }
        }
    }

    private void updateSand(int x, int y) {
        if (canMoveTo(x, y + 1, Material.SAND)) {
            move(x, y, x, y + 1);
        } else if (isWater(x, y + 1)) {
            swap(x, y, x, y + 1);
        } else {
            boolean moveLeftFirst = Math.random() < 0.5;
            int[] dx = moveLeftFirst ? new int[]{-1, 1} : new int[]{1, -1};
            for (int i : dx) {
                if (canMoveTo(x + i * 2, y + 1, Material.SAND)) {
                    move(x, y, x + i * 2, y + 1);
                    return;
                } else if (isWater(x + i * 2, y + 1)) {
                    swap(x, y, x + i * 2, y + 1);
                    return;
                }
            }
        }
    }

    private void updateWater(int x, int y) {
        if (canMoveTo(x, y + 1, Material.WATER)) {
            move(x, y, x, y + 1);
            return;
        }

        int[] dx = Math.random() < 0.5 ? new int[]{-1, 1} : new int[]{1, -1};

        for (int i : dx) {
            if (isWater(x + i, y + 1) && canMoveTo(x, y + 1, Material.WATER)) {
                swap(x, y, x + i, y + 1);
                return;
            }
        }

        int[] dx2 = Math.random() < 0.5 ? new int[]{-1, -2, 1, 2} : new int[]{1, 2, -1, -2};
        for (int i : dx2) {
            if (canMoveTo(x + i, y, Material.WATER)) {
                move(x, y, x + i, y);
                return;
            }
        }
    }

    private void updateOil(int x, int y) {
        if (canMoveTo(x, y + 1, Material.OIL)) {
            move(x, y, x, y + 1);
            return;
        }

        int[] dx = Math.random() < 0.5 ? new int[]{-1, 1} : new int[]{1, -1};

        for (int i : dx) {
            if (isWater(x + i, y + 1) && canMoveTo(x, y + 1, Material.OIL)) {
                swap(x, y, x + i, y + 1);
                return;
            }
        }

        int[] dx2 = Math.random() < 0.5 ? new int[]{-1, -2, 1, 2} : new int[]{1, 2, -1, -2};
        for (int i : dx2) {
            if (canMoveTo(x + i, y, Material.OIL)) {
                move(x, y, x + i, y);
                return;
            }
        }
    }

    private boolean isWater(int x, int y) {
        if(!world.isBounds(x, y)) return false;
        return world.grid[y][x].material == Material.WATER;
    }

    private void swap(int x1, int y1, int x2, int y2) {
        Cell c1 = world.grid[y1][x1];
        Cell c2 = world.grid[y2][x2];

        Material temp = c1.material;
        c1.material = c2.material;
        c2.material = temp;

        c1.updated = true;
        c2.updated = true;
    }

    private void updateFire(int x, int y) {
        if(Math.random() < 0.1) {
            world.set(x, y, Material.AIR);
        } else {
            tryIgnite(x + 1, y);
            tryIgnite(x - 1, y);
            tryIgnite(x, y + 1);
            tryIgnite(x, y - 1);
        }
    }

    private void tryIgnite(int x, int y) {
        if(!world.isBounds(x, y)) return;
        
        Cell targetCell = world.grid[y][x];
        if(targetCell.material.flammable > 0 && Math.random() < targetCell.material.flammable / 100.0) {
            world.set(x, y, Material.FIRE);
        }
    }

    private boolean canMoveTo(int x, int y, Material movingMaterial) {
        if (!world.isBounds(x, y)) return false;
        Material target = world.grid[y][x].material;
        return target == Material.AIR || movingMaterial.density > target.density;
    }

    private void move(int fromX, int fromY, int toX, int toY) {
        Cell from = world.grid[fromY][fromX];
        Cell to = world.grid[toY][toX];

        to.material = from.material;
        to.updated = true;

        from.material = Material.AIR;
        from.updated = true;
    }
}
