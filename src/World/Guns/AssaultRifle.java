package src.World.Guns;

public class AssaultRifle extends Shootable{

    public AssaultRifle(boolean soundEnabled)
    {
        reloadSound = "AR_reload.wav";
        shootSound = "AR_shoot.wav";
        weaponName = "Assault Rifle";
        maxMagSize = 25;
        dmg = 40;
        bulletsRemaining = maxMagSize;
        totalBulletsRemaining = maxMagSize * 4;
        fireRate = 0.2;
        currentReloadTime = 0.0;
        reloadTime = 3.0;
        this.soundEnabled = soundEnabled;
        recoil = 20;
        cost = 1500;
    }
}
