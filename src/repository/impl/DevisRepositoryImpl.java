package repository.impl;

import config.DataBaseConnection;
import domain.Devis;
import repository.interfaces.IDevisRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DevisRepositoryImpl implements IDevisRepository {
    private Connection connection;

    public DevisRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }

    @Override
    public Devis save(Devis devis) {
        String sql = "INSERT INTO devis (montant_estime, date_emission, date_validite, accepte, projet_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setBigDecimal(1, devis.getMontantEstime());
            pstmt.setDate(2, Date.valueOf(devis.getDateEmission()));
            pstmt.setDate(3, Date.valueOf(devis.getDateValidite()));
            pstmt.setBoolean(4, devis.isAccepte());
            pstmt.setLong(5, devis.getProjet().getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating devis failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    devis.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating devis failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devis;
    }

    @Override
    public Optional<Devis> findById(Long id) {
        String sql = "SELECT * FROM devis WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToDevis(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Devis> findAll() {
        List<Devis> devisList = new ArrayList<>();
        String sql = "SELECT * FROM devis";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                devisList.add(mapResultSetToDevis(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devisList;
    }

    @Override
    public Devis update(Devis devis) {
        String sql = "UPDATE devis SET montant_estime = ?, date_emission = ?, date_validite = ?, accepte = ?, projet_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, devis.getMontantEstime());
            pstmt.setDate(2, Date.valueOf(devis.getDateEmission()));
            pstmt.setDate(3, Date.valueOf(devis.getDateValidite()));
            pstmt.setBoolean(4, devis.isAccepte());
            pstmt.setLong(5, devis.getProjet().getId());
            pstmt.setLong(6, devis.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devis;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM devis WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Devis> findByProjetId(Long projetId) {
        List<Devis> devisList = new ArrayList<>();
        String sql = "SELECT * FROM devis WHERE projet_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, projetId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                devisList.add(mapResultSetToDevis(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devisList;
    }

    private Devis mapResultSetToDevis(ResultSet rs) throws SQLException {
        Devis devis = new Devis();
        devis.setId(rs.getLong("id"));
        devis.setMontantEstime(rs.getBigDecimal("montant_estime"));
        devis.setDateEmission(rs.getDate("date_emission").toLocalDate());
        devis.setDateValidite(rs.getDate("date_validite").toLocalDate());
        devis.setAccepte(rs.getBoolean("accepte"));
        return devis;
    }
}