package src.World.Entity.Pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import src.Math.Vec2;
import src.World.Entity.Bot;

public class BFSPathfinder extends Pathfinder
{
    protected boolean[][] visited;
    protected Vec2[][] parents;
    protected ArrayList<Vec2> curPath;
    protected int pathIndex;
    protected int sz;

    private Vec2 prevPosFail, prevDestFail;

    public BFSPathfinder(Bot bot) 
    {
        super(bot);
        curPath = new ArrayList<>();
        pathIndex = 0;

        prevDestFail = new Vec2(-1, -1);
        prevPosFail = new Vec2(-1, -1);

        sz = (int) Math.sqrt(bot.world.getSize());

        parents = new Vec2[sz][sz];
        visited = new boolean[sz][sz];

        FWPathfinder test = new FWPathfinder(bot);
        FWPathfinder.printPath(0, 14);
    }

    protected void bfs(int x, int y)
    {
        Queue<Vec2> q = new LinkedList<>();
        q.add(new Vec2(x, y));

        while(!q.isEmpty())
        {
            Vec2 cur = q.remove();
            x = (int)cur.getX();
            y = (int)cur.getY();

            visited[x][y] = true;

            for(int k = 0; k < 4; k++)
            {
                int nx = xDirs[k] + x;
                int ny = yDirs[k] + y;
                if(nx == -1 || ny == -1 || nx == sz || ny == sz)
                {
                    continue;
                }

                if(bot.world.getSegment(nx, ny).isWall) 
                {
                    continue;
                }
            
                if(!visited[nx][ny]) 
                {
                    parents[nx][ny] = new Vec2(x, y);
                    visited[nx][ny] = true;
                    q.add(new Vec2(nx, ny));
                }
            }
        }
        
    }

    protected boolean createPath(Vec2 destination)
    {
        parents = new Vec2[sz][sz];
        visited = new boolean[sz][sz];

        int px = (int) Math.round(bot.getPos().getX()), py = (int) Math.round(bot.getPos().getY());
        int dx = (int) Math.round(destination.getX()), dy = (int) Math.round(destination.getY());

        bfs(px, py);

        if(!visited[dx][dy] || !visited[px][py])
        {
            return false;
        }

        curPath = new ArrayList<>();
        pathIndex = 0;
        while(dx != px || dy != py)
        {
            curPath.add(0, new Vec2(dx, dy));

            if(parents[dx][dy] == null) 
            {
                curPath.clear();
                return false;
            }

            int oldX = dx, oldY = dy;
            dx = (int) Math.round(parents[oldX][oldY].getX());
            dy = (int) Math.round(parents[oldX][oldY].getY());
        }

        return true;
    }

    public void pathFind(Vec2 destination)
    {
        long cur = System.currentTimeMillis();
        if(curPath.size() == 0 || !curPath.get(curPath.size() - 1).equals(destination))
        {
            if(prevDestFail.equals(destination, 1.0) && prevPosFail.equals(bot.getPos()))
            {
                return;
            }

            if(!createPath(destination))
            {
                prevDestFail = destination;
                prevPosFail = bot.getPos();
                System.out.printf("BFS: NO PATH FOUND FROM \t{%.1f, %.1f} to \t{%.1f, %.1f}\n", 
                    bot.getPos().getX(), 
                    bot.getPos().getY(),
                    destination.getX(),
                    destination.getY()
                );

                for(int i = 0; i < visited.length; i++)
                {
                    for(int j = 0; j < visited[i].length; j++)
                    {
                        System.err.print((visited[j][i] ? 1 : 0) + " ");
                    }
                    System.err.println();
                }
                System.err.println();
                return;
            }
        }

        if(curPath.size() == 0) return;
        if(pathIndex == curPath.size()) return;
        
        if(curPath.get(pathIndex).equals(bot.getPos()))
        {
            pathIndex++;
        }

        if(pathIndex == curPath.size()) return;

        goTo(curPath.get(pathIndex));
        
        timeUsed += System.currentTimeMillis() - cur;
    }
}
