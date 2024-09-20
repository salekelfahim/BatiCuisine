package domain;

import java.math.BigDecimal;

public class Materiau extends Composant {
    private BigDecimal coutUnitaire;
    private BigDecimal quantite;
    private BigDecimal coutTransport;
    private BigDecimal coefficientQualite;

    public Materiau() {}

    public Materiau(String nom, BigDecimal tauxTva, Projet projet, BigDecimal coutUnitaire, BigDecimal quantite, BigDecimal coutTransport, BigDecimal coefficientQualite) {
        super(nom, "Materiau", tauxTva, projet);
        this.coutUnitaire = coutUnitaire;
        this.quantite = quantite;
        this.coutTransport = coutTransport;
        this.coefficientQualite = coefficientQualite;
    }

    public BigDecimal getCoutUnitaire() { return coutUnitaire; }
    public void setCoutUnitaire(BigDecimal coutUnitaire) { this.coutUnitaire = coutUnitaire; }
    public BigDecimal getQuantite() { return quantite; }
    public void setQuantite(BigDecimal quantite) { this.quantite = quantite; }
    public BigDecimal getCoutTransport() { return coutTransport; }
    public void setCoutTransport(BigDecimal coutTransport) { this.coutTransport = coutTransport; }
    public BigDecimal getCoefficientQualite() { return coefficientQualite; }
    public void setCoefficientQualite(BigDecimal coefficientQualite) { this.coefficientQualite = coefficientQualite; }

    @Override
    public BigDecimal calculerCout() {
        return coutUnitaire.multiply(quantite).multiply(coefficientQualite).add(coutTransport);
    }
}
