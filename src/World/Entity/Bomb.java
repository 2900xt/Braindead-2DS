package src.World.Entity;
import java.awt.event.KeyEvent;

import javax.sound.sampled.Clip;

import src.Game.Sound;
import src.Math.Vec2;
import src.World.World;
import src.World.Guns.None;

public class Bomb {
    public Vec2 location;
    public boolean planted;
    public boolean dropped;
    public boolean defused;
    public boolean blownUp;
    public double timeTillExplosion;
    public Playable holder;

    public Bomb(Playable holder)
    {
        this.location = holder.getPos();
        this.planted = false;
        this.dropped = false;
        this.defused = false;
        this.blownUp = false;
        this.timeTillExplosion = 40.0;
        this.holder = holder;
        bomb = this;
    }

    public void plantBomb(World w)
    {
        if(plantingBomb || planted) return;

        if(holder == null) return;

        if(!w.getSegment(holder.getPos().getX() + 0.5, holder.getPos().getY() + 0.5).plantable) return;
        plantingBomb = true;
        world = w;
        Thread t = new Thread(new BombPlanter());
        t.start();
    }

    public boolean plantingBomb = false;
    public double timeToPlant = 2.5;
    private World world;
    private Bomb bomb;

    private class BombPlanter implements Runnable
    {
        @Override
        public void run() {
            Vec2 originalPos = holder.getPos();
            holder.currentWeapon = new None();
            bombTickSound = Sound.playSoundFile("bombTick.wav");
            while(true)
            {
                try {
                    long init = System.currentTimeMillis();
                    Thread.sleep(10);
                    if(!originalPos.equals(holder.getPos()) || holder.isDead())
                    {
                        timeToPlant = 3.0;
                        plantingBomb = false;
                        planted = false;
                        bombTickSound.stop();
                        return;
                    }

                    if(holder instanceof Player)
                    {
                        Player p = (Player)holder;
                        if(!p.keysPressed.contains(KeyEvent.VK_E) || p.currentWeapon.getName() != null)
                        {
                            timeToPlant = 3.0;
                            plantingBomb = false;
                            planted = false;
                            bombTickSound.stop();
                            return;
                        }
                    }

                    timeToPlant -= (System.currentTimeMillis() - init)/1000.0;
                    if(timeToPlant <= 0)
                    {
                        plantingBomb = false;
                        planted = true;
                        holder.bomb = null;
                        location = holder.getPos();

                        if(holder instanceof Player)
                        {
                            Player p = (Player)holder;
                            p.creds += 300;
                        }

                        holder = null;
                        world.setBomb(bomb);
                        Thread bombTickThread = new Thread(new BombTick());
                        bombTickThread.start();
                        return;
                    }
                } catch (Throwable t) {}
            }
        }
    }

    private class BombTick implements Runnable
    {
        @Override
        public void run() {
            timeTillExplosion = 40.0;
            while(true) {
                long init = System.currentTimeMillis();
                try {Thread.sleep(10);} catch (Throwable t) {}
                timeTillExplosion -= (System.currentTimeMillis() - init)/1000.0;
                if(!planted) return;
                if(timeTillExplosion <= 0)
                {
                    blownUp = true;
                }
            }
        }
    }
    public Clip bombTickSound;
    public double timeToDefuse = 5.0;
    public boolean defusingBomb = false;
    public Playable defuser;

    public void defuseBomb(Playable defuser, World w)
    {
        if(defusingBomb || defused) return;

        if(!planted) return;

        if(defuser == null) return;

        if(!w.getSegment(defuser.getPos().getX()+ 0.5, defuser.getPos().getY()+ 0.5).plantable) return;
        defusingBomb = true;
        
        this.defuser = defuser;
        defuser.bomb = bomb;
        Sound.playSoundFile("bombDefusing.wav");
        Thread t = new Thread(new Defuser());
        t.start();
    
    }

    private class Defuser implements Runnable
    {
        @Override
        public void run() {
            defuser.currentWeapon = new None();
            Vec2 originalPos = defuser.getPos();
            for(int i = 0; i < 5000; i++)
            {
                try {
                    
                    long init = System.currentTimeMillis();
                    Thread.sleep(10);
                    if(!originalPos.equals(defuser.getPos()) || defuser.isDead())
                    {
                        timeToDefuse = 5.0;
                        defusingBomb = false;
                        defused = false;
                        defuser.bomb = null;
                        return;
                    }

                    if(defuser instanceof Player)
                    {
                        Player p = (Player)defuser;
                        if(!p.keysPressed.contains(KeyEvent.VK_E) || p.currentWeapon.getName() != null)
                        {
                            timeToDefuse = 5.0;
                            defusingBomb = false;
                            defused = false;
                            defuser.bomb = null;
                            defuser = null;
                            return;
                        }
                    }

                    timeToDefuse -= (System.currentTimeMillis() - init)/1000.0;
                    if(timeToDefuse <= 0)
                    {
                        defusingBomb = false;
                        planted = false;
                        defused = true;
                        defuser.bomb = null;

                        if(defuser instanceof Player)
                        {
                            Player p = (Player)defuser;
                            p.creds += 300;
                        }

                        bombTickSound.stop();
                        Sound.playSoundFile("bombDefused.wav");
                        return;
                    }
                } catch (Throwable t) {}
            }
        }
    }

}
