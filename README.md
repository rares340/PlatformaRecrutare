Proiect Platforma de Recrutare si Joburi

Acesta este un proiect pentru gestionarea proceselor de recrutare, incluzând companii, anunțuri de angajare, aplicații și candidați. Aplicația permite urmărirea fluxului de selecție de la depunerea CV-ului până la oferta finală.

Sistemul este conceput cu o arhitectură bazată pe roluri, oferind două experiențe complet separate în funcție de tipul de utilizator logat:

Interfața pentru Candidați: Un portal simplificat unde aceștia pot vizualiza joburile active, își pot actualiza profilul/CV-ul și pot aplica la pozițiile dorite.

Interfața pentru HR: Un panou de control avansat pentru recrutorii companiei, unde aceștia pot posta joburi, pot evalua aplicațiile și pot schimba statusul candidaților în pipeline-ul de recrutare.

Este un proiect de invatare, dezvoltat ca aplicatie desktop locala, fiind conceput sa ruleze direct pe calculatorul utilizatorului, utilizand o baza de date locala.

Funcționalități Cheie:

Căutare Full-Text: Implementată în baza de date pentru căutarea rapidă și eficientă a cuvintelor cheie în textele CV-urilor.

Sistem de Matching M:N: Algoritm SQL pentru potrivirea competențelor deținute de candidați cu cerințele obligatorii și opționale ale joburilor.

Pipeline de Recrutare: Utilizarea view-urilor pentru a genera o privire de ansamblu în timp real asupra stadiului fiecărei aplicații (Screening, Interviu HR, Interviu Tehnic, Ofertat).

Ranking Candidați: Sistem automatizat direct în baza de date care evaluează, punctează și ordonează candidații în funcție de relevanța lor pentru un anumit job.

Scor de Compatibilitate: Calcularea automată a unui procent de potrivire între profilul candidatului și fișa postului, facilitând deciziile recrutorilor.

Analiza Ratei de Conversie: Generarea de statistici detaliate privind eficiența procesului de recrutare pe etape (ex: procentajul de candidați care trec de la faza de interviu la faza de ofertă).

Tehnologii utilizate:

MySQL pentru gestionarea bazei de date.

Java pentru implementarea interfetei grafice si a logicii de business.

JDBC pentru conectarea aplicatiei la serverul de baza de date.

Git pentru versionarea codului sursa.


