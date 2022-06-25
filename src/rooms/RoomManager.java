package rooms;

import characters.Boss;
import characters.EnemyWithFangs;
import characters.EnemyWithPistol;
import main.GamePanel;
import objects.*;

import java.awt.*;
import java.util.HashMap;

public class RoomManager {

    public HashMap<String, Room> rooms;
    GamePanel gp;

    public int [][] currentMatrix;
    public Room currentRoom;

    public RoomManager(GamePanel gp) {
        this.gp = gp;

        currentMatrix = new int[gp.maxCols][gp.maxRows];
        rooms = new HashMap<>();

        setMaps();
        setCurrentRoom("ruins");
        setGameObjects();
        setEnemies();
    }

    public void setMaps() {
        rooms.put("ruins", new Room("ruins",this));
        rooms.get("ruins").phase = "ruins unique phase";

        rooms.put("castle", new Room("castle",this));
        rooms.put("sea", new Room("sea",this));
        rooms.get("sea").isWaterRoom = true;
        rooms.put("swamp", new Room("swamp",this));
        rooms.get("swamp").isWaterRoom = true;
        rooms.put("cave", new Room("cave",this));
        rooms.put("dungeon", new Room("dungeon",this));
        rooms.put("finalMap", new Room("finalMap",this));

        //NEIGHBOURS
        rooms.get("ruins").upRoom = null;
        rooms.get("ruins").downRoom = rooms.get("castle");
        rooms.get("ruins").leftRoom = null;
        rooms.get("ruins").rightRoom = null;

        rooms.get("castle").upRoom = rooms.get("ruins");
        rooms.get("castle").downRoom = rooms.get("finalMap");
        rooms.get("castle").leftRoom = rooms.get("cave");
        rooms.get("castle").rightRoom = rooms.get("sea");

        rooms.get("sea").upRoom = rooms.get("swamp");
        rooms.get("sea").downRoom = null;
        rooms.get("sea").leftRoom = rooms.get("castle");
        rooms.get("sea").rightRoom = null;

        rooms.get("swamp").upRoom = null;
        rooms.get("swamp").downRoom = rooms.get("sea");
        rooms.get("swamp").leftRoom = null;
        rooms.get("swamp").rightRoom = null;

        rooms.get("cave").upRoom = rooms.get("dungeon");
        rooms.get("cave").downRoom = null;
        rooms.get("cave").leftRoom = null;
        rooms.get("cave").rightRoom = rooms.get("castle");

        rooms.get("dungeon").upRoom = null;
        rooms.get("dungeon").downRoom = rooms.get("cave");
        rooms.get("dungeon").leftRoom = null;
        rooms.get("dungeon").rightRoom = null;

        rooms.get("finalMap").upRoom = rooms.get("castle");
        rooms.get("finalMap").downRoom = null;
        rooms.get("finalMap").leftRoom = null;
        rooms.get("finalMap").rightRoom = null;

        for (String room: rooms.keySet()) {
            rooms.get(room).setAllImages(room);
            rooms.get(room).setMatrix(room);
        }
    }

    public void drawFirstPart(Graphics2D g2d) {
        if(!currentRoom.isWaterRoom) {
            for (int row = 0; row < gp.maxRows; row++) {

                for (int col = 0; col < gp.maxCols; col++) {
                    if (currentMatrix[col][row] <= 12) {
                        g2d.drawImage(currentRoom.squares[currentMatrix[col][row]].img,
                                (col - 1) * gp.squareSize,
                                (row - 1) * gp.squareSize,
                                null);
                    }
                }
            }
        } else {
            //WATER ROOMS
            for (int row = 0; row < gp.maxRows; row++) {

                for (int col = 0; col < gp.maxCols; col++) {
                    if(currentMatrix[col][row] > 12) {
                        g2d.drawImage(currentRoom.squares[currentMatrix[col][row]].img,
                                (col - 1) * gp.squareSize,
                                (row - 1) * gp.squareSize,
                                null);
                    }
                }
            }
            for (int row = 0; row < gp.maxRows; row++) {

                for (int col = 0; col < gp.maxCols; col++) {
                    if(currentMatrix[col][row] <= 12) {
                        for (int i = 0; i < 24; i++) {
                            g2d.setColor(new Color(0, 0, 0, 8));
                            g2d.fillRect((col - 1) * gp.squareSize - i,
                                    (row - 1) * gp.squareSize - i,
                                    gp.squareSize, gp.squareSize);
                        }
                    }
                }
            }
            for (int row = 0; row < gp.maxRows; row++) {

                for (int col = 0; col < gp.maxCols; col++) {
                    if(currentMatrix[col][row] <= 12) {
                        g2d.drawImage(currentRoom.squares[currentMatrix[col][row]].img,
                                (col - 1) * gp.squareSize,
                                (row - 1) * gp.squareSize,
                                null);
                    }
                }
            }
        }
    }
    public void drawShadows(Graphics2D g2d) {
        //THIS PART ONLY FOR INNER ROOMS
        if(!currentRoom.isWaterRoom) {
            for (int row = 0; row < gp.maxRows; row++) {

                for (int col = 0; col < gp.maxCols; col++) {
                    if(currentMatrix[col][row] > 12) {
                        for (int i = 0; i < 24; i++) {
                            g2d.setColor(new Color(0, 0, 0, 10));
                            g2d.fillRect((col - 1) * gp.squareSize - i,
                                    (row - 1) * gp.squareSize - i,
                                    gp.squareSize, gp.squareSize);
                        }
                    }
                }
            }
        }
    }
    public void drawFirstLayer(Graphics2D g2d) {
        //THIS PART ONLY FOR INNER ROOMS
        if(!currentRoom.isWaterRoom) {
            for (int row = 0; row < gp.maxRows; row++) {

                for (int col = 0; col < gp.maxCols; col++) {
                    if(currentMatrix[col][row] > 12 && currentMatrix[col][row] != 14 && currentMatrix[col][row] != 23 && currentMatrix[col][row] != 24) {
                        g2d.drawImage(currentRoom.squares[currentMatrix[col][row]].img,
                                (col - 1) * gp.squareSize,
                                (row - 1) * gp.squareSize,
                                null);
                    }
                }
            }
        }
    }
    public void drawFinalLayer(Graphics2D g2d) {
        //THIS PART ONLY FOR INNER ROOMS
        if(!currentRoom.isWaterRoom) {
            for (int row = 0; row < gp.maxRows; row++) {

                for (int col = 0; col < gp.maxCols; col++) {
                    if(currentMatrix[col][row] == 14 || currentMatrix[col][row] == 23 || currentMatrix[col][row] == 24) {
                        g2d.drawImage(currentRoom.squares[currentMatrix[col][row]].img,
                                (col - 1) * gp.squareSize,
                                (row - 1) * gp.squareSize,
                                null);
                    }
                }
            }
        }
    }

