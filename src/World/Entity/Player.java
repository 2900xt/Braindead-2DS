package src.World.Entity;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import javax.swing.JPanel;

import src.Math.*;
import src.World.World;
import src.World.Guns.None;
import src.World.Guns.Shootable;

public class Player extends Playable{

    public double creds;
    public int killCount;

    public Shootable primary, secondary;
    
    public Player(int x, int y, boolean _team) {
        super(x, y, _team, 200, _team);
        creds = 2000;
        killCount = 0;
        primary = new None();
        secondary = new None();
    }

    public Player(Vec2 pos, boolean _team) {
        super((int)pos.getX(), (int)pos.getY(), _team, 200, _team);
        creds = 2000;
        killCount = 0;
        primary = new None();
        secondary = new None();
    }

    //This method enables the mouse and key listeners associeted with this player
    public void enableMovement(JPanel panel, World world)
    {
        panel.addKeyListener(new onKeyPress());
        panel.addMouseListener(new mouseListener());
        this.world = world;
    }

    public void respawn()
    {
        if(team)
        {
            setPos(world.TSpawn.add(new Vec2(0, -1)));
            bomb = new Bomb(this);
        } else 
        {
            setPos(world.CTSpawn);
            bomb = null;
        }
        if(dead)
        {
            primary = new None();
            secondary = new None();
            creds += 800;
        } else 
        {
            creds += 400;
        }

        dead = false;
        killCount = 0;
        hitPoints = 200;
        equipWeapon(new None());
        velocity = new Vec2();
        world.addEntity(this);
    }

    public HashSet<Integer> getKeysPressed()
    {
        return keysPressed;
    }

    public boolean isMouseClicked()
    {
        return mouseClicked;
    }

    private boolean isPlayerShooting = false;

    //This class lets us record both mouse motion and mouse clicks
    public class mouseListener implements MouseListener
    {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            isPlayerShooting = true;
            mouseClicked = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isPlayerShooting = false;
            mouseClicked = false;
        }
        
    }

    /*
     * In order to process more than one key at the same time:
     * Whenever a key is pressed, add the corresponding key code to the 'keysPressed' hashset
     * Whenever a key is released, remove the key from the set
     * 
     * This way, we can see what keys are currently pressed every game tick, which lets us see more than one key press at the same time
     */
    HashSet<Integer> keysPressed = new HashSet<>();

    public class onKeyPress implements KeyListener
    {

        //Triggers on a key press
        @Override
        public void keyPressed(KeyEvent e) {
            keysPressed.add(e.getKeyCode());
        }

        //Triggers on key release
        @Override
        public void keyReleased(KeyEvent e) {
            keysPressed.remove(e.getKeyCode());
        }

        //Keytyped is unreliable
        @Override
        public void keyTyped(KeyEvent e) {}
    }


    public void onTick()
    {

        //Check if the 'keysPressed' set contains a key, and then perform the corresponding event.
        Vec2 acceleration = new Vec2();

        if(keysPressed.contains(KeyEvent.VK_W))     //Move forwards in world space
        { 
            acceleration = acceleration.add(new Vec2(0, -0.027));
        }
        if(keysPressed.contains(KeyEvent.VK_S))     //Move backwards in world space
        { 
            acceleration = acceleration.add(new Vec2(0, 0.027));
        }
        if(keysPressed.contains(KeyEvent.VK_A))     //Move to the left in world space
        { 
            acceleration = acceleration.add(new Vec2(-0.027, 0));
        }
        if(keysPressed.contains(KeyEvent.VK_D))     //Move to the right in world space
        { 
            acceleration = acceleration.add(new Vec2(0.027, 0));
        }
        if(keysPressed.contains(KeyEvent.VK_R))
        {
            currentWeapon.reload();
        }
        if(keysPressed.contains(KeyEvent.VK_E))
        {
            if(team && bomb != null)
                bomb.plantBomb(world);

            if(!team && world.getBomb() != null)
                world.getBomb().defuseBomb(this, world);
        }

        if(keysPressed.contains(KeyEvent.VK_1))
        {
            equipWeapon(primary);
        }
        if(keysPressed.contains(KeyEvent.VK_2))
        {
            equipWeapon(secondary);
        }

        //We add the acceleration to our current player velocity.
        velocity = velocity.add(acceleration);

        //Make the player move slower if they are reloading
        if(currentWeapon.isReloading())
        {
            velocity = velocity.scalarMultiply(0.5);
        }

        if(checkForWalls(velocity))
        {
            velocity = new Vec2();
        }

        //Shoot the weapon if the player is pressing the shoot key
        if(isPlayerShooting && !currentWeapon.isReloading() && !currentWeapon.isShooting())
        {
            shootWeapon();
        }

        //x = x + v
        pos = pos.add(velocity);
        
        playFootsteps(acceleration);

        //Reduce the velocity to make sure that the player is actively pressing the movement keys
        velocity = velocity.scalarMultiply(0.6);
    }

    public void increaseKillCount()
    {
        killCount++;
    }

    public int getKills()
    {
        return killCount;
    }
}
