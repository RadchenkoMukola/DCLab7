package org.example.Storage;
import org.example.Elements.Team;
import org.example.Elements.Player;
import java.util.List;

public interface Repository {
    int countTeams();
    int countPlayers();
    void insertTeam(Team team);
    void insertPlayer(int teamId, Player player);

    void deleteTeam(int id);
    void deletePlayer(int id);

    Team getTeam(int id);
    Player getPlayer(int id);

    List<Team> getTeam();
    List<Player> getPlayer();
}