package src.World.Entity;
import java.awt.Color;
import java.awt.Graphics;

import src.Math.Vec2;

public class Bullet {
    Vec2 location;
    Vec2 speed;
    double damage;
    Playable shooter;
    public boolean dead;

    public Bullet(Vec2 startingLocation, Vec2 speed, double dmg, Playable shooter)
    {
        location = startingLocation;
        this.speed = speed;
        dead = false;
        this.shooter = shooter;
        damage = dmg;
    }
    
    public void draw(Graphics g, Vec2 camera, double segmentSz)
    {
        //Normalize the location vector
        Vec2 offsetPos = location.scalarMultiply(segmentSz).subtract(camera);
        g.setColor(Color.BLACK);
        g.fillOval((int)offsetPos.getX(), (int)offsetPos.getY(), 10, 10);
    }

    public void update()
    {
        location = location.add(speed);
    }

    public Vec2 getLocation()
    {
        return location;
    }

    public double getDamage()
    {
        return damage;
    }

    public boolean getTeam()
    {
        return shooter.team;
    }

    public Playable getShooter()
    {
        return shooter;
    }
}
