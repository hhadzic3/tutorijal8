package ba.unsa.etf.rs.tutorijal8;

import org.sqlite.JDBC;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
public class TransportDAO {
    //konekcija na bazu!!
    private Connection conn;
    private static PreparedStatement dajVozaceUpit,dajBusUpit,
            odrediIdDriveraUpit,truncVozaciBuseva, dodajVouzacaBusa , addDriver , obrisiBusUpit ,
            dodajBusUpit ,obrisiDriverUpit ,odrediIdBusaUpit, truncBus , truncDriver , dajJMB , getDodjelaVozaci;


    private static TransportDAO instance;
    private Driver driver;
    static {
        try {
            DriverManager.registerDriver(new JDBC());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static TransportDAO getInstance() {
        if(instance == null) instance = new TransportDAO();
        return instance;
    }

    private TransportDAO(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:Baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            obrisiBusUpit = conn.prepareStatement("DELETE FROM Bus WHERE bus_id=?");
            obrisiDriverUpit = conn.prepareStatement("DELETE FROM Vozac WHERE  vozac_id=?");
            addDriver = conn.prepareStatement("INSERT INTO Vozac VALUES (?,?,?,?,?,?)");
            dajVozaceUpit = conn.prepareStatement("SELECT * FROM Vozac;");
            dajBusUpit = conn.prepareStatement("SELECT * FROM Bus");
            dodajBusUpit = conn.prepareStatement("INSERT INTO Bus VALUES(?,?,?,?)");
            odrediIdBusaUpit = conn.prepareStatement("SELECT MAX(bus_id)+1 FROM Bus");
            odrediIdDriveraUpit = conn.prepareStatement("SELECT MAX(vozac_id)+1 FROM Vozac");
            truncBus = conn.prepareStatement("DELETE FROM Bus");
            truncDriver = conn.prepareStatement("DELETE FROM Vozac");
            truncVozaciBuseva = conn.prepareStatement("DELETE FROM VozaciBuseva");
            getDodjelaVozaci = conn.prepareStatement("SELECT DISTINCT v.vozac_id, v.ime, v.prezime, v.JMB, v.datum_rodjenja, v.datum_zaposljenja" +
                    " FROM VozaciBuseva vd , Vozac v WHERE vd.driverId = v.vozac_id AND vd.busId=?");
            dodajVouzacaBusa = conn.prepareStatement("INSERT INTO VozaciBuseva VALUES (?,?,null)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addDriver(String name, String surname, int jmb, LocalDate dateOfBirth, LocalDate hireDate) {
        try {

            ResultSet result = odrediIdDriveraUpit.executeQuery();
            result.next();
            Integer id = result.getInt(1);
            if (id == null) {
                id = 1;
            }
            addDriver.setInt(1, id);
            addDriver.setString(2, name);
            addDriver.setString(3, surname);
            addDriver.setInt(4, jmb);
            addDriver.setDate(5, Date.valueOf(dateOfBirth));
            addDriver.setDate(6, Date.valueOf(hireDate));
            addDriver.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException();
        }
    }

    public void addDriver(Driver driver){
        ArrayList<Driver> drivers = getDrivers();
        if(drivers.contains(driver)) throw new IllegalArgumentException("Taj vozač već postoji!");
        try {
            ResultSet rs = odrediIdDriveraUpit.executeQuery();
            int id = 1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            addDriver.setInt(1, id);
            addDriver.setString(2, driver.getName());
            addDriver.setString(3, driver.getPrezime());
            addDriver.setString(4 , driver.getJMB());
            addDriver.setDate(5 , convertToDateViaSqlDate(driver.getBirthday()));
            addDriver.setDate(6 , convertToDateViaSqlDate(driver.getWorkDate()));
            addDriver.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Taj vozač već postoji!");
        }
    }

    public ArrayList<Driver> getDrivers() {
        ArrayList<Driver> drivers = new ArrayList<Driver>();
        ResultSet result = null;
        try {
            result = dajVozaceUpit.executeQuery();
            Driver driver;
            while (  ( driver = dajVozaceUpit(result) ) != null )
                drivers.add(driver);
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }


    private Driver dajVozaceUpit(ResultSet result) {
        Driver driver = null;
        try {
            if (result.next() ){
                int id = result.getInt("vozac_id");
                String name = result.getString("ime");
                String surname = result.getString("prezime");
                String jmb = result.getString("JMB");
                LocalDate rodjendan = (result.getDate("datum_rodjenja").toLocalDate());
                LocalDate datum_zap = (result.getDate("datum_zaposljenja").toLocalDate());

                driver = new Driver( name , surname , jmb , rodjendan , datum_zap);
                driver.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    public ArrayList<Bus> getBusses() {
        ArrayList<Bus> buses = new ArrayList<>();
        try {
            ResultSet result = dajBusUpit.executeQuery();
            while(result.next()) {
                Integer id = result.getInt(1);
                String maker = result.getString(2);
                String series = result.getString(3);
                int brojSjedista = result.getInt(4);
                getDodjelaVozaci.setInt(1, id);

                ResultSet ResultatDrugi = getDodjelaVozaci.executeQuery();
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
                if (drivers.size() == 1) {
                    buses.add(new Bus(id, maker, series, brojSjedista, drivers.get(0), null));
                }
                else if (drivers.size() == 2) {
                    buses.add(new Bus(id, maker, series, brojSjedista, drivers.get(0), drivers.get(1)));
                }
                else {
                    buses.add(new Bus(id, maker, series, brojSjedista, null, null));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;

    }


    private Bus dajBusUpit(ResultSet result) {
        Bus bus = null;
        try {
            if (result.next() ){
                int id = result.getInt("bus_id");
                String proizvodjac = result.getString("proizvodjac");
                String serija = result.getString("serija");
                int brojSjedista = result.getInt("broj_sjedista");
                getDodjelaVozaci.setInt(1,id);

                bus = new Bus( proizvodjac , serija , brojSjedista);
                bus.setId(id);

                ResultSet result2 = getDodjelaVozaci.executeQuery();
                Driver v1;
                ArrayList<Driver> drivers = new ArrayList<Driver>();
                while (result2.next()) {
                    Integer idDriver = result2.getInt(1);
                    String name = result2.getString(2);
                    String surname = result2.getString(3);
                    String jmb = result2.getString(4);
                    Date birthDate = result2.getDate(5);
                    Date hireDate = result2.getDate(5);
                    drivers.add(new Driver(idDriver, name, surname, jmb, birthDate.toLocalDate(), hireDate.toLocalDate()));
                    System.out.println("size:" + drivers.size());
                }
                if (drivers.size() == 1)
                    bus = new Bus(id, proizvodjac, serija, brojSjedista, drivers.get(0), null);
                else if (drivers.size() == 2)
                    bus=new Bus(id, proizvodjac, serija, brojSjedista, drivers.get(0), drivers.get(1));
                else bus=new Bus(id, proizvodjac, serija, brojSjedista, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bus;
    }


    public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }


    public void addBus(Bus bus) {
        try {
            ResultSet rs = odrediIdBusaUpit.executeQuery();
            int id = 1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            dodajBusUpit.setInt(1, id);
            dodajBusUpit.setString(2, bus.getMaker());
            dodajBusUpit.setString(3, bus.getSerija());
            dodajBusUpit.setInt(4, bus.getSeatNumber());
            dodajBusUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBus(Bus bus) {
        try {
            obrisiBusUpit.setInt(1, bus.getId());
            obrisiBusUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteDriver(Driver driver) {
        try {
            obrisiDriverUpit.setInt(1, driver.getId());
            obrisiDriverUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetDatabase() {
        try {
            truncVozaciBuseva.executeUpdate();
            truncBus.executeUpdate();
            truncDriver.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodijeliVozacuAutobus(Driver driver, Bus bus, int which) {
        try {
            dodajVouzacaBusa.setInt(1 , bus.getId());
            dodajVouzacaBusa.setInt(2,driver.getId());
            dodajVouzacaBusa.executeUpdate();
            if(which == 1){
                bus.setFirstDriver(driver);
            }
            if (which == 2){
                bus.setSecondDriver(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
