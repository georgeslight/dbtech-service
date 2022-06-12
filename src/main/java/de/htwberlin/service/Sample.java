package de.htwberlin.service;

import de.htwberlin.exceptions.CoolingSystemException;

import java.sql.Connection;
import java.sql.Timestamp;

public class Sample {

    private Integer sampleId;
    private Integer sampleKindId;
    private Timestamp expirationDate;

    private Connection connection = null;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @SuppressWarnings("unused")
    private Connection useConnection() {
        if(connection == null) {
            throw new CoolingSystemException("Service has no connection");
        }
        return connection;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public Integer getSampleKindId() {
        return sampleKindId;
    }

    public void setSampleKindId(Integer sampleKindId) {
        this.sampleKindId = sampleKindId;
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

    }

    public void delete() {

    }
}
