/**
 * @(#)SoundEffect.java
 * @author Borhan Saflo
 * This file contains everything that is related to sound effects in the game. It will take a request for a sound effect to be played, choose the right file for that sounds, create a new thread, play the sound effect, and kill the thread after the sound effect has been played.
 */

//imports
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class SoundEffects implements Runnable {

    //All the sound file variables that are used in the game
    private final File smgShoot = new File("Assets/SoundFX/smg.wav");
    private final File rifleShoot = new File("Assets/SoundFX/rifle.wav");
    private final File sniperShoot = new File("Assets/SoundFX/sniper.wav");
    private final File shotgunShoot = new File("Assets/SoundFX/shotgun.wav");
    private final File pistolShoot = new File("Assets/SoundFX/pistol.wav");

    private final File smgReload = new File("Assets/SoundFX/smgReload.wav");
    private final File rifleReload = new File("Assets/SoundFX/rifleReload.wav");
    private final File sniperReload = new File("Assets/SoundFX/sniperReload.wav");
    private final File shotgunReload = new File("Assets/SoundFX/shotgunReload.wav");
    private final File pistolReload = new File("Assets/SoundFX/pistolReload.wav");

    private final File smgEquip = new File("Assets/SoundFX/smgEquip.wav");
    private final File rifleEquip = new File("Assets/SoundFX/rifleEquip.wav");
    private final File sniperEquip = new File("Assets/SoundFX/sniperEquip.wav");
    private final File shotgunEquip = new File("Assets/SoundFX/shotgunEquip.wav");

    private final File zombie0 = new File("Assets/SoundFX/zombie/zombie0.wav");
    private final File zombie1 = new File("Assets/SoundFX/zombie/zombie1.wav");
    private final File zombie2 = new File("Assets/SoundFX/zombie/zombie2.wav");
    private final File zombie3 = new File("Assets/SoundFX/zombie/zombie3.wav");
    private final File zombie4 = new File("Assets/SoundFX/zombie/zombie4.wav");
    private final File zombie5 = new File("Assets/SoundFX/zombie/zombie5.wav");
    private final File zombie6 = new File("Assets/SoundFX/zombie/zombie6.wav");
    private final File zombie7 = new File("Assets/SoundFX/zombie/zombie7.wav");

    private final File health = new File("Assets/SoundFX/health.wav");
    private final File shield = new File("Assets/SoundFX/shield.wav");
    private final File ammo = new File("Assets/SoundFX/ammo.wav");

    private final File empty = new File("Assets/SoundFX/empty.wav");
    private final File splat = new File("Assets/SoundFX/splat.wav");
    private final File bossKilled = new File("Assets/SoundFX/bossKilled.wav");

    private final File button = new File("Assets/SoundFX/button.wav");

    private long startingTime; //This variables will store the time that the sound started playing so it could compare how much time passed since the audio started playing.
    private final long soundLength; //The length of the audio so it will kill the thread after the audio is done playing
    private final File resourceLocation; //The location of the audio file
    private Thread thread; //The new thread that the sound will be played on
    private boolean audioRunning; //boolean variable indicates if the audio is currently playing

    //The sound length and the sound name will be passed when the method is called.
    public SoundEffects(long soundLength, String sound) {
        this.soundLength = soundLength;
        this.resourceLocation = getFile(sound);
    }

    //This method grabs the appropriate sound file to be used.
    public File getFile(String sound) {
        if (sound.equals("SMG")) return smgShoot;
        if (sound.equals("Rifle")) return rifleShoot;
        if (sound.equals("Sniper")) return sniperShoot;
        if (sound.equals("Shotgun")) return shotgunShoot;
        if (sound.equals("Pistol")) return pistolShoot;

        if (sound.equals("SMGReload")) return smgReload;
        if (sound.equals("RifleReload")) return rifleReload;
        if (sound.equals("SniperReload")) return sniperReload;
        if (sound.equals("ShotgunReload")) return shotgunReload;
        if (sound.equals("PistolReload")) return pistolReload;

        if (sound.equals("SMGEquip")) return smgEquip;
        if (sound.equals("RifleEquip")) return rifleEquip;
        if (sound.equals("SniperEquip")) return sniperEquip;
        if (sound.equals("ShotgunEquip")) return shotgunEquip;

        if (sound.equals("Zombie0")) return zombie0;
        if (sound.equals("Zombie1")) return zombie1;
        if (sound.equals("Zombie2")) return zombie2;
        if (sound.equals("Zombie3")) return zombie3;
        if (sound.equals("Zombie4")) return zombie4;
        if (sound.equals("Zombie5")) return zombie5;
        if (sound.equals("Zombie6")) return zombie6;
        if (sound.equals("Zombie7")) return zombie7;

        if (sound.equals("Health")) return health;
        if (sound.equals("Shield")) return shield;
        if (sound.equals("Ammo")) return ammo;

        if (sound.equals("Empty")) return empty;
        if (sound.equals("Splat")) return splat;
        if (sound.equals("Boss")) return bossKilled;

        if (sound.equals("Button")) return button;

        return empty;
    }

    //Starts a new thread to play the sound
    public void playSound() {
        thread = new Thread(this);
        startingTime = System.currentTimeMillis(); //records the time when the audio started playing
        audioRunning = false;
        thread.start();
    }

    @Override
    public void run() {
        if (!audioRunning) {
            try {
                Clip c = AudioSystem.getClip();
                c.open(AudioSystem.getAudioInputStream(resourceLocation));

                //Lower the volume of the sound effect
                FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
                float gainValue = (((float) 30) * 40f / 100f) - 35f;
                gainControl.setValue(gainValue);

                c.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            audioRunning = false;
        }

        while (true) if (System.currentTimeMillis() > startingTime + soundLength) stopAudio();
    }

    //Stop the thread causing the sound to stop as well.
    public void stopAudio() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isAudioRunning() {
        return audioRunning;
    }
}