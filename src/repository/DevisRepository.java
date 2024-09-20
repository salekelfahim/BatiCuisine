package repository;

import config.DataBaseConnection;

import java.sql.Connection;

public class DevisRepository {
    private Connection connection;

    public DevisRepository() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
