package src.World.Guns;

import src.Game.Sound;

public abstract class Shootable {
    
    //Max amount of bullets
    public int maxMagSize = 0;
    public double dmg = 0;

    //Current amount of bullets
    public int bulletsRemaining = maxMagSize;
    public int totalBulletsRemaining = maxMagSize * 4;

    //Time in seconds to wait before able to fire again
    public double fireRate = 0;

    //Time in seconds for the gun to reload
    public double reloadTime = 0;
    public double currentReloadTime = 0;

    protected boolean reloading = false, ableToShoot = true;

    protected String reloadSound, shootSound;
    protected String weaponName;

    protected double recoil;

    public int cost = 0;

    public boolean soundEnabled;

    public double getDamage()
    {
        return dmg;
    }

    public double getCurrentReloadTime()
    {
        return currentReloadTime;
    }

    public double getReloadTime()
    {
        return reloadTime;
    }

    public int getRemainingBullets()
    {
        return bulletsRemaining;
    }

    public int getMagSize()
    {
        return maxMagSize;
    }

    public boolean isReloading()
    {
        return reloading;
    }

    public String getName()
    {
        return weaponName;
    }
    
    public double getRecoil()
    {
        return recoil;
    }

    public boolean isShooting()
    {
        return !ableToShoot;
    }

    public int getCost()
    {
        return cost;
    }

    public int getTotalRemainingBullets()
    {
        return totalBulletsRemaining;
    }

    public void shoot()
    {
        //You can't shoot with zero bullets, so we automatically trigger reload
        if(bulletsRemaining == 0) 
        {
            reload();
            return;
        }

        //You need to wait for the weapon to reload before shooting
        //You also cannot shoot faster than the 'fireRate', as implemented with the 'ableToShoot' boolean
        if(!(ableToShoot && !reloading)) return;
        bulletsRemaining -= 1;

        ableToShoot = false;

        if(soundEnabled)
        Sound.playSoundFile(shootSound);

        //This thread resets the 'ableToShoot' variable after 'fireRate' has elapsed
        Thread waitThread = new Thread(new WaitForBullet());
        waitThread.start();
    }

    public void reload()
    {
        //This is to make sure that you can't simultaneously reload twice
        if(reloading || !ableToShoot) return;
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
                for(double i = 0; i < reloadTime * 100; i++)
                {
                    Thread.sleep(10);
                    currentReloadTime += 0.01;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Reset the variables here to make the gun able to shoot again
            if(bulletsRemaining > 0)
            {
                totalBulletsRemaining += bulletsRemaining;
            }
            
            totalBulletsRemaining -= maxMagSize;
            bulletsRemaining = maxMagSize;

            if(totalBulletsRemaining < 0)
            {
                bulletsRemaining += totalBulletsRemaining;
                totalBulletsRemaining = 0;
            }

            reloading = false;
            currentReloadTime = 0.0;
        }
    }

    private class WaitForBullet implements Runnable{
    @Override
        public void run() {
            try {
                Thread.sleep((long)(fireRate * 1000));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //Reset the variables here to make the gun able to shoot again
            ableToShoot = true;
        }
    }

}
