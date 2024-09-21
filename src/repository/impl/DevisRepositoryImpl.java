package repository.impl;

import config.DataBaseConnection;

import java.sql.Connection;

public class DevisRepositoryImpl {
    private Connection connection;

    public DevisRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
