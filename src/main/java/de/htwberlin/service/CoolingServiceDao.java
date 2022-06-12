package de.htwberlin.service;

import de.htwberlin.exceptions.CoolingSystemException;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.sql.*;
import java.util.List;

public class CoolingServiceDao implements  ICoolingService{
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

    @Override
    public void transferSample(Integer sampleId, Integer diameterInCm) {
        L.info("transferSample: sampleId: " + sampleId + ", diameterInCM: " + diameterInCm);

        SampleFinder sampleFinder = new SampleFinder();
        sampleFinder.setConnection(useConnection());

        TrayFinder trayFinder = new TrayFinder();
        trayFinder.setConnection(useConnection());

        Place place = new Place();
        place.setConnection(useConnection());

        Tray tray = new Tray();
        tray.setConnection(useConnection());

        Sample sampleById = sampleFinder.findById(sampleId);
        if (sampleById == null) throw new CoolingSystemException("sampleId does not exist: " + sampleId);

        List<Tray> traysByDiameter = trayFinder.findByDiameter(diameterInCm);
        if (traysByDiameter.isEmpty()) throw new CoolingSystemException("No tray with suitable diameter available.");

        List<Tray> traysByExpirationDate = trayFinder.findByExpirationDate(sampleId);
        if (traysByExpirationDate.isEmpty()) throw new CoolingSystemException("No tray with suitable expiration date available.");

        List<Tray> traysByDiameterAndExpirationDate = trayFinder.getCommonTrays(traysByDiameter, traysByExpirationDate);
        if (traysByDiameterAndExpirationDate.isEmpty()) throw new CoolingSystemException("No tray with suitable diameter and expiration date available.");

        List<Tray> usedTraysAvailable = trayFinder.usedTraysWithAvailableSpace();
        List<Tray> usedTraysByDiameterAndExpirationDateWithAvailableSpace = trayFinder.getCommonTrays(traysByDiameterAndExpirationDate, usedTraysAvailable);
        if (usedTraysByDiameterAndExpirationDateWithAvailableSpace.isEmpty()) {

            List<Tray> allAvailableTrays = trayFinder.allTraysWithAvailableSpace();
            List<Tray> unusedTraysByDiameterAndExpirationDateWithAvailableSpace = trayFinder.getCommonTrays(traysByDiameterAndExpirationDate, allAvailableTrays);
            if(unusedTraysByDiameterAndExpirationDateWithAvailableSpace.isEmpty()) {
                throw new CoolingSystemException("All trays are full.");
            } else {
                place.setTrayId(unusedTraysByDiameterAndExpirationDateWithAvailableSpace.get(0).getTrayId());
            }
        } else {
            place.setTrayId(usedTraysByDiameterAndExpirationDateWithAvailableSpace.get(0).getTrayId());
        }

        place.setPlaceNo();
        place.setSampleId(sampleId);
        place.insert();

        if (trayFinder.getTrayExpirationDate(place.getTrayId()) == null) {
            tray.setTrayId(place.getTrayId());
            tray.setExpirationDate(trayFinder.newTrayExpirationDate(place.getTrayId()));
            tray.update();
        }
    }
}
