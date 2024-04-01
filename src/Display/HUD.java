package src.Display;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;

import src.Game.Game;
import src.World.Entity.Player;

public class HUD {
    private int xSize, ySize;
    private Player player;
    private Game data;
    public static boolean HUDEnable;
    public HUD(int screenX, int screenY, Player player, Game data)
    {
        this.xSize = screenX;
        this.ySize = screenY;
        this.player = player;
        this.data = data;
    }

    private void drawProgressBar(Graphics g, double progress, int x, int y, int width, int height)
    {
        g.drawRect(x, y, width, height);
        g.fillRect(x, y, (int)(progress * width), height);
    }

    public void draw(Graphics g)
    {
        if(!HUDEnable) return;
        //Get the remaining bullets from the player's mag and draw them
        String remainingBullets = player.getWeapon().getName() + ": " + player.getWeapon().getRemainingBullets() + " / " + player.getWeapon().getMagSize() + " / " + player.getWeapon().getTotalRemainingBullets();

        //Now draw the remaining HP
        String remainingHP = "HP: " + player.getHitPoints() + " / 200.0";

        //Call the graphics library to render
        g.setColor(new Color(245, 5, 5, 200));
        g.setFont(new Font("DynaPuff", Font.BOLD, 20));
        g.drawString(remainingBullets, 0, ySize - 5);
        g.drawString(remainingHP, xSize - 200, ySize - 5);

        //Draw a progress bar if the player is reloading
        if(player.getWeapon().isReloading())
        {
            g.drawString("Reloading...", 340, ySize - 30);
            drawProgressBar(g, player.getWeapon().getCurrentReloadTime() / player.getWeapon().getReloadTime() , 350, ySize - 25, 100, 20);
        }

        //Draw a progress bar if the player is planting or defusing the bomb
        if(player.bomb != null && player.bomb.plantingBomb)
        {
            double progress = (2.5 - player.bomb.timeToPlant) / 2.5;
            drawProgressBar(g, progress, (xSize - 300) / 2, 200, 300, 20);


            g.setFont(new Font("DynaPuff", Font.BOLD, 40));
            g.drawString("Planting Bomb..." , (xSize - 300) / 2, 125);
        }

        if(player.bomb != null && player.bomb.defusingBomb)
        {
            double progress = (5.0 - player.bomb.timeToDefuse) / 5.0 ;
            drawProgressBar(g, progress, (xSize - 300) / 2, 200, 300, 20);

            g.setFont(new Font("DynaPuff", Font.BOLD, 40));
            g.drawString("Defusing Bomb...", (xSize - 300) / 2, 125);
        }

        //Draw a progress bar to indicate how many seconds left until the bomb explodes
        if(player.world.getBomb() != null && player.world.getBomb().planted)
        {
            g.setColor(new Color(222, 7, 21, 200));
            double progress = (player.world.getBomb().timeTillExplosion) / 40;
            drawProgressBar(g, progress, (xSize - 300) / 2, 50, 300, 20);
        } else {
            int minLeft = (int)(data.timeLeft / 60);
            int secondsLeft = (int)(data.timeLeft % 60);
            g.setFont(new Font("DynaPuff", Font.BOLD, 40));
            g.setColor(new Color(222, 7, 21, 175));
            g.drawString(minLeft + (secondsLeft < 10 ? ":0" : ":") + secondsLeft, (xSize - 50) / 2, 75);
        }

        //Draw the current score
        
        g.setColor(new Color(5, 78, 161, 200));
        g.fillRect((xSize + 30) / 2, 0, 30, 30);

        g.setColor(new Color(161, 88, 5, 200));
        g.fillRect((xSize - 30) / 2, 0, 30, 30);

        g.setColor(Color.BLACK);
        g.setFont(new Font("DynaPuff", Font.BOLD, 30));
        g.drawString(data.CTWins.toString(), (xSize + 30) / 2 + 2, 28);
        g.drawString(data.TWins.toString(), (xSize - 30) / 2 + 2, 28);

        //Draw the player's inventory

        if(player.primary != null)
        {
            g.drawImage(new ImageIcon("./res/images/weapons/" + player.primary.getName() + ".png").getImage(), xSize - 200, ySize - 200, 200, 50, null);
            if(player.getWeapon() == player.primary)
            {
                g.setColor(new Color(0, 0, 0, 70));
                g.fillRect(xSize - 200, ySize - 200, 200, 50);
                g.setColor(Color.BLACK);
                g.drawRect(xSize - 200, ySize - 200, 200, 50);
            }
        }
        if(player.secondary != null)
        {
            g.drawImage(new ImageIcon("./res/images/weapons/" + player.secondary.getName() + ".png").getImage(), xSize - 200, ySize - 125, 200, 50, null);
            if(player.getWeapon() == player.secondary)
            {
                g.setColor(new Color(0, 0, 0, 70));
                g.fillRect(xSize - 200, ySize - 125, 200, 50);
                g.setColor(Color.BLACK);
                g.drawRect(xSize - 200, ySize - 125, 200, 50);
            }
        }
        if(player.bomb != null)
        {
            g.drawImage(new ImageIcon("./res/images/textures/bomb.jpg").getImage(), xSize - 75, ySize - 75, 50, 50, null);
            if(player.bomb.plantingBomb)
            {
                g.setColor(new Color(0, 0, 0, 70));
                g.fillRect(xSize -75, ySize - 75, 200, 50);
                g.setColor(Color.BLACK);
                g.drawRect(xSize -75, ySize - 75, 200, 50);
            }
        }
    }
}
