package repository;

import config.DataBaseConnection;

import java.sql.Connection;

public class MateriauRepository {
    private Connection connection;

    public MateriauRepository() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
