/**
 * @(#)Item.java
 * @author Borhan Saflo
 * This file contains everything that is related to spawnable items. This includes what type the item is and what it should do depending on the type.
 */

//imports
import java.awt.*;

public class Item {

    //Loading in the needed objects
    private final GamePanel game;
    private final Player player;
    private final ItemSettings weaponSettings;

    private final double x; //X coordinate of the item/weapon which doesn't change
    private double y; //Y coordinate of the item/weapon which does change when the item goes up and down.
    private final int width; //The width of the item/weapon
    private final int height; //The height of the item/weapon
    private final Image icon; //The image of the item/weapon
    private final Rectangle itemRect; //The hit box of the item/weapon so when it interacts with the player, the player gets the item
    private final String weaponName; //The name of item/weapon
    private final String type; //The type of the item. ie. Weapon
    private int animationRange = -50; //how high and low the weapon animation goes
    private double animationMultiplier = 1;

    public Item(GamePanel game, Player player, ItemSettings weaponSettings, Image icon, double x, double y, int width, int height, String type, String weaponName) {
        this.player = player;
        this.game = game;
        this.weaponSettings = weaponSettings;
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.weaponName = weaponName;
        this.type = type;
        this.itemRect = new Rectangle((int) x, (int) y, width, height); //hit box of the item so when the player collides with the item's hit box, the item is picked up.
    }

    //The update method checks collision with the player and the item and it updates the item hovering animation
    public void update() {

        //If the type of the item is a weapon
        if (type.equals("Weapon")) {
            if (itemRect.intersects(player.getX(), player.getY(), 43, 49) && !player.getReloading()) {
                game.getItems().remove(this);
                weaponSettings.changeWeapon(weaponName);
                SoundEffects se = new SoundEffects(1000, weaponSettings.getCurrentWeapon() + "Equip");
                game.getSoundEffects().add(se);
                se.playSound();
                player.loadSkin();
            }
        }

        //If the item type is health
        if (type.equals("Health")) {
            if (itemRect.intersects(player.getX(), player.getY(), 43, 49)) {
                if (player.getHealth() < 100) {
                    game.getItems().remove(this);
                    player.regenerateHealth();
                    SoundEffects se = new SoundEffects(1400, "Health");
                    game.getSoundEffects().add(se);
                    se.playSound();
                }
            }
        }

        //If the item type is shield
        if (type.equals("Shield")) {
            if (itemRect.intersects(player.getX(), player.getY(), 43, 49)) {
                if (player.getShield() < 100) {
                    game.getItems().remove(this);
                    player.regenerateShield();
                    SoundEffects se = new SoundEffects(1000, "Shield");
                    game.getSoundEffects().add(se);
                    se.playSound();
                }
            }
        }

        //If the item type is ammo
        if (type.equals("Ammo")) {
            if (itemRect.intersects(player.getX(), player.getY(), 43, 49)) {
                if (weaponSettings.getTotalAmmo() != weaponSettings.getMaxAmmo() && !player.getReloading()) {
                    game.getItems().remove(this);
                    weaponSettings.ammoRefill();
                    SoundEffects se = new SoundEffects(1800, "Ammo");
                    game.getSoundEffects().add(se);
                    se.playSound();
                }
            }
        }

        //The hovering animation, if the multiplier is negative, it goes down, if the multiplier is positive, the item goes up
        if (animationMultiplier == -1) y -= animation();
        if (animationMultiplier == 1) y += animation();
    }

    //This method Calculates the y coordinate of the hovering animation, it uses a Sin function.
    public double animation() {
        if (animationRange >= 50) animationMultiplier = -1;
        if (animationRange <= -50) animationMultiplier = 1;
        animationRange += animationMultiplier;
        return -(Math.cos(Math.PI * animationRange) - 1) / 3;
    }

    //draws the item/weapon each update, the item is drawn with its new y coordinate
    public void draw(Graphics2D g) {
        double offsetX = 500 - player.getX();
        double offsetY = 400 - player.getY();
        g.drawImage(icon, (int) offsetX + (int) x, (int) offsetY + (int) y, width, height, null);
    }
}
