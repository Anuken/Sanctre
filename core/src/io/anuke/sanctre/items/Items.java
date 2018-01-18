package io.anuke.sanctre.items;

import io.anuke.sanctre.items.types.Sword;

public class Items {
    public static final Item

    marbleSword = new Sword("marblesword"){
        {
            length = 14f;
            basethick = 15f;
            arc = 200f;
            swingduration = 7f;
        }
    },
    phasesword = new Sword("phaseswordDX"){
        {
            length = 25f;
            basethick = 35f;
            arc = 200f;
            swingduration = 8f;
        }
    };
}
