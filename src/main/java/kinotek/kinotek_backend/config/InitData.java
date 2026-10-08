package kinotek.kinotek_backend.config;

import kinotek.kinotek_backend.model.cinema.*;
import kinotek.kinotek_backend.model.user.Customer;
import kinotek.kinotek_backend.repository.cinema.*;
import kinotek.kinotek_backend.repository.user.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static javax.management.Query.times;
// attributter og constructor er opsat manuelt.
// Resten i denne klasse er genereret af Claude Code - se prompt

// Claude Code Prompt (opsummeret)
/*

Kontekst:
- 3. semester, Spring Boot + JPA. Projekt: Kino (lille biograf på landet med 2 sale).
- Vedlagt: projektbeskrivelse (Projekt1-forslag.docx) og ER-diagram.
- InitData ligger i config-pakken og implementerer CommandLineRunner.
- Constructor injection i stedet for @Autowired.
- InitData skal bruges i både dev (H2), test og prod (MySQL).

Opgave: Generér init data. Stil alle relevante spørgsmål, du har brug for.

Film:
- Film udgivet i år (2026), som en mindre biograf på landet ville vise.
- Ca. 10 film. Gerne lidt flere, hvis historikken kræver det.
- Danske beskrivelser af filmene.
- imageRef udfylder vi selv bagefter.

AgeRating (lookup-tabel, manuelle id'er):
- Det danske system: A, 7, 11, 15.

Genre (lookup-tabel, manuelle id'er):
- Find genrer på IMDb.
- Mange-til-mange mellem movie og genre. Tilføj flere genrer pr. film, hvor det er relevant (fx Sci-Fi + Komedie).

Auditorium:
- To sale.
  - Lille sal: 20 rækker med 12 sæder pr. række.
  - Store sal: 25 rækker med 16 sæder pr. række.
- Rækker nummereres med bogstaver A–Z (A forrest, Z bagerst).
- Sæder nummereres simpelt fra 1 til n, så der er ingen forskel på lige og ulige ned langs midten.
- Handicapsæder: ja.

Showings:
- Blokke af 3 timer, forskudt mellem salene,
  fx Film A kl. 15 i lille sal, Film B kl. 16.30 i store sal, Film A kl. 18 i lille sal osv.
- Spilletider: forslaget med daglige forestillinger, men med tidligere tider lørdag og søndag.
- Periode: historik fra august 2026 og frem til slutningen af december 2026.

Kunder og bookinger:
- Ja til testkunder, fakturaer og bookinger.
- Historik med bookinger tilbage fra august 2026.
- Belægningsgrad:
  - ca. 30 % af filmene performer dårligt: 10–20 % belægning i gennemsnit
  - ca. 10 % af filmene performer godt: over 80 % belægning
  - resten ligger midt imellem

Afgrænsning:
- Ingen medarbejderdata.
- Vi har selv rettet modelfejlene.
- Giv mig koden som tekst, jeg selv kan kopiere ind, i stedet for at rette i projektet.


 */

// Claude Code egne valg
/*
Mine egne valg

Disse punkter havde I ikke specificeret, så jeg bestemte dem selv:

Filmene: 17 film fundet ved websøgning (danske premierelister, Wikipedia, IMDb).
Fem spilletider og alle aldersgrænserne er mine skøn.
Jeg valgte, hvilke film der blev flops og hits.
Spilletider:
Hverdage: kl. 15, 18 og 21 i lille sal og kl. 16:30 og 19:30 i store sal.
Weekend og helligdage: derudover kl. 12 i lille sal og kl. 13:30 i store sal.
Lukket juleaften og nytårsaften.
Programregler: film tilladt fra 15 år vises ikke før kl. 18, der er børnefilm om formiddagen i weekenden, og store sal viser de mest populære film.
Handicapsæder: de to yderste sæder i hver side af række A.
Kunder og fakturaer:
40 testkunder. Ca. 60 % af fakturaerne har en kunde tilknyttet.
Grupper på 1–6 personer sidder samlet, og hver gruppe bliver én faktura.
Bookinger:
Fremtidige forestillinger har kun forsalg.
Fast random-seed, så dataene bliver ens hver gang.
Et tjek, der springer InitData over, hvis databasen allerede har data.

Hvis I skal dokumentere det i rapporten, kan I skrive, at koden blev testet med en simulering med repositories i hukommelsen og ikke mod en rigtig Hibernate/H2-database.
 */

