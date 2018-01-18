package io.anuke.sanctre.entities;

import io.anuke.sanctre.graphics.Emitter;

public class Enemy extends Spark {
    public Emitter emitter;
    //TODO targeting
    public Player target;

    @Override
    public void added(){
        super.added();
        if(emitter != null)
            emitter.init(this);
    }

    @Override
    public void update(){
        if(emitter != null)
            emitter.update(this);

        move();
    }

    public void move(){}
}
