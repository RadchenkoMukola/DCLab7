package org.example.Storage;
import org.example.Elements.Team;
import org.example.Elements.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;

public class DB implements Repository {
    private Connection connection;

    public DB(String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(url, user, password);

        Statement statement = connection.createStatement();

        String teamsTable = "CREATE TABLE IF NOT EXISTS teams (id SERIAL PRIMARY KEY, name varchar(30), money int)";
        String playersTable = "CREATE TABLE IF NOT EXISTS players (id SERIAL PRIMARY KEY, team_id int, name varchar(30), CONSTRAINT fk_team FOREIGN KEY(team_id) REFERENCES teams(id))";

        statement.execute(teamsTable);
        statement.execute(playersTable);
    }

    @Override
    public int countTeams() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM teams";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public int countPlayers() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) as count FROM players";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void insertTeam(Team team) {
        try {
            Statement statement = connection.createStatement();
            String teamQuery = "INSERT INTO teams(name, money) VALUES('" + team.getName() + "'," + team.getMoney() + ")";
            statement.execute(teamQuery);

            String findQuery = "SELECT * FROM teams WHERE name='" + team.getName() + "' AND money=" + team.getMoney();
            ResultSet resultSet = statement.executeQuery(findQuery);
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                for (Player player : team.getPlayers()) {
                    String playersQuery = "INSERT INTO players(team_id, name) VALUES(" + id + ",'" + player.getName() + "')";
                    statement.execute(playersQuery);
                }
            }
        } catch (SQLException e) {
            System.out.println("Cannot add team as it already exists");
        }
    }

    @Override
    public void insertPlayer(int teamId, Player player) {
        try {
            Statement statement = connection.createStatement();
            String playersQuery = "INSERT INTO players(team_id, name) VALUES(" + teamId + ",'" + player.getName() + "')";
            statement.execute(playersQuery);
        } catch (SQLException e) {
            System.out.println("Cannot add player as it already exists");
        }
    }

    @Override
    public void deleteTeam(int id) {
        try {
            Statement statement = connection.createStatement();
            String playersQuery = "DELETE FROM players WHERE team_id = " + id;
            statement.execute(playersQuery);
            String teamQuery = "DELETE FROM teams WHERE id = " + id;
            statement.execute(teamQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletePlayer(int id) {
        try {
            Statement statement = connection.createStatement();
            String playersQuery = "DELETE FROM players WHERE id = " + id;
            statement.execute(playersQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Team getTeam(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM teams WHERE id = " + id + ") JOIN (SELECT id as player_id, team_id as id, name as player_name FROM players) USING (id)";

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Team team = new Team();
                team.setId(resultSet.getInt("id"));
                team.setName(resultSet.getString("name"));
                team.setMoney(resultSet.getInt("money"));

                do {
                    Player player = new Player();
                    player.setId(resultSet.getInt("player_id"));
                    player.setName(resultSet.getString("player_name"));
                    team.addPlayer(player);
                } while (resultSet.next());

                return team;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Player getPlayer(int id) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM players WHERE id=" + id;

            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Player player = new Player();
                player.setId(resultSet.getInt("id"));
                player.setName(resultSet.getString("name"));
                return player;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Team> getTeam() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM (SELECT * FROM teams) LEFT JOIN (SELECT id as player_id, team_id as id, name as player_name FROM players) USING (id)";

            List<Team> teams = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Team team = null;

                do {
                    if (team == null || resultSet.getInt("id") != team.getId()) {
                        if (team != null) {
                            teams.add(team);
                        }

                        team = new Team();
                        team.setId(resultSet.getInt("id"));
                        team.setName(resultSet.getString("name"));
                        team.setMoney(resultSet.getInt("money"));
                    }

                    Player player = new Player();
                    player.setId(resultSet.getInt("player_id"));
                    if (resultSet.wasNull()) {
                        continue;
                    }
                    player.setName(resultSet.getString("player_name"));
                    team.addPlayer(player);
                } while (resultSet.next());

                teams.add(team);
            }

            return teams;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Player> getPlayer() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM players";

            List<Player> players = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Player player = new Player();
                player.setId(resultSet.getInt("player_id"));
                player.setName(resultSet.getString("player_name"));
                players.add(player);
            }

            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}