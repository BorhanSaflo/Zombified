/**
 * @(#)ObjectLoader.java
 * @author Borhan Saflo
 * This file contains methods that load in the items and objects from reading pixel maps for each level.
 */

//imports
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ObjectLoader {

    //Objects variables
    private final GamePanel game;
    private final Player player;
    private final LevelLoader loader;
    private final ItemSettings itemSettings;
    private final Hud hud;

    private BufferedImage pixelMap; // read pixels; //Load the pixel map of the objects

    public ObjectLoader(GamePanel game, String name, Player player, ItemSettings itemSettings, Hud hud, LevelLoader loader) {
        this.player = player;
        this.itemSettings = itemSettings;
        this.hud = hud;
        this.game = game;
        this.loader = loader;
        loadPixelMap(name);
        spawnObjects();
    }

    //Load the pixel map that shows where every item/object is
    public void loadPixelMap(String name) {
        try {
            pixelMap = ImageIO.read(new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //This method will read each pixel in the pixel map that was made, depending on the color of the pixel it will spawn the correct item/object
    public void spawnObjects() {
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 30; y++) {
                int col = pixelMap.getRGB(x, y);
                col = col & 0xffffff; // This gets rid of the 2 bytes for the alpha
                if (col == 16711680) {
                    int random = (int) (Math.random() * 100);
                    if (random > 50) {
                        Zombie zombie = new Zombie(game, player, itemSettings, hud, x * 64, y * 64, loader, false);
                        game.getZombies().add(zombie);
                    }
                }

                if (col == 4202496) {
                    Zombie zombie = new Zombie(game, player, itemSettings, hud, x * 64, y * 64, loader, true);
                    game.getZombies().add(zombie);
                }

                if (col == 65280) {
                    Item SMG = new Item(game, player, itemSettings, itemSettings.getItemIcon("SMG"), x * 64, y * 64, 128, 48, "Weapon", "SMG");
                    game.getItems().add(SMG);
                }

                if (col == 255) {
                    Item Rifle = new Item(game, player, itemSettings, itemSettings.getItemIcon("Rifle"), x * 64, y * 64, 100, 50, "Weapon", "Rifle");
                    game.getItems().add(Rifle);
                }

                if (col == 16748544) {
                    Item Sniper = new Item(game, player, itemSettings, itemSettings.getItemIcon("Sniper"), x * 64, y * 64, 75, 30, "Weapon", "Sniper");
                    game.getItems().add(Sniper);
                }

                if (col == 7864575) {
                    Item Shotgun = new Item(game, player, itemSettings, itemSettings.getItemIcon("Shotgun"), x * 64, y * 64, 75, 30, "Weapon", "Shotgun");
                    game.getItems().add(Shotgun);
                }

                if (col == 16776960) {
                    Item Ammo = new Item(game, player, itemSettings, itemSettings.getItemIcon("Ammo"), x * 64, y * 64, 32, 29, "Ammo", "Na");
                    game.getItems().add(Ammo);
                }

                if (col == 16711935) {
                    Item Health = new Item(game, player, itemSettings, itemSettings.getItemIcon("Health"), x * 64, y * 64, 32, 29, "Health", "Na");
                    game.getItems().add(Health);
                }

                if (col == 65535) {
                    Item Shield = new Item(game, player, itemSettings, itemSettings.getItemIcon("Shield"), x * 64, y * 64, 32, 29, "Shield", "Na");
                    game.getItems().add(Shield);
                }
            }
        }
    }
}
