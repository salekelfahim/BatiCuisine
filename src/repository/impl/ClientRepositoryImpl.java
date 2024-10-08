package repository.impl;

import config.DataBaseConnection;
import domain.Client;
import repository.interfaces.IClientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepositoryImpl implements IClientRepository {
    private Connection connection;

    public ClientRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
    public void save(Client client) {
        String sql = "INSERT INTO clients (nom, adresse, telephone, est_professionnel) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getAdresse());
            pstmt.setString(3, client.getTelephone());
            pstmt.setBoolean(4, client.isEstProfessionnel());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        client.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Client> findById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Client(
                            rs.getLong("id"),
                            rs.getString("nom"),
                            rs.getString("adresse"),
                            rs.getString("telephone"),
                            rs.getBoolean("est_professionnel")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(new Client(
                        rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("telephone"),
                        rs.getBoolean("est_professionnel")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public void update(Client client) {
        String sql = "UPDATE clients SET nom = ?, adresse = ?, telephone = ?, est_professionnel = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getAdresse());
            pstmt.setString(3, client.getTelephone());
            pstmt.setBoolean(4, client.isEstProfessionnel());
            pstmt.setLong(5, client.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Client> findByNom(String nom) throws SQLException {
        String query = "SELECT * FROM clients WHERE nom = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nom);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Client client = new Client(
                            resultSet.getString("nom"),
                            resultSet.getString("adresse"),
                            resultSet.getString("telephone"),
                            resultSet.getBoolean("est_professionnel")
                    );
                    client.setId(resultSet.getLong("id"));
                    return Optional.of(client);
                } else {
                    return Optional.empty();
                }
            }
        }
    }
}


