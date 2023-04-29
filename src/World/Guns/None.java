package src.World.Guns;

public class None extends Shootable{

    public None()
    {
        ableToShoot = false;
    }
    public String getWeaponName() {return "None";}
    public boolean isShooting() {return false;}
    public boolean ableToShoot() {return ableToShoot = false;}
    public void shoot(){}
    public void reload(){}
    
}
