
insert into Candidati (nume, prenume, email, telefon, cv_text) 
values ('Popescu', 'Ion', 'ion.popescu@email.com', '0712345678', 'Programator Java pasionat, cunostinte bune de MySQL si Swing.');

insert into Utilizatori (email, parola, rol, id_candidat) 
values ('ion.popescu@email.com', 'parola123', 'CANDIDAT', 1);

insert into Utilizatori (email, parola, rol, id_candidat) 
values ('hr@companie.ro', 'admin123', 'HR', null);

select * from utilizatori;