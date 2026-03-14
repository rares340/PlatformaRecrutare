create database if not exists platforma_recrutare;
use platforma_recrutare;

create table if not exists Companii(
id_companie int primary key auto_increment,
nume varchar(100) not null,
industrie varchar (50),
adresa text
);

create table if not exists Joburi(
id_job int primary key auto_increment,
id_companie int,
titlu varchar(100) not null,
descriere text,
oras varchar(50),
salariu_min decimal(10,2),
salariu_max decimal(10,2),
foreign key (id_companie) references Companii(id_companie) on delete cascade
);

create table if not exists Candidati(
id_candidat int primary key auto_increment,
nume varchar(50) not null,
prenume varchar(50) not null,
email varchar(50) unique,
telefon varchar(20),
cv_text longtext,
fulltext(cv_text) 
);

create table if not exists Aplicatii(
id_aplicatie int primary key auto_increment,
id_job int,
id_candidat int,
data_aplicarii datetime default current_timestamp,
status enum('Nou','Interviu','Oferta','Respins') default 'Nou',
foreign key (id_job) references Joburi(id_job),
foreign key (id_candidat) references Candidati(id_candidat)
);

create table if not exists Competente(
id_competenta int primary key auto_increment,
nume_competenta varchar(100) not null unique
);

create table if not exists CompetenteCandidati(
id_candidat int,
id_competenta int,
nivel enum('Junior','Intermediar','Senior','Expert'),
primary key (id_candidat,id_competenta),
foreign key (id_candidat) references Candidati(id_candidat) on delete cascade,
foreign key (id_competenta) references Competente(id_competenta) on delete cascade
);

create table if not exists CompetenteJob(
id_job int,
id_competenta int,
primary key (id_job,id_competenta),
foreign key (id_job) references Joburi(id_job) on delete cascade,
foreign key (id_competenta) references Competente(id_competenta) on delete cascade
);

create table if not exists EtapeSelectie(
id_etapa int primary key auto_increment,
nume_etapa varchar(50) not null,
ordine int
);

create table if not exists Interviuri(
id_interviu int primary key auto_increment,
id_etapa int,
id_aplicatie int,
data_interviu datetime not null,
feedback text,
scor_evaluare int check (scor_evaluare between 1 and 10),
foreign key (id_etapa) references EtapeSelectie(id_etapa),
foreign key (id_aplicatie) references Aplicatii(id_aplicatie) on delete cascade
);

create table if not exists Oferte(
id_oferta int primary key auto_increment,
id_aplicatie int unique,
salariu_oferit decimal(10,2),
data_expirare date,
status_oferta enum('Trimisa','Acceptata','Refuzata','Expirata') default 'Trimisa',
foreign key (id_aplicatie) references Aplicatii(id_aplicatie) on delete cascade
);
