package src.World.Entity.Pathfinding;

import src.World.Entity.Bot;

public class FWPathfinder extends Pathfinder
{
    private static final int INF = (int)(1e9);
    private static int[][] parents, adjMat;
    protected static int sz, ssz;

    private static void init(Bot bot)
    {   
        sz = bot.world.getSize();
        ssz = (int) (Math.sqrt(sz));
        parents = new int[sz][sz];
        adjMat = new int[sz][sz];

        for(int i = 0; i < sz; i++) for(int j = 0; j < sz; j++)
        {
            adjMat[i][j] = INF;
        }

        for(int i = 0; i < sz; i++) 
        {
            if(i + 1 < sz) 
            {
                adjMat[i][i + 1] = 1;
                adjMat[i + 1][i] = 1;
            }

            if(i - 1 >= 0) 
            {
                adjMat[i][i - 1] = 1;
                adjMat[i - 1][i] = 1;
            }

            if(i + ssz < sz)
            {
                adjMat[i][i + ssz] = 1;
                adjMat[i + ssz][i] = 1;
            } 

            if(i - ssz >= 0) 
            {
                adjMat[i][i - ssz] = 1;
                adjMat[i - ssz][i] = 1;
            }

            adjMat[i][i] = 0;
        }

        for(int i = 0; i < sz; i ++) for(int j = 0; j < sz; j++) parents[i][j] = i;

        for(int k = 0; k < sz; k++)
        {
            for(int i = 0; i < sz; i++)
            {
                for(int j = 0; j < sz; j++)
                {
                    if(adjMat[i][k] + adjMat[k][j] < adjMat[i][j])
                    {
                        adjMat[i][j] = adjMat[i][k] + adjMat[k][j];
                        parents[i][j] = parents[k][j];
                    }
                }
            }
        }
    }

    public static void printPath(int i, int j)
    {
        if(i != j) printPath(i, parents[i][j]);
        System.out.println(j);
    }

    public FWPathfinder(Bot bot) 
    {
        super(bot);
        if(parents == null) init(bot);
    }
}
