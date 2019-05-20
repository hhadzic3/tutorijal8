package ba.unsa.etf.rs.tutorijal8;

import org.sqlite.JDBC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

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
            dodajBusUpit = conn.prepareStatement("INSERT INTO Bus VALUES(?,?,?,?,?)");
            odrediIdBusaUpit = conn.prepareStatement("SELECT MAX(bus_id)+1 FROM Bus");
            odrediIdDriveraUpit = conn.prepareStatement("SELECT MAX(vozac_id)+1 FROM Vozac");
            truncBus = conn.prepareStatement("DELETE FROM Bus");
            truncDriver = conn.prepareStatement("DELETE FROM Vozac");
            truncVozaciBuseva = conn.prepareStatement("DELETE FROM VozaciBuseva");

            getDodjelaVozaci = conn.prepareStatement("SELECT DISTINCT v.vozac_id, v.ime, v.prezime, v.JMB, v.datum_rodjenja, v.datum_zaposljenja" +
                    " FROM VozaciBuseva vd , Vozac v WHERE vd.driverId = v.vozac_id AND vd.busId=?");
            dodajVouzacaBusa = conn.prepareStatement("INSERT INTO VozaciBuseva VALUES (?,?,null )");

        } catch (SQLException e) {
            regenerisiBazu();
            e.printStackTrace();
        }
    }
    //Rjesavamo slucaj ukoliko baza nije kreirana!
    public void regenerisiBazu() {
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("Baza.db.sql"));
            String sqlUpit = "";
            while (ulaz.hasNext()){
                sqlUpit += ulaz.nextLine();
                if (sqlUpit.charAt(sqlUpit.length() - 1 ) == ';'){
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ulaz.close();
    }

    public Date convertToDateViaSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

    public void addDriver(Driver driver){
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
            dodajBusUpit.setInt(5,bus.getNumberOfDrivers());
            dodajBusUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public ArrayList<Bus> getBusses() {
        ArrayList<Bus> busevi = new ArrayList<Bus>();
        ResultSet result = null;
        try {
            result = dajBusUpit.executeQuery();
            Bus bus;
            while ( ( bus = dajBusUpit(result) ) != null )
                busevi.add(bus);
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busevi;
    }

    public LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }
    private Driver dajVozaceUpit(ResultSet result) {
        Driver driver = null;
        try {
            if (result.next() ){
                int id = result.getInt("vozac_id");
                String name = result.getString("ime");
                String surname = result.getString("prezime");
                String jmb = result.getString("JMB");
                LocalDate rodjendan = convertToLocalDateViaSqlDate(result.getDate("datum_rodjenja"));
                LocalDate datum_zap = convertToLocalDateViaSqlDate(result.getDate("datum_zaposljenja"));

                driver = new Driver( name , surname , jmb , rodjendan , datum_zap);
                driver.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return driver;
    }


    private Bus dajBusUpit(ResultSet result) {
        Bus bus = null;
        try {
            if (result.next() ){
                int id = result.getInt("bus_id");
                String proizvodjac = result.getString("proizvodjac");
                String serija = result.getString("serija");
                int brojSjedista = result.getInt("broj_sjedista");
               // getDodjelaVozaci.setInt(1,id);

                bus = new Bus( proizvodjac , serija , brojSjedista);
                bus.setId(id);
/*                ResultSet result2 = getDodjelaVozaci.executeQuery();
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
  */
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bus;
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
