package domain;

import java.util.List;
import java.util.ArrayList;

public class Client {
    private Long id;
    private String nom;
    private String adresse;
    private String telephone;
    private boolean estProfessionnel;
    private List<Projet> projets = new ArrayList<>();

    public Client() {}

    public Client(String nom, String adresse, String telephone, boolean estProfessionnel) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.estProfessionnel = estProfessionnel;
    }

    public Client(long id, String nom, String adresse, String telephone, boolean estProfessionnel) {
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public boolean isEstProfessionnel() { return estProfessionnel; }
    public void setEstProfessionnel(boolean estProfessionnel) { this.estProfessionnel = estProfessionnel; }
    public List<Projet> getProjets() { return projets; }
    public void setProjets(List<Projet> projets) { this.projets = projets; }
}
