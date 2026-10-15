
public final class Player {

    private int x; // position x du joueur dans la grille
    private int y; // position y du joueur dans la grille
    private int energie;  // énergie restante du joueur
    private int bombes; // nombre explosifs disponibles

  // constructeur :cree un joueur à la position (x,y) avec une energie initiale
    public Player(int x, int y, int energieInitiale) {
        this.x = x;
        this.y = y;
        this.energie = energieInitiale;
        this.bombes = 0;
    }

    // les getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getEnergie() {
        return energie;
    }

    public int getBombes() {
        return bombes;
    }

    // deplacer vers la case (nx,ny)
    public void deplacerVers(int nx, int ny) {
        this.x = nx;
        this.y = ny;
    }

    // Vérifie si le joueur a assez d energie pour jouer
    public boolean energieCheck(int cost) {
        return energie >= cost;
    }
    // apres une action l energie diminue 
    public void consommerEnergie(int cost) {
        energie -= cost;
    }

    public void ajouterEnergie(int amount) {
        energie += amount;
    }

    // le joueur a au moins une bombe
    public boolean peutExploser() {
        return bombes > 0;
    }
  // ajouter une bomb
    public void ajouterExplosif(int amount) {
        bombes += amount;
    }
  // Consomme une bombe (si aucune : erreur)
    public void consomeExplosif() {
        if (bombes <= 0) {
            throw new IllegalStateException("Aucun explosif disponible.");
        }
        bombes--;
    }
}