@Component
@Profile({"dev", "prod"})
public class InitData implements CommandLineRunner {


    // ---------------------------------------------------------------- konfiguration

    private static final LocalDate PROGRAM_START = LocalDate.of(2026, 8, 1);
    private static final LocalDate PROGRAM_END = LocalDate.of(2026, 12, 31);

    /** Biografen holder lukket juleaften og nytårsaften. */
    private static final Set<LocalDate> CLOSED_DAYS = Set.of(
            LocalDate.of(2026, 12, 24),
            LocalDate.of(2026, 12, 31));

    /** Helligdage spiller efter weekendprogrammet (tidlige forestillinger). */
    private static final Set<LocalDate> HOLIDAYS = Set.of(
            LocalDate.of(2026, 12, 25),
            LocalDate.of(2026, 12, 26));

    /** Spilletider. Hver forestilling er en blok på 3 timer, salene er forskudt 1,5 time. */
    private static final List<LocalTime> SMALL_WEEKDAY = times("15:00", "18:00", "21:00");
    private static final List<LocalTime> LARGE_WEEKDAY = times("16:30", "19:30");
    private static final List<LocalTime> SMALL_WEEKEND = times("12:00", "15:00", "18:00", "21:00");
    private static final List<LocalTime> LARGE_WEEKEND = times("13:30", "16:30", "19:30");

    /** Fast seed = samme "tilfældige" data hver gang appen starter. */
    private final Random random = new Random(42);

    // ---------------------------------------------------------------- repositories
    // Booking, SeatRow og Seat har ikke brug for egne repositories her – de gemmes via cascade
    // (Auditorium -> SeatRow -> Seat og Invoice -> Booking).

    private final AgeRatingRepository ageRatingRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final BookingRepository bookingRepository;
    private final GenreRepository genreRepository;
    private final InvoiceRepository invoiceRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    private final SeatRowRepository seatRowRepository;
    private final ShowingRepository showingRepository;
    private final CustomerRepository customerRepository;

    public InitData(AgeRatingRepository ageRatingRepository,
                    AuditoriumRepository auditoriumRepository,
                    BookingRepository bookingRepository,
                    GenreRepository genreRepository,
                    InvoiceRepository invoiceRepository,
                    MovieRepository movieRepository,
                    SeatRepository seatRepository,
                    SeatRowRepository seatRowRepository,
                    ShowingRepository showingRepository,
                    CustomerRepository customerRepository) {
        this.ageRatingRepository = ageRatingRepository;
        this.auditoriumRepository = auditoriumRepository;
        this.bookingRepository = bookingRepository;
        this.genreRepository = genreRepository;
        this.invoiceRepository = invoiceRepository;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
        this.seatRowRepository = seatRowRepository;
        this.showingRepository = showingRepository;
        this.customerRepository = customerRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        if (movieRepository.count() > 0) {
            System.out.println("InitData: databasen har allerede data – springer over.");
            return;
        }

        Map<String, AgeRating> ageRatings = createAgeRatings();
        Map<String, Genre> genres = createGenres();

        Auditorium small = createAuditorium("Lille sal", 20, 12);
        Auditorium large = createAuditorium("Store sal", 25, 16);

        List<ProgramEntry> program = createMovies(ageRatings, genres);
        List<Showing> showings = createShowings(program, small, large);
        List<Customer> customers = createCustomers();
        int bookings = createBookings(showings, program, customers);

        System.out.printf("InitData: %d film, %d forestillinger, %d kunder, %d bookinger oprettet.%n",
                program.size(), showings.size(), customers.size(), bookings);
    }

    // ---------------------------------------------------------------- lookup-tabeller (manuelle id'er)