    public void setCurrentRoom(String roomName) {
        currentRoom = rooms.get(roomName);
        currentMatrix = currentRoom.roomMatrix;
    }

    public void setGameObjects() {
        //RUINS
        rooms.get("ruins").addGameObject(new WoodenBox(gp), gp.squareSize * 10, gp.squareSize * 10);
        rooms.get("ruins").addGameObject(new Shop(gp), gp.squareSize * 9, (int)(gp.squareSize * 0.5));
        rooms.get("ruins").addGameObject(new Lever(gp), gp.squareSize * 10 + 14, gp.squareSize * 14 + 12);
        rooms.get("ruins").addGameObject(new Door(gp, "ruins", "closed", "door_horizontal"), gp.squareSize * 11, gp.squareSize * 14);

        //CASTLE
        rooms.get("castle").addGameObject(new Door(gp, "castle", "opened", "door_horizontal"), gp.squareSize * 11, 0);
        rooms.get("castle").addGameObject(new Door(gp, "castle", "closed", "door_vertical"), gp.squareSize * 23, gp.squareSize * 6);
        rooms.get("castle").addGameObject(new Door(gp, "swamp", "closed", "door_vertical"), gp.squareSize, gp.squareSize * 6);

        //SEA
        rooms.get("sea").addGameObject(new Door(gp, "sea", "opened", "door_vertical"), gp.squareSize, gp.squareSize * 6);
        rooms.get("sea").addGameObject(new Door(gp, "sea", "closed", "door_horizontal"), gp.squareSize * 11, 0);

        //SWAMP
        rooms.get("swamp").addGameObject(new Door(gp, "swamp", "opened", "door_horizontal"), gp.squareSize * 11, gp.squareSize * 14);

        //CAVE
        for (int i = gp.squareSize * 4; i < gp.squareSize * 21; i+= gp.squareSize * 8) {
            rooms.get("cave").addGameObject(new Torch(gp, "torch_block"), i, gp.squareSize * 3 + 24);
            rooms.get("cave").addGameObject(new Torch(gp, "torch_block"), i, gp.squareSize * 12 + 8);
        }
        rooms.get("cave").addGameObject(new Door(gp, "cave", "opened", "door_vertical"), gp.squareSize * 23, gp.squareSize * 6);
        rooms.get("cave").addGameObject(new Door(gp, "cave", "closed", "door_horizontal"), gp.squareSize * 11, 0);

        //DUNGEON
        rooms.get("dungeon").addGameObject(new Torch(gp, "torch_left"), gp.squareSize * 11, gp.squareSize * 14);
        rooms.get("dungeon").addGameObject(new Torch(gp, "torch_right"), gp.squareSize * 13, gp.squareSize * 14);
        rooms.get("dungeon").addGameObject(new Door(gp, "dungeon", "opened", "door_horizontal"), gp.squareSize * 11, gp.squareSize * 14);

        //FINAL MAP
        rooms.get("finalMap").addGameObject(new Lever(gp), gp.squareSize * 12, gp.squareSize * 12);
        rooms.get("finalMap").addGameObject(new Door(gp, "finalMap", "opened", "door_horizontal"), gp.squareSize * 11, 0);
    }

    public void setEnemies() {
        rooms.get("castle").addEnemiesData(new EnemyWithPistol(gp, 18, 11));
        rooms.get("sea").addEnemiesData(new EnemyWithPistol(gp, 4, 11));
        rooms.get("swamp").addEnemiesData(new EnemyWithPistol(gp, 9, 11));
        rooms.get("cave").addEnemiesData(new EnemyWithFangs(gp, 18, 6));
        rooms.get("dungeon").addEnemiesData(new EnemyWithFangs(gp, 4, 6));
        rooms.get("finalMap").addEnemiesData(new Boss(gp));
    }
}
