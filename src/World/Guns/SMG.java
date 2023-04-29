package src.World.Guns;

public class SMG extends Shootable{


    public SMG(boolean soundEnabled)
    {
        reloadSound = "AR_reload.wav";
        shootSound = "SMG_shoot.wav";
        weaponName = "SMG";
        maxMagSize = 33;
        dmg = 18;
        bulletsRemaining = maxMagSize;
        totalBulletsRemaining = maxMagSize * 4;
        fireRate = 0.05;
        currentReloadTime = 0.0;
        reloadTime = 1.5;
        this.soundEnabled = soundEnabled;
        recoil = 40;
        cost = 850;
    }
}
