package src.World.Guns;

public class Revolver extends Shootable {


    public Revolver(boolean soundEnabled)
    {
        reloadSound = "AR_reload.wav";
        shootSound = "R_shoot.wav";
        weaponName = "Revolver";
        maxMagSize = 6;
        dmg = 55;
        bulletsRemaining = maxMagSize;
        totalBulletsRemaining = maxMagSize * 12;
        fireRate = 0.8;
        currentReloadTime = 0.0;
        reloadTime = 2.5;
        recoil = 50;
        cost = 500;
        this.soundEnabled = soundEnabled;
    }
}
