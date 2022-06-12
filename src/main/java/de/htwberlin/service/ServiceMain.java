package de.htwberlin.service;

import de.htwberlin.utils.DbCred;
import de.htwberlin.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ServiceMain {

    private static final Logger L = LoggerFactory.getLogger(ServiceMain.class);

    public static void main(String[] args) {
        ICoolingService cs = new CoolingServiceDao();

        try (Connection connection = JdbcUtils.getConnectionViaDriverManager(DbCred.url, DbCred.user, DbCred.password)) {
            cs.setConnection(connection);
            Integer sampleId = 99;
            Integer diameterInCm = 1;
            cs.transferSample(sampleId, diameterInCm);
        } catch (SQLException e) {
            L.error("Verbindungsaufbau gescheitert", e);
        }
    }
}
