/**
 * @(#)Splat.java
 * @author Borhan Saflo
 * This file contains simple methods that are responsible for drawing a random colored splat after the death of a zombie.
 */

//imports
import javax.swing.*;
import java.awt.*;

public class Splat {
    //The x and y cord where the splat will be
    private final int x;
    private final int y;
    //Select a random number and select the image that corresponds to that number.
    private final int splatNumber = (int) (Math.random() * 4);
    private final Image splatImage = new ImageIcon("Assets/splats/splat" + splatNumber + ".png").getImage();

    public Splat(double x, double y) {
        //The x  and y cord will be passed when the zombie dies
        this.x = (int) x;
        this.y = (int) y;
    }

    //Draw the splat on screen
    public void draw(Graphics2D g, Player player) {
        double offsetX = 500 - player.getX();
        double offsetY = 400 - player.getY();
        g.drawImage(splatImage, x + (int) offsetX, y + (int) offsetY, null);
    }
}