    private Map<String, AgeRating> createAgeRatings() {
        String[] ratings = {"A", "7", "11", "15"};
        List<AgeRating> list = new ArrayList<>();
        for (int i = 0; i < ratings.length; i++) {
            AgeRating ar = new AgeRating();
            ar.setId(i + 1);
            ar.setAgeRating(ratings[i]);
            list.add(ar);
        }
        Map<String, AgeRating> map = new HashMap<>();
        for (AgeRating saved : ageRatingRepository.saveAll(list)) {
            map.put(saved.getAgeRating(), saved);
        }
        return map;
    }

    private Map<String, Genre> createGenres() {
        // Genrenavne som på IMDb
        String[] names = {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime",
                "Documentary", "Drama", "Family", "Fantasy", "History", "Horror", "Music",
                "Musical", "Mystery", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"};
        List<Genre> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Genre g = new Genre();
            g.setId(i + 1);
            g.setGenreName(names[i]);
            list.add(g);
        }
        Map<String, Genre> map = new HashMap<>();
        for (Genre saved : genreRepository.saveAll(list)) {
            map.put(saved.getGenreName(), saved);
        }
        return map;
    }

    // ---------------------------------------------------------------- sale, rækker og sæder

    /** Rækker hedder A (forrest) ... Z (bagerst). Sæderne nummereres 1..n. */
    private Auditorium createAuditorium(String name, int rowCount, int seatsPerRow) {
        if (rowCount > 26) {
            throw new IllegalArgumentException("Maks 26 rækker (A-Z)");
        }
        Auditorium auditorium = new Auditorium();
        auditorium.setAuditoriumName(name);

        for (int r = 0; r < rowCount; r++) {
            SeatRow row = new SeatRow();
            row.setRowLetter(String.valueOf((char) ('A' + r)));
            auditorium.addRow(row);

            for (int s = 1; s <= seatsPerRow; s++) {
                Seat seat = new Seat();
                seat.setSeatNumber(s);
                // Handicappladser: de to yderste sæder i hver side af række A
                seat.setAccessible(r == 0 && (s <= 2 || s > seatsPerRow - 2));
                row.addSeat(seat);
            }
        }
        // Cascade ALL: Auditorium -> SeatRow -> Seat gemmes i ét kald
        return auditoriumRepository.save(auditorium);
    }

    // ---------------------------------------------------------------- film

    private enum Performance {
        FLOP(0.10, 0.20),     // dårlig belægning, tages af plakaten før tid
        MIDDEL(0.35, 0.60),
        HIT(0.82, 0.92);

        final double min, max;

        Performance(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }

    /**
     * Data om én film. programStart/End og performance gemmes ikke i databasen,
     * de bruges kun til at generere forestillinger og bookinger.
     */
    private record FilmData(String title, int duration, String ageRating, String imdbId,
                            LocalDate programStart, LocalDate programEnd, Performance performance,
                            String description, String... genres) {
    }

    /** En gemt film + dens programdata og den belægningsgrad den "skal" ramme. */
    private record ProgramEntry(Movie movie, FilmData data, double targetOccupancy) {
        boolean isPlaying(LocalDate day) {
            return !day.isBefore(data.programStart()) && !day.isAfter(data.programEnd());
        }

        boolean isFamilyFilm() {
            return data.ageRating().equals("A") || data.ageRating().equals("7");
        }
    }

