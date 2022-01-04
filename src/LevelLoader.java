/**
 * @(#)LevelLoader.java
 * @author Borhan Saflo
 * This file contains everything that is related to the loading in the level structure. It will read pixel maps about how the level should be structured and where everything is. This includes obstacles, buildings, exits and entrances for the buildings and collision checks for both the player and the zombies.
 */

//Imports
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class LevelLoader {
    private final GamePanel game; //Game Object
    private final Player player; //Player Object
    private BufferedImage pixelMap, back; //The pixel map and the back images
    private final HashMap<Integer, Image> tilePics; //Map that holds each tiles, image
    private final ArrayList<Rectangle> obstacles = new ArrayList<>(); //Array list that holds the obstacles dimensions as a rectangle
    private final ArrayList<Rectangle> insideTiles = new ArrayList<>(); //Array list that holds which tiles count as inside a building
    private final ArrayList<Rectangle> entranceTiles = new ArrayList<>(); //Array list that holds the entrance of buildings tiles.
    private final ArrayList<Rectangle> exitTiles = new ArrayList<>(); //Array list that holds the exit of buildings tiles.

    public LevelLoader(GamePanel game, String name, Player player) {
        tilePics = new HashMap<Integer, Image>();
        this.game = game;
        this.player = player;
        loadHeader(name);
        makeFull();
    }

    public Image loadImage(String name) {
        return new ImageIcon(name).getImage();
    }

    public BufferedImage loadBuffImage(String name) {
        try {
            return ImageIO.read(new File(name));
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    //reads the level settings file
    public void loadHeader(String name) {
        try {
            Scanner inFile = new Scanner(new File(name));
            back = loadBuffImage(inFile.nextLine());     // Needs to be BufferedImages so I can draw Images
            pixelMap = loadBuffImage(inFile.nextLine()); // read pixels
            int numTile = Integer.parseInt(inFile.nextLine());

            for (int i = 0; i < numTile; i++) {            // The 16 is saying it's base 16
                int col = Integer.parseInt(inFile.nextLine(), 16);
                tilePics.put(col, loadImage(inFile.nextLine()));
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void makeFull() {
        Graphics buffG = back.getGraphics();

        int wid = pixelMap.getWidth(); //Get the width of the pixel map
        int height = pixelMap.getHeight(); //Get the height of the pixel map

        // Go to each pixel of the map picture, if the colour is in out
        // HashMap then draw the image to our background.
        for (int x = 0; x < wid; x++) {

            for (int y = 0; y < height; y++) {

                int col = pixelMap.getRGB(x, y); //get color of the current pixel

                col = col & 0xffffff; // This gets rid of the 2 bytes for the alpha

                if (tilePics.containsKey(col)) {

                    Image tile = tilePics.get(col);

                    int tileHeight = 64; //Height of each tile that will be drawn
                    int tileWidth = 64; //Width of each tile that will be drawn

                    int offset = tileHeight - tile.getHeight(null); // so objects are on the ground
                    buffG.drawImage(tile, x * tileWidth, y * tileHeight + offset, null);

                    Rectangle tileSquare = new Rectangle(x * tileWidth, y * tileHeight + offset, 64, 64);

                    //If the color of the pixel represents an obstacle, add it to the obstacles arraylist
                    if (col == 16711935 || col == 6553855 || col == 16737280 || col == 44799 || col == 16776960 || col == 65298 || col == 65280 || col == 65535) {
                        obstacles.add(tileSquare);
                    }
                    //If the pixel color represents a tile that is inside a building, add it to the inside tiles arraylist.
                    if (col == 16711680 || col == 16777215) insideTiles.add(tileSquare);

                    //If the pixel color represents a tile that is an entrance to a building, add it to the entrance tiles arraylist.
                    if (col == 16777215) entranceTiles.add(tileSquare);

                    //If the pixel color represents a tile that is an exit to a building, add it to the exit tiles arraylist.
                    if (col == 7957842) exitTiles.add(tileSquare);
                }
            }
        }
    }

    //this method checks if the player is currently in a building
    public boolean isPlayerInside() {
        for (Rectangle insideTile : insideTiles) {
            if (insideTile.contains(player.getX(), player.getY())) {
                return true;
            }
        }
        return false;
    }

    //This method checks if the zombie is inside a building
    public boolean isZombieInside(Zombie zombie) {
        for (Rectangle insideTile : insideTiles) {
            if (insideTile.contains(zombie.getX(), zombie.getY())) {
                return true;
            }
        }
        return false;
    }

    //This method calculates the location of the closest entrance of a building
    public double closestEntrance(Zombie zombie) {
        double closest = 5000; //A large Placeholder number
        double entranceX = 1600;
        double entranceY = 960;

        for (Rectangle entranceTile : entranceTiles) {

            double entranceDistanceX = entranceTile.getCenterX() - zombie.getX(); //Get the x distance to the entrance
            double entranceDistanceY = entranceTile.getCenterY() - zombie.getY(); //Get the y distance to the entrance
            double entranceDistance = Math.sqrt(entranceDistanceX * entranceDistanceX + entranceDistanceY * entranceDistanceY);  //Add the the x and y vectors to get the actual distance of the entrance

            //If the entrance that was just calculated is less than the current closest, set the closest to the new entrance
            if (Math.abs(entranceDistance) < Math.abs(closest)) {
                closest = entranceDistance;
                entranceX = entranceTile.getCenterX();
                entranceY = entranceTile.getCenterY();
            }
        }
        //Return the direction to that closest entrance
        return Math.atan2(entranceY - zombie.getY(), entranceX - zombie.getX());
    }

    //This method calculates the location of the closest exit of a building
    public double closestExit(Zombie zombie) {
        double closest = 5000; //A large Placeholder number
        double exitX = 0;
        double exitY = 0;

        for (Rectangle exitTile : exitTiles) {

            double entranceDistanceX = exitTile.getCenterX() - zombie.getX(); //Get the x distance to the exit
            double entranceDistanceY = exitTile.getCenterY() - zombie.getY(); //Get the y distance to the exit
            double entranceDistance = Math.sqrt(entranceDistanceX * entranceDistanceX + entranceDistanceY * entranceDistanceY); //Add the the x and y vectors to get the actual distance of the exit

            //If the exit that was just calculated is less than the current closest, set the closest to the new exit
            if (Math.abs(entranceDistance) < Math.abs(closest)) {
                closest = entranceDistance;
                exitX = exitTile.getCenterX();
                exitY = exitTile.getCenterY();
            }
        }
        //Return the direction to that closest exit
        return Math.atan2(exitY - zombie.getY(), exitX - zombie.getX());
    }

    //The following Couple methods checks if the player is colliding with an obstacle is each direction
    public boolean collisionN() {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(player.getX(), player.getY() - 3, 43, 49)) {
                return false;
            }
        }
        return true;
    }

    public boolean collisionS() {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(player.getX(), player.getY() + 3, 43, 49)) {
                return false;
            }
        }
        return true;
    }

    public boolean collisionW() {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(player.getX() - 3, player.getY(), 43, 49)) {
                return false;
            }
        }
        return true;
    }

    public boolean collisionE() {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(player.getX() + 3, player.getY(), 43, 49)) {
                return false;
            }
        }
        return true;
    }


    //The following couple methods checks if the zombie is colliding with an obstacle is each direction
    public boolean collisionZombieN(Zombie zombie) {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(zombie.getX(), zombie.getY() - 2, 43, 49) && zombie.getTheta() > 180 && zombie.getTheta() < 360) {
                return true;
            }
        }
        return false;
    }

    public boolean collisionZombieS(Zombie zombie) {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(zombie.getX(), zombie.getY() + 2, 43, 49) && zombie.getTheta() < 180 && zombie.getTheta() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean collisionZombieW(Zombie zombie) {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(zombie.getX() - 2, zombie.getY(), 43, 49) && zombie.getTheta() > 90 && zombie.getTheta() < 270) {
                return true;
            }
        }
        return false;
    }

    public boolean collisionZombieE(Zombie zombie) {
        for (Rectangle rectangle : obstacles) {
            if (rectangle.intersects(zombie.getX() + 2, zombie.getY(), 43, 49) && ((zombie.getTheta() >= 270 && zombie.getTheta() <= 360) || (zombie.getTheta() < 90 && zombie.getTheta() >= 0))) {
                return true;
            }
        }
        return false;
    }

    //This method checks if a bullet collided with an obstacle
    public void bulletCollision() {
        for (int i = 0; i < game.getBullets().size(); i++) {
            for (Rectangle rectangle : obstacles) {
                if (rectangle.getBounds().contains(game.getBullets().get(i).getBulletX(), game.getBullets().get(i).getBulletY())) {
                    game.getBullets().remove(i);
                    break;
                }
            }
        }
    }

    //Getters abd setters
    public Image getBackground() {
        return back;
    }

}
