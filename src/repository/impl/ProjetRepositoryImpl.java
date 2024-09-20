package repository.impl;

import config.DataBaseConnection;

import java.sql.Connection;

public class ProjetRepositoryImpl {
    private Connection connection;

    public ProjetRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
