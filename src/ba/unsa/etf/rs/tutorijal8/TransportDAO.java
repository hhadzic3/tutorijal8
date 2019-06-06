package ba.unsa.etf.rs.tutorijal8;

import org.sqlite.JDBC;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransportDAO {
    private static TransportDAO instance;
    private Connection conn;

    private static PreparedStatement getDrivers, getBuses, insertIntoDodjela, deleteCurrentDriverDodjela,
            getNextIdDriver, deleteAllDodjela, insertIntoDriver, deleteCurrentBus,
            insertIntoBus, deleteCurrentDriver, getNextIdBus, deleteAllBus, deleteAllDriver, getDriversFromDodjela, deleteCurrentBusDodjela;

    public static TransportDAO getInstance() {
        if(instance == null) instance = new TransportDAO();
        return instance;
    }
    static {
        try {
            DriverManager.registerDriver(new JDBC());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private TransportDAO(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:Baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {// **** brisanje ****
            deleteCurrentBus = conn.prepareStatement("DELETE FROM Bus WHERE bus_id=?");
            deleteCurrentDriver = conn.prepareStatement("DELETE FROM Vozac WHERE  vozac_id=?");
            deleteCurrentBusDodjela = conn.prepareStatement("DELETE FROM dodjela WHERE bus_id = ?");
            deleteCurrentDriverDodjela = conn.prepareStatement("DELETE FROM dodjela WHERE driver_id = ?");
            deleteAllBus = conn.prepareStatement("DELETE FROM Bus");
            deleteAllDriver = conn.prepareStatement("DELETE FROM Vozac");
            deleteAllDodjela = conn.prepareStatement("DELETE FROM dodjela WHERE 1=1;");
            // **** get get get ****
            getDriversFromDodjela = conn.prepareStatement("SELECT DISTINCT dr.vozac_id, dr.ime, dr.prezime, dr.JMB, dr.datum_rodjenja, dr.datum_zaposlenja" +
                    " FROM dodjela d INNER JOIN Vozac dr ON (d.driver_id = dr.vozac_id) WHERE d.bus_id=?");
            getDrivers = conn.prepareStatement("SELECT * FROM Vozac;");
            getBuses = conn.prepareStatement("SELECT * FROM Bus");
            getNextIdBus = conn.prepareStatement("SELECT MAX(bus_id)+1 FROM Bus");
            getNextIdDriver = conn.prepareStatement("SELECT MAX(vozac_id)+1 FROM Vozac");
            // ***** insert into *****
            insertIntoBus = conn.prepareStatement("INSERT INTO Bus VALUES(?,?,?,?)");
            insertIntoDriver = conn.prepareStatement("INSERT INTO Vozac VALUES (?,?,?,?,?,?)");
            insertIntoDodjela = conn.prepareStatement("INSERT OR REPLACE INTO dodjela(bus_id, driver_id) VALUES (?,?)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDriver(Driver driver){
        ArrayList<Driver> drivers = getDrivers();
        if(drivers.contains(driver)) throw new IllegalArgumentException("Taj vozač već postoji!");
        try {
            ResultSet rs = getNextIdDriver.executeQuery();
            int id = 1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            insertIntoDriver.setInt(1, id);
            insertIntoDriver.setString(2, driver.getName());
            insertIntoDriver.setString(3, driver.getPrezime());
            insertIntoDriver.setString(4 , driver.getJMB());
            insertIntoDriver.setDate(5 , Date.valueOf((driver.getBirthday())));
            insertIntoDriver.setDate(6 , Date.valueOf((driver.getWorkDate())));
            insertIntoDriver.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Taj vozač već postoji!");
        }
    }


    public void addBus(Bus bus) {
        try {
            ResultSet rs = getNextIdBus.executeQuery();
            int id = 1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            insertIntoBus.setInt(1, id);
            insertIntoBus.setString(2, bus.getMaker());
            insertIntoBus.setString(3, bus.getSerija());
            insertIntoBus.setInt(4, bus.getSeatNumber());
            insertIntoBus.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Driver> getDrivers() {
        ArrayList<Driver> drivers = new ArrayList<Driver>();
        ResultSet result = null;
        try {
            result = getDrivers.executeQuery();
            Driver driver;
            while (  ( driver = dajVozaceUpit(result) ) != null )
                drivers.add(driver);
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }

    public ArrayList<Bus> getBusses() {
        ArrayList<Bus> buses = new ArrayList<>();
        try {
            ResultSet result = getBuses.executeQuery();
            while(result.next()) {
                Integer id = result.getInt(1);
                String maker = result.getString(2);
                String series = result.getString(3);
                int brojSjedista = result.getInt(4);
                getDriversFromDodjela.setInt(1, id);

                ResultSet ResultatDrugi = getDriversFromDodjela.executeQuery();
                Driver driver;
                ArrayList<Driver> drivers = new ArrayList<Driver>();
                while (ResultatDrugi.next()) {
                    Integer id_drivera = ResultatDrugi.getInt(1);
                    String name = ResultatDrugi.getString(2);
                    String surname = ResultatDrugi.getString(3);
                    String jmb = ResultatDrugi.getString(4);
                    Date birthDate = ResultatDrugi.getDate(5);
                    Date hireDate = ResultatDrugi.getDate(6);
                    drivers.add(new Driver(id_drivera, name, surname, jmb, birthDate.toLocalDate(), hireDate.toLocalDate()));
                }
                if (drivers.size() == 1) buses.add(new Bus(id, maker, series, brojSjedista, drivers.get(0), null));

                else if (drivers.size() == 2) buses.add(new Bus(id, maker, series, brojSjedista, drivers.get(0), drivers.get(1)));

                else buses.add(new Bus(id, maker, series, brojSjedista, null, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    private Driver dajVozaceUpit(ResultSet result) {
        Driver driver = null;
        try {
            if (result.next() ){
                int id = result.getInt("vozac_id");
                String name = result.getString("ime");
                String surname = result.getString("prezime");
                String jmb = result.getString("JMB");
                LocalDate rodjendan = (result.getDate("datum_rodjenja")).toLocalDate();
                LocalDate datum_zap = (result.getDate("datum_zaposlenja")).toLocalDate();

                driver = new Driver( name , surname , jmb , rodjendan , datum_zap);
                driver.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driver;
    }

   public void deleteBus(Bus bus) {
        try {
            deleteCurrentBusDodjela.setInt(1, bus.getId());
            deleteCurrentBusDodjela.executeUpdate();
            deleteCurrentBus.setInt(1, bus.getId());
            deleteCurrentBus.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDriver(Driver driver) {
        try {
            deleteCurrentDriverDodjela.setInt(1, driver.getId());
            deleteCurrentDriverDodjela.executeUpdate();
            deleteCurrentDriver.setInt(1, driver.getId());
            deleteCurrentDriver.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetDatabase() {
        try {
            deleteAllDodjela.executeUpdate();
            deleteAllBus.executeUpdate();
            deleteAllDriver.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodijeliVozacuAutobus(Driver driver, Bus bus, int which) {
        try {
            insertIntoDodjela.setInt(1 , bus.getId());
            insertIntoDodjela.setInt(2,driver.getId());
            insertIntoDodjela.executeUpdate();
            if(which == 1){
                bus.setDriverOne(driver);
            }
            if (which == 2){
                bus.setDriverTwo(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}