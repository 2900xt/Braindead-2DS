package src.Game;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.image.*;
import java.util.ConcurrentModificationException;
import java.awt.*;
import java.awt.event.*;

import src.Display.HUD;
import src.Display.Minimap;
import src.Display.Renderer;
import src.Display.UI;
import src.World.World;
import src.World.Entity.*;
import src.World.Guns.*;

public class Game extends JPanel
{

    public final int WIDTH;
    public final int HEIGHT;

    private BufferedImage image;
    private Graphics g;
    private World world;
    private Minimap minimap;
    private HUD hud;
    public static Renderer renderer;
    private JPanel panel;

    public static int CTCount, TCount;


    public Game(int width, int height, World world, boolean team)
    {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.world = world;
        this.panel = this;

        //Initialize the graphics library
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();
        setFocusable(true);

        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setFont(new Font("DynaPuff", Font.BOLD, 50));
        g.setColor(Color.BLACK);
        g.drawString("Loading res...", 250, 500);

        Player p1 = new Player(world.TSpawn, team);

        //Create the HUD
        hud = new HUD(WIDTH, HEIGHT, p1, this);

        //Create a player
        world.createPlayer(p1);
        world.getPlayer().enableMovement(panel, world);
        world.getPlayer().equipWeapon(new AssaultRifle(true));

        //Create the minimap
        minimap = new Minimap(0, 0, world);

        //Create a 2D renderer
        renderer = new Renderer(image, world, this);
        renderer.enableAim();

        //Launch the game
        Thread game = new Thread(new GameRunner());
        game.start();
        
    }

    public Integer CTWins = 0, TWins = 0;
    private static int roundsToWin = 10;
    public double timeLeft;

    private class GameRunner implements Runnable
    {
        private Boolean getRoundWinner()
        {
            //Check if there are any teams alive
            int aliveCT = 0, aliveT = 0;
            for(Playable entity : world.getEntities())
            {
                if(!entity.isDead() && entity.getTeam())
                {
                    aliveT++;
                } else if (!entity.isDead())
                {
                    aliveCT++;
                }
            }

            if(timeLeft <= 0 && aliveCT >= 1)
            {
                return false;
            }
            if(aliveCT == 0)
            {
                return true; //True means the T won
            }
            if(aliveT == 0 && world.getBomb() == null)
            {
                return false;   //False means the CT won
            }
            if(aliveT == 0 && !world.getBomb().planted)
            {
                return false;
            }
            if(world.getBomb() == null)
            {
                return null;
            }
            if(world.getBomb().defused)
            {
                return false;
            }
            if(world.getBomb().blownUp)
            {
                return true;
            }

            return null;    //No one won
        }

        private void restartRound()
        {
            buyMode = false;
            teamSelectMode = false;

            timeLeft = 90;
            world.killAll();
            boolean team = world.getPlayer().getTeam();
            for(int i = 0; i < (team ? TCount - 1 : TCount); i++)
            {
                Playable currentEntity = new Bot((int)world.TSpawn.getX() + i, (int)world.TSpawn.getY(), true, false, 200.0, world);
                if(!team && i == 0) 
                {
                    currentEntity.bomb = new Bomb(currentEntity);
                }
                world.addEntity(currentEntity);
            }

            for(int i = 0; i < (!team ?  CTCount - 1 : CTCount); i++)
            {
                world.addEntity(new Bot((int)world.CTSpawn.getX() + i, (int)world.CTSpawn.getY(), false, false, 200.0, world));
            }

            world.addEntity(world.getPlayer());
            world.getPlayer().respawn();

            if(world.getBomb() != null && world.getBomb().planted)
            {
                world.getBomb().bombTickSound.stop();
            }

            world.setBomb(null);
            world.getPlayer().enableMovement(panel, world);
        }

        @Override
        public void run() {

            restartRound();

            try {
            while(true)
            {
                if(CTWins >= roundsToWin && CTWins >= TWins + 2)
                {
                    System.out.println("CT Wins!" + CTWins);
                    System.exit(101);
                }
                if(TWins >= roundsToWin && TWins >= CTWins + 2)
                {
                    System.out.println("T Wins!" + TWins);
                    System.exit(100);
                }

                Timer round = new Timer(25, new OnTick());
                round.start();
                Boolean roundStatus;
                do {
                    roundStatus = getRoundWinner();
                } while (roundStatus == null);
                round.stop();

                if(roundStatus)
                {
                    TWins++;
                }
                else { 
                    CTWins++;
                }

                try {
                    Thread.sleep(10);
                } catch (Exception e) {
            
                }

                UI.displayWinScreen(g, roundStatus, image, world.getPlayer(), world);
                repaint();

                try {
                    Thread.sleep(3 * 1000);
                } catch (Exception e) {
            
                }

                if(roundStatus)
                {
                    Sound.playSoundFile("TWin.wav");
                } else {
                    Sound.playSoundFile("CTWin.wav");
                }

                restartRound();
            }
        } catch (ConcurrentModificationException e)
        {
            System.out.println("TIE");
            restartRound();
        }
        }
        
    }

    public void paintComponent(Graphics g)
    {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    private boolean teamSelectMode = false;
    private boolean buyMode = false;

    private class OnTick implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {

            if(world.getPlayer().getKeysPressed().contains(KeyEvent.VK_T))
            {
                renderer.disableAim();
                teamSelectMode = true;
            }

            if(world.getPlayer().getKeysPressed().contains(KeyEvent.VK_B))
            {
                renderer.disableAim();
                buyMode = true;
            }

            for(Playable entity : world.getEntities())
            {
                entity.onTick();
            }

            world.updateBullets();
            world.checkCollisions();
            renderer.renderWorld(g);
            minimap.draw(g);
            hud.draw(g);

            if(teamSelectMode)
            {
                teamSelectMode = !UI.drawChooseTeamScreen(g, renderer, world.getPlayer(), world);
            } else if (buyMode) {
                buyMode = !UI.displayBuyMenu(g, world.getPlayer(), image, renderer);
            } else {
                renderer.enableAim();
            }

            if(UI.killScreenTime > 0)
            {
                UI.drawKillScreen(world.getPlayer(), g, image);
                UI.killScreenTime -= 0.01;
            }

            timeLeft -= 0.02;
            repaint();
        }
    }
}