    private List<FilmData> filmData() {
        // Spilletider markeret "ca." var ikke offentliggjort ved oprettelsen og er estimater.
        // Aldersgrænser er vores eget skøn efter det danske system – tjek Medierådet.
        return List.of(
                new FilmData("Toy Story 5", 102, "A", "tt29355505",
                        date(8, 1), date(9, 6), Performance.MIDDEL,
                        "Woody, Buzz og Jessie får kamp til stregen, da en tablet bliver Bonnies nye yndlingslegetøj. "
                                + "Legetøjet må finde ud af, om der stadig er plads til dem i en verden af skærme.",
                        "Animation", "Adventure", "Comedy", "Family"),
                new FilmData("Minions & Monsters", 90, "7", "tt32890033",
                        date(8, 1), date(9, 27), Performance.MIDDEL,
                        "I 1920'ernes Hollywood drømmer tre minions om at lave film. Da de finder en magisk tryllebog, "
                                + "kommer de til at hidkalde en flok monstre – og må rydde op i kaosset, før det er for sent.",
                        "Animation", "Comedy", "Family", "Fantasy"),
                new FilmData("The Odyssey", 173, "15", "tt33764258",
                        date(8, 1), date(9, 20), Performance.MIDDEL,
                        "Efter Den Trojanske Krig kæmper kong Odysseus sig hjem mod Ithaka og sin hustru Penelope. "
                                + "Christopher Nolans episke filmatisering af Homers klassiker – fyldt med guder, uhyrer og fristelser.",
                        "Action", "Adventure", "Fantasy", "Drama"),
                new FilmData("Spider-Man: Brand New Day", 145, "11", "tt22084616",
                        date(8, 1), date(10, 4), Performance.MIDDEL,
                        "Verden har glemt, hvem Peter Parker er, og han lever nu kun for at beskytte New York som Spider-Man. "
                                + "Men kræfterne forandrer sig, og ensomheden tærer på ham, da han møder en ung telepat, der leder efter sin forsvundne søster.",
                        "Action", "Adventure", "Sci-Fi"),
                new FilmData("The Dog Stars", 118, "15", "tt21285562",
                        date(8, 27), date(9, 13), Performance.FLOP,
                        "Mange år efter en pandemi, der næsten udslettede menneskeheden, lever den tidligere mekaniker Hig "
                                + "med sin hund Jasper i Colorado. Et svagt radiosignal giver ham håb om, at der findes mere derude. Instrueret af Ridley Scott.",
                        "Sci-Fi", "Thriller", "Drama"),
                new FilmData("Nøjsomheden", 105, "11", "tt39379748",
                        date(8, 27), date(9, 17), Performance.FLOP,
                        "Den boglige Mona bor i et udsat boligområde i Helsingør og forelsker sig i Nikolaj fra den pæne del af byen. "
                                + "Samtidig prøver hun at få sin fætter til at forsone sig med deres døende tante. Instrueret af Hella Joof.",
                        "Comedy", "Romance", "Drama"),
                new FilmData("Practical Magic 2", 130, "11", "tt32588798",
                        date(9, 10), date(11, 8), Performance.MIDDEL,
                        "Søstrene Owens er tilbage, og familiens gamle forbandelse – der rammer enhver mand, som forelsker sig i en Owens-kvinde – "
                                + "truer nu en ny generation. Sandra Bullock og Nicole Kidman i efterfølgeren til kultklassikeren.",
                        "Fantasy", "Romance", "Comedy"),
                new FilmData("Heart of the Beast", 101, "15", "tt7526136",
                        date(9, 24), date(10, 11), Performance.FLOP,
                        "En tidligere specialsoldat og hans traumatiserede kamphund Odin overlever et flystyrt i Alaskas vildmark. "
                                + "Nu venter en barsk vandring på næsten 100 kilometer mod sikkerhed. Med Brad Pitt.",
                        "Adventure", "Thriller", "Drama"),
                new FilmData("Offroad", 86, "7", "tt36854705",
                        date(10, 1), date(12, 20), Performance.HIT,
                        "Tre veninder tager til Tenerife for at finde frihed – og en mand ved navn Francisco. "
                                + "Ferien udvikler sig til en kaotisk roadtrip i en stjålen autocamper. Dansk komedie af Rasmus Heide.",
                        "Comedy", "Adventure"),
                new FilmData("Digger", 129, "11", "tt31450459",
                        date(10, 1), date(10, 18), Performance.FLOP,
                        "Tom Cruise spiller oliemagnaten Digger Rockwell, verdens mægtigste mand, der udløser en global katastrofe "
                                + "og nu må bevise, at han er menneskehedens redningsmand. Satirisk komedie af Alejandro G. Iñárritu.",
                        "Comedy", "Drama"),
                new FilmData("Verity", 117, "15", "tt32261958",
                        date(10, 1), date(10, 22), Performance.FLOP,
                        "En forfatter i pengenød hyres til at færdiggøre en bestsellerserie for Verity Crawford, der ligger hjælpeløs efter en ulykke. "
                                + "I hjemmet finder hun et manuskript med foruroligende hemmeligheder. Med Anne Hathaway og Dakota Johnson.",
                        "Thriller", "Mystery"),
                new FilmData("The Social Reckoning", 125 /* ca. */, "11", "tt37510326",
                        date(10, 8), date(11, 29), Performance.MIDDEL,
                        "Aaron Sorkins opfølger på The Social Network om whistlebloweren Frances Haugen og journalisten Jeff Horwitz, "
                                + "der i 2021 afslører Facebooks interne dokumenter. Jeremy Strong spiller Mark Zuckerberg.",
                        "Drama", "Biography"),
                new FilmData("Klara and the Sun", 116, "11", "tt14371256",
                        date(10, 22), date(12, 13), Performance.MIDDEL,
                        "I en fremtid splittet af genteknologi bliver den udrangerede robot Klara købt som ledsager for en syg pige. "
                                + "Kan en maskines kærlighed være ægte? Efter Kazuo Ishiguros roman, instrueret af Taika Waititi.",
                        "Sci-Fi", "Drama"),
                new FilmData("The Cat in the Hat", 90 /* ca. */, "A", "tt2321555",
                        date(11, 5), date(12, 31), Performance.MIDDEL,
                        "Katten med hatten får sin sværeste opgave til dato: at muntre søskendeparret Gabby og Sebastian op, "
                                + "der har svært ved at falde til efter flytningen til en ny by. Animeret komedie efter Dr. Seuss.",
                        "Animation", "Comedy", "Family", "Fantasy"),
                new FilmData("The Hunger Games: Sunrise on the Reaping", 140 /* ca. */, "15", "tt32558705",
                        date(11, 19), date(12, 31), Performance.MIDDEL,
                        "24 år før Katniss' tid trækkes den unge Haymitch Abernathy ud til de 50. Hungerleje – et jubilæumsspil "
                                + "med dobbelt så mange deltagere. Prequel efter Suzanne Collins' roman.",
                        "Action", "Adventure", "Sci-Fi"),
                new FilmData("Avengers: Doomsday", 165 /* ca. */, "11", "tt21357150",
                        date(12, 16), date(12, 31), Performance.HIT,
                        "Multiverset er ved at bryde sammen, og Avengers, X-Men, Fantastic Four og helte fra andre universer "
                                + "må stå sammen mod Doctor Doom – spillet af Robert Downey Jr.",
                        "Action", "Adventure", "Sci-Fi"),
                new FilmData("Svindler", 105 /* ca. */, "11", "tt38855775",
                        date(12, 25), date(12, 31), Performance.MIDDEL,
                        "En svindler og en efterforsker tvinges til at arbejde sammen for at fange en endnu større forbryder "
                                + "på tværs af Europa. Dansk krimikomedie af Mikkel Serup.",
                        "Comedy", "Crime")
        );
    }

