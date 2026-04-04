
insert into Candidati (nume, prenume, email, telefon, cv_text) 
values ('Popescu', 'Ion', 'ion.popescu@email.com', '0712345678', 'Programator Java pasionat, cunostinte bune de MySQL si Swing.');

insert into Utilizatori (email, parola, rol, id_candidat) 
values ('ion.popescu@email.com', 'parola123', 'CANDIDAT', 1);

insert into Utilizatori (email, parola, rol, id_candidat) 
values ('hr@companie.ro', 'admin123', 'HR', null);

select * from utilizatori;


-- Adaugam o companie de test
INSERT INTO Companii (nume, industrie, adresa) VALUES ('Tech Corp', 'IT', 'Bucuresti');

-- Adaugam un job de test legat de acea companie (id_companie = 1)
INSERT INTO Joburi (id_companie, titlu, descriere, oras, salariu_min, salariu_max) 
VALUES (1, 'Junior Java Developer', 'Cautam un programator incepator pentru o super echipa!', 'Bucuresti', 4000, 6000);

set sql_safe_updates=0;
update Utilizatori set id_companie = 1 where rol = "HR";
set sql_safe_updates=1;
