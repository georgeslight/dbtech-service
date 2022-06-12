package de.htwberlin.service;

import de.htwberlin.exceptions.CoolingSystemException;

import de.htwberlin.exceptions.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Place {
    private Integer trayId;
    private Integer placeNo;
    private Integer sampleId;

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

    public void setPlaceNo() {
        int capacity = 0;
        List<Integer> placeList = new ArrayList<>();

        String sql = String.join(" ", "select place.placeNo, tray.capacity",
                "from place join tray on place.trayId = tray.trayId",
                "where place.trayId = ?");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trayId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    placeList.add(rs.getInt("placeNo"));
                    capacity = rs.getInt("capacity");
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
        int pos = 1;
        for (int i = 1; i <= capacity; i++) {
            boolean isPlace = placeList.contains(i);
            if (!isPlace) {
                pos = i;
                break;
            }
        }
        this.placeNo = pos;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public void insert() {
        String sql = String.join(" ",
                "insert into place(trayId, placeNo, sampleId)",
                "values(?, ?, ?)");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, this.trayId);
            stmt.setInt(2, this.placeNo);
            stmt.setInt(3, this.sampleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
    }
}
