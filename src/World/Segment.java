package src.World;

import src.Math.Vec2;

public class Segment
{
    public int texture;
    public boolean isWall;
    public boolean isWallBangable;
    public boolean plantable;
    public char site;
    public Vec2 position;   //Position of the wall in world-space
}