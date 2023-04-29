package src.Display;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import src.World.*;

public class Minimap {
    int screenX, screenY;
    World world;
    int size;


    public static boolean MMapEnable;

    public void setSize(int size)
    {
        this.size = size;
    }

    public Minimap(int x, int y, World world)
    {
        screenX = x;
        screenY = y;
        this.world = world;
        size = 5;
    }

    public void draw(Graphics g)
    {
        if(!MMapEnable) return;
        int mapSize = (world.getPlayer().getKeysPressed().contains(KeyEvent.VK_M) ? size * 3 : size);
        int currentX = screenX, currentY = screenY;
        for(int i = 0; i < world.data.length; i++)
        {
            for(int j = 0; j < world.data[0].length; j++)
            {
                Segment currentSegment = world.data[i][j];
                //Draw a pixel with a different color depending on if the next segment is a wall
                
                if(currentSegment.plantable)
                {
                    g.setFont(new Font("DynaPuff", Font.PLAIN, mapSize * 2));
                    g.setColor(Color.BLUE);
                    g.drawString(currentSegment.site + "", currentX, currentY - 1);
                    g.setColor(Color.GREEN);
                }

                else if(currentSegment.isWall)
                {
                    g.setColor(new Color(3, 138, 145, 100));
                } else {
                    g.setColor(new Color(220, 239, 245, 150));
                }

                
                g.fillRect(currentX, currentY, mapSize, mapSize);
                currentX += mapSize;
            }
            currentX = screenX;
            currentY += mapSize;
        }

        g.setColor(Color.red);

        g.fillRect(
            (int)world.getPlayer().getPos().scalarMultiply(mapSize).getX(),
            (int)world.getPlayer().getPos().scalarMultiply(mapSize).getY(),
            mapSize,
            mapSize
        );

    }

}
