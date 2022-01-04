/**
 * @(#)Bullet.java
 * @author Borhan Saflo
 * This file contains everything that is related to the bullets, including how the bullets behave depending on the weapon and what direction they are heading.
 */

//Import statements.
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class Bullet {

    //Loading the needed objects
    private final GamePanel game;
    private final Player player;
    private final ItemSettings weapon;

    private final double vx; //The x velocity of the bullet.
    private final double vy; //The y velocity of the bullet.
    private double x; //The x coordinate of the bullet.
    private double y; //The y coordinate of the bullet.
    private double lifetime; //The variable that golds the lifetime of the bullet.

    public Bullet(GamePanel game, double mx, double my, Player player, ItemSettings weapon) {
        this.weapon = weapon;
        this.player = player;
        this.game = game;

        //Bullet is spawned in the middle of the player
        this.x = player.getX() + 25;
        this.y = player.getY() + 22;

        double theta = Math.atan2(my - 422, mx - 525); //the direction of the bullet is the direction of the mouse
        double theta2 = Math.toDegrees(theta); //Convert theta to degrees

        //Generating random numbers for spread, the range depends on the weapon that is currently held.
        Random rand = new Random();
        int spread = weapon.getSpread();
        int spread2 = (spread != 0) ? rand.nextInt(spread + spread) - spread : 0; //if the spread value of the weapon is 0, no need to generate a random number, the value will just be 0.

        //Add the spread to the theta and calculate the velocity of the x and y.
        this.vx = weapon.getSpeed() * Math.cos(Math.toRadians(theta2 + spread2));
        this.vy = weapon.getSpeed() * Math.sin(Math.toRadians(theta2 + spread2));
        this.lifetime = weapon.getLifetime(); //The lifetime of the bullet.
    }

    //The update method updates the bullet.
    public void update() {
        x += vx; //Every update, Add the x velocity to the x coordinate.
        y += vy; //Every update, Add the y velocity to the y coordinate.

        lifetime -= 1; //Every update, subtract 1 from the bullet's lifetime.

        //For every bullet.
        for (int i = 0; i < game.getBullets().size(); i++) {
            //If the life time of the bullet is 0.
            if (lifetime <= 0) {
                //noinspection SuspiciousListRemoveInLoop
                game.getBullets().remove(i); //Remove the bullet because the life time of the bullet is 0.
            }
        }
    }

    //The draw method for the player's bullets.
    public void draw(Graphics2D g) {
        double offsetX = 500 - player.getX();
        double offsetY = 400 - player.getY();
        Ellipse2D.Double oval;
        if (weapon.getCurrentWeapon().equals("Shotgun")) oval = new Ellipse2D.Double.Double(offsetX + x, offsetY + y, 3, 3);
        else oval = new Ellipse2D.Double.Double(offsetX + x, offsetY + y, 4, 4);
        g.setColor(Color.white);
        g.fill(oval);
    }

    //Getter Methods
    public double getBulletX() {
        return x;
    }

    public double getBulletY() {
        return y;
    }
}