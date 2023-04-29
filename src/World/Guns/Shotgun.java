package src.World.Guns;

import src.Game.Sound;

public class Shotgun extends Shootable {


    public Shotgun(boolean soundEnabled)
    {
        reloadSound = "SG_reload.wav";
        shootSound = "SG_shoot.wav";
        weaponName = "Shotgun";
        maxMagSize = 5;
        dmg = 20;
        bulletsRemaining = maxMagSize;
        totalBulletsRemaining = maxMagSize * 4;
        fireRate = 1.0;
        currentReloadTime = 0.0;
        reloadTime = 1.0;
        recoil = 30;
        cost = 1000;
        this.soundEnabled = soundEnabled;
    }

    public void reload()
    {
        //This is to make sure that you can't simultaneously reload twice
        if(reloading || totalBulletsRemaining == 0 || bulletsRemaining == maxMagSize) return;
        reloading = true;

        if(soundEnabled)
        Sound.playSoundFile(reloadSound);
        //This thread resets the 'reloading' variable asynchronously after 'reloadTime'
        //Lets us resume the game while the weapon is in a state of 'reloading'
        Thread reloadThread = new Thread(new Reloader());
        reloadThread.start();
    }

    private class Reloader implements Runnable
    {
        @Override
        public void run() {
            try {
                for(double i = 0; i < reloadTime * 1000; i++)
                {
                    Thread.sleep(1);
                    currentReloadTime += 0.001;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Reset the variables here to make the gun able to shoot again

            totalBulletsRemaining -= 1;
            bulletsRemaining += 1;

            if(totalBulletsRemaining < 0)
            {
                bulletsRemaining += totalBulletsRemaining;
                totalBulletsRemaining = 0;
            }

            reloading = false;
            currentReloadTime = 0.0;
        }
    }
}
