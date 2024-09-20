package domain;

import java.math.BigDecimal;

public abstract class Composant {
    private Long id;
    private String nom;
    private String typeComposant;
    private BigDecimal tauxTva;
    private Projet projet;

    public Composant() {}

    public Composant(String nom, String typeComposant, BigDecimal tauxTva, Projet projet) {
        this.nom = nom;
        this.typeComposant = typeComposant;
        this.tauxTva = tauxTva;
        this.projet = projet;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTypeComposant() { return typeComposant; }
    public void setTypeComposant(String typeComposant) { this.typeComposant = typeComposant; }
    public BigDecimal getTauxTva() { return tauxTva; }
    public void setTauxTva(BigDecimal tauxTva) { this.tauxTva = tauxTva; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }

    public abstract BigDecimal calculerCout();
}

