package repository;

import config.DataBaseConnection;

import java.sql.Connection;

public class ComposantRepository {
    private Connection connection;

    public ComposantRepository() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