    private List<ProgramEntry> createMovies(Map<String, AgeRating> ageRatings, Map<String, Genre> genres) {
        List<ProgramEntry> program = new ArrayList<>();
        for (FilmData fd : filmData()) {
            Movie movie = new Movie();
            movie.setMovieName(fd.title());
            movie.setDuration(fd.duration());
            movie.setDescription(fd.description());
            movie.setImdbRef("https://www.imdb.com/title/" + fd.imdbId() + "/");
            movie.setImageRef(null); // udfyldes senere
            movie.setAgeRating(ageRatings.get(fd.ageRating()));

            Set<Genre> movieGenres = new HashSet<>();
            for (String genreName : fd.genres()) {
                Genre genre = genres.get(genreName);
                if (genre == null) {
                    throw new IllegalStateException("Ukendt genre: " + genreName);
                }
                movieGenres.add(genre);
            }
            movie.setGenres(movieGenres);

            Movie saved = movieRepository.save(movie);
            double target = between(fd.performance().min, fd.performance().max);
            program.add(new ProgramEntry(saved, fd, target));
        }
        return program;
    }

    // ---------------------------------------------------------------- forestillinger

    /** Et ledigt "hul" i programmet: tidspunkt + sal. */
    private record Slot(LocalTime time, Auditorium auditorium) {
    }

