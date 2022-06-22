package main;

import bullets.Bullet;
import characters.Character;
import characters.Player;
import gameobject.GameObject;
import objects.StaticObject;
import rooms.RoomManager;
import visualEffects.VisualManager;

import javax.swing.*;
import java.awt.*;
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

    //SYSTEM
    Thread gameThread;
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

    //GAME STATE
    public final int titleState = 0;
    public final int pauseState = 1;
    public final int gameState = 2;
    public int currentState = titleState;


    public GamePanel() {

        //SETTINGS
        this.setPreferredSize(new Dimension(maxScreenWidth, maxScreenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        //KEYS LISTENER
        this.addKeyListener(keyR);
        this.addMouseListener(mouseR);
        this.setFocusable(true);
    }

    public void setupGame(){
        //DEFAULT SETUP
//        playMusic(0);

        visualManager.setup();
    }

    public void startGameThread() {

        //STARTING THREAD
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double frameInterval = (double)1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / frameInterval;

            lastTime = currentTime;

            //UPDATE AND REPAINT 60 FPS
            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {

        //TITLE STATE
        if (currentState == titleState) {

        }
        //GAME STATE
        if (currentState == gameState) {

            //ENEMIES
            for (int i = 0; i < roomManager.currentRoom.enemies.size(); i++) {
                if(!roomManager.currentRoom.enemies.get(i).isDead)
                    roomManager.currentRoom.enemies.get(i).update();
            }

            //PLAYER
            player.update();

            //OBJECTS
            for (StaticObject obj : roomManager.currentRoom.staticObjects) {
                obj.update();
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

    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        if (currentState == titleState) {
            ui.draw(g2d);

        } else {
            //ROOM FLOOR
            roomManager.drawFirstPart(g2d);

            //BULLETS
            if(bullets.size() > 0) {
                for (Bullet bullet : bullets) {
                    bullet.draw(g2d);
                }
            }

            //ROOM SHADOWS AND TOP LAYER
            roomManager.drawShadows(g2d);
            roomManager.drawFirstLayer(g2d);
            //ROOM FINAL BOTTOM LAYER
            roomManager.drawFinalLayer(g2d);

            //STATIC OBJECTS SHADOWS
            for (StaticObject obj : roomManager.currentRoom.staticObjects) {
                if (obj != null && obj.collision) obj.shadow(g2d);
            }

            //STATIC OBJECTS WITH NO COLLISION
            for (StaticObject obj : roomManager.currentRoom.staticObjects) {
                if (obj != null && !obj.collision) {
                    obj.draw(g2d);
                }
            }

            //FILLING GAME OBJECTS LIST
            gameObjectsList.add(player);

            for (StaticObject obj : roomManager.currentRoom.staticObjects) {
                if (obj != null && obj.collision) {
                    gameObjectsList.add(obj);
                }
            }
            for (Character enemy : roomManager.currentRoom.enemies) {
                if(enemy != null && !enemy.isDead) {
                    gameObjectsList.add(enemy);
                }
            }
            //SORTING GAME OBJECTS LIST
            gameObjectsList.sort(new Comparator<GameObject>() {
                @Override
                public int compare(GameObject obj1, GameObject obj2) {

                    return Integer.compare(obj1.screenY, obj2.screenY);
                }
            });

            //DRAWING ALL GAME OBJECTS (PLAYER; ENEMIES; STATIC OBJECTS)
            for (int i = 0; i < gameObjectsList.size(); i++) {
                gameObjectsList.get(i).draw(g2d);
            }
            gameObjectsList = new ArrayList<>();

            //VISUAL EFFECTS
            visualManager.draw(g2d);

            //UI
            ui.draw(g2d);
        }

        g2d.dispose();
    }

    public void playMusic(int index) {
        if(currentSong != index) {
            music.setClip(index);
            music.currentSecond = 0;
            currentSong = index;
        }
        music.play();
        music.loop();
    }
    public void pauseMusic() {
        music.stop();
    }
    public void playSound(int index) {
        sounds.setClip(index);
        sounds.play();
    }
    public void pauseSound() {
        sounds.stop();
    }

    private void collectGarbage() {
        //DEAD ENEMIES
        for (int i = 0; i < roomManager.currentRoom.enemies.size(); i++) {
            if(roomManager.currentRoom.enemies.get(i).isDead)
                roomManager.currentRoom.enemies.remove(roomManager.currentRoom.enemies.get(i));
        }
    }
}
