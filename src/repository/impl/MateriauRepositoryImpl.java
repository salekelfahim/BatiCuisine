package repository.impl;

import config.DataBaseConnection;

import java.sql.Connection;

public class MateriauRepositoryImpl {
    private Connection connection;

    public MateriauRepositoryImpl() {
        this.connection = DataBaseConnection.getInstance().getConnection();
    }
}