    private List<Showing> createShowings(List<ProgramEntry> program, Auditorium small, Auditorium large) {
        List<Showing> showings = new ArrayList<>();
        Map<Movie, Integer> totalCount = new IdentityHashMap<>();   // visninger i alt pr. film

        for (LocalDate day = PROGRAM_START; !day.isAfter(PROGRAM_END); day = day.plusDays(1)) {
            if (CLOSED_DAYS.contains(day)) {
                continue;
            }
            LocalDate today = day; // lambdas kræver en (effectively) final variabel
            List<ProgramEntry> playing = program.stream().filter(p -> p.isPlaying(today)).toList();
            if (playing.isEmpty()) {
                continue;
            }
            boolean weekend = isWeekendOrHoliday(day);

            // Dagens slots i tidsrækkefølge på tværs af begge sale (12:00, 13:30, 15:00, 16:30 ...)
            List<Slot> slots = new ArrayList<>();
            (weekend ? SMALL_WEEKEND : SMALL_WEEKDAY).forEach(t -> slots.add(new Slot(t, small)));
            (weekend ? LARGE_WEEKEND : LARGE_WEEKDAY).forEach(t -> slots.add(new Slot(t, large)));
            slots.sort(Comparator.comparing(Slot::time));

            Map<Movie, Integer> todayCount = new IdentityHashMap<>();
            for (Slot slot : slots) {
                List<ProgramEntry> candidates = new ArrayList<>(candidatesFor(playing, slot.time(), weekend));

                // Store sal: kun de to mest populære af kandidaterne
                if (slot.auditorium() == large) {
                    candidates.sort(Comparator.comparingDouble(ProgramEntry::targetOccupancy).reversed());
                    candidates = candidates.subList(0, Math.min(2, candidates.size()));
                }

                // Vælg den film der har fået færrest visninger i dag, derefter i forhold til popularitet
                ProgramEntry pick = Collections.min(candidates, Comparator
                        .comparingInt((ProgramEntry p) -> todayCount.getOrDefault(p.movie(), 0))
                        .thenComparingDouble(p -> totalCount.getOrDefault(p.movie(), 0) / p.targetOccupancy()));

                todayCount.merge(pick.movie(), 1, Integer::sum);
                totalCount.merge(pick.movie(), 1, Integer::sum);
                showings.add(newShowing(pick.movie(), slot.auditorium(), day, slot.time()));
            }
        }
        return showingRepository.saveAll(showings);
    }

    /**
     * Hvilke film må spille på et givet tidspunkt?
     * - Weekend før kl. 15: børnefilm (A/7) hvis der er nogen.
     * - Før kl. 18: ingen film tilladt fra 15 år.
     * Falder tilbage til alle aktuelle film, hvis reglerne efterlader en tom liste.
     */
    private List<ProgramEntry> candidatesFor(List<ProgramEntry> playing, LocalTime time, boolean weekend) {
        if (weekend && time.isBefore(LocalTime.of(15, 0))) {
            List<ProgramEntry> family = playing.stream().filter(ProgramEntry::isFamilyFilm).toList();
            if (!family.isEmpty()) {
                return family;
            }
        }
        if (time.isBefore(LocalTime.of(18, 0))) {
            List<ProgramEntry> notAdult = playing.stream()
                    .filter(p -> !p.data().ageRating().equals("15")).toList();
            if (!notAdult.isEmpty()) {
                return notAdult;
            }
        }
        return playing;
    }

    private Showing newShowing(Movie movie, Auditorium auditorium, LocalDate day, LocalTime time) {
        Showing showing = new Showing();
        showing.setMovie(movie);
        showing.setAuditorium(auditorium);
        showing.setDateTime(LocalDateTime.of(day, time));
        return showing;
    }

    // ---------------------------------------------------------------- kunder

