/**
 * @(#)Main.java
 * @author Borhan Saflo
 * This is the main file of the zombified game, it includes some essdential methods for running and updating the game.
 */

//Imports
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.ArrayList;

public class Main extends JFrame {

    public Main() {
        super("Zombified"); //Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        try {
            setIconImage(ImageIO.read(new File("Assets/Logo.png"))); //Logo of the window
        } catch (IOException e) {
            e.printStackTrace();
        }

        GamePanel game = new GamePanel();
        add(game);
        setResizable(false); //Make the window not resizeable
        setVisible(true);
    }

    public static void main(String[] arguments) {
        Main frame = new Main();
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    private final boolean[] keys; //A Boolean list that holds if each key is pressed or not.
    private double mx; //Holds the X Coordinate of the mouse
    private double my; //Holds the Y Coordinate of the mouse
    private boolean click = false; //Boolean variable that holds if the mouse is pressed down.
    private boolean paused = false; //Boolean variable that holds if the game is paused or not.
    private final Image background = new ImageIcon("Assets/back.png").getImage(); //Holds the back button image that is located in the high score menu
    private final Image playBtn = new ImageIcon("Assets/Buttons/play.png").getImage(); //Holds the play button image
    private final Image highBtn = new ImageIcon("Assets/Buttons/high_score.png").getImage(); //Holds the high score menu button

    private final int playX = 75, playY = 400; //The X and Y coordinates of the play button.
    private final int highX = 600, highY = 400; //The X and Y coordinates of the high score menu button.

    public enum STATE {MENU, PRELEVEL1, LEVEL1, LEVEL1OVER, LEVEL2, LEVEL2OVER, LEVEL3, LEVEL3OVER, OVER, SCORE} //Holds the possible states of the game

    private static STATE State = STATE.MENU; //Holds the current state of the game.
    private int level = 1; //Holds the current level

    //Creating all the Objects
    private final Hud hud = new Hud(this);
    private final ItemSettings itemSettings = new ItemSettings();
    private final Player player = new Player(this, itemSettings);
    private LevelLoader levelLoader = new LevelLoader(this, "maps/level1.txt", player);
    private ObjectLoader zombieLevel = new ObjectLoader(this, "maps/level1_Zombies.png", player, itemSettings, hud, levelLoader);
    private final HighScore highScore = new HighScore();

    //Objects Arrays
    private final static ArrayList<Bullet> bullets = new ArrayList<>();
    private final static ArrayList<Zombie> zombies = new ArrayList<>();
    private final static ArrayList<Item> ITEMS = new ArrayList<>();
    private final static ArrayList<Splat> splats = new ArrayList<>();
    private final ArrayList<SoundEffects> soundEffects = new ArrayList<>();

    public GamePanel() {
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        keys = new boolean[KeyEvent.KEY_LAST + 1];
        Timer myTimer = new Timer(10, this); //Trigger every 100 ms
        myTimer.start();
        cursorChange("Normal"); //Change cursor to normal when the game starts
    }

    //New Game Method that resets values so the player can play a new game
    public void newGame() {
        level=1;
        itemSettings.changeWeapon("Pistol");
        player.reset();
        player.loadSkin();
        bullets.clear();
        zombies.clear();
        ITEMS.clear();
        splats.clear();
        levelLoader = new LevelLoader(this, "maps/level1.txt", player);
        zombieLevel = new ObjectLoader(this, "maps/level1_Zombies.png", player, itemSettings, hud, levelLoader);
    }

    //Next Level Method that resets some values that are needed to be rested for the next level.
    public void nextLevel() {
        player.reset();
        itemSettings.resetAmmo();
        bullets.clear();
        zombies.clear();
        ITEMS.clear();
        splats.clear();
        levelLoader = new LevelLoader(this, "maps/level" + level + ".txt", player);
        zombieLevel = new ObjectLoader(this, "maps/level" + level + "_Zombies.png", player, itemSettings, hud, levelLoader);
        if (State == STATE.LEVEL1OVER) {
            player.setCoordinate(3000, 1800);
            State = STATE.LEVEL2;
        }
        if (State == STATE.LEVEL2OVER) {
            player.setCoordinate(3000, 900);
            State = STATE.LEVEL3;
        }
        hud.playerText();
        cursorChange("CrossHair");
    }

    //Game Over Method is called when player dies.
    public void gameOver() {
        State = STATE.OVER;
        highScore.highScore(hud.getScore());
        newGame();
        cursorChange("Normal");
    }

    //The cursor change method changes the cursor between the normal cursor and the cross hair cursor that is used in game
    public void cursorChange(String type) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage("Assets/icons/Crosshair.png"); //The custom crosshair cursor image
        Point middle = new Point(16, 16); //Center the cursor's hot point to the middle of the crosshair
        Cursor c = toolkit.createCustomCursor(image, middle, "img");

        if (type.equals("CrossHair")) this.setCursor(c); //set the cursor to the custom crosshair cursor
        if (type.equals("Normal")) setCursor(Cursor.getDefaultCursor()); //set the cursor to the default cursor
    }

    //The main game loop that will be update the game.
    public void actionPerformed(ActionEvent evt) {
        repaint(); //Every update, run the paint method

        //If the player is currently in one of the three levels
        if (State == STATE.LEVEL1 || State == STATE.LEVEL2 || State == STATE.LEVEL3) {

            //If the game is not paused, update the game
            if (!paused) {

                player.update(mx, my, levelLoader); //Update the player

                levelLoader.bulletCollision(); //Check the bullet collision with the walls.

                //Update the each object that is in an array list
                for (int i = 0; i < bullets.size(); i++) bullets.get(i).update();
                for (int i = 0; i < zombies.size(); i++) zombies.get(i).update(levelLoader);
                for (int i = 0; i < ITEMS.size(); i++) ITEMS.get(i).update();

                soundEffects.removeIf(se -> !se.isAudioRunning()); //Remove the sound effect from the sound effects array if the audio is done playing.

                //End the level if the there is no more zombies left
                if (zombies.size() == 0) {
                    if (level == 1) {
                        State = STATE.LEVEL1OVER;
                        cursorChange("Normal");
                    }
                    if (level == 2) {
                        State = STATE.LEVEL2OVER;
                        cursorChange("Normal");
                    }
                    if (level == 3) {
                        highScore.highScore(hud.getScore());
                        State = STATE.LEVEL3OVER;
                        cursorChange("Normal");
                    }
                }
            }
        }
    }

    //The main paint method that updates everything that is displayed on the screen
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //on menu
        if (State == STATE.MENU) {
            g.drawImage(background, 0, 0, null);
            g.drawImage(playBtn, playX, playY, null);
            g.drawImage(highBtn, highX, highY, null);
        }

        //in game
        if (State == STATE.LEVEL1 || State == STATE.LEVEL2 || State == STATE.LEVEL3) {
            double offsetX = 500 - player.getX();
            double offsetY = 400 - player.getY();
            g2.setColor(new Color(50, 50, 50));
            g2.fillRect(0, 0, 3000, 3000);
            g2.drawImage(levelLoader.getBackground(), (int) offsetX, (int) offsetY, null);
            for (Splat s : splats) s.draw(g2, player);
            for (Item item : ITEMS) item.draw(g2);
            for (Zombie zombie : zombies) zombie.draw(g2);
            hud.draw(g2, this, player, itemSettings);
            player.draw(this, g2);
            for (Bullet b : bullets) b.draw(g2);
        }

        //on the score menu
        if (State == STATE.SCORE) {
            highScore.draw(g2);
        }

        //after each level
        if (State == STATE.PRELEVEL1 || State == STATE.OVER || State == STATE.LEVEL1OVER || State == STATE.LEVEL2OVER || State == STATE.LEVEL3OVER) {
            hud.draw(g2, this, player, itemSettings);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE && itemSettings.getCurrentAmmo() == 0) {
            SoundEffects se = new SoundEffects(500, "Empty");
            getSoundEffects().add(se);
            se.playSound();
        }
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
        }
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mx = e.getX(); //Get the X position the mouse.
        my = e.getY();//Get the Y position the mouse.
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mx = e.getX(); //Get the X position the mouse.
        my = e.getY();//Get the Y position the mouse.

        if (mx > hud.getPauseX() && mx < hud.getPauseX() + 70 && my > hud.getPauseY() && my < hud.getPauseY() + 50)
            cursorChange("Normal");
        else {
            if (State == STATE.LEVEL1 && !paused) cursorChange("CrossHair");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (State == STATE.MENU) {
            //If the player presses the play button on the main menu
            if (mx > playX && mx < playX + 300 && my > playY && my < playY + 100) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                State = STATE.PRELEVEL1;

            }
            //If the player presses the high score button on the main menu which takes the player to the high score menu
            if (mx > highX && mx < highX + 300 && my > highY && my < highY + 100) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                State = STATE.SCORE; //Change the state to score which is the high score menu.
            }
        }
        if (State == STATE.PRELEVEL1) {
            if (mx > hud.getNextX() && mx < hud.getNextX() + 300 && my > hud.getNextY() && my < hud.getNextY() + 100) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                hud.resetScore();
                State = STATE.LEVEL1;
                hud.playerText();
                cursorChange("CrossHair");
            }
        }
        if (State == STATE.SCORE) {
            //If the player presses the back button in the high scores menu.
            if (mx > highScore.getBackX() && mx < highScore.getBackX() + 300 && my > highScore.getBackY() && my < highScore.getBackY() + 100) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                State = STATE.MENU;
            }

            //If the player presses the reset button in the high score menu, which resets the high score.
            if (mx > highScore.getResetX() && mx < highScore.getResetX() + 300 && my > highScore.getResetY() && my < highScore.getResetY() + 100) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                highScore.save(0);
                highScore.load();
            }
        }

        if (State == STATE.OVER) {
            if (mx > hud.getMenuX() && mx < hud.getMenuX() + 300 && my > hud.getMenuY() && my < hud.getMenuY() + 100) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                State = STATE.MENU;
            }
        }

        if (State == STATE.LEVEL1OVER || State == STATE.LEVEL2OVER) {
            if (mx > hud.getNextX() && mx < hud.getNextX() + 300 && my > hud.getNextY() && my < hud.getNextY() + 100) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                level += 1;
                nextLevel();
            }
        }

        if (State == STATE.LEVEL3OVER) {
            if (mx > hud.getNextX() && mx < hud.getNextX() + 300 && my > hud.getNextY() && my < hud.getNextY() + 100) {
                State = STATE.MENU;
                newGame();
                cursorChange("Normal");
            }
        }
    }

    //When the mouse is pressed
    @Override
    public void mousePressed(MouseEvent e) {
        if (State == STATE.LEVEL1 || State == STATE.LEVEL2 || State == STATE.LEVEL3) {
            if (mx > hud.getPauseX() && mx < hud.getPauseX() + 50 && my > hud.getPauseY() && my < hud.getPauseY() + 50) {
                SoundEffects se = new SoundEffects(500, "Button");
                getSoundEffects().add(se);
                se.playSound();
                paused = !paused;
            }
            if (SwingUtilities.isLeftMouseButton(e) && !paused) {
                click = true;
                if (itemSettings.getCurrentAmmo() == 0) {
                    SoundEffects se = new SoundEffects(500, "Empty");
                    getSoundEffects().add(se);
                    se.playSound();
                }
            }
            if (SwingUtilities.isRightMouseButton(e)) player.reload();
        }
    }

    //When the mouse is released from a click
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) click = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    //Getters and Setters
    public boolean getPause() {
        return paused;
    }

    public boolean getClick() {
        return click;
    }

    public boolean[] getKeys() {
        return keys;
    }

    public ArrayList<SoundEffects> getSoundEffects() {
        return soundEffects;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public ArrayList<Zombie> getZombies() {
        return zombies;
    }

    public ArrayList<Item> getItems() {
        return ITEMS;
    }

    public ArrayList<Splat> getSplats() {
        return splats;
    }

    public STATE getState() {
        return State;
    }

    public int getLevel() {
        return level;
    }
}

