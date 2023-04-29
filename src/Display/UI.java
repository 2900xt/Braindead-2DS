package src.Display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import src.Game.Sound;
import src.World.World;
import src.World.Entity.Playable;
import src.World.Entity.Player;
import src.World.Guns.AssaultRifle;
import src.World.Guns.Revolver;
import src.World.Guns.SMG;
import src.World.Guns.Shootable;
import src.World.Guns.Shotgun;

public class UI {
    public static boolean drawChooseTeamScreen(Graphics g, Renderer r, Player p, World w)
    {
        int width = r.getBuffer().getWidth(), height = r.getBuffer().getHeight();
        int boxWidth = width / 3;
        int boxHeight = height / 3;

        g.setColor(Color.BLACK);

        g.setFont(new Font("DynaPuff", Font.BOLD, 50));
        g.drawString("Select Team", width / 2 - 175, 200);

        g.setColor(new Color(20, 20, 20, 200));
        g.fillRect(0, 0, width, height);

        int CT_BOX_X = width / 2 - boxWidth - 50;
        int CT_BOX_Y = height / 2 - boxHeight / 2;
        g.setColor(new Color(5, 78, 161, 100));
        g.fillRect(CT_BOX_X, CT_BOX_Y, boxWidth, boxHeight);

        int T_BOX_X = width / 2 + 50;
        int T_BOX_Y = height / 2 - boxHeight / 2;
        g.setColor(new Color(161, 88, 5, 100));
        g.fillRect(T_BOX_X, T_BOX_Y, boxWidth, boxHeight);

        if(isOnBox(T_BOX_X, T_BOX_Y, boxWidth, boxHeight, r))
        {
            g.setColor(new Color(161, 88, 5, 225));
            g.fillRect(T_BOX_X, T_BOX_Y, boxWidth, boxHeight);
        }

        if(isOnBox(CT_BOX_X, CT_BOX_Y, boxWidth, boxHeight, r))
        {
            g.setColor(new Color(5, 78, 161, 225));
            g.fillRect(CT_BOX_X, CT_BOX_Y, boxWidth, boxHeight);
        }

        r.drawCursor(g);

        if(isOnBox(CT_BOX_X, CT_BOX_Y, boxWidth, boxHeight, r) && p.isMouseClicked())
        {
            p.setTeam(false);
            return true;
        }

        if(isOnBox(T_BOX_X, T_BOX_Y, boxWidth, boxHeight, r) && p.isMouseClicked())
        {
            p.setTeam(true);
            return true;
        }

        if(p.getKeysPressed().contains(KeyEvent.VK_ESCAPE))
        {
            return true;
        }


        return false;
    }

    public static void displayWinScreen(Graphics g, boolean team, BufferedImage img, Player p, World w)
    {
        if(team)
        {
            g.setColor(new Color(92, 48, 2, 200));
        } else 
        {
            g.setColor(new Color(4, 67, 130, 200));
        }

        int iconWidth = img.getWidth() / 4;
        int iconHeight = img.getHeight() / 4;

        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        
        g.setFont(new Font("DynaPuff", Font.BOLD, 70));
        g.setColor(Color.BLACK);
        String text = (team ? "Terrorists Win" : "Counter Terrorists Win");
        g.drawString(text, (team ? 250 : 100), (int)(img.getHeight() / 1.5 - iconWidth)); 


        if(team)
        {
            g.drawImage(new ImageIcon("./Assets/textures/T_logo.png").getImage(), (img.getWidth() / 2) - iconWidth / 2, (img.getHeight() / 2) - iconHeight / 2, iconWidth, iconHeight, null);
        } else 
        {
            g.drawImage(new ImageIcon("./Assets/textures/CT_logo.png").getImage(), (img.getWidth() / 2) - iconWidth / 2, (img.getHeight() / 2) - iconHeight / 2, iconWidth, iconHeight, null);
        }

        if(p.getKills() >= 5)
        {
            Sound.playSoundFile("ace.wav");
        }
        int TCount = 0, CTCount = 0;
        for(Playable entity : w.getEntities())
        {
            if(entity.getTeam()) TCount++;
            else CTCount++;
        }

        if((TCount  >= 5 || CTCount >= 5) && p.getKills() < 5)
        {
            Sound.playSoundFile("flawless.wav");
        }

    }
    public static boolean isOnBox(int x, int y, int width, int height, Renderer r)
    {
        return r.getMouseX() >= x
            && r.getMouseX() <= x + width
            && r.getMouseY() >= y
            && r.getMouseY() <= y + height;
    }
    
    public static void drawWeaponStats(Graphics g, Shootable s, Player p, int startX, int startY)
    {
        g.setColor(Color.WHITE);
        g.drawString(s.getName(), startX, startY);
        startY += 42;
        if(p.creds >= s.getCost())
        {
            g.setColor(Color.GREEN);
        } else 
        {
            g.setColor(Color.RED);
        }
        g.drawString("Cost:   " + s.cost + "       You Have:   " + p.creds, startX, startY);
        startY += 42;
        g.setColor(Color.WHITE);
        g.drawString("Damage Per Bullet:   " + s.dmg, startX, startY);
        startY += 42;
        g.drawString("Mag size:   " + s.getMagSize() + " / " + s.getTotalRemainingBullets(), startX, startY);
        startY += 42;
        g.drawString("Fire Rate:   " + s.fireRate, startX, startY);
        
    }

