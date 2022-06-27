package game.resources;

public enum ResourceTypes {
    Tree,
    Rock;

    public static int size() {
        return values().length;
    }
}
