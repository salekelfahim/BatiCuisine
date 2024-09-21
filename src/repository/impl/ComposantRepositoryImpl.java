package repository.impl;

import config.DataBaseConnection;
import domain.Composant;
import repository.interfaces.IComposantRepository;

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
    public Composant save(Composant composant) {
        String sql = "INSERT INTO composants (nom, type_composant, taux_tva, projet_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, composant.getNom());
            pstmt.setString(2, composant.getTypeComposant());
            pstmt.setBigDecimal(3, composant.getTauxTva());
            pstmt.setLong(4, composant.getProjet().getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating composant failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    composant.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating composant failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composant;
    }

    @Override
    public Optional<Composant> findById(Long id) {
        String sql = "SELECT * FROM composants WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToComposant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Composant> findAll() {
        List<Composant> composants = new ArrayList<>();
        String sql = "SELECT * FROM composants";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                composants.add(mapResultSetToComposant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composants;
    }

    @Override
    public List<Composant> findByProjetId(Long projetId) {
        List<Composant> composants = new ArrayList<>();
        String sql = "SELECT * FROM composants WHERE projet_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, projetId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                composants.add(mapResultSetToComposant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composants;
    }

    @Override
    public Composant update(Composant composant) {
        String sql = "UPDATE composants SET nom = ?, type_composant = ?, taux_tva = ?, projet_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, composant.getNom());
            pstmt.setString(2, composant.getTypeComposant());
            pstmt.setBigDecimal(3, composant.getTauxTva());
            pstmt.setLong(4, composant.getProjet().getId());
            pstmt.setLong(5, composant.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return composant;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM composants WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Composant mapResultSetToComposant(ResultSet rs) throws SQLException {
        Composant composant = new Composant();
        composant.setId(rs.getLong("id"));
        composant.setNom(rs.getString("nom"));
        composant.setTypeComposant(rs.getString("type_composant"));
        composant.setTauxTva(rs.getBigDecimal("taux_tva"));
        composant.setProjet(composant.getProjet());
        return composant;
    }
}
