import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mecze", "root", "");
        Statement statement = connection.createStatement();

        Scanner scanner = new Scanner(System.in);
        String input = "-1";

        // pętla powtarzania wyboru przez użytkownika
        while (!input.equals("0")) {
            System.out.print("""
                    Co chcesz zrobić? (podaj liczbę odpowiadającą działaniu)
                    1.  Wyświel mecze podanej drużyny
                    2.  Wyświetl gole strzelone przez podaną drużynę
                    3.  Wyświetl listę drużyn, z którymi wygrała podaną drużyna
                    4.  Wyświetl listę drużyn, z którymi przgrała podaną drużyna
                    5.  Wyświetl gole strzelone przez podanego zawodnika
                    6.  Dodaj mecz
                    7.  Dodaj drużynę
                    8.  Dodaj gol
                    9.  Usuń wybrany mecz
                    10. Usuń wybraną drużynę
                    11. Usuń wybrany gol
                    0.  Wyjdź z programu
                    Wybrana operacja: """);
            input = scanner.nextLine();
            try {
                switch (input) {
                    default -> System.out.println("Błąd: Nie rozpoznano polecenia.\n");
                    case "1" -> showGamesByTeam(statement);
                    case "2" -> showGoalsByTeam(statement);
                    case "3" -> showWinsByTeam(statement);
                    case "4" -> showLossesByTeam(statement);
                    case "5" -> showGoalsByPlayer(statement);
                    case "6" -> addGame(statement);
                    case "7" -> addTeam(statement);
                    case "8" -> addGoal(statement);
                    case "9" -> deleteGame(statement);
                    case "10" -> deleteTeam(statement);
                    case "11" -> deleteGoal(statement);
                    case "0" -> System.out.println("Dziękujemy za skorzystanie z programu");
                }
            } catch (Exception e) {
                System.out.println("Wystąpił błąd, spróbuj ponownie");
            }
        }
    }

    // metoda pozwalająca na wybór drużyny, której mecze zostaną wyświetlone
    public static void showGamesByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Mecze której z poniższych drużyn chcesz wyświetlić?");

        // pobranie drużyn z bazy
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");

        // pobranie kodu drużyny od użytkownika
        input = scanner.nextLine().toUpperCase();

        // pobranie pasujących rekordów z bazy
        resultSet = statement.executeQuery(
                "select mdate, stadium, team1, team2 from game where team1 = '" + input + "' OR team2 = '" + input + "'");

        // wyświtlenie pasujących rekordów
        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("team1") + " - " +
                resultSet.getString("team2") + ": " +
                resultSet.getString("mdate") + " - " +
                resultSet.getString("stadium"));
        System.out.println();
    }

    // metoda pozwalająca na wybór drużyny, której gole zostaną wyświetlone
    public static void showGoalsByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Gole której z poniższych drużyn chcesz wyświetlić?");

        // pobranie drużyn z bazy
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");

        // pobranie kodu drużyny od użytkownika
        input = scanner.nextLine().toUpperCase();

        // pobranie pasujących rekordów z bazy
        resultSet = statement.executeQuery(
                "select goal.player, game.team1, game.team2, game.mdate, goal.gtime from goal " +
                        "inner join game on game.id = goal.matchid " +
                        "where goal.teamid = '" + input + "' order by game.mdate desc");

        // wyświtlenie pasujących rekordów
        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("player") + " - (" +
                    resultSet.getString("team1") + " - " + resultSet.getString("team2") +
                    ") " + resultSet.getString("mdate") + " - " +
                    resultSet.getString("gtime") + "'" );
        System.out.println();
    }


    // metoda pozwalająca na wybór drużyny, której wygrane mecze zostaną wyświetlone
    public static void showWinsByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Wygrane której z poniższych drużyn chcesz wyświetlić?");

        // pobranie drużyn z bazy
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");

        // pobranie kodu drużyny od użytkownika
        input = scanner.nextLine().toUpperCase();

        // pobranie pasujących rekordów z bazy
        resultSet = statement.executeQuery("SELECT " +
                "CASE " +
                "WHEN team1 > team2 THEN team1 " +
                "WHEN team1 < team2 THEN team2 " +
                "END as Won, " +
                "CASE " +
                "WHEN team1 > team2 THEN team2 " +
                "WHEN team1 < team2 THEN team1 " +
                "END as Lost " +
                "FROM game");

        // wyświtlenie pasujących rekordów
        System.out.print("\n" + input + " wygrali z: ");
        while (resultSet.next())
            if (resultSet.getString("Won").equals(input))
                System.out.print(resultSet.getString("Lost") + ", ");
        System.out.println("\n");
    }


    // metoda pozwalająca na wybór drużyny, której przegrane mecze zostaną wyświetlone
    public static void showLossesByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Przegrane której z poniższych drużyn chcesz wyświetlić?");

        // pobranie drużyn z bazy
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");

        // pobranie kodu drużyny od użytkownika
        input = scanner.nextLine().toUpperCase();

        // pobranie pasujących rekordów z bazy
        resultSet = statement.executeQuery("SELECT " +
                "CASE " +
                "WHEN team1 > team2 THEN team1 " +
                "WHEN team1 < team2 THEN team2 " +
                "END as Won, " +
                "CASE " +
                "WHEN team1 > team2 THEN team2 " +
                "WHEN team1 < team2 THEN team1 " +
                "END as Lost " +
                "FROM game");

        // wyświtlenie pasujących rekordów
        System.out.print("\n" + input + " przegrali z: ");
        while (resultSet.next())
            if (resultSet.getString("Lost").equals(input))
                System.out.print(resultSet.getString("Won") + ", ");
        System.out.println("\n");
    }


    // metoda pozwalająca na wybór zawodnika, którego gole zostaną wyświetlone
    public static void showGoalsByPlayer(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Gole którego z poniższych graczy chcesz wyświetlić?");

        // pobranie danych zawodników z bazy
        resultSet = statement.executeQuery("select distinct player from goal order by player asc");
        while (resultSet.next()) System.out.println(resultSet.getString("player"));
        System.out.print("Imię i nazwisko gracza: ");

        // pobranie danych zawodnika od użytkownika
        input = scanner.nextLine();

        // pobranie pasujących rekordów z bazy
        resultSet = statement.executeQuery(
                "select goal.player, game.team1, game.team2, game.mdate, goal.gtime from goal " +
                        "inner join game on game.id = goal.matchid " +
                        "where goal.player = '" + input + "' order by game.mdate desc");

        // wyświtlenie pasujących rekordów
        System.out.println("\n" + input + ":");
        while (resultSet.next())
            System.out.println("(" + resultSet.getString("team1") + " - " +
                    resultSet.getString("team2") + ") " +
                    resultSet.getString("mdate") + " - " +
                    resultSet.getString("gtime") + "'" );
        System.out.println();
    }

    // metoda pozwalająca na dodanie meczu do bazy
    public static void addGame(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String team1, team2, stadium, mdate;
        int id = 0;
        ResultSet resultSet;

        System.out.println("Wybierz drużyny z listy poniżej.");

        // pobranie drużyn z bazy
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));

        // pobranie danych o drużynach i lokalizacji od użytkownika
        System.out.print("Drużyna 1 id: ");
        team1 = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, team1)) throw new Exception("");
        System.out.print("Drużyna 2 id: ");
        team2 = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, team2)) throw new Exception("");
        System.out.print("\nPodaj nazwę stadionu: ");
        stadium = scanner.nextLine();
        System.out.print("\nPodaj datę (dd [month] yyyy): ");
        mdate = scanner.nextLine();

        // wyznaczenie kolejnego id
        resultSet = statement.executeQuery("select max(id) as id from game");
        while (resultSet.next()) id = resultSet.getInt("id");
        id++;

        // wprowadzenie danych do bazy
        statement.executeUpdate(
                "insert into game (id, mdate, stadium, team1, team2) " +
                        "Values ('" + id + "','" + mdate + "','" + stadium + "','" + team1 + "','" + team2 + "')");

        System.out.println("Mecz został dodany do bazy danych\n");
    }

    // metoda pozwalająca na dodanie drużyny do bazy
    public static void addTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String id, teamname, coach;

        // pobranie danych o kraju oraz trenerze od użytkownika
        System.out.print("ID Drużyny (3 litery): ");
        id = scanner.nextLine().toUpperCase();
        if(teamExists(statement, id)) throw new Exception("");
        System.out.print("\nPodaj pełną nazwę drużyny (kraju): ");
        teamname = scanner.nextLine();
        System.out.print("\nPodaj imięi nazwisko trenera: ");
        coach = scanner.nextLine();

        // wprowadzenie danych do bazy
        statement.executeUpdate(
                "insert into eteam (id, teamname, coach) " +
                        "Values ('" + id + "','" + teamname + "','" + coach + "')");

        System.out.println("Drużyna została dodana do bazy danych\n");
    }

    // metoda pozwalająca na dodanie gola do bazy
    public static void addGoal(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        String teamid, player;
        int matchid, gtime;

        System.out.println("Wybierz drużynę z listy poniżej.");

        // pobranie drużyn z bazy
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));

        // pobranie danych o drużynie oraz zawodniku od użytkownika
        System.out.print("ID Drużyny: ");
        teamid = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, teamid)) throw new Exception("");
        System.out.print("\nPodaj imię i nazwisko gracza, który strzelił gola: ");
        player = scanner.nextLine();

        // pobranie i wyświetlenie meczy z bazy
        System.out.println("Wybierz mecz.");
        resultSet = statement.executeQuery(
                "select * from game where team1 = '" + teamid + "' OR team2 = '" + teamid + "'");
        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("id") + ": " +
                    resultSet.getString("team1") + " - " +
                    resultSet.getString("team2") + ": " +
                    resultSet.getString("mdate") + " - " +
                    resultSet.getString("stadium"));

        // pobranie id meczu od użytkownika
        System.out.print("ID: ");
        matchid = scanner.nextInt();
        if(!gameExists(statement, matchid,teamid)) throw new Exception("");

        // pobranie minuty meczu od użytkownika
        System.out.print("\nPodaj minutę meczu: ");
        gtime = scanner.nextInt();

        // wprowadzenie danych do bazy
        statement.executeUpdate(
                "insert into goal (matchid, teamid, player, gtime) " +
                        "Values ('" + matchid + "','" + teamid + "','" + player + "','" + gtime + "')");

        System.out.println("Gol został dodany do bazy danych\n");
    }

    // metoda pozwalająca na usunięcie wybranego meczu z bazy
    public static void deleteGame(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        int id;

        System.out.println("\nWybierz mecz z listy poniżej.");

        // pobranie i wyświtlenie meczy z bazy
        resultSet = statement.executeQuery(
                "select * from game");
        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("id") + ": " +
                    resultSet.getString("team1") + " - " +
                    resultSet.getString("team2") + ": " +
                    resultSet.getString("mdate") + " - " +
                    resultSet.getString("stadium"));

        // pobranie id meczu od użytkownika
        System.out.print("ID Meczu: ");
        id = scanner.nextInt();
        if(!gameExists(statement, id)) throw new Exception("");

        // wprowadzenie danych do bazy
        statement.executeUpdate(
                "delete from game where id = '" + id + "'");

        System.out.println("Mecz został usunięty z bazy danych\n");
    }

    // metoda pozwalająca na usunięcie wybranej drużyny z bazy
    public static void deleteTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        String id;

        System.out.println("Wybierz drużynę z listy poniżej.");

        // pobranie i wyświtlenie drużyn z bazy
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));

        // pobranie id drużyny od użytkownika
        System.out.print("ID Drużyny: ");
        id = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, id)) throw new Exception("");

        // wprowadzenie danych do bazy
        statement.executeUpdate(
                "delete from eteam where id = '" + id + "'");

        System.out.println("Drużyna została usunięta z bazy danych\n");
    }

    // metoda pozwalająca na usunięcie wybranego gola z bazy
    public static void deleteGoal(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        int matchid, gtime;

        System.out.println("\nWybierz mecz z listy poniżej.");

        // pobranie i wyświtlenie meczy z bazy
        resultSet = statement.executeQuery(
                "select * from game");
        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("id") + ": " +
                    resultSet.getString("team1") + " - " +
                    resultSet.getString("team2") + ": " +
                    resultSet.getString("mdate") + " - " +
                    resultSet.getString("stadium"));

        // pobranie id meczu od użytkownika
        System.out.print("ID Meczu: ");
        matchid = scanner.nextInt();
        if(!gameExists(statement, matchid)) throw new Exception("");

        System.out.println("Wybierz minutę meczu.");

        // pobranie i wyświtlenie goli z bazy
        resultSet = statement.executeQuery(
                "select gtime from goal where matchid = '" + matchid + "'");
        while (resultSet.next())
            System.out.println(resultSet.getString("gtime"));

        // pobranie minuty gola od użytkownika
        System.out.print("Minuta meczu: ");
        gtime = scanner.nextInt();
        if(!goalExists(statement, matchid, gtime)) throw new Exception("");

        // wprowadzenie danych do bazy
        statement.executeUpdate(
                "delete from goal where matchid = '" + matchid + "' AND gtime = '" + gtime + "'");

        System.out.println("Gol został usunięty z bazy danych\n");
    }

    // metoda sprawdzająca czy kod drużyny istnieje w bazie
    public static boolean teamExists(Statement statement, String teamID) throws Exception {
        ResultSet resultSet = statement.executeQuery("select id from eteam");
        while(resultSet.next())
            if(resultSet.getString("id").equals(teamID))
                return true;
        return false;
    }

    // metoda sprawdzająca czy mecz istnieje w bazie (id oraz kod drużyny)
    public static boolean gameExists(Statement statement, int matchID, String teamID) throws Exception {
        ResultSet resultSet = statement.executeQuery("select id from game where team1 = '" + teamID + "' OR team2 = '" + teamID + "'");
        while(resultSet.next())
            if(resultSet.getInt("id") == (matchID))
                return true;
        return false;
    }

    // metoda sprawdzająca czy mecz istnieje w bazie (tylko id)
    public static boolean gameExists(Statement statement, int matchID) throws Exception {
        ResultSet resultSet = statement.executeQuery("select id from game");
        while(resultSet.next())
            if(resultSet.getInt("id") == (matchID))
                return true;
        return false;
    }

    // metoda sprawdzająca czy gol istnieje w bazie
    public static boolean goalExists(Statement statement, int matchid, int minute) throws Exception {
        ResultSet resultSet = statement.executeQuery("select gtime from goal where matchid = '" + matchid + "'");
        while(resultSet.next())
            if(resultSet.getInt("gtime") == (minute))
                return true;
        return false;
    }
}
