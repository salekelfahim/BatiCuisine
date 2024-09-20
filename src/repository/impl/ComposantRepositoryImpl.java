package repository.impl;

import config.DataBaseConnection;

import java.sql.Connection;

public class ComposantRepositoryImpl {
    private Connection connection;

    public ComposantRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
