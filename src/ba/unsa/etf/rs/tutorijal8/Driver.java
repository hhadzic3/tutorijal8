package ba.unsa.etf.rs.tutorijal8;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class Driver {
    private Integer id;
    private String Ime;
    private String Prezime;
    private String JMB;
    private LocalDate BirthDate;
    private LocalDate WorkDate;

    public Driver() { }

    public Driver(String ime, String prezime, String JMB, LocalDate birthDate, LocalDate workDate) {
        this.Ime = ime;
        Prezime = prezime;
        this.JMB = JMB;
        BirthDate = birthDate;
        WorkDate = workDate;
    }

    public Driver(int id , String ime, String prezime, String JMB, LocalDate birthDate, LocalDate workDate) {
        this.id = id;
        this.Ime = ime;
        Prezime = prezime;
        this.JMB = JMB;
        BirthDate = birthDate;
        WorkDate = workDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Ime;
    }

    public void setIme(String ime) {
        Ime = ime;
    }

    public String getPrezime() {
        return Prezime;
    }

    public void setPrezime(String prezime) {
        Prezime = prezime;
    }

    public String getJMB() {
        return JMB;
    }

    public void setJMB(String JMB) {
        this.JMB = JMB;
    }

    public LocalDate getBirthday() {
        return BirthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        BirthDate = birthDate;
    }

    public LocalDate getWorkDate() {
        return WorkDate;
    }

    public void setWorkDate(LocalDate workDate) {
        WorkDate = workDate;
    }

    @Override
    public String toString() {
        return  " - (" + this.getName() + " " + this.getPrezime() + " ( " + this.getJMB() + " ))";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Driver)) return false;
        Driver driver = (Driver) o;
        return Objects.equals(getJMB(), driver.getJMB());
    }
}
