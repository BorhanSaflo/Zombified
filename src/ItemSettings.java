//imports
import javax.swing.*;
import java.awt.*;

public class ItemSettings {
    private String currentWeapon; //Holds the current weapon that the player is holding
    private int damage; //Holds the amount of damage each bullet does.
    private double speed; //Holds the speed of the bullet
    private int cooldown; //Holds the cooldown between each bullet
    private int lifetime; //Holds the life of the bullet
    private double kickback; //Holds the amount of kickback that affects the player
    private int spread; //The amount of spread of the bullets. Less = more accurate, More = less accurate
    private int totalAmmo; //Holds the total amount of ammo the player has in the inventory
    private int clipAmmo; //Holds the amount of ammo each clip has
    private int currentAmmo; //Holds the amount of ammo that the current clip has
    private int reloadTime; //Holds the amount of time it takes to reload the current weapon.
    private int skinNumber; //Holds the number of the first frame of the player that holds that specific weapon.
    private int maxAmmo; //Holds the max amount of ammo that the player could have.

    //Image of each weapon/item
    private final Image pistolIcon = new ImageIcon("Assets/Weapons/pistol.png").getImage();
    private final Image SMGIcon = new ImageIcon("Assets/Weapons/smg.png").getImage();
    private final Image rifleIcon = new ImageIcon("Assets/Weapons/rifle.png").getImage();
    private final Image sniperIcon = new ImageIcon("Assets/Weapons/sniper.png").getImage();
    private final Image shotgunIcon = new ImageIcon("Assets/Weapons/shotgun.png").getImage();

    private final Image healthIcon = new ImageIcon("Assets/items/health.png").getImage();
    private final Image shieldIcon = new ImageIcon("Assets/items/shield.png").getImage();
    private final Image ammoIcon = new ImageIcon("Assets/items/ammo.png").getImage();

    //The player starts with the pistol
    public ItemSettings() {
        changeWeapon("Pistol");
    }

    //Get the weapon/item image based on the name that is given
    public Image getItemIcon(String item) {
        if (item.equals("SMG")) return SMGIcon;
        if (item.equals("Rifle")) return rifleIcon;
        if (item.equals("Ammo")) return ammoIcon;
        if (item.equals("Health")) return healthIcon;
        if (item.equals("Shield")) return shieldIcon;
        if (item.equals("Sniper")) return sniperIcon;
        if (item.equals("Shotgun")) return shotgunIcon;

        return pistolIcon;
    }

    //Resets the amount of ammo that the player has
    public void resetAmmo() {
        totalAmmo = maxAmmo;
        currentAmmo = clipAmmo;
    }

    //This method is given a weapon name and it changes all the bullet/weapon variables based on the weapon name that is given
    public void changeWeapon(String weaponName) {

        //Pistol settings
        if (weaponName.equals("Pistol")) {
            currentWeapon = weaponName;
            skinNumber = 0;
            damage = 20;
            speed = 10;
            cooldown = 75;
            lifetime = 100;
            kickback = 2;
            spread = 1;
            reloadTime = 2000;
            clipAmmo = 10;
            maxAmmo = clipAmmo * 3;
            totalAmmo = maxAmmo;
            currentAmmo = clipAmmo;
        }

        //Rifle settings
        if (weaponName.equals("Rifle")) {
            currentWeapon = weaponName;
            skinNumber = 4;
            damage = 20;
            speed = 20;
            cooldown = 20;
            lifetime = 50;
            kickback = 1.5;
            reloadTime = 2000;
            spread = 3;
            clipAmmo = 20;
            maxAmmo = clipAmmo * 5;
            totalAmmo = maxAmmo;
            currentAmmo = clipAmmo;
        }

        //SMG settings
        if (weaponName.equals("SMG")) {
            currentWeapon = weaponName;
            skinNumber = 8;
            damage = 8;
            speed = 25;
            cooldown = 10;
            lifetime = 40;
            kickback = 1.5;
            reloadTime = 1500;
            spread = 4;
            clipAmmo = 25;
            maxAmmo = clipAmmo * 5;
            totalAmmo = maxAmmo;
            currentAmmo = clipAmmo;
        }

        //Sniper settings
        if (weaponName.equals("Sniper")) {
            currentWeapon = weaponName;
            skinNumber = 12;
            damage = 100;
            speed = 30;
            cooldown = 200;
            lifetime = 100;
            kickback = 3;
            reloadTime = 3000;
            spread = 0;
            clipAmmo = 5;
            maxAmmo = clipAmmo * 3;
            totalAmmo = maxAmmo;
            currentAmmo = clipAmmo;
        }

        //Shotgun settings
        if (weaponName.equals("Shotgun")) {
            currentWeapon = weaponName;
            skinNumber = 16;
            damage = 7;
            speed = 8;
            cooldown = 100;
            lifetime = 50;
            kickback = 5;
            reloadTime = 3000;
            spread = 7;
            clipAmmo = 5;
            maxAmmo = clipAmmo * 3;
            totalAmmo = maxAmmo;
            currentAmmo = clipAmmo;
        }
    }

    //This method is called from the reload method and it gives the player a new clip.
    public void newClip(int ammoNeed) {
        if (ammoNeed <= totalAmmo) {
            totalAmmo -= ammoNeed;
            currentAmmo += ammoNeed;
        } else {
            currentAmmo += totalAmmo;
            totalAmmo = 0;
        }
    }

    //When the player picks up a ammo supply item off the ground
    public void ammoRefill() {
        if (totalAmmo > maxAmmo - 2 * clipAmmo) totalAmmo = maxAmmo;
        else totalAmmo += 2 * clipAmmo;
    }

    //Getters and Setters
    public int getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getLifetime() {
        return lifetime;
    }

    public int getClipAmmo() {
        return clipAmmo;
    }

    public int getTotalAmmo() {
        return totalAmmo;
    }

    public double getKickback() {
        return kickback;
    }

    public String getCurrentWeapon() {
        return currentWeapon;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public void subtractCurrentAmmo() {
        currentAmmo -= 1;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getSpread() {
        return spread;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public int getSkinNumber() {
        return skinNumber;
    }

}
