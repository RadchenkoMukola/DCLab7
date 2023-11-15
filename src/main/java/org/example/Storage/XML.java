package org.example.Storage;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import org.example.Elements.Team;
import org.example.Elements.Player;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XML implements Repository {
    private final List<Team> teams = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    private int maxTeamId = 0;
    private int maxPlayerId = 0;

    public XML(String path) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(path));
            doc.getDocumentElement().normalize();

            NodeList elements = doc.getElementsByTagName(Team.TEAM);
            for (int i = 0; i < elements.getLength(); ++i) {
                Element element = (Element) elements.item(i);

                Team team = new Team();
                parseTeam(element, team);

                maxTeamId = Math.max(maxTeamId, team.getId());
                teams.add(team);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countTeams() {
        return teams.size();
    }

    @Override
    public int countPlayers() {
        return players.size();
    }

    @Override
    public void insertTeam(Team value) {
        maxTeamId++;
        value.setId(maxTeamId);
        teams.add(value);

        for (Player player: value.getPlayers()) {
            maxPlayerId++;
            player.setId(maxPlayerId);
            players.add(player);
        }
    }

    @Override
    public void insertPlayer(int teamId, Player player) {
        Team team = getTeam(teamId);

        if (team != null) {
            maxPlayerId++;
            player.setId(maxPlayerId);

            team.addPlayer(player);
            players.add(player);
        }
    }

    @Override
    public void deleteTeam(int id) {
        for (int i = 0; i < teams.size(); ++i) {
            if (teams.get(i).getId() == id) {
                players.removeAll(teams.get(i).getPlayers());
                teams.remove(i);
                return;
            }
        }
    }

    @Override
    public void deletePlayer(int id) {
        Player player = getPlayer(id);
        if (player != null) {
            for (Team team : teams) {
                if (team.hasPlayer(player)) {
                    team.removePlayer(player);
                }
            }
        }
    }

    @Override
    public Team getTeam(int id) {
        for (Team item : teams) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public Player getPlayer(int id) {
        for (Player item : players) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public List<Team> getTeam() {
        return teams;
    }

    @Override
    public List<Player> getPlayer() {
        return players;
    }

    public void save(String file) throws ParserConfigurationException, TransformerException {
        Document doc = createDocumentWithItems();

        Source source = new DOMSource(doc);
        Result result = new StreamResult(new File(file));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(source, result);
    }

    private void parseTeam(Element element, Team item) {
        item.setId(Integer.parseInt(element.getAttribute(Team.ID)));
        item.setName(element.getAttribute(Team.NAME));
        item.setMoney(Integer.parseInt(element.getAttribute(Team.MONEY)));

        NodeList children = element.getElementsByTagName(Player.PLAYER);
        for (int i = 0; i < children.getLength(); ++i) {
            Element child = (Element) children.item(i);

            Player player = new Player();
            player.setId(Integer.parseInt(child.getAttribute(Player.ID)));
            player.setName(child.getAttribute(Player.NAME));
            item.addPlayer(player);

            maxPlayerId = Math.max(maxPlayerId, player.getId());
            players.add(player);
        }
    }

    private Document createDocumentWithItems() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element teams = doc.createElement(Team.TEAMS);
        for (Team item: this.teams) {
            Element team = doc.createElement(Team.TEAM);

            team.setAttribute(Team.ID, String.valueOf(item.getId()));
            team.setAttribute(Team.NAME, item.getName());
            team.setAttribute(Team.MONEY, String.valueOf(item.getMoney()));

            Element players = doc.createElement(Team.PLAYERS);
            for (Player subitem: item.getPlayers()) {
                Element player = doc.createElement(Player.PLAYER);
                player.setAttribute(Player.ID, String.valueOf(subitem.getId()));
                player.setAttribute(Player.NAME, subitem.getName());
                players.appendChild(player);
            }
            team.appendChild(players);

            teams.appendChild(team);
        }

        doc.appendChild(teams);

        return doc;
    }
}