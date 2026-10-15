
import java.util.List;

public final class Grid {

    private final int width;// largeur de la grille
    private final int height;// hauteur de la grille
    private final TileType[][] cells;// tableau 2D contenant toutes les cases

    // Construit la grille à partir d'un tableau 2D de TileType
    public Grid(TileType[][] cells) {
        this.height = cells.length;
        if (height == 0) {
            this.width = 0;
        } else {
            this.width = cells[0].length;
        }
        this.cells = cells;
    }

    // getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
   // Vérifie si une position est danq la grille
    public boolean estDansLaGrille(int x, int y) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }
    // le type de la case à la position (x,y)
    public TileType get(int x, int y) {
        return cells[y][x];
    }

    // setter 
    public void set(int x, int y, TileType t) {
        cells[y][x] = t;
    }

    // cree une copie de la grille pour ne pas modifier l'originale 
    public Grid copieGrid() {
        var copie = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(cells[y], 0, copie[y], 0, width);
        }
        return new Grid(copie);
    }

    // creerDepuisLignes lit les lignes du niveau caractere par caractere,
    // les transforme en TileType[][], enleve @ et . de la grille car ils sont gerés separement et puis cree Grid
    public static Grid creerDepuisLignes(List<String> lignes) {
        if (lignes.isEmpty()) {
            throw new IllegalArgumentException("Niveau vide.");
        }
        //nombre de lignes et longueur de la 1ere ligne 
        int hauteur = lignes.size(); 
        int largeur = lignes.get(0).length();

        var cells = new TileType[hauteur][largeur];

        //Parcourir chaque ligne y
        for (int y = 0; y < hauteur; y++) {
            var row = lignes.get(y);
            // Parcourir chaque caractere de la ligne x
            for (int x = 0; x < largeur; x++) {
                // Convertir le caractère en TileType
                TileType t = TileType.convertirCaractere(row.charAt(x));
                //on ne stocke pas le @ et (.) dans la grille
                if (t == TileType.JOUEUR || t == TileType.OBJECTIF) {
                    t = TileType.SOL_VIDE;
                }
                cells[y][x] = t;
            }
        }
        return new Grid(cells);
    }
}
