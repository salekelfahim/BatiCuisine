package repository;

import config.DataBaseConnection;

import java.sql.Connection;

public class ProjetRepository {
    private Connection connection;

    public ProjetRepository() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
