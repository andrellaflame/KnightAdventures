/**
 * @author Anatolii Andrusenko, Andrii Sulimenko, Vladyslav Marchenko
 * @version 1.0
 *
 * class GamePanel which is Runnable JPanel and gathers the whole game and runs it as a Thread
 */
package main;

import bullets.Bullet;
import characters.Player;
import gameobject.GameObject;
import objects.StaticObject;
import rooms.RoomManager;
import visualEffects.VisualManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable{

    //GAME SCREEN
    public final int squareSize = 48; // 48x48px

    public final int maxCols = 27;
    public final int maxRows = 19;
    public final int maxScreenWidth = (maxCols - 2) * squareSize;
    public final int maxScreenHeight = (maxRows - 2) * squareSize;

    //GAME FPS
    public final int FPS = 60;

    //DIFFICULTY
    public int difficulty;
    public long[] records = new long[5];
    public long timeInSeconds;

    //SYSTEM
    public Thread gameThread;
    public KeyRecorder keyR = new KeyRecorder(this);
    public MouseRecorder mouseR = new MouseRecorder(this);
    public RoomManager roomManager = new RoomManager(this);
    public UI ui = new UI(this);
    public CollisionViewer colViewer = new CollisionViewer(this);
    public VisualManager visualManager = new VisualManager(this);

    //GAME OBJECTS
    public Player player = new Player(this, keyR);
    ArrayList<GameObject> gameObjectsList = new ArrayList<>();

    //BULLETS
    public ArrayList<Bullet> bullets = new ArrayList<>();

    //SOUNDS
    public Music music = new Music();
    int currentSong = -1;
    public Music sounds = new Music();
    public Clip waterSound;

    //GAME STATE
    public final int titleState = 0;
    public final int pauseState = 1;
    public final int gameState = 2;
    public final int gameOverState = 3;
    public int currentState = titleState;

    /**
     * constructor
     */
    public GamePanel() {

        //SETTINGS
        this.setPreferredSize(new Dimension(maxScreenWidth, maxScreenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        //KEYS LISTENER
        this.addKeyListener(keyR);
        this.addMouseListener(mouseR);
        this.setFocusable(true);

        //RECORDS
        for (int i = 1; i <= 5; i++) {
            try (BufferedReader br = new BufferedReader(new FileReader("resources/files/records/record_" + i + ".txt"))){
                long secondsRecord = Integer.parseInt(br.readLine());
                records[i - 1] = secondsRecord;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * first game setup
     */
    public void setupGame(){
        //DEFAULT SETUP
        try {
            AudioInputStream aus = AudioSystem.getAudioInputStream(music.soundFiles[24]);
            waterSound = AudioSystem.getClip();
            waterSound.open(aus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        playMusic(0);
        waterSound.start();
        waterSound.loop(Clip.LOOP_CONTINUOUSLY);

        roomManager.setGameObjects();
        roomManager.setEnemies();
        player.setDefaultParameters();
        visualManager.setup();
    }

    /**
     * method to retry the game from the last completed room
     */
    public void retry() {
        roomManager.currentRoom.enemies = new ArrayList<>();
        roomManager.currentRoom.phase = "initial";
        roomManager.currentRoom.changingPhase = false;
        roomManager.currentRoom.enemiesSpawned = false;
        roomManager.currentRoom.bossSpawned = false;
        for (StaticObject obj: roomManager.currentRoom.staticObjects) {
            obj.interactingFrames = 0;
            obj.isInteracted = false;
            switch (obj.name) {
                case "target", "coin" -> obj.isGarbage = true;
                case "torch_left", "torch_right" -> obj.setAnimation(StaticObject.ANIMATION_CONTINUOUSLY);
                case "boss_door" -> {
                    if(roomManager.currentRoom.name.equals("finalMap")) {
                        obj.noAnimation = obj.images.get(obj.images.size() - 1);
                        obj.collision = false;
                    }
                }
                case "door_horizontal", "door_vertical" -> {
                    if(roomManager.rooms.get(obj.relatedRoom).phase.equals("completed")) {
                        obj.noAnimation = obj.images.get(0);
                        obj.collision = false;
                    }
                }
                case "lever" -> obj.noAnimation = obj.images.get(0);
                case "chest" -> {
                    obj.isOpened = false;
                    obj.emptyChest = false;
                }
            }
        }

        player.HP = player.maxHP;
        player.coinsAmount = player.backupCoinsAmount;
        player.armor = player.backupArmor;
        player.hasTorch = false;
        player.setPlayerImages(false);
        player.interactedObjectName = "";
        player.screenX = squareSize * (maxCols - 3) / 2.0;
        player.screenY = 10 * squareSize;
        player.speed = FPS/20;
        player.lastBulletFrames = 0;
        player.direction = "down";

        bullets = new ArrayList<>();

        roomManager.setCurrentRoom("ruins");

        stopMusic();
        playMusic(roomManager.currentRoom.songIndex);
        waterSound.start();
        waterSound.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * method to restart game completely
     */
    public void restartGame() {
        Main.restartGame();
    }

    /**
     * initial method which starts game thread
     */
    public void startGameThread() {
        //STARTING THREAD
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * run method for game thread
     */
    @Override
    public void run() {

        double frameInterval = (double)1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        double timeDelta = 0;

        while(gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / frameInterval;

            if(currentState == gameState
                    && !roomManager.rooms.get("finalMap").phase.equals("completed"))
                timeDelta += (currentTime - lastTime) / 1000000000.0;

            lastTime = currentTime;

            //UPDATE AND REPAINT 60 FPS
            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
            if(timeDelta >= 1) {
                timeInSeconds++;
                timeDelta--;
            }
        }
    }

    /**
     * update method for game thread (is called from run())
     */
    private void update() {
        //TITLE STATE
        if (currentState == titleState) {}
        //GAME STATE
        if (currentState == gameState) {

            //ENEMIES
            for (int i = 0; i < roomManager.currentRoom.enemies.size(); i++) {
                if(!roomManager.currentRoom.enemies.get(i).isDead)
                    roomManager.currentRoom.enemies.get(i).update();
            }

            //PLAYER
            if(!ui.shopIsOpened)
                player.update();

            //ROOM
            roomManager.currentRoom.update();

            //OBJECTS
            for (int i = 0; i < roomManager.currentRoom.staticObjects.size(); i++) {
                roomManager.currentRoom.staticObjects.get(i).update();
            }

            //BULLETS
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).update();
            }

            //COLLECT ALL GARBAGE
            collectGarbage();
        }
        //PAUSE STATE
        if (currentState == pauseState) {

        }
        //GAME OVER STATE
        if (currentState == gameOverState) {

        }
    }

    /**
     * paintComponent method for game thread (is called from run())
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        if (currentState == titleState) {
            ui.draw(g2d);

        } else {
            //ROOM FLOOR
            roomManager.drawFirstPart(g2d);

            //STATIC OBJECTS WITH NO COLLISION (w/o opened doors)
            for (int i = 0; i < roomManager.currentRoom.staticObjects.size(); i++) {
                if (roomManager.currentRoom.staticObjects.get(i) != null
                        && !roomManager.currentRoom.staticObjects.get(i).collision
                        && !((roomManager.currentRoom.staticObjects.get(i).name.equals("door_horizontal")
                        || roomManager.currentRoom.staticObjects.get(i).name.equals("door_vertical")) &&
                        roomManager.currentRoom.staticObjects.get(i).animation == StaticObject.ANIMATION_ONCE)) {
                    roomManager.currentRoom.staticObjects.get(i).draw(g2d);
                }
            }

            //STATIC OBJECTS SHADOWS
            for (int i = 0; i < roomManager.currentRoom.staticObjects.size(); i++) {
                if (roomManager.currentRoom.staticObjects.get(i) != null
                        && roomManager.currentRoom.staticObjects.get(i).collision) {
                    roomManager.currentRoom.staticObjects.get(i).shadow(g2d);
                }
            }

            //ROOM SHADOWS AND TOP LAYER
            roomManager.drawShadows(g2d);

            //ROOM FIRST LAYER
            roomManager.drawFirstLayer(g2d);

            //ROOM FINAL BOTTOM LAYER
            roomManager.drawFinalLayer(g2d);

            //BULLETS
            if(bullets.size() > 0) {
                for (int i = 0; i < bullets.size(); i++) {
                    if(!bullets.get(i).bossMortar)
                        bullets.get(i).draw(g2d);
                }
            }

            //FILLING GAME OBJECTS LIST
            gameObjectsList.add(player);

            for (int i = 0; i < roomManager.currentRoom.staticObjects.size(); i++) {
                if (roomManager.currentRoom.staticObjects.get(i) != null
                        && (roomManager.currentRoom.staticObjects.get(i).collision
                        || ((roomManager.currentRoom.staticObjects.get(i).name.equals("door_horizontal")
                        || roomManager.currentRoom.staticObjects.get(i).name.equals("door_vertical"))
                        && roomManager.currentRoom.staticObjects.get(i).animation == StaticObject.ANIMATION_ONCE))) {
                    gameObjectsList.add(roomManager.currentRoom.staticObjects.get(i));
                }
            }
            for (int i = 0; i < roomManager.currentRoom.enemies.size(); i++) {
                if(roomManager.currentRoom.enemies.get(i) != null && !roomManager.currentRoom.enemies.get(i).isDead) {
                    gameObjectsList.add(roomManager.currentRoom.enemies.get(i));
                }
            }
            //SORTING GAME OBJECTS LIST
            gameObjectsList.sort(Comparator.comparingInt(obj -> (int) obj.screenY));

            //DRAWING ALL GAME OBJECTS (PLAYER; ENEMIES; STATIC OBJECTS)
            for (int i = 0; i < gameObjectsList.size(); i++) {
                gameObjectsList.get(i).draw(g2d);
            }
            gameObjectsList = new ArrayList<>();

            //MORTAR BOSS MISSILES
            if(bullets.size() > 0) {
                for (Bullet bullet : bullets) {
                    if(bullet.bossMortar)
                        bullet.draw(g2d);
                }
            }

            //VISUAL EFFECTS
            visualManager.draw(g2d);

            //UI
            ui.draw(g2d);
        }

        g2d.dispose();
    }

    /**
     * method to start (or continue) the music
     * @param index song index
     */
    public void playMusic(int index) {
        if(currentSong != index) {
            music.setClip(index);
            music.currentSecond = 0;
            currentSong = index;
        }
        music.play();
        music.loop();
    }
    /**
     * method to pause the music
     */
    public void pauseMusic() {
        music.pause();
    }
    /**
     * method to stop the music
     */
    public void stopMusic() {
        music.stop();
    }
    /**
     * method to play a sound effect
     */
    public void playSound(int index) {
        sounds.setClip(index);
        sounds.play();
    }

    /**
     * method to collect all garbage we indicate throughout the game process
     */
    private void collectGarbage() {
        //DEAD ENEMIES
        for (int i = 0; i < roomManager.currentRoom.enemies.size(); i++) {
            if(roomManager.currentRoom.enemies.get(i).isDead)
                roomManager.currentRoom.enemies.remove(roomManager.currentRoom.enemies.get(i));
        }
        //GARBAGE STATIC OBJECTS
        for (int i = 0; i < roomManager.currentRoom.staticObjects.size(); i++) {
            if(roomManager.currentRoom.staticObjects.get(i).isGarbage)
                roomManager.currentRoom.staticObjects.remove(roomManager.currentRoom.staticObjects.get(i));
        }
    }
}
