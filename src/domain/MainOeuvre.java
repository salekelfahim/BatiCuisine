package domain;

import java.math.BigDecimal;

public class MainOeuvre extends Composant {
    private BigDecimal tauxHoraire;
    private BigDecimal heuresTravail;
    private BigDecimal productiviteOuvrier;

    // Constructors, getters, and setters
    public MainOeuvre() {}

    public MainOeuvre(String nom, BigDecimal tauxTva, Projet projet, BigDecimal tauxHoraire, BigDecimal heuresTravail, BigDecimal productiviteOuvrier) {
        super(nom, "MainOeuvre", tauxTva, projet);
        this.tauxHoraire = tauxHoraire;
        this.heuresTravail = heuresTravail;
        this.productiviteOuvrier = productiviteOuvrier;
    }

    // Getters and setters
    public BigDecimal getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(BigDecimal tauxHoraire) { this.tauxHoraire = tauxHoraire; }
    public BigDecimal getHeuresTravail() { return heuresTravail; }
    public void setHeuresTravail(BigDecimal heuresTravail) { this.heuresTravail = heuresTravail; }
    public BigDecimal getProductiviteOuvrier() { return productiviteOuvrier; }
    public void setProductiviteOuvrier(BigDecimal productiviteOuvrier) { this.productiviteOuvrier = productiviteOuvrier; }

    @Override
    public BigDecimal calculerCout() {
        return tauxHoraire.multiply(heuresTravail).multiply(productiviteOuvrier);
    }
}
