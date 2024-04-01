package src.World.Entity;

import javax.sound.sampled.Clip;

import src.Game.Sound;
import src.Math.Vec2;
import src.World.Segment;
import src.World.World;
import src.World.Guns.AssaultRifle;
import src.World.Guns.None;
import src.World.Guns.Shootable;
import src.World.Guns.Shotgun;

public abstract class Playable {
    protected Vec2 pos;
    protected Vec2 velocity;
    public World world;
    protected double hitPoints;
    protected Shootable currentWeapon;
    protected boolean team; //true if player is T
    protected double angleDegrees; //Contains the angle of the gun
    protected boolean dead;
    protected boolean mouseClicked;
    public Bomb bomb;
    protected double startingHP;
    private Clip movementSound;

    public Playable(int x, int y, boolean _team, double hp, boolean hasBomb)
    {
        hitPoints = hp;
        startingHP = hp;
        pos = new Vec2(x, y);
        velocity = new Vec2();
        angleDegrees = 0;
        team = _team;
        dead = false;
        movementSound = null;

        if(team && hasBomb) bomb = new Bomb(this);
    }

    public boolean isDead()
    {
        return dead;
    }

    public double getAngle()
    {
        return angleDegrees;
    }

    public void move()
    {
        pos = pos.add(velocity);
    }

    public Vec2 getPos()
    {
        return pos;
    }

    public void setPos(Vec2 newPos)
    {
        this.pos = new Vec2(newPos);
    }

    public void setSpeed(Vec2 speed)
    {
        velocity = new Vec2(speed);
    }

    public Vec2 getSpeed()
    {
        return velocity;
    }

    public void equipWeapon(Shootable gun)
    {
        currentWeapon = gun;
    }

    public Shootable getWeapon()
    {
        return currentWeapon;
    }

    public double getHitPoints()
    {
        return hitPoints;
    }

    public void move(Vec2 displacement)
    {
        pos = pos.add(displacement);
        if(this instanceof Player)
            playFootsteps(displacement);
    }

    public boolean checkForWalls(Vec2 velocity)
    {
        //Check if the next 'segment' that the player is attempting to walk into is a wall

        Vec2 displacement = new Vec2(velocity);

        if(velocity.getX() > 0) displacement.setX(velocity.getX() + 1);
        if(velocity.getY() > 0) displacement.setY(velocity.getY() + 1);
        
        Vec2 newPos = pos.add(displacement);

        Segment newSegment = world.getSegment(newPos.getX(), newPos.getY());

        //stop the player from moving outside the map
        if(newSegment == null || newSegment.isWall)
        {
            return true;
        }

        //Check again
        displacement = velocity;
        
        newPos = pos.add(displacement);

        newSegment = world.getSegment(newPos.round().getX(), newPos.round().getY());

        //stop the player from moving outside the map
        if(newSegment == null || newSegment.isWall)
        {
            return true;
        }

        newPos = pos.add(velocity);

        return false;
    }

    public void shootWeapon()
    {
        if(currentWeapon instanceof None)
        {
            return;
        }
        
        double recoilAngle = 0;
        currentWeapon.shoot();
        if(velocity.getMagnitude() >= 0.01)
        {
            recoilAngle = Math.random() * currentWeapon.getRecoil();
            if(Math.random() >= 0.5) recoilAngle *= -1;
        }
        world.shootBullet(pos.getX(), pos.getY(), angleDegrees + recoilAngle, currentWeapon.getDamage(), this);

        if(currentWeapon instanceof Shotgun)
        {
            for(int i = 0; i < 12; i++)
            {
                recoilAngle = Math.random() * currentWeapon.getRecoil();
                if(Math.random() >= 0.5) recoilAngle *= -1;
                world.shootBullet(pos.getX(), pos.getY(), angleDegrees + recoilAngle, currentWeapon.getDamage(), this);
            }
        }
    }

    public void playFootsteps(Vec2 acceleration)
    {
        if(acceleration.getX() >= 0.01 || velocity.getY() >= 0.01 || velocity.getX() <= -0.01 || velocity.getY() <= -0.01)
        {
            if(movementSound == null || !movementSound.isActive())
                movementSound = Sound.playSoundFile("footsteps.wav");
        }
    }


    public void takeDamage(double val)
    {
        this.hitPoints -= val;
        if(this.hitPoints <= 0)
        {
            dead = true;
            this.hitPoints = 0;
        }
    }

    public void setTeam(boolean team)
    {
        this.team = team;
    }

    public boolean getTeam()
    {
        return team;
    }

    public void setAngle(double val)
    {
        this.angleDegrees = val;
    }

    protected void setHP(double hp)
    {
        this.hitPoints = hp;
    }

    public void respawn()
    {
        if(team)
        {
            setPos(world.TSpawn);
            bomb = new Bomb(this);
        } else 
        {
            setPos(world.CTSpawn);
        }

        hitPoints = startingHP;
        dead = false;
        equipWeapon(new AssaultRifle(false));
        velocity = new Vec2();
    }

    public abstract void onTick();
}
