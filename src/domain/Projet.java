package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Projet {
    private Long id;
    private String nomProjet;
    private BigDecimal margeBeneficiaire;
    private BigDecimal coutTotal;
    private EtatProjet etatProjet;
    private Client client;
    private List<Composant> composants = new ArrayList<>();
    private Devis devis;

    public Projet() {}

    public Projet(String nomProjet, BigDecimal margeBeneficiaire, BigDecimal coutTotal, EtatProjet etatProjet, Client client) {
        this.nomProjet = nomProjet;
        this.margeBeneficiaire = margeBeneficiaire;
        this.coutTotal = coutTotal;
        this.etatProjet = etatProjet;
        this.client = client;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomProjet() { return nomProjet; }
    public void setNomProjet(String nomProjet) { this.nomProjet = nomProjet; }
    public BigDecimal getMargeBeneficiaire() { return margeBeneficiaire; }
    public void setMargeBeneficiaire(BigDecimal margeBeneficiaire) { this.margeBeneficiaire = margeBeneficiaire; }
    public BigDecimal getCoutTotal() { return coutTotal; }
    public void setCoutTotal(BigDecimal coutTotal) { this.coutTotal = coutTotal; }
    public EtatProjet getEtatProjet() { return etatProjet; }
    public void setEtatProjet(EtatProjet etatProjet) { this.etatProjet = etatProjet; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public List<Composant> getComposants() { return composants; }
    public void setComposants(List<Composant> composants) { this.composants = composants; }
    public Devis getDevis() { return devis; }
    public void setDevis(Devis devis) { this.devis = devis; }

    @Override
    public String toString() {
        return "Projet{" +
                "id=" + id +
                ", nomProjet='" + nomProjet + '\'' +
                ", margeBeneficiaire=" + margeBeneficiaire +
                ", coutTotal=" + coutTotal +
                ", etatProjet=" + etatProjet +
                ", client=" + client +
                ", composants=" + composants +
                ", devis=" + devis +
                '}';
    }
}