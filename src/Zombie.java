/**
 * @(#)Zombie.java
 * @author Borhan Saflo
 * This file contains everything that has to do with the zombies. This includes their behaviour and their pathfinding system.
 */

//imports
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Zombie {

    //Importing all the needed objects
    private final GamePanel game;
    private final Player player;
    private final ItemSettings weapon;
    private final Hud hud;
    private final LevelLoader loader;

    private boolean goToTarget = true; //Boolean variable that indicates if the zombie should head to its target or its currently facing an obstacle

    private double x, y; //Holds the current X and Y coordinates
    private double theta = (int) (Math.random() * 360); //Holds the direction that the zombie is facing, it generates a random angle when the zombie spawns.
    private final double speed; //Holds the speed of the zombie, it generates a random speed between 2 and 0.3 when the zombie spawns.
    private final Image zombie; //Holds the image of the zombie
    private Rectangle2D.Double zombieRect;
    private double maxHealth = (int) (Math.random() * (110 - 90)) + 90; //Holds the max health that the zombie could have.
    private double health = maxHealth; //Holds the current health of the zombies, it is at max when the zombie spawns
    private final boolean isBoss; //Boolean variable that determines if the zombie is a boss zombie

    public Zombie(GamePanel game, Player player, ItemSettings weapon, Hud hud, double x, double y, LevelLoader loader, boolean isBoss) {
        //Set the variables to the parameters values
        this.x = x;
        this.y = y;
        this.game = game;
        this.player = player;
        this.weapon = weapon;
        this.hud = hud;
        this.loader = loader;
        this.isBoss = isBoss;
        this.speed = (isBoss) ? Math.random() * (2.2 - 1) + 1 : Math.random() * (2 - 0.3) + 0.3;

        if (isBoss) {
            this.maxHealth = 1000;
            this.health = maxHealth;
            this.zombie = new ImageIcon("Assets/zombie/zombieBoss.png").getImage();
        } else {
            int r = (int) (Math.random() * 8); //Generate a random integer between 0 and 8
            this.zombie = new ImageIcon("Assets/zombie/zombie" + r + ".png").getImage(); //Set the zombie image to the zombie image that as the same number has the random integer that was generated.
        }
    }

    public void update(LevelLoader levelLoader) {

        bulletCollision(); //Check bullet collisions each update

        //Colliding Rectangles
        zombieRect = new Rectangle2D.Double(x, y, 35, 43);
        Rectangle2D playerRect = new Rectangle2D.Double(player.getX(), player.getY(), 43, 49);

        double vx = 0; //Holds the X  velocity of the zombie
        double vy = 0; //Holds the Y velocity of the zombie

        //Distance to the player calculations
        double playerDistanceX = x - player.getX();
        double playerDistanceY = y - player.getY();
        double playerDistance = Math.sqrt(playerDistanceX * playerDistanceX + playerDistanceY * playerDistanceY);

        //If the Zombie is in 1000 distance away from the player
        if (0 < Math.abs(playerDistance) && Math.abs(playerDistance) < 1000) {

            //If the player and the zombie are both outside
            if (!loader.isPlayerInside() && !loader.isZombieInside(this)) {

                //If the zombie should go to the target or worry about an obstacle in the way
                if (goToTarget) {
                    theta = Math.atan2(player.getY() - y, player.getX() - x);
                    theta = (Math.toDegrees(theta) + 360) % 360;
                }

                //If the zombie is not colliding with any obstacles, then its allowed to proceed to the their target
                if (!levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieW(this)) {
                    goToTarget = true;
                }

                //If the zombie collides with an obstacle that is in the south direction of the zombie
                if (levelLoader.collisionZombieS(this)) {
                    goToTarget = false;
                    if (theta < 90 && theta > 0) theta = 0;
                    if (theta > 90 && theta < 180) theta = 180;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the west direction of the zombie
                if (levelLoader.collisionZombieW(this)) {
                    goToTarget = false;
                    if (theta < 180 && theta >= 90) theta = 90;
                    if (theta <= 270 && theta >= 180) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the north direction of the zombie
                if (levelLoader.collisionZombieN(this)) {
                    goToTarget = false;
                    if (theta < 270 && theta > 180) theta = 180;
                    if (theta > 270 && theta <= 360) theta = 0;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the east direction of the zombie
                if (levelLoader.collisionZombieE(this)) {
                    goToTarget = false;

                    if (theta <= 90 && theta >= 0) theta = 90;
                    if (theta >= 270 && theta <= 360) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                //If the zombie is not colliding with an obstacle, calculates the velocity it needs to go their target
                if (!levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieW(this)) {
                    vx = speed * Math.cos(Math.toRadians(theta));
                    vy = speed * Math.sin(Math.toRadians(theta));
                }
            }

            //If the player is outside and the zombie is inside.
            if (!loader.isPlayerInside() && loader.isZombieInside(this)) {

                //If the zombie should go to the target or worry about an obstacle in the way
                if (!goToTarget) {
                    theta = loader.closestExit(this);
                    theta = (Math.toDegrees(theta) + 360) % 360;
                }

                //If the zombie is not colliding with any obstacles, then its allowed to proceed to the their target
                if (!levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieW(this)) {
                    goToTarget = true;
                }

                //If the zombie collides with an obstacle that is in the south direction of the zombie
                if (levelLoader.collisionZombieS(this)) {
                    goToTarget = false;
                    if (theta <= 90 && theta >= 0) theta = 0;
                    if (theta > 90 && theta <= 180) theta = 180;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the west direction of the zombie
                if (levelLoader.collisionZombieW(this)) {
                    goToTarget = false;
                    if (theta < 180 && theta >= 90) theta = 90;
                    if (theta <= 270 && theta >= 180) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the north direction of the zombie
                if (levelLoader.collisionZombieN(this)) {
                    goToTarget = false;
                    if (theta < 270 && theta >= 180) theta = 180;
                    if (theta >= 270 && theta <= 360) theta = 0;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the east direction of the zombie
                if (levelLoader.collisionZombieE(this)) {
                    goToTarget = false;

                    if (theta <= 90 && theta >= 0) theta = 90;
                    if (theta >= 270 && theta <= 360) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                if (!levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieW(this)) {
                    vx = speed * Math.cos(Math.toRadians(theta));
                    vy = speed * Math.sin(Math.toRadians(theta));
                }

            }

            //If the player is inside and the zombie is outside
            if (loader.isPlayerInside() && !loader.isZombieInside(this)) {

                //If the zombie should go to the target or worry about an obstacle in the way
                if (goToTarget) {
                    theta = loader.closestEntrance(this);
                    theta = (Math.toDegrees(theta) + 360) % 360;
                }

                //If the zombie is not colliding with any obstacles, then its allowed to proceed to the their target
                if (!levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieW(this)) {
                    goToTarget = true;
                }

                //If the zombie collides with an obstacle that is in the south direction of the zombie
                if (levelLoader.collisionZombieS(this)) {
                    goToTarget = false;
                    if (theta < 90 && theta > 0) theta = 0;
                    if (theta > 90 && theta < 180) theta = 180;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the west direction of the zombie
                if (levelLoader.collisionZombieW(this)) {
                    goToTarget = false;
                    if (theta < 180 && theta >= 90) theta = 90;
                    if (theta <= 270 && theta >= 180) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the north direction of the zombie
                if (levelLoader.collisionZombieN(this)) {
                    goToTarget = false;
                    if (theta < 270 && theta > 180) theta = 180;
                    if (theta > 270 && theta <= 360) theta = 0;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the east direction of the zombie
                if (levelLoader.collisionZombieE(this)) {
                    goToTarget = false;

                    if (theta <= 90 && theta >= 0) theta = 90;
                    if (theta >= 270 && theta <= 360) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                if (!levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieW(this)) {
                    vx = speed * Math.cos(Math.toRadians(theta));
                    vy = speed * Math.sin(Math.toRadians(theta));
                }
            }

            //If the zombie and the player are both inside the building
            if (loader.isPlayerInside() && loader.isZombieInside(this)) {

                //If the zombie should go to the target or worry about an obstacle in the way
                if (goToTarget && 0 < Math.abs(playerDistance) && Math.abs(playerDistance) < 400) {
                    theta = Math.atan2(player.getY() - y, player.getX() - x);
                    theta = (Math.toDegrees(theta) + 360) % 360;
                }

                //If the zombie is not colliding with any obstacles, then its allowed to proceed to the their target
                if (!levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieW(this)) {
                    goToTarget = true;
                }

                //If the zombie collides with an obstacle that is in the south direction of the zombie
                if (levelLoader.collisionZombieS(this)) {
                    goToTarget = false;
                    if (theta < 90 && theta > 0) theta = 0;
                    if (theta > 90 && theta < 180) theta = 180;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the west direction of the zombie
                if (levelLoader.collisionZombieW(this)) {
                    goToTarget = false;
                    if (theta < 180 && theta >= 90) theta = 90;
                    if (theta <= 270 && theta >= 180) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the north direction of the zombie
                if (levelLoader.collisionZombieN(this)) {
                    goToTarget = false;
                    if (theta < 270 && theta > 180) theta = 180;
                    if (theta > 270 && theta <= 360) theta = 0;
                    vx = 2 * Math.cos(Math.toRadians(theta));
                }

                //If the zombie collides with an obstacle that is in the east direction of the zombie
                if (levelLoader.collisionZombieE(this)) {
                    goToTarget = false;

                    if (theta <= 90 && theta >= 0) theta = 90;
                    if (theta >= 270 && theta <= 360) theta = 270;
                    vy = 2 * Math.sin(Math.toRadians(theta));
                }

                if (!levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieW(this)) {
                    vx = speed * Math.cos(Math.toRadians(theta));
                    vy = speed * Math.sin(Math.toRadians(theta));
                }
            }

            //If the zombie is in distance, there is a random chance of playing a zombie sound effect
            int rand = (int) (Math.random() * 4000);
            if (rand < 7) {
                SoundEffects se = new SoundEffects(2000, "Zombie" + rand);
                game.getSoundEffects().add(se);
                se.playSound();
            }
        } else vx = vy = 0;

        //To Not Collide with each other
        //For Each zombie
        for (int i = 0; i < game.getZombies().size(); i++) {

            //if the zombie is not itself
            if (game.getZombies().get(i) != this) {

                double distanceX = x - game.getZombies().get(i).getX(); //Get the x distance to the other zombie
                double distanceY = y - game.getZombies().get(i).getY(); //Get the y distance to the other zombie
                double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY); //Get the total distance from adding the x and y distances

                //If they are too close to each other, push them away from each other
                if (0 < Math.abs(distance) && Math.abs(distance) < 50 && !levelLoader.collisionZombieN(this) && !levelLoader.collisionZombieS(this) && !levelLoader.collisionZombieE(this) && !levelLoader.collisionZombieW(this)) {

                    double top = Math.min(distance, 1);
                    distanceX *= top / distance;
                    distanceY *= top / distance;
                    vx += distanceX;
                    vy += distanceY;
                }
            }
        }

        //if the zombies collide with the player, damage the player and set the velocity to 0 so they stop.
        if (playerRect.intersects(zombieRect)) {
            vx = vy = 0;
            if (isBoss) player.damageHealth(3);
            else player.damageHealth(1);
        }

        //Each update, add the X and Y velocity to the X and Y coordinates
        x += vx;
        y += vy;

        //If the zombie is dead
        if (health <= 0) {

            //If the zombie that died was a boss zombie, play a special sound effect
            if(isBoss) {
                SoundEffects se1 = new SoundEffects(6000, "Boss");
                game.getSoundEffects().add(se1);
                se1.playSound();
            }

            game.getZombies().remove(this); //Remove the zombie from the zombies object arraylist

            Splat splat = new Splat(x, y); //Create a splat object where the zombie died
            game.getSplats().add(splat);

            //Play the splat sound effect
            SoundEffects se = new SoundEffects(500, "Splat");
            game.getSoundEffects().add(se);
            se.playSound();

            if (isBoss) hud.addScore(100); //If the zombie was a boss zombie, add 100 points to the score
            else hud.addScore(10); //If the zombie was a regular zombie, add 10 points to the score
        }
    }

    //Bullet collisions with the zombie
    public void bulletCollision() {
        //For each bullet
        for (int i = 0; i < game.getBullets().size(); i++) {
            //For each zombie
            for (int j = 0; j < game.getZombies().size(); j++) {
                //If the bullet collides with the zombie
                if (zombieRect.contains(game.getBullets().get(i).getBulletX(), game.getBullets().get(i).getBulletY())) {
                    game.getBullets().remove(i); //remove the bullet object from the bullets array list
                    health -= weapon.getDamage(); //Damage the zombie's health
                    break;
                }
            }
        }
    }

    //The draw methods draws the zombies
    public void draw(Graphics2D g) {
        //Calculate screen offsets
        double offsetX = 500 - player.getX();
        double offsetY = 400 - player.getY();

        //If the zombie was a boss zombie and it was hurt, display a health bar that is scaled to his large health
        if (isBoss) {
            if (health <= maxHealth - 1) {
                g.rotate((Math.toRadians(theta) - 1.57), offsetX + x + 35 / 2.0, offsetY + y + 43 / 2.0);
                if (health > 900) g.setColor(Color.green);
                if (health <= 900) g.setColor(new Color(163, 255, 0, 255));
                if (health <= 700) g.setColor(new Color(255, 255, 0, 255));
                if (health <= 500) g.setColor(Color.ORANGE);
                if (health <= 300) g.setColor(new Color(255, 51, 0, 255));
                if (health <= 100) g.setColor(Color.red);
                g.fillRect((int) offsetX + (int) x + 2, (int) offsetY + (int) y - 4, (int) health / 30, 6);
                g.rotate(-(Math.toRadians(theta) - 1.57), offsetX + x + 35 / 2.0, offsetY + y + 43 / 2.0);
            }
        }

        //If the zombie was a regular zombie and it was hurt, display a health bar scaled to a regular zombie's health
        else {
            if (health <= maxHealth - 1) {
                g.rotate((Math.toRadians(theta) - 1.57), offsetX + x + 35 / 2.0, offsetY + y + 43 / 2.0);
                if (health > 90) g.setColor(Color.green);
                if (health <= 90) g.setColor(new Color(163, 255, 0, 255));
                if (health <= 80) g.setColor(new Color(255, 255, 0, 255));
                if (health <= 60) g.setColor(Color.ORANGE);
                if (health <= 40) g.setColor(new Color(255, 51, 0, 255));
                if (health <= 20) g.setColor(Color.red);
                g.fillRect((int) offsetX + (int) x + 2, (int) offsetY + (int) y - 4, (int) health / 3, 6);
                g.rotate(-(Math.toRadians(theta) - 1.57), offsetX + x + 35 / 2.0, offsetY + y + 43 / 2.0);
            }
        }

        g.rotate(Math.toRadians(theta), offsetX + x + 35 / 2.0, offsetY + y + 43 / 2.0);
        g.drawImage(zombie, (int) offsetX + (int) x, (int) offsetY + (int) y, null);
        g.rotate(-Math.toRadians(theta), offsetX + x + 35 / 2.0, offsetY + y + 43 / 2.0);
    }

    //Getters and Setters
    public double getX() {
        return x;
    }

    public double getTheta() {
        return theta;
    }

    public double getY() {
        return y;
    }
}