    private List<Customer> createCustomers() {
        String[] firstNames = {"Anna", "Mads", "Sofie", "Jens", "Ida", "Lars", "Freja", "Peter", "Emma", "Søren",
                "Mette", "Niels", "Karen", "Frederik", "Laura", "Mikkel", "Camilla", "Rasmus", "Julie", "Henrik"};
        String[] lastNames = {"Jensen", "Nielsen", "Hansen", "Pedersen", "Andersen", "Christensen", "Larsen",
                "Sørensen", "Rasmussen", "Jørgensen", "Petersen", "Madsen", "Kristensen", "Olsen", "Thomsen"};

        List<Customer> customers = new ArrayList<>();
        customers.add(newCustomer("Test Kunde", "test@kinotek.dk"));
        for (int i = 1; i < 40; i++) {
            String first = firstNames[random.nextInt(firstNames.length)];
            String last = lastNames[random.nextInt(lastNames.length)];
            String email = (first + "." + last + i + "@example.dk").toLowerCase()
                    .replace("ø", "oe").replace("æ", "ae").replace("å", "aa");
            customers.add(newCustomer(first + " " + last, email));
        }
        return customerRepository.saveAll(customers);
    }

    private Customer newCustomer(String name, String email) {
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setPassword("test1234");
        return c;
    }

    // ---------------------------------------------------------------- bookinger og fakturaer

    /**
     * Fylder hver forestilling op til filmens belægningsgrad.
     * Forestillinger i fortiden = færdigsolgte. Fremtidige = kun forsalg (stiger tæt på dagen).
     * Hver gruppe (1-6 personer på samme række) bliver én faktura.
     */
    private int createBookings(List<Showing> showings, List<ProgramEntry> program, List<Customer> customers) {
        Map<Movie, ProgramEntry> byMovie = new IdentityHashMap<>();
        program.forEach(p -> byMovie.put(p.movie(), p));

        LocalDateTime now = LocalDateTime.now();
        int total = 0;

        for (Showing showing : showings) {
            ProgramEntry entry = byMovie.get(showing.getMovie());
            double occupancy = occupancyFor(entry, showing.getDateTime(), now);

            int capacity = showing.getAuditorium().getRows().stream().mapToInt(r -> r.getSeats().size()).sum();
            int seatsToBook = (int) Math.round(capacity * occupancy);
            if (seatsToBook == 0) {
                continue;
            }

            List<Invoice> invoices = new ArrayList<>();
            for (List<Seat> group : pickSeatGroups(showing.getAuditorium(), seatsToBook)) {
                Invoice invoice = new Invoice();
                // ca. 60% køber med login, resten er telefon/skranke uden kundekonto
                invoice.setCustomer(random.nextDouble() < 0.6 ? customers.get(random.nextInt(customers.size())) : null);
                invoice.setPurchaseTime(purchaseTimeFor(showing.getDateTime(), now));

                for (Seat seat : group) {
                    Booking booking = new Booking();
                    booking.setSeat(seat);
                    booking.setShowing(showing);
                    booking.setInvoice(invoice);
                    invoice.getBookings().add(booking);
                    total++;
                }
                invoices.add(invoice);
            }
            // Cascade ALL: Invoice -> Booking. Ét saveAll pr. forestilling holder transaktionerne små.
            invoiceRepository.saveAll(invoices);
        }
        return total;
    }

    private double occupancyFor(ProgramEntry entry, LocalDateTime start, LocalDateTime now) {
        DayOfWeek dow = start.getDayOfWeek();
        boolean busyDay = dow == DayOfWeek.FRIDAY || isWeekendOrHoliday(start.toLocalDate());
        double dayFactor = busyDay ? 1.12 : 0.91;   // gennemsnit over en uge ≈ 1
        double noise = between(0.85, 1.15);
        double occupancy = entry.targetOccupancy() * dayFactor * noise;

        if (start.isAfter(now)) {
            // Forsalg: 5% en måned før, stigende til fuld belægning på dagen
            long daysAhead = ChronoUnit.DAYS.between(now.toLocalDate(), start.toLocalDate());
            occupancy *= Math.max(0.05, 1.0 - daysAhead / 30.0);
        }
        return Math.max(0.0, Math.min(1.0, occupancy));
    }

