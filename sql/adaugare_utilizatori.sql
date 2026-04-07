-- 1. Inseram Companiile (ID 1 si 2)
INSERT INTO Companii (nume, industrie, adresa) VALUES 
('Tech Corp SRL', 'IT / Software', 'Bucuresti, Str. Victoriei'),
('Global Finance', 'Bancar / Economic', 'Cluj-Napoca, Str. Avram Iancu');

-- 2. Inseram Candidatii (ID 1 si 2)
INSERT INTO Candidati (nume, prenume, email, telefon, cv_text) VALUES 
('Popescu', 'Ion', 'ion.p@email.com', '0711111111', 'Pasionat de programare, am lucrat cu Java.'),
('Ionescu', 'Maria', 'maria.i@email.com', '0722222222', 'Experienta in analiza financiara.');

-- 3. Inseram Utilizatorii 
INSERT INTO Utilizatori (email, parola, rol, id_companie, id_candidat) VALUES 
('hr@techcorp.ro', '1234', 'HR', 1, NULL),
('hr@finance.ro', '1234', 'HR', 2, NULL),
('ion.p@email.com', '1234', 'Candidat', NULL, 1),
('maria.i@email.com', '1234', 'Candidat', NULL, 2);

-- 4. Inseram Joburile (ID 1 si 2 la Tech Corp, ID 3 la Finance)
INSERT INTO Joburi (id_companie, titlu, descriere, oras, salariu_min, salariu_max) VALUES 
(1, 'Junior Java Developer', 'Dezvoltare aplicatii in Java Swing.', 'Bucuresti', 4000, 6000),
(1, 'Senior Database Admin', 'Optimizare structuri de date.', 'Remote', 8000, 12000),
(2, 'Analist Financiar', 'Analiza de risc si rapoarte.', 'Cluj', 5000, 7500);

-- 5. Inseram Aplicatiile
INSERT INTO Aplicatii (id_job, id_candidat, status) VALUES 
(1, 1, 'Nou'),         -- Ion a aplicat la Java
(2, 2, 'Interviu'),    -- Maria a aplicat la DBA
(3, 2, 'Nou');         -- Maria a aplicat la Analist Financiar