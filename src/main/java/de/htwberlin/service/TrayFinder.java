package de.htwberlin.service;

import de.htwberlin.exceptions.CoolingSystemException;
import de.htwberlin.exceptions.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TrayFinder {

    private static final Logger L = LoggerFactory.getLogger(CoolingServiceDao.class);
    private Connection connection = null;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection useConnection() {
        if (connection == null) {
            throw new CoolingSystemException("Service has no connection");
        }
        return connection;
    }

    public List<Tray> findByDiameter(Integer diameterInCm) {
        Tray tray = new Tray();
        List<Tray> trays = new ArrayList<>();
        tray.setConnection(useConnection());

        String sql = String.join(" ", "select *",
                "from tray",
                "where diameterInCm = ?");
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, diameterInCm);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    tray = new Tray();
                    tray.setTrayId(rs.getInt("trayId"));
                    tray.setDiameterInCm(rs.getInt("diameterInCm"));
                    tray.setCapacity(rs.getInt("capacity"));
                    tray.setExpirationDate(rs.getTimestamp("expirationDate"));
                    trays.add(tray);
                }
                return trays;
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
    }

    public List<Tray> findByExpirationDate(Integer sampleId) {
        Tray tray = new Tray();
        List<Tray> trays = new ArrayList<>();
        tray.setConnection(useConnection());

        String sql = String.join(" ", "select *",
                "from tray",
                "where expirationDate >=",
                "(select expirationDate from sample where sampleId = ?)",
                "or expirationDate is null");
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sampleId);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tray = new Tray();
                    tray.setTrayId(rs.getInt("trayId"));
                    tray.setDiameterInCm(rs.getInt("diameterInCm"));
                    tray.setCapacity(rs.getInt("capacity"));
                    tray.setExpirationDate(rs.getTimestamp("expirationDate"));
                    trays.add(tray);
                }
                return trays;
            }
        } catch (SQLException e) {
            L.error("", e);
            {
                throw new DataException(e);
            }
        }
    }

    public List<Tray> usedTraysWithAvailableSpace() {
        Tray tray = new Tray();
        List<Tray> trays = new ArrayList<>();
        tray.setConnection(useConnection());

        String sql = String.join(" ", "with capacityAndCount(trayId, capacity, count)as",
                "(select tray.trayId, tray.capacity, sampleCount.count",
                "from tray left outer join (",
                "select tray.trayId, count(*) as count",
                "from sample join place on sample.sampleId = place.sampleId",
                "join tray on place.trayId = tray.trayId",
                "group by tray.trayId) sampleCount",
                "on tray.trayId = sampleCount.trayId)",
                "select tray.trayId, tray.diameterInCm, tray.capacity, tray.expirationDate ",
                "from capacityAndCount join tray",
                "on tray.trayId = capacityAndCount.trayId",
                "where capacityAndCount.capacity > capacityAndCount.count")/*,
                "or count is null")*/;
        try(Statement stmt = connection.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    tray = new Tray();
                    tray.setTrayId(rs.getInt("trayId"));
                    tray.setDiameterInCm(rs.getInt("diameterInCm"));
                    tray.setCapacity(rs.getInt("capacity"));
                    tray.setExpirationDate(rs.getTimestamp("expirationDate"));
                    trays.add(tray);
                }
                return trays;
            }
        } catch(SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
    }

    public List<Tray> allTraysWithAvailableSpace() {
        Tray tray = new Tray();
        List<Tray> trays = new ArrayList<>();
        tray.setConnection(useConnection());

        String sql = String.join(" ", "with capacityAndCount(trayId, capacity, count)as",
                "(select tray.trayId, tray.capacity, sampleCount.count",
                "from tray left outer join (",
                "select tray.trayId, count(*) as count",
                "from sample join place on sample.sampleId = place.sampleId",
                "join tray on place.trayId = tray.trayId",
                "group by tray.trayId) sampleCount",
                "on tray.trayId = sampleCount.trayId)",
                "select tray.trayId, tray.diameterInCm, tray.capacity, tray.expirationDate ",
                "from capacityAndCount join tray",
                "on tray.trayId = capacityAndCount.trayId",
                "where capacityAndCount.capacity > capacityAndCount.count",
                "or count is null");
        try(Statement stmt = connection.createStatement()) {
            try(ResultSet rs = stmt.executeQuery(sql)) {
                while(rs.next()) {
                    tray = new Tray();
                    tray.setTrayId(rs.getInt("trayId"));
                    tray.setDiameterInCm(rs.getInt("diameterInCm"));
                    tray.setCapacity(rs.getInt("capacity"));
                    tray.setExpirationDate(rs.getTimestamp("expirationDate"));
                    trays.add(tray);
                }
                return trays;
            }
        } catch(SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
    }

    public Timestamp getTrayExpirationDate(Integer trayId) {
        Timestamp trayExpirationDate = null;
        String sql = String.join(" ", "select expirationDate",
                "from tray",
                "where trayId = ?");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trayId);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    trayExpirationDate = rs.getTimestamp("expirationDate");
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
        return trayExpirationDate;
    }

    public Timestamp newTrayExpirationDate(Integer trayId) {
        Timestamp expirationDate = null;
        String sql = String.join(" ", "select place.trayId, min(sample.expirationDate) minExpirationDate",
                "from sample join place on sample.sampleId = place.sampleId",
                "join tray on tray.trayId = place.trayId",
                "where place.trayId = ?",
                "group by place.trayId");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trayId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    expirationDate = rs.getTimestamp("minExpirationDate");
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(expirationDate);
        cal.add(Calendar.DATE, 30);

        return new Timestamp(cal.getTime().getTime());
    }

    public List<Tray> getCommonTrays(List<Tray> list1, List<Tray> list2) {
        List<Tray> equals = new ArrayList<>();
        for (Tray tray : list1) {
            for (Tray tray2 : list2) {
                if (tray.getTrayId().equals(tray2.getTrayId())) {
                    equals.add(tray);
                }
            }
        }
        return equals;
    }
}
