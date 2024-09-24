package repository.impl;

import domain.Composant;
import domain.Materiau;
import domain.MainOeuvre;
import domain.Projet;
import repository.interfaces.IComposantRepository;
import config.DataBaseConnection;
import service.CalculService;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComposantRepositoryImpl implements IComposantRepository {

    private Connection connection;

    public ComposantRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }

    @Override
    public void addComposant(Composant composant) {
        if (composant == null) {
            throw new IllegalArgumentException("Composant cannot be null");
        }

        String materiauQuery = "INSERT INTO materiaux (nom, type_composant, taux_tva, projet_id, cout_unitaire, quantite, cout_transport, coefficient_qualite) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        String mainOeuvreQuery = "INSERT INTO mainOeuvres (nom, type_composant, taux_tva, projet_id, taux_horaire, heures_travail, productivite_ouvrier) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        CalculService calculService = new CalculService();
        BigDecimal coutComposant = BigDecimal.ZERO;

        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseConnection.getInstance().getConnection();
            }

            connection.setAutoCommit(false);

            try (PreparedStatement materiauStmt = connection.prepareStatement(materiauQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement mainOeuvreStmt = connection.prepareStatement(mainOeuvreQuery, Statement.RETURN_GENERATED_KEYS)) {

                if ("Materiau".equalsIgnoreCase(composant.getTypeComposant())) {
                    if (!(composant instanceof Materiau)) {
                        throw new IllegalArgumentException("Composant of type Materiau expected");
                    }
                    Materiau materiau = (Materiau) composant;

                    materiauStmt.setString(1, composant.getNom());
                    materiauStmt.setString(2, composant.getTypeComposant());
                    materiauStmt.setBigDecimal(3, composant.getTauxTva());
                    materiauStmt.setLong(4, materiau.getProjet().getId());
                    materiauStmt.setBigDecimal(5, materiau.getCoutUnitaire());
                    materiauStmt.setBigDecimal(6, materiau.getQuantite());
                    materiauStmt.setBigDecimal(7, materiau.getCoutTransport());
                    materiauStmt.setBigDecimal(8, materiau.getCoefficientQualite());

                    materiauStmt.executeUpdate();
                    ResultSet rs = materiauStmt.getGeneratedKeys();
                    if (rs.next()) {
                        long generatedId = rs.getLong(1);
                        coutComposant = calculService.calculateCostWithVAT(materiau);
                    }                }
                else if ("MainOeuvre".equalsIgnoreCase(composant.getTypeComposant())) {
                    if (!(composant instanceof MainOeuvre)) {
                        throw new IllegalArgumentException("Composant of type MainOeuvre expected");
                    }
                    MainOeuvre mainOeuvre = (MainOeuvre) composant;

                    mainOeuvreStmt.setString(1, composant.getNom());
                    mainOeuvreStmt.setString(2, composant.getTypeComposant());
                    mainOeuvreStmt.setBigDecimal(3, composant.getTauxTva());
                    mainOeuvreStmt.setLong(4, mainOeuvre.getProjet().getId());
                    mainOeuvreStmt.setBigDecimal(5, mainOeuvre.getTauxHoraire());
                    mainOeuvreStmt.setBigDecimal(6, mainOeuvre.getHeuresTravail());
                    mainOeuvreStmt.setBigDecimal(7, mainOeuvre.getProductiviteOuvrier());

                    mainOeuvreStmt.executeUpdate();
                    ResultSet rs = mainOeuvreStmt.getGeneratedKeys();
                    if (rs.next()) {
                        long generatedId = rs.getLong(1);
                        coutComposant = calculService.calculateCostWithVAT(mainOeuvre);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid component type: " + composant.getTypeComposant());
                }

                Projet projet = composant.getProjet();
                BigDecimal existingCoutTotal = projet.getCoutTotal();
                projet.setCoutTotal(existingCoutTotal.add(coutComposant));

                String updateProjetSql = "UPDATE projets SET cout_total = ? WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateProjetSql)) {
                    updateStmt.setBigDecimal(1, projet.getCoutTotal());
                    updateStmt.setLong(2, projet.getId());
                    updateStmt.executeUpdate();
                }

                connection.commit();
            } catch (SQLException | IllegalArgumentException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding composant", e);
        }
    }


    @Override
    public List<Composant> getAllComposants() {
        List<Composant> composants = new ArrayList<>();
        String query = "SELECT * FROM composants";
        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseConnection.getInstance().getConnection();
            }
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Composant composant;
                    String type = rs.getString("type_composant");
                    if ("Materiau".equals(type)) {
                        composant = new Materiau(
                                rs.getString("nom"),
                                rs.getString("type_composant"),
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
                                rs.getString("type_composant"),
                                rs.getBigDecimal("taux_tva"),
                                fetchProjetById(rs.getLong("projet_id")),
                                rs.getBigDecimal("taux_horaire"),
                                rs.getBigDecimal("heures_travail"),
                                rs.getBigDecimal("productivite_ouvrier")
                        );
                    }
                    composants.add(composant);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composants;
    }

    @Override
    public Composant getComposantById(Long id) {
        String query = "SELECT * FROM composants WHERE id = ?";
        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseConnection.getInstance().getConnection();
            }
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String type = rs.getString("type_composant");
                    if ("Materiau".equals(type)) {
                        return new Materiau(
                                rs.getString("nom"),
                                rs.getString("type_composant"),
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
                                rs.getString("type_composant"),
                                rs.getBigDecimal("taux_tva"),
                                fetchProjetById(rs.getLong("projet_id")),
                                rs.getBigDecimal("taux_horaire"),
                                rs.getBigDecimal("heures_travail"),
                                rs.getBigDecimal("productivite_ouvrier")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateComposant(Composant composant) {
        String query = "UPDATE composants SET nom = ?, type_composant = ?, taux_tva = ?, projet_id = ? WHERE id = ?";
        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseConnection.getInstance().getConnection();
            }
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, composant.getNom());
                stmt.setString(2, composant.getTypeComposant());
                stmt.setBigDecimal(3, composant.getTauxTva());
                stmt.setLong(4, composant.getProjet().getId());
                stmt.setLong(5, composant.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteComposant(Long id) {
        String query = "DELETE FROM composants WHERE id = ?";
        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseConnection.getInstance().getConnection();
            }
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Materiau> getAllMateriaux() {
        List<Materiau> materiaux = new ArrayList<>();
        String query = "SELECT * FROM composants WHERE type_composant = 'Materiau'";
        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseConnection.getInstance().getConnection();
            }
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Materiau materiau = new Materiau(
                            rs.getString("nom"),
                            rs.getString("type_composant"),
                            rs.getBigDecimal("taux_tva"),
                            fetchProjetById(rs.getLong("projet_id")),
                            rs.getBigDecimal("cout_unitaire"),
                            rs.getBigDecimal("quantite"),
                            rs.getBigDecimal("cout_transport"),
                            rs.getBigDecimal("coefficient_qualite")
                    );
                    materiaux.add(materiau);
                }
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
        try {
            if (connection == null || connection.isClosed()) {
                connection = DataBaseConnection.getInstance().getConnection();
            }
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    MainOeuvre mainOeuvre = new MainOeuvre(
                            rs.getString("nom"),
                            rs.getString("type_composant"),
                            rs.getBigDecimal("taux_tva"),
                            fetchProjetById(rs.getLong("projet_id")),
                            rs.getBigDecimal("taux_horaire"),
                            rs.getBigDecimal("heures_travail"),
                            rs.getBigDecimal("productivite_ouvrier")
                    );
                    mainOeuvres.add(mainOeuvre);
                }
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