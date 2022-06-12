package de.htwberlin.service;

import de.htwberlin.exceptions.CoolingSystemException;
import de.htwberlin.exceptions.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Tray {
    private Integer trayId;
    private Integer diameterInCm;
    private Integer capacity;
    private Timestamp expirationDate;

    private static final Logger L = LoggerFactory.getLogger(CoolingServiceDao.class);
    private Connection connection = null;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @SuppressWarnings("unused")
    private Connection useConnection() {
        if (connection == null) {
            throw new CoolingSystemException("Service has no connection");
        }
        return connection;
    }

    public Integer getTrayId() {
        return trayId;
    }

    public void setTrayId(Integer trayId) {
        this.trayId = trayId;
    }

    public Integer getDiameterInCm() {
        return diameterInCm;
    }

    public void setDiameterInCm(Integer diameterInCm) {
        this.diameterInCm = diameterInCm;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void insert() {

    }

    public void update() {
        String sql = String.join(" ", "update tray",
                                    "set expirationDate = ?",
                                    "where trayId = ?");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, this.expirationDate);
            stmt.setInt(2, this.trayId);
            stmt.execute();
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
    }

    public void delete() {

    }
}
