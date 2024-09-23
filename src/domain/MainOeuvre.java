package domain;

import java.math.BigDecimal;

public class MainOeuvre extends Composant {
    private BigDecimal tauxHoraire;
    private BigDecimal heuresTravail;
    private BigDecimal productiviteOuvrier;


    public MainOeuvre(String nom, String typeComposant, BigDecimal tauxTva, Projet projet, BigDecimal tauxHoraire, BigDecimal heuresTravail, BigDecimal productiviteOuvrier) {
        super(nom, "MainOeuvre", tauxTva, projet);
        this.tauxHoraire = tauxHoraire;
        this.heuresTravail = heuresTravail;
        this.productiviteOuvrier = productiviteOuvrier;
    }

    public BigDecimal getTauxHoraire() { return tauxHoraire; }
    public void setTauxHoraire(BigDecimal tauxHoraire) { this.tauxHoraire = tauxHoraire; }
    public BigDecimal getHeuresTravail() { return heuresTravail; }
    public void setHeuresTravail(BigDecimal heuresTravail) { this.heuresTravail = heuresTravail; }
    public BigDecimal getProductiviteOuvrier() { return productiviteOuvrier; }
    public void setProductiviteOuvrier(BigDecimal productiviteOuvrier) { this.productiviteOuvrier = productiviteOuvrier; }

    @Override
    public BigDecimal calculerCout() {
        BigDecimal costWithoutTax = tauxHoraire.multiply(heuresTravail).multiply(productiviteOuvrier);
        return costWithoutTax.add(costWithoutTax.multiply(getTauxTva()).divide(BigDecimal.valueOf(100)));
    }
    public BigDecimal calculerCoutSansTVA() {
        return tauxHoraire.multiply(heuresTravail).multiply(productiviteOuvrier);
    }}
