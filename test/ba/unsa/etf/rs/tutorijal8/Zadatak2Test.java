package ba.unsa.etf.rs.tutorijal8;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class Zadatak2Test {

    @BeforeEach
    void setUp() {
        TransportDAO dao = TransportDAO.getInstance();
        dao.resetDatabase();
    }


    @Test
    void dodijeliVozacuAutobus() {
        InputStream defaultSystemIn = setupCustomInputStreamAndReturnStandardInputStream(
                "dodaj autobus\nMan\nX534\n59\n" +
                        "dodaj autobus\nIsuzu\nA77\n42\n" +
                        "dodaj autobus\nIcarbus\nB856\n47\n" +
                        "ukloni autobus\n2\n" +
                        "dodaj vozaca\n" +
                        "Zadatak\nZadatkovic\n4444444444444\n" +
                        "1.1.1980\n1.6.2000\n" +
                        "dodaj vozaca\n" +
                        "Aplikacija\nAplikacijevic\n" +
                        "5555555555555\n" +
                        "12.12.1975\n" +
                        "1.1.2019\n"+
                        "Dodijeli vozaca autobusu\n" +
                        "2\n" +
                        "1\n");

        String[] args= {"a","b"};


        Main.main(args);


        revertToStandardInputStream(defaultSystemIn);

        TransportDAO dao = TransportDAO.getInstance();
        Driver driver = dao.getDrivers().get(1);
        Bus bus = dao.getBusses().get(0);
        assertAll(()->{
            assertEquals(bus.getDriverOne(),driver);
        });
    }

    @Test
    void dodijeliIIspisiAutobuse() {
        InputStream defaultSystemIn = setupCustomInputStreamAndReturnStandardInputStream(
                "dodaj autobus\nMan\nX534\n59\n" +
                        "dodaj autobus\nIsuzu\nA77\n42\n" +
                        "dodaj autobus\nIcarbus\nB856\n47\n" +
                        "ukloni autobus\n2\n" +
                        "dodaj vozaca\n" +
                        "Zadatak\nZadatkovic\n4444444444444\n" +
                        "1.1.1980\n1.6.2000\n" +
                        "dodaj vozaca\n" +
                        "Aplikacija\nAplikacijevic\n" +
                        "5555555555555\n" +
                        "12.12.1975\n" +
                        "1.1.2019\n"+
                        "Dodijeli vozaca autobusu\n" +
                        "2\n" +
                        "1\n" +
                        "Ispisi autobuse\n");

        String[] args= {"a","b"};

        PrintStream defaultSystemOut = System.out;
        ByteArrayOutputStream outputHolder = setupCustomOutputStreamAndReturnIt();

        Main.main(args);

        revertToStandardOutputStream(defaultSystemOut);

        revertToStandardInputStream(defaultSystemIn);

        String expected = "Man"+" "+ "X534"+" ( seats: "+59+" )"+" - ("+"Aplikacija Aplikacijevic ( 5555555555555 )"+")";
        TransportDAO dao = TransportDAO.getInstance();
        Driver driver = dao.getDrivers().get(1);
        Bus bus = dao.getBusses().get(0);

        System.out.println(outputHolder.toString());
        assertAll(()->{
            assertTrue(outputHolder.toString().contains(expected));
            assertEquals(bus.getDriverOne(),driver);
        });
    }

    private void revertToStandardOutputStream(PrintStream defaultSystemOut) {
        System.out.flush();
        System.setOut(defaultSystemOut);
    }

    private ByteArrayOutputStream setupCustomOutputStreamAndReturnIt() {
        ByteArrayOutputStream outputHolder = new ByteArrayOutputStream();
        PrintStream myStream = new PrintStream(outputHolder);
        System.setOut(myStream);
        return outputHolder;
    }

    private void revertToStandardInputStream(InputStream defaultSystemIn) {
        System.setIn(defaultSystemIn);
    }

    private InputStream setupCustomInputStreamAndReturnStandardInputStream(String s) {
        String driverInput = s;
        byte[] input = new byte[driverInput.length()];
        for (int i = 0; i < driverInput.length(); i++) {
            input[i] = (byte) driverInput.charAt(i);
        }

        ByteArrayInputStream inputHolder = new ByteArrayInputStream(input);
        InputStream defaultSystemIn = System.in;
        System.setIn(inputHolder);
        return defaultSystemIn;
    }
}
