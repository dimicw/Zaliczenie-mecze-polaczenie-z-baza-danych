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

        boolean keepgoing = true;
        Scanner scanner = new Scanner(System.in);
        String input;

        do {
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
                    case "0" -> keepgoing = false;
                }
            } catch (Exception e) {
                System.out.println("Wystąpił błąd, spróbuj ponownie");
            }
        } while (keepgoing);
        System.out.println("Dziękujemy za skorzystanie z programu");
    }

    public static void showGamesByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Mecze której z poniższych drużyn chcesz wyświetlić?");
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");
        input = scanner.nextLine().toUpperCase();

        resultSet = statement.executeQuery(
                "select mdate, stadium, team1, team2 from game where team1 = '" + input + "' OR team2 = '" + input + "'");

        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("team1") + " - " +
                resultSet.getString("team2") + ": " +
                resultSet.getString("mdate") + " - " +
                resultSet.getString("stadium"));
        System.out.println();
    }
    public static void showGoalsByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Gole której z poniższych drużyn chcesz wyświetlić?");
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");
        input = scanner.nextLine().toUpperCase();

        resultSet = statement.executeQuery(
                "select goal.player, game.team1, game.team2, game.mdate, goal.gtime from goal " +
                        "inner join game on game.id = goal.matchid " +
                        "where goal.teamid = '" + input + "' order by game.mdate desc");

        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("player") + " - (" +
                    resultSet.getString("team1") + " - " + resultSet.getString("team2") +
                    ") " + resultSet.getString("mdate") + " - " +
                    resultSet.getString("gtime") + "'" );
        System.out.println();
    }
    public static void showWinsByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Wygrane której z poniższych drużyn chcesz wyświetlić?");
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");
        input = scanner.nextLine().toUpperCase();

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
        System.out.print("\n" + input + " wygrali z: ");
        while (resultSet.next())
            if (resultSet.getString("Won").equals(input))
                System.out.print(resultSet.getString("Lost") + ", ");
        System.out.println("\n");
    }
    public static void showLossesByTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Przegrane której z poniższych drużyn chcesz wyświetlić?");
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID: ");
        input = scanner.nextLine().toUpperCase();

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
        System.out.print("\n" + input + " przegrali z: ");
        while (resultSet.next())
            if (resultSet.getString("Lost").equals(input))
                System.out.print(resultSet.getString("Won") + ", ");
        System.out.println("\n");
    }
    public static void showGoalsByPlayer(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input;
        ResultSet resultSet;

        System.out.println("Gole którego z poniższych graczy chcesz wyświetlić?");
        resultSet = statement.executeQuery("select distinct player from goal order by player asc");
        while (resultSet.next()) System.out.println(resultSet.getString("player"));
        System.out.print("Imię i nazwisko gracza: ");
        input = scanner.nextLine();

        resultSet = statement.executeQuery(
                "select goal.player, game.team1, game.team2, game.mdate, goal.gtime from goal " +
                        "inner join game on game.id = goal.matchid " +
                        "where goal.player = '" + input + "' order by game.mdate desc");

        System.out.println("\n" + input + ":");
        while (resultSet.next())
            System.out.println("(" + resultSet.getString("team1") + " - " +
                    resultSet.getString("team2") + ") " +
                    resultSet.getString("mdate") + " - " +
                    resultSet.getString("gtime") + "'" );
        System.out.println();
    }
    public static void addGame(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String team1, team2, stadium, mdate;
        int ID = 0;
        ResultSet resultSet;

        System.out.println("Wybierz drużyny z listy poniżej.");
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("Drużyna 1 ID: ");
        team1 = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, team1)) throw new Exception("");
        System.out.print("Drużyna 2 ID: ");
        team2 = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, team2)) throw new Exception("");
        System.out.print("\nPodaj nazwę stadionu: ");
        stadium = scanner.nextLine();
        System.out.print("\nPodaj datę (dd [month] yyyy): ");
        mdate = scanner.nextLine();
        resultSet = statement.executeQuery("select max(id) as ID from game");
        while (resultSet.next()) ID = resultSet.getInt("ID");
        ID++;

        statement.executeUpdate(
                "insert into game (id, mdate, stadium, team1, team2) " +
                        "Values ('" + ID + "','" + mdate + "','" + stadium + "','" + team1 + "','" + team2 + "')");

        System.out.println("Mecz został dodany do bazy danych\n");
    }
    public static void addTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String id, teamname, coach;

        System.out.print("ID Drużyny (3 litery): ");
        id = scanner.nextLine().toUpperCase();
        if(teamExists(statement, id)) throw new Exception("");
        System.out.print("\nPodaj pełną nazwę drużyny (kraju): ");
        teamname = scanner.nextLine();
        System.out.print("\nPodaj imięi nazwisko trenera: ");
        coach = scanner.nextLine();

        statement.executeUpdate(
                "insert into eteam (id, teamname, coach) " +
                        "Values ('" + id + "','" + teamname + "','" + coach + "')");

        System.out.println("Drużyna została dodana do bazy danych\n");
    }
    public static void addGoal(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        String teamid, player;
        int matchid, gtime;

        System.out.println("Wybierz drużynę z listy poniżej.");
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID Drużyny: ");
        teamid = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, teamid)) throw new Exception("");
        System.out.print("\nPodaj imię i nazwisko gracza, który strzelił gola: ");
        player = scanner.nextLine();
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
        System.out.print("ID: ");
        matchid = scanner.nextInt();
        if(!gameExists(statement, matchid,teamid)) throw new Exception("");
        System.out.print("\nPodaj minutę meczu: ");
        gtime = scanner.nextInt();

        statement.executeUpdate(
                "insert into goal (matchid, teamid, player, gtime) " +
                        "Values ('" + matchid + "','" + teamid + "','" + player + "','" + gtime + "')");

        System.out.println("Gol został dodany do bazy danych\n");
    }
    public static void deleteGame(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        int id;

        System.out.println("\nWybierz mecz z listy poniżej.");
        resultSet = statement.executeQuery(
                "select * from game");

        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("id") + ": " +
                    resultSet.getString("team1") + " - " +
                    resultSet.getString("team2") + ": " +
                    resultSet.getString("mdate") + " - " +
                    resultSet.getString("stadium"));
        System.out.print("ID Meczu: ");
        id = scanner.nextInt();
        if(!gameExists(statement, id)) throw new Exception("");

        statement.executeUpdate(
                "delete from game where id = '" + id + "'");

        System.out.println("Mecz został usunięty z bazy danych\n");
    }
    public static void deleteTeam(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        String id;

        System.out.println("Wybierz drużynę z listy poniżej.");
        resultSet = statement.executeQuery("select id, teamname from eteam order by id asc");
        while (resultSet.next()) System.out.println(resultSet.getString("id")
                + " - " + resultSet.getString("teamname"));
        System.out.print("ID Drużyny: ");
        id = scanner.nextLine().toUpperCase();
        if(!teamExists(statement, id)) throw new Exception("");

        statement.executeUpdate(
                "delete from eteam where id = '" + id + "'");

        System.out.println("Drużyna została usunięta z bazy danych\n");
    }
    public static void deleteGoal(Statement statement) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ResultSet resultSet;
        int matchid, gtime;

        System.out.println("\nWybierz mecz z listy poniżej.");
        resultSet = statement.executeQuery(
                "select * from game");

        System.out.println();
        while (resultSet.next())
            System.out.println(resultSet.getString("id") + ": " +
                    resultSet.getString("team1") + " - " +
                    resultSet.getString("team2") + ": " +
                    resultSet.getString("mdate") + " - " +
                    resultSet.getString("stadium"));
        System.out.print("ID Meczu: ");
        matchid = scanner.nextInt();
        if(!gameExists(statement, matchid)) throw new Exception("");

        System.out.println("Wybierz minutę meczu.");
        resultSet = statement.executeQuery(
                "select gtime from goal where matchid = '" + matchid + "'");
        while (resultSet.next())
            System.out.println(resultSet.getString("gtime"));
        System.out.print("Minuta meczu: ");
        gtime = scanner.nextInt();

        statement.executeUpdate(
                "delete from goal where matchid = '" + matchid + "' AND gtime = '" + gtime + "'");

        System.out.println("Gol został usunięty z bazy danych\n");
    }
    public static boolean teamExists(Statement statement, String teamID) throws Exception {
        ResultSet resultSet = statement.executeQuery("select id from eteam");
        while(resultSet.next()) if(resultSet.getString("id").equals(teamID)) return true;
        return false;
    }
    public static boolean gameExists(Statement statement, int matchID, String teamID) throws Exception {
        ResultSet resultSet = statement.executeQuery("select id from game where team1 = '" + teamID + "' OR team2 = '" + teamID + "'");
        while(resultSet.next()) if(resultSet.getInt("id") == (matchID)) return true;
        return false;
    }
    public static boolean gameExists(Statement statement, int matchID) throws Exception {
        ResultSet resultSet = statement.executeQuery("select id from game");
        while(resultSet.next()) if(resultSet.getInt("id") == (matchID)) return true;
        return false;
    }
}
