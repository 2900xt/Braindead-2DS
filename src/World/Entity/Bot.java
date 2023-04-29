package src.World.Entity;

import src.Math.Vec2;
import src.World.World;
import src.World.Guns.*;

public class Bot extends Playable {

    private Playable lockedOnEnemy;
    public boolean isDefusing;
    private Vec2 siteToPathfind;

    public static double recoil;
    public static double speed;

    public Bot(int x, int y, boolean team, boolean hasBomb, double startingHP, World w) {
        super(x, y, team, startingHP, hasBomb);
        equipWeapon(new Revolver(true));
        boolean site = Math.random() > 0.5;
        world = w;
        if(site)
        {
            siteToPathfind = world.ASite;
        }
        else 
        {
            siteToPathfind = world.BSite;
        }
    }

    public void pathFind(Vec2 destination)
    {
        if(pos.equals(destination)) return;

        //Try to walk all paths, from most to least optimal towards the destination
        Vec2 displacement = pos.subtract(destination);

        //Best angle should be arctan(y/x)
        double bestAngle = Math.atan(displacement.getY() / displacement.getX());

        if(displacement.getX() > 0)
        {
            bestAngle += Math.PI;
        }

        for(int i = 0; i < 18; i++)
        {
            double currentAngle1 = bestAngle - i * 20;
            Vec2 currentDistance1 = Vec2.fromPolar(speed, currentAngle1);
            double currentDistance1Magn = currentDistance1.subtract(displacement).getMagnitude();

            double currentAngle2 = bestAngle + i * 20;
            Vec2 currentDistance2 = Vec2.fromPolar(speed, currentAngle2);
            double currentDistance2Magn = currentDistance2.subtract(displacement).getMagnitude();

            if(!checkForWalls(currentDistance1) && currentDistance1Magn < currentDistance2Magn) {
                setAngle(currentAngle1);
                move(currentDistance1);
                return;
            }
            
            if(!checkForWalls(currentDistance2)) {
                setAngle(currentAngle2);
                move(currentDistance2);
                return;
            }
        }
        
    }

    public void doCT()
    {
        boolean free = false;
        if(world.getBomb() != null && world.getBomb().planted)
        {
            if(world.getBomb().location.equals(pos))
            {
                if(!world.getBomb().defusingBomb)
                    world.getBomb().defuseBomb(this, world);
                else if(world.getBomb().defuser == this){
                    world.getBomb().defuseBomb(this, world);
                } else {
                    free = true;
                }
            } else {
                pathFind(world.getBomb().location);
            }
        } else {
            free = true;
        }
        if (lockedOnEnemy != null && free)
        {
            pathFind(lockedOnEnemy.pos.add(new Vec2(Math.random(), Math.random())));

            if(!currentWeapon.isReloading() && !currentWeapon.isShooting())
            {
                attackEnemy();
            }  
        }

        if(lockedOnEnemy == null && free)
        {
            pathFind(siteToPathfind);
        }
    }

    public void doT()
    {
        if(bomb != null)
        {
            if(!world.BSite.equals(pos))
            {
                pathFind(world.BSite);
            } else 
            {
                bomb.plantBomb(world);
            }
        }

        if (lockedOnEnemy != null)
        {
            pathFind(lockedOnEnemy.pos);

            if(!currentWeapon.isReloading() && !currentWeapon.isShooting())
            {
                attackEnemy();
            }
        }

        if(lockedOnEnemy == null && world.getBomb() == null)
        {
            pathFind(siteToPathfind);
        }

        if(world.getBomb() == null) return;

        if(world.getBomb().planted)
        {
            pathFind(world.getBomb().location);
        }

    }

    public void attackEnemy()
    {
        Vec2 displacement = pos.subtract(lockedOnEnemy.pos);

        //Best angle should be arctan(y/x)
        double bestAngle = Math.atan(displacement.getY() / displacement.getX());

        if(displacement.getX() > 0)
        {
            bestAngle += Math.PI;
        }

        bestAngle += Math.PI;

        angleDegrees = Math.toDegrees(bestAngle);
        angleDegrees = angleDegrees + (Math.random() * recoil);
        shootWeapon();
    }

    public Playable lockOnEnemy()
    {
        //Search for the closest enemy and lock onto it

        Playable closestEnemy = null;
        Vec2 closestDistance = new Vec2(10000, 10000);
        for(Playable p : world.getEntities())
        {
            if(p.team == team) continue;
            Vec2 displacement = pos.subtract(p.pos);
            if(displacement.getMagnitude() < closestDistance.getMagnitude())
            {
                closestEnemy = p;
                closestDistance = displacement;
            }
        }

        if(closestDistance.getMagnitude() >= 8) return null;

        return closestEnemy;
    }

    @Override
    public void onTick()
    {
        if(lockedOnEnemy == null || lockedOnEnemy.isDead())
            lockedOnEnemy = lockOnEnemy();

        if(!team)
        {
            doCT();
        } else 
        {
            doT();
        }

        if(lockedOnEnemy != null && Math.abs(lockedOnEnemy.getPos().subtract(pos).getMagnitude()) >= 10)
        {
            lockedOnEnemy = null;
        }
    }
    
}
