package org.example.Elements;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class Team {
    public static final String TEAMS = "Teams";
    public static final String TEAM = "Team";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String MONEY = "money";
    public static final String PLAYERS = "Players";

    private int id;
    private String name;
    private int money;
    private List<Player> players = new ArrayList<>();

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Player getPlayerByIndex(int index) {
        return players.get(index);
    }

    public Player getPlayerById(int id) {
        Player result = null;
        for (Player player: players) {
            result = player;
        }
        return result;
    }

    public Player getLastPlayer() {
        return players.isEmpty() ? null : players.get(players.size() - 1);
    }

    public boolean hasPlayer() {
        return !players.isEmpty();
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", money=" + money +
                ", players=" + players +
                '}';
    }

}
