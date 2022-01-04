//imports
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class HighScore {
    private Font scoreFont;
    private final int backX = 50; //The x cord of the back button.
    private final int backY = 650; //The y cord of the back button.
    private final int resetX = 800; //The x cord of the reset button.
    private final int resetY = 700; //The y cord of the reset button.
    private final Image backBtn = new ImageIcon("Assets/buttons/back.png").getImage(); //load the back button image.
    private final Image resetBtn = new ImageIcon("Assets/buttons/reset.png").getImage(); //load the reset button image.

    private int highScore;

    public HighScore() {
        //Loading in custom font that will be used in the high score menu
        try {
            scoreFont = Font.createFont(Font.TRUETYPE_FONT, new File("Assets/impact.ttf")).deriveFont(50f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Assets/impact.ttf")));

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        load(); //load the high score from the score.txt file when the object is created
    }

    //The load method loads the scores and names from the txt file and puts them in array lists.
    public void load() {
        try {
            Scanner inFile = new Scanner(new BufferedReader(new FileReader(new File("Assets/scores.txt"))));
            highScore = Integer.parseInt(inFile.nextLine());
        } catch (FileNotFoundException ex) {
            PrintWriter outFile = null;
            try {
                //If it couldn't find a score.txt file, then create one with a score of 0
                outFile = new PrintWriter(new BufferedWriter(new FileWriter("Assets/scores.txt")));
                outFile.println(0);
                outFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //The save method is given an array list of names and scores and saves them to the scores txt file.
    public void save(int score) {
        try {
            //Save the passed parameter which represents the the new high score, in the score.txt file
            PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter("Assets/scores.txt")));
                outFile.println(score);
            outFile.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    //the High score method will check if the score given is higher than the current high score, if it is, the high score will get replaced.
    public void highScore(int score) {
        load();
        if (score > highScore) {
            highScore=score;
            save(score);
        }
    }

    //Draw a black screen with the score in the middle
    public void draw(Graphics2D g) {
        FontMetrics fm1 = g.getFontMetrics(scoreFont);
        g.setColor(Color.black);
        g.fillRect(0, 0, 1000, 800);

        g.setColor(Color.white);
        g.setFont(scoreFont);
        String text = "Your High Score: " + highScore;
        int width = fm1.stringWidth(text);
        g.drawString(text, 500 - width / 2, 800 / 3);

        g.drawImage(backBtn, backX, backY, null);
        g.drawImage(resetBtn, resetX, resetY, null);
    }

    //Getters and setters
    public int getBackX() {
        return backX;
    }

    public int getBackY() {
        return backY;
    }

    public int getResetX() {
        return resetX;
    }

    public int getResetY() {
        return resetY;
    }
}
