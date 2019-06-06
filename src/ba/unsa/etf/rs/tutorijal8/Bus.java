package ba.unsa.etf.rs.tutorijal8;

public class Bus {
    private Integer id = null;
    private String proizvodjac;
    private String serija;
    private int numberOfSeats;
    private Driver driverOne = null;
    private Driver driverTwo = null;

    public void setDriverOne(Driver driverOne) {
        this.driverOne = driverOne;
    }

    public void setDriverTwo(Driver driverTwo) {
        this.driverTwo = driverTwo;
    }

    public Bus(){ }

    public Bus(String proizvodjac, String serija, int numberOfSeats) {
        this.proizvodjac = proizvodjac;
        this.serija = serija;
        this.numberOfSeats = numberOfSeats;
    }

    public Bus( int Id, String proizvodjac, String serija, int numberOfSeats) {
        id = Id;
        this.proizvodjac = proizvodjac;
        this.serija = serija;
        this.numberOfSeats = numberOfSeats;

    }
   public Bus(int Id, String proizvodjac, String serija, int numberOfSeats, Driver driverOne, Driver driverTwo) {
        id = Id;
        this.proizvodjac = proizvodjac;
        this.serija = serija;
        this.numberOfSeats = numberOfSeats;
        this.driverOne = driverOne;
        this.driverTwo = driverTwo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaker() {
        return proizvodjac;
    }

    public void setProizvodjac(String proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    public String getSerija() {
        return serija;
    }

    public void setSerija(String serija) {
        this.serija = serija;
    }

    public int getSeatNumber() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Driver getDriverOne() {
       return driverOne;
    }

    public Driver getDriverTwo() {
        return driverTwo;
    }

    @Override
    public String toString () {
        String s = "";
        s += this.proizvodjac + " " + this.serija + " ( seats: " + this.getSeatNumber() + " )";
        if (driverOne != null) {
            s += driverOne.toString();
        }
        if (driverTwo != null) {
            s += driverTwo.toString();
        }
        return s;
    }

}


