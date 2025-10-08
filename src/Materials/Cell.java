package Materials;

public class Cell {
    public Material material;
    public boolean updated;

    public Cell(Material mat) {
        this.material = mat;
        this.updated = true;
    }

    public boolean isEmpty() {
        return material == Material.AIR;
    }
}
