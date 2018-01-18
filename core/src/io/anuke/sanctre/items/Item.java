package io.anuke.sanctre.items;

import io.anuke.sanctre.entities.Player;

public class Item {
    public final String name;

    public Item(String name){
        this.name = name;
    }

    public void update(Player player){ }
    public void draw(Player player){ }
    public void drawOver(Player player){ }
}
