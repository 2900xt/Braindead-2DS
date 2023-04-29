package src.Display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import src.Game.Game;
import src.Math.Vec2;
import src.World.Segment;
import src.World.World;
import src.World.Entity.Bullet;
import src.World.Entity.Playable;

public class Renderer {
    private BufferedImage buffer;
    private World world;
    private ArrayList<ImageIcon> textures;
    private ArrayList<Segment> segments;
    private ImageIcon wallpaper;
    private Game panel;
    public static int segmentSizePx = 75;
    public boolean cursorEnabled;

    public Renderer(BufferedImage image, World world, Game p)
    {
        this.buffer = image;
        this.world = world;
        this.panel = p;

        //Load the textures
        textures = new ArrayList<>();
        for(int i = 0 ; i < 11; i++)
        {
            textures.add(new ImageIcon("./res/images/textures/" + i + ".png"));
        }

        //Load the segments
        segments = new ArrayList<>();
        int segmentCount = (int)Math.sqrt(world.getSize());
        for(int y = 0; y < segmentCount; y++)
        {
            for(int x = 0; x < segmentCount; x++)
            {
                Segment worldSegment = world.getSegment(x, y);
                Segment normalized = new Segment();
                normalized.isWall = worldSegment.isWall;
                normalized.position = normalizeCoords(worldSegment.position);
                normalized.texture = worldSegment.texture;
                segments.add(normalized);
            }
        }

        wallpaper = new ImageIcon("./res/images/wallpaper.jpg");
    }



    public void renderWorld(Graphics g)
    {
        //Draw the wallpaper
        g.drawImage(wallpaper.getImage(), 0, 0, buffer.getWidth(), buffer.getHeight(), null);

        //Draw where the player is currently from a bird-eye view
        Vec2 camera = world.getPlayer().getPos().scalarMultiply(segmentSizePx).subtract(new Vec2(buffer.getWidth() / 2, buffer.getHeight() / 2));

        for (Segment current : segments) {
            drawSegment(camera, g, current);
        }

        if(world.getBomb() != null && world.getBomb().planted)
        {
            drawBomb(g, camera);
        }

        drawGun(g);

        for (Playable entity : world.getEntities())
        {
            drawEntity(g, entity, camera);
        }

        drawBullets(g, camera);

        if(world.getBomb() != null && world.getBomb().blownUp)
        {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0 ,buffer.getWidth(), buffer.getHeight());
        }

