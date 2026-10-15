
public enum Direction {
    UP(0, -1, 'z'),
    LEFT(-1, 0, 'q'),
    DOWN(0, 1, 's'),
    RIGHT(1, 0, 'd');

    private final int dx;
    private final int dy;
    private final char commande;

    Direction(int dx, int dy, char commande) {
        this.dx = dx;
        this.dy = dy;
        this.commande = commande;
    }

    // les getters
    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public char getCommande() {
        return commande;
    }

    // Conversion caractere -> commande 
    public static Direction depuisCommande(char c) {
        return switch (c) {
            case 'z' ->
                UP;
            case 'q' ->
                LEFT;
            case 's' ->
                DOWN;
            case 'd' ->
                RIGHT;
            default ->
                null;
        };
    }
}
