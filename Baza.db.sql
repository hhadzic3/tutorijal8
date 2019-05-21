BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "VozaciBuseva" (
	"driverId"	INTEGER,
	"busId"	INTEGER,
	"VozaciBuseva_id"	INTEGER,
	PRIMARY KEY("VozaciBuseva_id"),
	FOREIGN KEY("busId") REFERENCES "Bus"("bus_id"),
	FOREIGN KEY("driverId") REFERENCES "Vozac"("vozac_id")
);
CREATE TABLE IF NOT EXISTS "Vozac" (
	"vozac_id"	INTEGER,
	"ime"	TEXT,
	"prezime"	TEXT,
	"JMB"	TEXT UNIQUE ,
	"datum_rodjenja"	DATE,
	"datum_zaposljenja"	DATE,
	PRIMARY KEY("vozac_id")
);
CREATE TABLE IF NOT EXISTS "Bus" (
	"bus_id"	INTEGER,
	"proizvodjac"	TEXT,
	"serija"	TEXT,
	"broj_sjedista"	INTEGER,
	"broj_vozaca"	INTEGER,
	PRIMARY KEY("bus_id")
);
COMMIT;