        if(cursorEnabled)
        {
            drawCursor(g);
        }
        
    }

    public void drawCursor(Graphics g)
    {
        if(cursorEnabled)
        {
            g.setColor(new Color(250, 0, 0, 200));
            g.fillOval((int)mouseX, (int)mouseY, 10, 10);
        }
    }

    private void drawEntity(Graphics g, Playable entity, Vec2 camera)
    {
        Vec2 offsetPos = entity.getPos().scalarMultiply(segmentSizePx).subtract(camera);
        offsetPos = offsetPos.subtract(new Vec2(segmentSizePx / 2, segmentSizePx / 2));

        if(!checkIfOnScreen(offsetPos)) return;

        ImageIcon i;
        if(entity.getTeam()){
            g.setColor(Color.ORANGE);
            i = new ImageIcon("./res/images/textures/t.png");
        } else {
            g.setColor(Color.BLUE);
            i = new ImageIcon("./res/images/textures/ct.png");
        }
        g.drawImage(i.getImage(), (int)offsetPos.getX(), (int)offsetPos.getY() , segmentSizePx, segmentSizePx, null);
    }

    private Vec2 normalizeCoords(Vec2 data)
    {
        Vec2 result = data.scalarMultiply(segmentSizePx);
        return result;
    }

    public static void drawThickLine(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {

        //credit for the code: https://www.rgagnon.com/javadetails/java-0260.html
        // The thick line is in fact a filled polygon
        g.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;
        // line length
        double lineLength = Math.sqrt(dX * dX + dY * dY);
    
        double scale = (double)(thickness) / (2 * lineLength);
    
        // The x,y increments from an endpoint needed to create a rectangle...
        double ddx = -scale * (double)dY;
        double ddy = scale * (double)dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int)ddx;
        int dy = (int)ddy;
    
        // Now we can compute the corner points...
        int xPoints[] = new int[4];
        int yPoints[] = new int[4];
    
        xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;
    
        g.fillPolygon(xPoints, yPoints, 4);
    }

    private void drawGun(Graphics g)
    {
        if(world.getPlayer().isDead()) return;
        if( world.getPlayer().getWeapon() == null || world.getPlayer().getWeapon().getName() == null || world.getPlayer().getWeapon().getName().equals("None")) return;
        //Angle is arctan(dy/dx) from center

        double gunX = mouseX - (buffer.getWidth() / 2);
        double gunY = mouseY - (buffer.getHeight() / 2);
        double slope = (gunY) / (gunX);
        double angle = Math.atan(slope);
        if(angle < 0)
        {
            angle += Math.PI;
        }

        angle = Math.toDegrees(angle);
        if(mouseY >= (buffer.getHeight() / 2)) angle += 180;

        world.getPlayer().setAngle(angle);

        g.setColor(Color.RED);
        if(!cursorEnabled) g.drawLine(buffer.getWidth() / 2, buffer.getHeight() / 2, (int)mouseX, (int)mouseY);

        //Draw a weapon (box)
        gunX = -Math.cos(Math.toRadians(angle)) * segmentSizePx + buffer.getWidth() / 2;
        gunY = -Math.sin(Math.toRadians(angle)) * segmentSizePx + buffer.getHeight() / 2;
        drawThickLine(g, buffer.getWidth() / 2, buffer.getHeight() / 2, (int)gunX, (int)gunY, 10, Color.BLACK);

    }

    public boolean checkIfOnScreen(Vec2 point)
    {
        return 
        point.getX() < buffer.getWidth() + segmentSizePx && 
        point.getX() >= -segmentSizePx &&
        point.getY() < buffer.getHeight() + segmentSizePx &&
        point.getY() >= -segmentSizePx;
    }

    public void drawSegment(Vec2 camera, Graphics g, Segment segment)
    {
        Vec2 offsetPos = segment.position.subtract(camera);
        offsetPos.setY(offsetPos.getY() - segmentSizePx / 2);
        offsetPos.setX(offsetPos.getX() - segmentSizePx / 2);
        if(checkIfOnScreen(offsetPos)) {
            g.drawImage(textures.get(segment.texture).getImage(), (int)offsetPos.getX(), (int)offsetPos.getY(), segmentSizePx, segmentSizePx, null);
            if(segment.isWall)
            {
                g.setColor(Color.black);
                g.drawRect((int)offsetPos.getX(), (int)offsetPos.getY(), segmentSizePx, segmentSizePx);
            }
        }
    }

    public void drawBomb(Graphics g, Vec2 camera)
    {
        Vec2 bombPos = world.getBomb().location;
        Vec2 offsetPos = normalizeCoords(bombPos).subtract(camera);
        offsetPos.setY(offsetPos.getY() - segmentSizePx / 3);
        offsetPos.setX(offsetPos.getX() - segmentSizePx / 3);
        g.drawImage(new ImageIcon("./res/textures/bomb.jpg").getImage(), (int)offsetPos.getX(), (int)offsetPos.getY(), (int)(segmentSizePx / 1.5), (int)(segmentSizePx  / 1.5), null);
    }

    public void drawBullets(Graphics g, Vec2 camera)
    {
        for (Bullet bullet : world.getBullets())
        {
            bullet.draw(g, camera, segmentSizePx);
        }
    }

    MouseMotionListener mouseInput = new MouseMotion();

    public void enableAim()
    {
        cursorEnabled = true;
        panel.addMouseMotionListener(mouseInput);
        panel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("./res/Null_Cursor.png").getImage(), new Point(0, 0), ""));
    }

    public void disableAim()
    {
        cursorEnabled = true;
    }

    private double mouseX = 1, mouseY = 1;

    private class MouseMotion implements MouseMotionListener
    {

        @Override
        public void mouseDragged(MouseEvent arg0) {
            mouseX = arg0.getX();
            mouseY = arg0.getY();
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            mouseX = arg0.getX();
            mouseY = arg0.getY();
        }
        
    }

    public BufferedImage getBuffer()
    {
        return buffer;
    }

    public int getMouseX()
    {
        return (int)mouseX;
    }

    public int getMouseY()
    {
        return (int)mouseY;
    }
}