    private LocalDateTime purchaseTimeFor(LocalDateTime showingStart, LocalDateTime now) {
        // Køb mellem 14 dage og 15 minutter før forestillingen – men aldrig i fremtiden
        long minutesBefore = 15 + (long) (random.nextDouble() * 14 * 24 * 60);
        LocalDateTime purchase = showingStart.minusMinutes(minutesBefore);
        if (purchase.isAfter(now)) {
            purchase = now.minusMinutes(random.nextInt(60 * 24 * 7));
        }
        return purchase.withSecond(0).withNano(0);
    }

    /**
     * Vælger sæder i grupper (familier/vennepar sidder samlet).
     * Folk foretrækker midten af salen og midten af rækken. Handicappladser bookes sjældent.
     */
    private List<List<Seat>> pickSeatGroups(Auditorium auditorium, int seatsToBook) {
        List<SeatRow> rows = auditorium.getRows();
        int rowCount = rows.size();
        boolean[][] taken = new boolean[rowCount][];
        for (int r = 0; r < rowCount; r++) {
            List<Seat> seats = rows.get(r).getSeats();
            taken[r] = new boolean[seats.size()];
            for (int s = 0; s < seats.size(); s++) {
                // Handicappladser holdes fri i 95% af forestillingerne
                if (seats.get(s).isAccessible() && random.nextDouble() < 0.95) {
                    taken[r][s] = true;
                }
            }
        }

        List<List<Seat>> groups = new ArrayList<>();
        int remaining = seatsToBook;
        int failedAttempts = 0;

        while (remaining > 0 && failedAttempts < 50) {
            int size = Math.min(remaining, randomGroupSize());
            // Foretrukken række: ca. 60% bagud i salen, normalfordelt
            int r = (int) Math.round(rowCount * 0.6 + random.nextGaussian() * rowCount / 4.0);
            r = Math.max(0, Math.min(rowCount - 1, r));

            int start = findFreeBlock(taken[r], size);
            if (start < 0) {
                failedAttempts++;
                continue;
            }
            List<Seat> group = new ArrayList<>();
            for (int s = start; s < start + size; s++) {
                taken[r][s] = true;
                group.add(rows.get(r).getSeats().get(s));
            }
            groups.add(group);
            remaining -= size;
            failedAttempts = 0;
        }

        // Næsten fuld sal: fyld de sidste ledige pladser enkeltvis
        for (int r = 0; r < rowCount && remaining > 0; r++) {
            for (int s = 0; s < taken[r].length && remaining > 0; s++) {
                if (!taken[r][s] && !rows.get(r).getSeats().get(s).isAccessible()) {
                    taken[r][s] = true;
                    groups.add(List.of(rows.get(r).getSeats().get(s)));
                    remaining--;
                }
            }
        }
        return groups;
    }

    /** Finder den ledige blok af størrelse 'size' der ligger tættest på midten af rækken. -1 hvis ingen. */
    private int findFreeBlock(boolean[] taken, int size) {
        int best = -1;
        double bestDistance = Double.MAX_VALUE;
        double middle = (taken.length - size) / 2.0;
        for (int start = 0; start + size <= taken.length; start++) {
            boolean free = true;
            for (int s = start; s < start + size; s++) {
                if (taken[s]) {
                    free = false;
                    break;
                }
            }
            if (free && Math.abs(start - middle) < bestDistance) {
                best = start;
                bestDistance = Math.abs(start - middle);
            }
        }
        return best;
    }

    private int randomGroupSize() {
        double x = random.nextDouble();
        if (x < 0.15) return 1;
        if (x < 0.60) return 2;
        if (x < 0.72) return 3;
        if (x < 0.92) return 4;
        if (x < 0.98) return 5;
        return 6;
    }

    // ---------------------------------------------------------------- hjælpere

    private boolean isWeekendOrHoliday(LocalDate day) {
        return day.getDayOfWeek() == DayOfWeek.SATURDAY
                || day.getDayOfWeek() == DayOfWeek.SUNDAY
                || HOLIDAYS.contains(day);
    }

    private double between(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private static LocalDate date(int month, int day) {
        return LocalDate.of(2026, month, day);
    }

    private static List<LocalTime> times(String... times) {
        return Arrays.stream(times).map(LocalTime::parse).toList();
    }

}

