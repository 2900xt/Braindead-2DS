package src.World.Entity.Pathfinding;

import src.Math.Vec2;
import src.World.Entity.Bot;

public class Pathfinder 
{
    protected static int[] xDirs = {0, 1, 0, -1};
    protected static int[] yDirs = {1, 0, -1, 0};

    protected Bot bot;

    public Pathfinder(Bot bot)
    {
        this.bot = bot;
    }

    protected void goTo(Vec2 destination)
    {
        if(bot.getPos().equals(destination)) return;

        //Try to walk all paths, from most to least optimal towards the destination
        Vec2 displacement = bot.getPos().subtract(destination);

        //Best angle should be arctan(y/x)
        double bestAngle = Math.atan(displacement.getY() / displacement.getX());

        if(displacement.getX() > 0)
        {
            bestAngle += Math.PI;
        }

        for(int i = 0; i < 18; i++)
        {
            double currentAngle1 = bestAngle - i * 20;
            Vec2 currentDistance1 = Vec2.fromPolar(Bot.speed, currentAngle1);
            double currentDistance1Magn = currentDistance1.subtract(displacement).getMagnitude();

            double currentAngle2 = bestAngle + i * 20;
            Vec2 currentDistance2 = Vec2.fromPolar(Bot.speed, currentAngle2);
            double currentDistance2Magn = currentDistance2.subtract(displacement).getMagnitude();

            if(!bot.checkForWalls(currentDistance1) && currentDistance1Magn < currentDistance2Magn) {
                bot.setAngle(currentAngle1);
                bot.move(currentDistance1);
                return;
            }
            
            if(!bot.checkForWalls(currentDistance2)) {
                bot.setAngle(currentAngle2);
                bot.move(currentDistance2);
                return;
            }
        }
    }
    
    public void pathFind(Vec2 destination)
    {
        goTo(destination);
    }
}