    public static boolean displayBuyMenu(Graphics g, Player p, BufferedImage img, Renderer r)
    {
        g.setColor(new Color(20, 20, 20, 200));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());


        if(p.getKeysPressed().contains(KeyEvent.VK_ESCAPE))
        {
            return true;
        }

        int BOX_GAP = 20;

        int BOX_WIDTH = img.getWidth() / 2 - BOX_GAP;
        int BOX_HEIGHT = img.getHeight() / 3 - BOX_GAP;
        int AR_BOX_X = BOX_GAP, AR_BOX_Y = BOX_GAP;
        int SG_BOX_X = BOX_WIDTH + BOX_GAP * 2, SG_BOX_Y = BOX_GAP;
        int SM_BOX_X = BOX_GAP, SM_BOX_Y = BOX_HEIGHT + BOX_GAP * 2;
        int R_BOX_X = BOX_WIDTH + BOX_GAP * 2, R_BOX_Y = BOX_HEIGHT + BOX_GAP * 2;

        g.setColor(new Color(30, 30, 30, 200));
        g.fillRect(R_BOX_X, R_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        g.fillRect(SM_BOX_X, SM_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        g.fillRect(SG_BOX_X, SG_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        g.fillRect(AR_BOX_X, AR_BOX_Y, BOX_WIDTH, BOX_HEIGHT);

        g.drawImage(new ImageIcon("./Assets/textures/weapons/Assault Rifle.png").getImage() , AR_BOX_X, AR_BOX_Y, BOX_WIDTH, BOX_HEIGHT, null);
        g.drawImage(new ImageIcon("./Assets/textures/weapons/Shotgun.png").getImage()       , SG_BOX_X, SG_BOX_Y, BOX_WIDTH, BOX_HEIGHT, null);
        g.drawImage(new ImageIcon("./Assets/textures/weapons/SMG.png").getImage()           , SM_BOX_X, SM_BOX_Y, BOX_WIDTH, BOX_HEIGHT, null);
        g.drawImage(new ImageIcon("./Assets/textures/weapons/Revolver.png").getImage()      , R_BOX_X,  R_BOX_Y,  BOX_WIDTH, BOX_HEIGHT, null);

        int STATS_X = BOX_GAP;
        int STATS_Y = SM_BOX_Y + BOX_HEIGHT + BOX_GAP * 2;

        g.setColor(new Color(60, 60, 60, 200));
        if(isOnBox(AR_BOX_X, AR_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r))
        {
            g.fillRect(AR_BOX_X, AR_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
            drawWeaponStats(g, new AssaultRifle(false), p, STATS_X, STATS_Y);
        }

        if(isOnBox(SG_BOX_X, SG_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r))
        {
            g.fillRect(SG_BOX_X, SG_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
            drawWeaponStats(g, new Shotgun(false), p, STATS_X, STATS_Y);
        }

        if(isOnBox(SM_BOX_X, SM_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r))
        {
            g.fillRect(SM_BOX_X, SM_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
            drawWeaponStats(g, new SMG(false),p , STATS_X, STATS_Y);
        }

        if(isOnBox(R_BOX_X, R_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r))
        {
           g.fillRect(R_BOX_X, R_BOX_Y, BOX_WIDTH, BOX_HEIGHT);
           drawWeaponStats(g, new Revolver(false), p, STATS_X, STATS_Y);
        }

        r.drawCursor(g);
        if(isOnBox(AR_BOX_X, AR_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r) && p.isMouseClicked() && p.creds >= new AssaultRifle(false).getCost())
        {
            p.creds -= new AssaultRifle(false).getCost();
            p.primary = new AssaultRifle(true);
            p.equipWeapon(p.primary);
            return true;
        } else if(isOnBox(SG_BOX_X, SG_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r) && p.isMouseClicked() && p.creds >= new Shotgun(false).getCost()) {
            p.creds -= new Shotgun(false).getCost();
            p.primary = new Shotgun(true);
            p.equipWeapon(p.primary);
            return true;
        } else if(isOnBox(SM_BOX_X, SM_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r) && p.isMouseClicked() && p.creds >= new SMG(false).getCost()) {
            p.creds -= new SMG(false).getCost();
            p.primary = new SMG(true);
            p.equipWeapon(p.primary);
            return true;
        } else if (isOnBox(R_BOX_X, R_BOX_Y, BOX_WIDTH, BOX_HEIGHT, r) && p.isMouseClicked() && p.creds >= new Revolver(false).getCost()) {
            p.creds -= new Revolver(false).getCost();
            p.secondary = new Revolver(true);
            p.equipWeapon(p.secondary);
            return true;
        } else {
            return false;
        }

    }

    public static double killScreenTime = 0;

    public static void drawKillScreen(Player p, Graphics g, BufferedImage img)
    {
        g.drawImage(new ImageIcon("./Assets/images/kill" + Math.min(p.getKills(), 5) + ".png").getImage(), (img.getWidth() - 150) / 2, (img.getHeight() - 250), 150, 150, null);
    }

}
