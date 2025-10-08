package Materials;

public enum Material {
    AIR(false, 0, 0),
    STONE(false, 1, 0),
    SAND(true, 2, 0),
    WATER(true, 0, -1),
    OIL(true, 1, 80),
    ACID(true, 1, 20),
    FIRE(true, 1, 100),
    FLOOR(false, 5, 0);


    public final boolean isDynamic;
    public final int density;
    public final int flammable;

    Material(boolean dynamic, int density, int flammable) {
        this.isDynamic = dynamic;
        this.density = density;
        this.flammable = flammable;
    }
}
