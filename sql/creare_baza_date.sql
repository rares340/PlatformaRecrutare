create database if not exists platforma_recrutare;
use platforma_recrutare;

create table Companii(
id_companie int primary key auto_increment,
nume varchar(100) not null,
industrie varchar (50),
adresa text
);

create table Joburi(
id_job int primary key auto_increment,
id_companie int,
titlu varchar(100) not null,
descriere text,
oras varchar(50),
salariu_min decimal(10,2),
salariu_max decimal(10,2),
foreign key (id_companie) references Companii(id_companie) on delete cascade
);

create table Candidati(
id_candidat int primary key auto_increment,
nume varchar(50) not null,
prenume varchar(50) not null,
email varchar(50) unique,
telefon varchar(20),
cv_text longtext,
fulltext(cv_text) 
);

create table Aplicatii(
id_aplicatie int primary key auto_increment,
id_job int,
id_candidat int,
data_aplicarii datetime default current_timestamp,
status enum('Nou','Interviu','Oferta','Respins') default 'Nou',
foreign key (id_job) references Joburi(id_job),
foreign key (id_candidat) references Candidati(id_candidat)
);
