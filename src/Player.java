/**
 * @(#)Player.java
 * @author Borhan Saflo
 * This file contains everything that is related to the player. This includes the movment of the player, the player walking animation and reloading methods.
 */

//imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Player implements ActionListener {

    private final GamePanel game; //Game object
    private final ItemSettings weapon; //weapon settings object

    //Player variables
    private double x = 650, y = 600; //The x and y coordinates of the player
    private double theta = 0; //The direction of the player
    private Image[] pics; //The array that will hold the animation frames for the player
    private int delay= 0; //delay variable for the player's animation
    private int frame = 0; //this variable what frame is the player current at in the animation
    private int cooldown = 0; //the shooting cooldown
    private int flashCooldown = 0; //the gun flash cooldown
    private final Image flash = new ImageIcon("Assets/flash.png").getImage(); //Load the gun flash image
    private double health = 100; //The player's health, 100 when the game starts.
    private double shield = 100; //The player's shield, 100 when the game starts.
    private boolean reloading; //Reloading boolean variable, indicates if the player is currently reloading or not.
    private boolean shooting = false; //Shooting boolean variable, indicates if the player is currently shooting or not.
    private Timer reloadTimer; //The reload timer that waits a couple of seconds before reloading the weapon

    public Player(GamePanel game, ItemSettings weapon) {
        this.game = game;
        this.weapon = weapon;
        loadSkin();
    }

    //The reset method will be called when a new game is starting, it resets the player's position and health.
    public void reset() {
        x=650;
        y=600;
        health = shield = 100;
    }

    //Load the correct skin based on what weapon the player is currently using
    public void loadSkin() {
        pics = new Image[4];
        for (int i = 0; i < 4; i++) {
            int number = i + weapon.getSkinNumber();
            pics[i] = new ImageIcon("Assets/player/player" + number + ".png").getImage();
        }
    }

    //Method to check if the gun the player is currently using is cooled down to shoot again
    public boolean ready() {
        if (cooldown > weapon.getCooldown()) {
            cooldown = 0;
            return true;
        } else {
            return false;
        }
    }

    //Similarly to the method above, this method checks if the flash that gun shows is ready to be displayed based on the weapon cooldown
    public boolean flashReady() {
        if (flashCooldown > weapon.getCooldown()) {
            flashCooldown = 0;
            return true;
        } else {
            return false;
        }
    }

    //The update method updates the player
    public void update(double mx, double my, LevelLoader levelLoader) {

        theta = Math.atan2(my - 422, mx - 525); //The theta of the player is always in the direction of the mouse

        cooldown += 1; //Every update add 1 to the cooldown variable

        flashCooldown += 1; //Every update add 1 to the flash cooldown variable

        //The x and y velocity of the player
        double vy;
        double vx = vy = 0;

        //If the player is moving around, change the frame of the player so it shows an animation of the player walking
        if (game.getKeys()[KeyEvent.VK_W] && levelLoader.collisionN() || game.getKeys()[KeyEvent.VK_S] && levelLoader.collisionS() || game.getKeys()[KeyEvent.VK_A] && levelLoader.collisionW() || game.getKeys()[KeyEvent.VK_D] && levelLoader.collisionE()) {
            int WAIT = 5;
            if (delay % WAIT == 0) {
                frame = (frame + 1) % pics.length;
            }
        }

        //If the W key is pressed and the player is not colliding with any obstacles, move north
        if (game.getKeys()[KeyEvent.VK_W] && levelLoader.collisionN()) {
            if (y > 0) {
                vy = -2;
                double length = Math.sqrt(vx * vx + vy * vy);
                double top = Math.min(length, 2);
                vy *= top / length;
                y += vy;
            }
        }

        //If the S key is pressed and the player is not colliding with any obstacles, move South
        if (game.getKeys()[KeyEvent.VK_S] && levelLoader.collisionS()) {
            if (y < 1870) {
                vy = 2;
                double length = Math.sqrt(vx * vx + vy * vy);
                double top = Math.min(length, 2);
                vy *= top / length;
                y += vy;
            }
        }

        //If the A key is pressed and the player is not colliding with any obstacles, move west
        if (game.getKeys()[KeyEvent.VK_A] && levelLoader.collisionW()) {
            if (x > 2) {
                if (x > 0) {
                    vx = -2;
                    double length = Math.sqrt(vx * vx + vy * vy);
                    double top = Math.min(length, 2);
                    vx *= top / length;
                    x += vx;
                }
            }
        }

        //If the D key is pressed and the player is not colliding with any obstacles, move east
        if (game.getKeys()[KeyEvent.VK_D] && levelLoader.collisionE()) {
            if (x < 3155) {
                if (x < 3150) {
                    vx = 2;
                    double length = Math.sqrt(vx * vx + vy * vy);
                    double top = Math.min(length, 2);
                    vx *= top / length;
                    x += vx;
                }
            }
        }

        //If the space bar is pressed or the mouse is pressed down, the weapon is cooled down, the player has ammo, the player is not reloading, and the game is not paused.
        if ((game.getKeys()[KeyEvent.VK_SPACE] || game.getClick()) && ready() && weapon.getCurrentAmmo() != 0 && !reloading && !game.getPause()) {

            shooting = true; //set the shooting boolean variable to true so the gun flash can appear

            //Play the gun's shooting sound effect
            SoundEffects se = new SoundEffects(1000, weapon.getCurrentWeapon());
            game.getSoundEffects().add(se);
            se.playSound();

            //If the weapon is a shotgun, shoot 10 bullets
            if (weapon.getCurrentWeapon().equals("Shotgun")) {
                for (int i = 0; i < 10; i++) {
                    Bullet b = new Bullet(game, mx, my, this, weapon);
                    game.getBullets().add(b);
                }
            }

            //If its any other weapon, shoot one player
            else {
                Bullet b = new Bullet(game, mx, my, this, weapon);
                game.getBullets().add(b);
            }

            weapon.subtractCurrentAmmo(); //Subtract one from the current ammo

            //Weapon kickback causing the player to move back
            //if the player is not colliding with any obstacles.
            if (levelLoader.collisionN() && levelLoader.collisionS() && levelLoader.collisionW() && levelLoader.collisionE()) {

                vx = -Math.cos(theta); //Set the x velocity to the opposite of the direction the player is looking towards
                vy = -Math.sin(theta); //Set the y velocity to the opposite of the direction the player is looking towards

                //Normalize the velocities
                double length = Math.sqrt(vx * vx + vy * vy);
                double top = Math.min(length, weapon.getKickback());
                vx *= top / length;
                vy *= top / length;

                //Update the x and y coordinates
                x += vx;
                y += vy;
            }
        }

        //If the R key is pressed and the player is currently not reloading, call the reload method
        if ((game.getKeys()[KeyEvent.VK_R]) && !reloading) reload();

        //If the players health is 0, the game is over
        if (health<=0) game.gameOver();

        delay += 1; //Every update, add one to the delay variable
    }

    //This method is called when the player wants to reload the weapon.
    public void reload() {

        int ammoNeeded = weapon.getClipAmmo() - weapon.getCurrentAmmo(); //Calculate how much ammo the is needed for a full clip

        //if the current clip is not full and the player has more ammo in the inventory.
        if (ammoNeeded > 0 && weapon.getTotalAmmo() > 0) {
            reloading = true;
            reloadTimer = new Timer(weapon.getReloadTime(), this); //the reload timer, so it waits someone time before the weapon is reloaded
            reloadTimer.setRepeats(false);
            reloadTimer.start(); //start the timer

            //Play the reload sound effect for the weapon that the player is currently using
            SoundEffects se = new SoundEffects(weapon.getReloadTime(), weapon.getCurrentWeapon() + "Reload");
            game.getSoundEffects().add(se);
            se.playSound();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int ammoNeeded = weapon.getClipAmmo() - weapon.getCurrentAmmo(); //Calculate the amount of ammo needed for a full clip.
        weapon.newClip(ammoNeeded); //Reloads the weapon
        reloading = false;
        reloadTimer.stop();
    }

    public void damageHealth(int damage) {
        if (shield >= 0) shield -= damage;
        else health -= damage;
    }

    //when the player picks a health pack
    public void regenerateHealth() {
        if (health >= 50) health = 100;
        else health += 50;
    }

    //when the player picks a shield
    public void regenerateShield() {
        if (shield >= 50) shield = 100;
        else shield += 50;
    }

    public void draw(GamePanel gamePanel, Graphics2D g) {
        //X and Y offsets, the player is always in the middle of the screen
        int X = 500;
        int Y = 400;

        //Rotate the player towards theta which is the direction of the mouse
        g.rotate (theta, X + 25, Y + 22);

        //draw the player for each frame of the animation.
        g.drawImage(pics[frame], X, Y, null);

        if ((!gamePanel.getKeys()[KeyEvent.VK_SPACE] && !gamePanel.getClick())) {
            shooting = false;
        }

        //If the player is shooting, flash cool down is ready, the player is not reloading, and the player doesnt have 0 ammo, draw the weapon flash.
        if (shooting && flashReady() && !reloading && weapon.getCurrentAmmo() != 0) g.drawImage(flash,  X + 45, Y + 15, null);

        g.rotate(-theta, X + 25, Y + 22);
    }

    //Getters and setters
    public boolean getReloading() {
        return reloading;
    }

    public double getHealth() {
        return health;
    }

    public double getShield() {
        return shield;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setCoordinate(int xCord, int yCord) {
        x=xCord;
        y=yCord;
    }

}
