package repository.impl;

import config.DataBaseConnection;

import java.sql.Connection;

public class MainOeuvreRepositoryImpl {
    private Connection connection;

    public MainOeuvreRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
