package domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Devis {
    private Long id;
    private BigDecimal montantEstime;
    private LocalDate dateEmission;
    private LocalDate dateValidite;
    private boolean accepte;
    private Projet projet;

    public Devis() {}

    public Devis(BigDecimal montantEstime, LocalDate dateEmission, LocalDate dateValidite, boolean accepte, Projet projet) {
        this.montantEstime = montantEstime;
        this.dateEmission = dateEmission;
        this.dateValidite = dateValidite;
        this.accepte = accepte;
        this.projet = projet;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getMontantEstime() { return montantEstime; }
    public void setMontantEstime(BigDecimal montantEstime) { this.montantEstime = montantEstime; }
    public LocalDate getDateEmission() { return dateEmission; }
    public void setDateEmission(LocalDate dateEmission) { this.dateEmission = dateEmission; }
    public LocalDate getDateValidite() { return dateValidite; }
    public void setDateValidite(LocalDate dateValidite) { this.dateValidite = dateValidite; }
    public boolean isAccepte() { return accepte; }
    public void setAccepte(boolean accepte) { this.accepte = accepte; }
    public Projet getProjet() { return projet; }
    public void setProjet(Projet projet) { this.projet = projet; }
}