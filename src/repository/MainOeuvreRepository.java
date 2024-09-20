package repository;

import config.DataBaseConnection;

import java.sql.Connection;

public class MainOeuvreRepository {
    private Connection connection;

    public MainOeuvreRepository() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
