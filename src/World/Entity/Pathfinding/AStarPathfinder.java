package src.World.Entity.Pathfinding;

import java.util.PriorityQueue;
import src.Math.Vec2;
import src.World.Entity.Bot;

public class AStarPathfinder extends BFSPathfinder
{
    public AStarPathfinder(Bot bot) 
    {
        super(bot);
    }

    private static class Edge implements Comparable<Edge>
    {
        public int priority;
        public Vec2 pos;

        public Edge(int pri, Vec2 pos)
        {
            this.priority = pri;
            this.pos = pos;
        }

        @Override
        public int compareTo(Edge o) 
        {
            return (int)(o.priority - priority);
        }
    };

    private int EuclideanHeuristic(int x1, int y1, int x2, int y2)
    {
        return (int) (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private int ManhattanHeuristic(int x1, int y1, int x2, int y2)
    {
        return (int) (Math.abs(x1 - x2) + Math.abs(y1 - y2));
    }
    
    protected void bfs(int x, int y)
    {
        PriorityQueue<Edge> q = new PriorityQueue<>();
        q.add(new Edge(-EuclideanHeuristic(x, y, (int)dest.getX(), (int)dest.getY()), new Vec2(x, y)));

        while(!q.isEmpty())
        {
            Edge cur = q.remove();
            x = (int)cur.pos.getX();
            y = (int)cur.pos.getY();

            visited[x][y] = true;

            int[][] distances = new int[sz][sz];
            
            for(int i = 0; i < sz; i++) for(int j = 0; j < sz; j++) distances[i][j] = (int)(1e9);

            distances[x][y] = 0;

            for(int k = 0; k < 8; k++)
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
            
                if(!visited[nx][ny] && distances[nx][ny] >= distances[x][y] + 1) 
                {
                    parents[nx][ny] = new Vec2(x, y);
                    visited[nx][ny] = true;
                    distances[nx][ny] = distances[x][y] + 1;

                    int h = -EuclideanHeuristic(nx, ny, (int)dest.getX(), (int)dest.getY());
                    q.add(new Edge(h, new Vec2(nx, ny)));
                }
            }
        }
    }

    private Vec2 dest;
    
    protected boolean createPath(Vec2 destination)
    {
        dest = destination;
        return super.createPath(destination);
    }
}
