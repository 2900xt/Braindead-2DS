package src.Math;
public class Vec2 implements Comparable<Vec2>
{
    private double[] data;

    //Constructor - sets the vector to 0

    public Vec2()
    {
        data = new double[2];
        setX(0);
        setY(0);
    }

    public Vec2(double val)
    {
        data = new double[2];
        setX(val);
        setY(val);
    }

    //Constructor - sets the vector to given values

    public Vec2(double x, double y)
    {
        data = new double[2];
        setX(x);
        setY(y);
    }

    //Copy constructor

    public Vec2(Vec2 other)
    {
        data = new double[2];
        setX(other.getX());
        setY(other.getY());
    }

    public static Vec2 fromPolar(double magnitude, double angle)
    {
        return new Vec2(Math.cos(angle) * magnitude, Math.sin(angle) * magnitude);
    }

    public Vec2 add(Vec2 other)
    {
        Vec2 newVector = new Vec2();
        for(int i = 0; i < getData().length; i++)
        {
            newVector.getData()[i] = getData()[i] + other.getData()[i];
        }
        return newVector;
    }

    public Vec2 subtract(Vec2 other)
    {
        Vec2 newVector = new Vec2();
        for(int i = 0; i < getData().length; i++)
        {
            newVector.getData()[i] = getData()[i] - other.getData()[i];
        }
        return newVector;
    }

    public Vec2 scalarMultiply(double scalar)
    {
        Vec2 newVector = new Vec2();
        for(int i = 0; i < getData().length; i++)
        {
            newVector.getData()[i] = getData()[i] * scalar;
        }
        return newVector;
    }

    public Vec2 round()
    {
        Vec2 newVector = new Vec2();
        newVector.setX(Math.round(getX()));
        newVector.setY(Math.round(getY()));
        return newVector;
    }

    public String toString()
    {
        return "{ " + getX() + ", " + getY() + " }";
    }


    public double getX()
    {
        return data[0];
    }

    public double getY()
    {
        return data[1];
    }

    public void setX(double x)
    {
        data[0] = x;
    }

    public void setY(double y)
    {
        data[1] = y;
    }

    public double[] getData()
    {
        return data;
    }

    public double getMagnitude()
    {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
    }

    public boolean equals(Vec2 other, double eps)
    {
        return Math.abs(getX() - other.getX()) + Math.abs(getY() - other.getY()) < eps;
    }

    public boolean equals(Vec2 other)
    {
        return equals(other, 0.5);
    }

    @Override
    public int compareTo(Vec2 o) 
    {
        return (int) (o.getX() - getX() + o.getY() - getY());
    }
}
