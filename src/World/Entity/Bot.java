package src.World.Entity;

import src.Math.Vec2;
import src.World.World;
import src.World.Entity.Pathfinding.AStarPathfinder;
import src.World.Entity.Pathfinding.BFSPathfinder;
import src.World.Entity.Pathfinding.Pathfinder;
import src.World.Guns.*;

public class Bot extends Playable {

    private Playable lockedOnEnemy;
    public boolean isDefusing;
    public Pathfinder pf;
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

        pf = new AStarPathfinder(this);
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
                pf.pathFind(world.getBomb().location);
            }
        } else {
            free = true;
        }
        if (lockedOnEnemy != null && free)
        {
            pf.pathFind(lockedOnEnemy.pos);

            if(!currentWeapon.isReloading() && !currentWeapon.isShooting())
            {
                attackEnemy();
            }  
        }

        if(lockedOnEnemy == null && free)
        {
            pf.pathFind(siteToPathfind);
        }
    }

    public void doT()
    {
        if(bomb != null)
        {
            if(!world.BSite.equals(pos))
            {
                pf.pathFind(world.BSite);
            } else 
            {
                bomb.plantBomb(world);
            }
        }

        if (lockedOnEnemy != null)
        {
            pf.pathFind(lockedOnEnemy.pos);

            if(!currentWeapon.isReloading() && !currentWeapon.isShooting())
            {
                attackEnemy();
            }
        }

        if(lockedOnEnemy == null && world.getBomb() == null)
        {
            pf.pathFind(siteToPathfind);
        }

        if(world.getBomb() == null) return;

        if(world.getBomb().planted)
        {
            pf.pathFind(world.getBomb().location);
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
