//Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Hud implements ActionListener {

    private final GamePanel game; //Game Object

    //All the font sizes that will be used
    private Font hudFont;
    private Font pausedFont;
    private Font reloadingFont;
    private Font scoreFont;
    private Font textFont;

    //Player bubble text all the beginning of each level
    Timer textTimer; //Timer that will display the text for a couple of seconds
    private boolean playerText = false; //The boolean variable that determines if it should be displayed of not.

    //Loading images, includes some buttons and ammo icon in the HUD.
    private final Image ammoIcon = new ImageIcon("Assets/icons/ammo.png").getImage();
    private final Image ammoIcon2 = new ImageIcon("Assets/icons/ammo2.png").getImage();
    private final Image pauseBtn = new ImageIcon("Assets/buttons/pause.png").getImage();
    private final Image menuBtn = new ImageIcon("Assets/buttons/menu.png").getImage();
    private final Image nextBtn = new ImageIcon("Assets/buttons/next.png").getImage();
    private final Image readyBtn = new ImageIcon("Assets/buttons/ready.png").getImage();

    //Coordinates of the buttons
    private final int pauseX = 930; //The x cord of the reset button.
    private final int pauseY = 10; //The y cord of the reset button.
    private final int menuX = 350; //Variable for the x cord of the menu.
    private final int menuY = 450; //Variable for the y cord of the menu.
    private final int nextX = 350; //The x cord of the next button
    private final int nextY = 600; //The y cord for the next button

    private int score = 0; //This variable will hold the player's score.

    public Hud(GamePanel game) {
        this.game = game;

        //Setting each font to the custom font that will be used in the game and the font's size
        String fontFile = "Assets/impact.ttf";
        try {
            reloadingFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile)).deriveFont(50f);
            hudFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile)).deriveFont(30f);
            pausedFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile)).deriveFont(100f);
            scoreFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile)).deriveFont(40f);
            textFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile)).deriveFont(15f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontFile)));

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    //This method will be called from other classes to display the player text bubble, it will start the timer.
    public void playerText() {
        textTimer = new Timer(7000,this);
        playerText=true;
        textTimer.start();
    }

    //Method that will be triggered by the text timer to display the player text bubble at the beginning of each level.
    @Override
    public void actionPerformed(ActionEvent e) {
        playerText=false;
        textTimer.stop();
    }

    public void draw(Graphics2D g, GamePanel gamePanel, Player player, ItemSettings weapon) {
        FontMetrics fm1 = g.getFontMetrics(pausedFont);
        FontMetrics fm2 = g.getFontMetrics(reloadingFont);
        FontMetrics fm3 = g.getFontMetrics(scoreFont);

        //The screen that will appear before starting level 1 which contains a background story of why the player is there.
        if (game.getState() == GamePanel.STATE.PRELEVEL1) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 1000, 800);
            g.setColor(Color.red);
            g.setFont(pausedFont);
            String helpText = "HELP NEEDED";
            int width1 = fm1.stringWidth(helpText);
            g.drawString(helpText, 500 - width1 / 2, 800 / 3);

            g.setColor(Color.white);
            g.setFont(hudFont);
            g.drawString("I heard you're one of the toughest warriors around? well its ",130,400);
            g.drawString("time to put that to the test, there is an evil king called Zombie",130,450);
            g.drawString("King. His evil plan is ZOMBIFY and takeover our town!",130,500);
            g.setColor(Color.red);
            g.drawString(" We really need your help!",350,550);

            g.drawImage(readyBtn,nextX,nextY,null);
        }

        //The main HUD and screens that will be used throughout the game.
        if (game.getState() == GamePanel.STATE.LEVEL1 || game.getState() == GamePanel.STATE.LEVEL2 || game.getState() == GamePanel.STATE.LEVEL3) {

            Color color = new Color(0, 0, 0, 150); //A semi transparent black color
            g.setColor(color);

            //If the game is paused, display the semi transparent black screen with the paused text.
            if (gamePanel.getPause()) {
                g.fillRect(0, 0, 1000, 800);
                g.setFont(pausedFont);
                g.setColor(Color.red);
                g.drawString("Paused", 350, 200);
                g.drawImage(pauseBtn, pauseX, pauseY, null);
            }

            //If the game is not paused, draw the hud elements
            else {

                g.drawImage(pauseBtn, pauseX, pauseY, null); //Draw the pause button which is located at the top right of the screen

                //If the player is reloading, display the reloading text on the HUD
                if (player.getReloading()) {
                    g.setFont(reloadingFont);
                    g.setColor(Color.red);
                    g.drawString("Reloading...", 400, 100);
                }

                //If the playerText boolean variable is true which indicates if the bubble text of the player should be displayed which will be true in the beginning of each level.
                if (playerText) {

                    //The bubble text for the beginning of level 1
                    if(game.getState() == GamePanel.STATE.LEVEL1) {
                        g.setColor(new Color(255, 245, 140));
                        int[] pointsX = {530, 540, 520};
                        int[] pointsY = {410, 460, 460};
                        g.rotate(90, 510, 400);
                        g.fillPolygon(pointsX, pointsY, 3);
                        g.rotate(-90, 510, 400);
                        g.fillRect(250, 350, 200, 100);
                        g.setColor(Color.black);
                        g.setFont(textFont);
                        g.drawString("Woah?! what is this place?", 260, 375);
                        g.drawString("What zombies are they", 260, 395);
                        g.drawString("talking abou- Oh no...", 260, 415);
                        g.drawString("WHATS THAT THING! eww", 260, 435);
                    }

                    //The bubble text for the beginning of level 2
                    if(game.getState() == GamePanel.STATE.LEVEL2) {
                        g.setColor(new Color(255, 245, 140));
                        int[] pointsX = {530, 540, 520};
                        int[] pointsY = {410, 460, 460};
                        g.rotate(90, 510, 400);
                        g.fillPolygon(pointsX, pointsY, 3);
                        g.rotate(-90, 510, 400);
                        g.fillRect(250, 350, 200, 100);
                        g.setColor(Color.black);
                        g.setFont(textFont);
                        g.drawString("PHEWW, that was close", 260, 375);
                        g.drawString("Oh no... i hear a lot of them!", 260, 395);
                        g.drawString("I probably need a better", 260, 415);
                        g.drawString("weapon. Here goes nothing...", 260, 435);
                    }

                    //The bubble text for the beginning of level 3
                    if(game.getState() == GamePanel.STATE.LEVEL3) {
                        g.setColor(new Color(255, 245, 140));
                        int[] pointsX = {530, 540, 520};
                        int[] pointsY = {410, 460, 460};
                        g.rotate(90, 510, 400);
                        g.fillPolygon(pointsX, pointsY, 3);
                        g.rotate(-90, 510, 400);
                        g.fillRect(250, 350, 200, 100);
                        g.setColor(Color.black);
                        g.setFont(textFont);
                        g.drawString("Yikes.. That sounded Scary...", 260, 375);
                        g.drawString("I- I mean I CAN DO THIS!", 260, 395);
                        g.drawString("lets find this evil king and", 260, 415);
                        g.drawString("show him who is the boss", 260, 435);
                    }
                }

                //HUD Elements in-game

                //Score text which is displayed at the top left of the screen
                g.setColor(Color.white);
                g.setFont(scoreFont);
                g.drawString(String.valueOf(score), 20, 80);

                //The current level and zombie counter texts
                g.setColor(Color.red);
                g.setFont(hudFont);
                g.drawString("Level " + game.getLevel(), 20, 40);
                g.drawString("Zombies: " + gamePanel.getZombies().size(), 20, 690);

                //The shield bar, which is displayed at the bottom left of the screen
                g.setColor(color);
                g.fillRect(15, 699, 150, 20);
                g.setColor(Color.blue);
                g.fillRect(15, 699, (int) player.getShield() * 15 / 10, 20);
                g.setColor(Color.white);
                g.setStroke(new java.awt.BasicStroke(2));
                g.drawRect(15, 699, 150, 20);

                //The heal bar, which is displayed at the bottom left screen
                g.setColor(color);
                g.fillRect(15, 725, 150, 20);

                //This part determines what should the color of the bar be based on the amount of health that the player has. Ranges from green to red.
                if (player.getHealth() > 90) g.setColor(Color.green);
                if (player.getHealth() <= 90) g.setColor(new Color(163, 255, 0, 255));
                if (player.getHealth() <= 80) g.setColor(new Color(255, 255, 0, 255));
                if (player.getHealth() <= 60) g.setColor(Color.ORANGE);
                if (player.getHealth() <= 40) g.setColor(new Color(255, 51, 0, 255));
                if (player.getHealth() <= 20) g.setColor(Color.red);

                if (player.getHealth() > 0) g.fillRect(15, 725, (int) player.getHealth() * 15 / 10, 20);
                g.setColor(Color.white);
                g.drawRect(15, 725, 150, 20);

                //The weapon stats box which is located at the bottom right of the screen
                g.setColor(color);
                g.fillRect(820, 650, 150, 100);
                g.setColor(Color.white);
                g.setStroke(new java.awt.BasicStroke(2));
                g.drawRect(820, 650, 150, 100);
                if (weapon.getCurrentAmmo() == 0) {
                    g.setColor(Color.red);
                    g.drawImage(ammoIcon2, 835, 708, null);
                } else {
                    g.setColor(Color.white);
                    g.drawImage(ammoIcon, 835, 708, null);
                }
                g.setFont(hudFont);
                g.drawString(weapon.getCurrentAmmo() + "/" + weapon.getTotalAmmo(), 870, 735); //Displays the current ammo the player has and how much they have in total
                g.drawImage(weapon.getItemIcon(weapon.getCurrentWeapon()), 825, 655, 130, 50, null); //An image of the weapon they are currently using
            }
        }

        //The screen that will be displayed when the player dies and loses the game.
        if(game.getState() == GamePanel.STATE.OVER) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 1000, 800);
            g.setColor(Color.red);
            g.setFont(pausedFont);
            String overText = "Gamer Over";
            int width1 = fm1.stringWidth(overText);
            g.drawString(overText, 500 - width1 / 2, 800 / 3);

            g.setColor(Color.white);
            g.setFont(reloadingFont);
            String scoreText = "Score: " + score;
            int width2 = fm2.stringWidth(scoreText);
            g.drawString(scoreText, 500 - width2 / 2, 350);

            g.drawImage(menuBtn,menuX,menuY,null);
        }

        //The screen that will be displayed when the player completes level 1
        if(game.getState() == GamePanel.STATE.LEVEL1OVER) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 1000, 800);
            g.setColor(Color.green);
            g.setFont(pausedFont);
            String passedText = "Level 1 PASSED";
            int width1 = fm1.stringWidth(passedText);
            g.drawString(passedText, 500 - width1 / 2, 800 / 3);

            g.setColor(Color.white);
            g.setFont(hudFont);
            g.drawString("Good job! That was amazing but hurry they are taking over",150,400);
            g.drawString(" the other side of the town!",350,450);

            g.drawImage(nextBtn,nextX,nextY,null);
        }

        //The screen that will be displayed when the player completes level 2
        if(game.getState() == GamePanel.STATE.LEVEL2OVER) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 1000, 800);
            g.setColor(Color.green);
            g.setFont(pausedFont);
            String passedText = "Level 2 PASSED";
            int width1 = fm1.stringWidth(passedText);
            g.drawString(passedText, 500 - width1 / 2, 800 / 3);

            g.setColor(Color.white);
            g.setFont(hudFont);
            g.drawString("WOAH! that was terrific!   you killed it there! But its not time to",120,400);
            g.drawString("celebrate yet... I got some bad news, zombie king and his army",120,450);
            g.drawString("are heading towards the town hall. we really need your help!!",120,500);

            g.drawImage(nextBtn,nextX,nextY,null);
        }

        //The screen that will be displayed when the player completes level 3 which the last level which means they have completed the game
        if(game.getState() == GamePanel.STATE.LEVEL3OVER) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 1000, 800);
            g.setColor(Color.green);
            g.setFont(pausedFont);
            String victoryText = "VICTORY";
            int width1 = fm1.stringWidth(victoryText);
            g.drawString(victoryText, 500 - width1 / 2, 800 / 3);

            g.setColor(Color.white);
            g.setFont(hudFont);
            g.drawString("What did i just witness?  That was... AMAZING!  you're truly the",100,350);
            g.drawString("definition of a true warrior. thanks to you, we don't have to deal",100,400);
            g.drawString("with Zombie king ever again! Your name will go in our history books!",100,450);
            g.setColor(Color.yellow);
            g.setFont(scoreFont);
            String scoreText = "Final Score: " + score;
            int width2 = fm3.stringWidth(scoreText);
            g.drawString(scoreText, 500 - width2 / 2, 550);

            g.drawImage(menuBtn,nextX,nextY,null);
        }

    }

    //Getters and Setters
    public void resetScore() {
        score=0;
    }

    public int getNextX() {
        return nextX;
    }

    public int getNextY() {
        return nextY;
    }

    public int getMenuX() {
        return menuX;
    }

    public int getMenuY() {
        return menuY;
    }

    public int getPauseX() {
        return pauseX;
    }

    public int getPauseY() {
        return pauseY;
    }

    public void addScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

}
