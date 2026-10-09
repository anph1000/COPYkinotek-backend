# KinoTek
 
> [Link til website](indsæt-link-her)
> - brugernavn: indsæt her
> - password: indsæt her
 
Vi har udviklet en webapplikation til en lille biograf, Kino, som i dag tager imod alle reservationer over telefonen.
Med applikationen kan biografen planlægge forestillinger, administrere film og håndtere reservationer og billetsalg.
Projektet er udarbejdet som et skoleprojekt (Projekt 1) på 3. semester af Datamatiker uddannelsen.
 
Systemet består af to separate projekter:
- **Backend:** [kinotek-backend](https://github.com/Gruppe-1-KinoTek/kinotek-backend) (Spring Boot REST API)
- **Frontend:** [kinotek-frontend](https://github.com/Gruppe-1-KinoTek/kinotek-frontend) (HTML, CSS og JavaScript)
 
## Indhold
- [Features](#features)
- [Teknologier](#teknologier)
- [Værktøjer](#værktøjer)
- [Modeller og diagrammer](#modeller-og-diagrammer)
- [Installation](#installation)
- [Brug](#brug)
- [Developers](#developers)
 
---
## Features
- Oversigt over film med genre, aldersgrænse og spilletid
- Opret, rediger og slet film (medarbejder)
- Sædeoversigt for en forestilling, så man kan se hvilke sæder der er ledige og optaget
- Reservation, ændring og aflysning af billetter, både for kunder med profil og gæster
- Planlægning af forestillinger: opret, ret og slet forestillinger (admin)
- Programoversigt per sal
- Kunder kan se deres egen ordrehistorik
- Login for medarbejdere
- Fleksibel opbygning af sale, rækker og sæder, så biografen kan få flere sale eller ændre antallet af rækker og sæder uden at koden skal ændres
 
## Teknologier
- ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) _Ver. 25_
- ![spring boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) _Ver. 4.1.1_ (Spring Web MVC og Spring Data JPA)
- ![mysql](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white) _Ver. 8.4_
- ![Maven](https://img.shields.io/badge/apachemaven-C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white) _Ver. 3.9.12_
- ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
- ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
- ![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
- ![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
- _H2 in-memory database til tests_
- _JUnit til tests af backenden_
 
## Værktøjer
- ![intellij](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
- ![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white) til SCRUM, sprints og backlog
- ![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)
- ![Claude](https://img.shields.io/badge/Claude-D97757?style=for-the-badge&logo=claude&logoColor=white)
 
AI er primært brugt til at generere vores testdata (InitData) og som hjælp til fejlhåndtering.
 
## Modeller og diagrammer
 
 
### ER-diagram
ER diagrammet viser databasens tabeller og relationer. En sal (`auditorium`) består af rækker (`seat_row`), som består af sæder (`seat`). Det gør det muligt at tilføje flere sale eller ændre antallet af rækker og sæder uden at ændre koden. En forestilling (`showing`) kobler en film til en sal på et bestemt tidspunkt. En `booking` er ét sæde til én forestilling, og flere bookinger samles på en `invoice`, som tilhører en kunde. Film og genrer har en mange-til-mange relation gennem `movie_genre`, og hver film har én aldersgrænse (`age_rating`). Medarbejdere har en rolle (`role`), som afgør hvad de har adgang til.
 
![ER-diagram](docs/er-diagram.png)
 
## Installation
 
 
### Backend lokalt
 
```
git clone https://github.com/Gruppe-1-KinoTek/kinotek-backend
cd kinotek-backend
./mvnw spring-boot:run
```

 
### Frontend lokalt
```
git clone https://github.com/Gruppe-1-KinoTek/kinotek-frontend
```
 
 
## Developers
 
- Erik Lindkvist Thomsen - [lindkvst](https://github.com/lindkvst)
- Anne-Sophie Phanseeda - [anph1000](https://github.com/anph1000)
- Johan Oliver Larsen - [johanlarsen](https://github.com/johanlarsen)
- Julie Jensine Juul Sundsdal - [jsdsdal](https://github.com/jsdsdal)
- NAVN - [Namirah0310](https://github.com/Namirah0310)
- NAVN - [Bishobos](https://github.com/Bishobos)
 
**Udarbejdet som skoleprojekt (Projekt 1) på Datamatiker 3. semester på EK (Erhvervsakademi København) i uge 40 og 41 i efteråret 2026.**
 
