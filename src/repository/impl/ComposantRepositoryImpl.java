package repository.impl;

import domain.Composant;
import domain.Materiau;
import domain.MainOeuvre;
import domain.Projet;
import repository.interfaces.IComposantRepository;
import config.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComposantRepositoryImpl implements IComposantRepository {
    @Override
    public void addComposant(Composant composant) {
        String query = "INSERT INTO composants (nom, type_composant, taux_tva, projet_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getInstance().getConnection(); // Use instance method
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, composant.getNom());
            stmt.setString(2, composant.getTypeComposant());
            stmt.setBigDecimal(3, composant.getTauxTva());
            stmt.setLong(4, composant.getProjet().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Composant> getAllComposants() {
        List<Composant> composants = new ArrayList<>();
        String query = "SELECT * FROM composants";
        try (Connection conn = DataBaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Composant composant;
                String type = rs.getString("type_composant");
                if ("Materiau".equals(type)) {
                    composant = new Materiau(
                            rs.getString("nom"),
                            rs.getBigDecimal("taux_tva"),
                            fetchProjetById(rs.getLong("projet_id")),
                            rs.getBigDecimal("cout_unitaire"),
                            rs.getBigDecimal("quantite"),
                            rs.getBigDecimal("cout_transport"),
                            rs.getBigDecimal("coefficient_qualite")
                    );
                } else {
                    composant = new MainOeuvre(
                            rs.getString("nom"),
                            rs.getBigDecimal("taux_tva"),
                            fetchProjetById(rs.getLong("projet_id")),
                            rs.getBigDecimal("taux_horaire"),
                            rs.getBigDecimal("heures_travail"),
                            rs.getBigDecimal("productivite_ouvrier")
                    );
                }
                composants.add(composant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composants;
    }

    @Override
    public Composant getComposantById(Long id) {
        String query = "SELECT * FROM composants WHERE id = ?";
        try (Connection conn = DataBaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type_composant");
                if ("Materiau".equals(type)) {
                    return new Materiau(
                            rs.getString("nom"),
                            rs.getBigDecimal("taux_tva"),
                            fetchProjetById(rs.getLong("projet_id")),
                            rs.getBigDecimal("cout_unitaire"),
                            rs.getBigDecimal("quantite"),
                            rs.getBigDecimal("cout_transport"),
                            rs.getBigDecimal("coefficient_qualite")
                    );
                } else {
                    return new MainOeuvre(
                            rs.getString("nom"),
                            rs.getBigDecimal("taux_tva"),
                            fetchProjetById(rs.getLong("projet_id")),
                            rs.getBigDecimal("taux_horaire"),
                            rs.getBigDecimal("heures_travail"),
                            rs.getBigDecimal("productivite_ouvrier")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if not found
    }

    @Override
    public void updateComposant(Composant composant) {
        String query = "UPDATE composants SET nom = ?, type_composant = ?, taux_tva = ?, projet_id = ? WHERE id = ?";
        try (Connection conn = DataBaseConnection.getInstance().getConnection(); // Use instance method
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, composant.getNom());
            stmt.setString(2, composant.getTypeComposant());
            stmt.setBigDecimal(3, composant.getTauxTva());
            stmt.setLong(4, composant.getProjet().getId());
            stmt.setLong(5, composant.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteComposant(Long id) {
        String query = "DELETE FROM composants WHERE id = ?";
        try (Connection conn = DataBaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Materiau> getAllMateriaux() {
        List<Materiau> materiaux = new ArrayList<>();
        String query = "SELECT * FROM composants WHERE type_composant = 'Materiau'";
        try (Connection conn = DataBaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Materiau materiau = new Materiau(
                        rs.getString("nom"),
                        rs.getBigDecimal("taux_tva"),
                        fetchProjetById(rs.getLong("projet_id")),
                        rs.getBigDecimal("cout_unitaire"),
                        rs.getBigDecimal("quantite"),
                        rs.getBigDecimal("cout_transport"),
                        rs.getBigDecimal("coefficient_qualite")
                );
                materiaux.add(materiau);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materiaux;
    }

    @Override
    public List<MainOeuvre> getAllMainOeuvres() {
        List<MainOeuvre> mainOeuvres = new ArrayList<>();
        String query = "SELECT * FROM composants WHERE type_composant = 'MainOeuvre'";
        try (Connection conn = DataBaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                MainOeuvre mainOeuvre = new MainOeuvre(
                        rs.getString("nom"),
                        rs.getBigDecimal("taux_tva"),
                        fetchProjetById(rs.getLong("projet_id")),
                        rs.getBigDecimal("taux_horaire"),
                        rs.getBigDecimal("heures_travail"),
                        rs.getBigDecimal("productivite_ouvrier")
                );
                mainOeuvres.add(mainOeuvre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mainOeuvres;
    }

    private Projet fetchProjetById(Long projetId) {
        ProjetRepositoryImpl projetRepository = new ProjetRepositoryImpl();
        Optional<Projet> projetOptional = projetRepository.findById(projetId);
        return projetOptional.orElse(null);
    }
}