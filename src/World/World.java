package src.World;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;

import src.Display.*;
import src.Game.Sound;
import src.Math.*;
import src.World.Entity.*;
public class World 
{

    public Segment[][] data;
    private HashSet<Playable> entities;
    private HashSet<Bullet> bullets;
    private Player player;
    private Bomb bomb;


    public Vec2 TSpawn, CTSpawn;
    public Vec2 ASite, BSite;


    public World(String filename)
    {

        //A temporary place to store the data inside the .map file
        char[] _fileData = null;

        //Try-catch block in case the file doesn't exist
        try {
            FileInputStream file = new FileInputStream(filename);
            _fileData = new String(file.readAllBytes()).toCharArray();
            file.close();
        } catch (Exception e) {
            System.err.printf("Unable to open map '%s'\n", filename);
            System.exit(1);
        }

        //Make sure that the file isn't empty
        assert(_fileData != null);

        //Store the actual file data, without '\n' characters
        ArrayList<Character> fileData = new ArrayList<>();

        //Remove the newlines
        for(int i = 0; i < _fileData.length; i++)
        {
            if((_fileData[i] >= '0' && _fileData[i] <= '9') || _fileData[i] == 'a' || _fileData[i] == 'b' || _fileData[i] == 'c' || _fileData[i] == 't')
            {
                fileData.add(_fileData[i]);
            }
        }

        final int dataLen = (int)Math.sqrt(fileData.size());
        data = new Segment[dataLen][dataLen];

        int fileDataCounter = 0;

        //This loop makes a 2D bitmap of the .map file
        for(int i = 0; i < dataLen; i++)
        {
            data[i] = new Segment[dataLen];
            for(int j = 0; j < dataLen; j++)
            {
                data[i][j] = new Segment();

                //If the texture is greater than 7, it is a wall
                data[i][j].isWall = fileData.get(fileDataCounter) >= '7';
                data[i][j].texture = fileData.get(fileDataCounter) - '0';
                data[i][j].isWallBangable = fileData.get(fileDataCounter) == '9';
                data[i][j].position = new Vec2(j, i);

                if(fileData.get(fileDataCounter) == 'a' )
                {
                    ASite = new Vec2(j, i);
                    data[i][j].site = fileData.get(fileDataCounter);
                    data[i][j].texture = 10;
                    data[i][j].isWall = false;
                    data[i][j].plantable = true;
                }

                else if(fileData.get(fileDataCounter) == 'b')
                {
                    BSite = new Vec2(j, i);
                    data[i][j].site = fileData.get(fileDataCounter);
                    data[i][j].texture = 10;
                    data[i][j].isWall = false;
                    data[i][j].plantable = true;
                }

                else if(fileData.get(fileDataCounter) == 'c')
                {
                    CTSpawn = new Vec2(j , i);
                    data[i][j].texture = 0;
                    data[i][j].isWall = false;
                }

                else if(fileData.get(fileDataCounter) == 't')
                {
                    TSpawn = new Vec2(j , i);
                    data[i][j].texture = 0;
                    data[i][j].isWall = false;
                }
                else if(Math.random() < 0.0005)
                {
                    data[i][j].texture = 6;
                }

                fileDataCounter++;

            }
        }

        bullets = new HashSet<>();
        entities = new HashSet<>();
    }

    public Segment getSegment(double x, double y)
    {
        if(x < 0 || y < 0)                        return null;
        if(x >= data.length || y >= data.length)  return null;
        return data[(int)y][(int)x];
    }

    public Player getPlayer()
    {
        return player;
    }
    
    public void createPlayer(Player player)
    {
        entities.add(player);
        this.player = player;
    }

    public HashSet<Playable> getEntities()
    {
        return entities;
    }

    public void addEntity(Playable entity)
    {
        entities.add(entity);
    }

    public void removeEntity(Playable entity)
    {
        entities.remove(entity);
    }

    public int getSize()
    {
        return data.length * data.length;
    }

    public void shootBullet(double x, double y, double angle, double dmg, Playable shooter)
    {
        x -= Math.cos(Math.toRadians(angle)) / 4;
        y -= Math.sin(Math.toRadians(angle)) / 4;

        Vec2 speed = new Vec2(
            -Math.cos(Math.toRadians(angle)) * 0.2,
            -Math.sin(Math.toRadians(angle)) * 0.2
        );
        bullets.add(new Bullet(new Vec2(x, y), speed, dmg, shooter));
    }

    private boolean collidesWith(Vec2 bullet, Vec2 entity)
    {
        double distance = Math.sqrt(Math.pow(bullet.getY() - entity.getY(), 2) + Math.pow(bullet.getX() - entity.getX(), 2));
        return distance < 0.5;
    }

    public void checkCollisions()
    {
        HashSet<Bullet> deadBullets = new HashSet<>();
        for (Bullet bullet : bullets) {
            if(deadBullets.contains(bullet)) continue;
            HashSet<Playable> newEntities = new HashSet<>();
            for (Playable entity : entities)
            {
                if(bullet.dead)
                {
                    deadBullets.add(bullet);
                }
                if(collidesWith(bullet.getLocation(), entity.getPos()) && bullet.getTeam() != entity.getTeam())
                {
                    entity.takeDamage(bullet.getDamage());
                    deadBullets.add(bullet);
                    if(entity.isDead() && bullet.getShooter() == player)
                    {
                        Player p = (Player)bullet.getShooter();
                        p.increaseKillCount();
                        p.creds += 200;

                        UI.killScreenTime = 3;
                        Sound.playSoundFile("kill/" + p.killCount + ".wav");
                        
                    }
                }
                if(!entity.isDead()) 
                {
                    newEntities.add(entity);
                }
            }
            entities = newEntities;
        }

        for(Bullet deadBullet : deadBullets)
        {
            bullets.remove(deadBullet);
        }
    }

    public void updateBullets()
    {
        for(Bullet bullet : bullets)
        {
            bullet.update();
            Segment s = getSegment(bullet.getLocation().round().getX(), (int)bullet.getLocation().round().getY());
            if(s.isWall && !s.isWallBangable)
            {
                bullet.dead = true;
            }
        }
    }

    public HashSet<Bullet> getBullets()
    {
        return bullets;
    }

    public void killAll()
    {
        entities = new HashSet<>();
        bullets = new HashSet<>();
    }

    public void setBomb(Bomb b)
    {
        this.bomb = b;
    }

    public Bomb getBomb()
    {
        return bomb;
    }
}
