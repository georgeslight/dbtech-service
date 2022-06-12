package de.htwberlin.service;

import de.htwberlin.exceptions.CoolingSystemException;
import de.htwberlin.exceptions.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;


public class SampleFinder {

    private static final Logger L = LoggerFactory.getLogger(CoolingServiceDao.class);
    private Connection connection = null;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Sample findById(Integer sampleId) {
        Sample sample = new Sample();

        String sql = String.join(" ", "select *",
                "from sample",
                "where sampleId = ?");

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sampleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    sample.setSampleId(rs.getInt("sampleId"));
                    sample.setSampleKindId(rs.getInt("sampleKindId"));
                    sample.setExpirationDate(rs.getTimestamp("expirationDate"));
                } else {
                    throw new CoolingSystemException("SampleId existiert nicht: " + sampleId);
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
        return sample;
    }

    public Timestamp getSampleExpirationDate(Integer sampleId) {
        Timestamp sampleExpirationDate = null;
        String sql = String.join(" ", "select expirationDate from sample where sampleId = ?");
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sampleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sampleExpirationDate = rs.getTimestamp("expirationDate");
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new DataException(e);
        }
        return sampleExpirationDate;
    }
}
