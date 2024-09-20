package repository;

import config.DataBaseConnection;

import java.sql.Connection;

public class ClientRepository {
    private Connection connection;

    public ClientRepository() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
