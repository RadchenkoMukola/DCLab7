package org.example.Elements;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter @Setter
public class Player {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PLAYER = "Player";

    private int id;
    private String name;

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
