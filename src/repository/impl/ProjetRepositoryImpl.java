package repository.impl;

import config.DataBaseConnection;
import domain.Client;
import domain.EtatProjet;
import domain.Projet;
import repository.interfaces.IProjetRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetRepositoryImpl implements IProjetRepository {
    private Connection connection;

    public ProjetRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }

    @Override
    public Projet save(Projet projet) {
        String sql = "INSERT INTO projets (nom_projet, marge_beneficiaire, cout_total, etat_projet, client_id) VALUES (?, ?, ?, CAST(? AS etatprojet), ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, projet.getNomProjet());
            pstmt.setBigDecimal(2, projet.getMargeBeneficiaire());
            pstmt.setBigDecimal(3, BigDecimal.ZERO);
            pstmt.setString(4, projet.getEtatProjet().name());
            pstmt.setLong(5, projet.getClient().getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating projet failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    projet.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating projet failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projet;
    }

    @Override
    public Optional<Projet> findById(Long id) {
        String sql = "SELECT * FROM projets WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProjet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Projet> findAll() {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM projets";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                projets.add(mapResultSetToProjet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

    @Override
    public Projet update(Projet projet) {
        String sql = "UPDATE projets SET nom_projet = ?, marge_beneficiaire = ?, cout_total = ?, etat_projet = ?, client_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, projet.getNomProjet());
            pstmt.setBigDecimal(2, projet.getMargeBeneficiaire());
            pstmt.setBigDecimal(3, projet.getCoutTotal());
            pstmt.setString(4, projet.getEtatProjet().name());
            pstmt.setLong(5, projet.getClient().getId());
            pstmt.setLong(6, projet.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projet;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM projets WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Projet> findByClientId(Long clientId) {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM projets WHERE client_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projets.add(mapResultSetToProjet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }

    private Projet mapResultSetToProjet(ResultSet rs) throws SQLException {
        Projet projet = new Projet();
        projet.setId(rs.getLong("id"));
        projet.setNomProjet(rs.getString("nom_projet"));
        projet.setMargeBeneficiaire(BigDecimal.valueOf(rs.getDouble("marge_beneficiaire")));
        projet.setCoutTotal(BigDecimal.valueOf(rs.getDouble("cout_total")));
        projet.setEtatProjet(EtatProjet.valueOf(rs.getString("etat_projet")));
        Client client = new Client();
        client.setId(rs.getLong("client_id"));
        projet.setClient(client);

        return projet;
    }
}
