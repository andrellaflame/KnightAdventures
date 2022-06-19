package rooms;

import main.GamePanel;
import objects.Shop;
import objects.WoodenBox;

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
    }

    public void setMaps() {

        rooms.put("ruins", new Room("ruins",this, 0));
        rooms.put("castle", new Room("castle",this, 0));
        rooms.put("sea", new Room("sea",this, 0));
        rooms.put("swamp", new Room("swamp",this, 0));
        rooms.put("cave", new Room("cave",this, 150));
        rooms.put("dungeon", new Room("dungeon",this, 220));

        //NEIGHBOURS
        rooms.get("ruins").upRoom = null;
        rooms.get("ruins").downRoom = rooms.get("castle");
        rooms.get("ruins").leftRoom = null;
        rooms.get("ruins").rightRoom = null;

        rooms.get("castle").upRoom = rooms.get("ruins");
        rooms.get("castle").downRoom = null;
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

        for (String room: rooms.keySet()) {
            rooms.get(room).setAllImages(room);
            rooms.get(room).setMatrix(room);
        }
    }

    public void draw(Graphics2D g2d) {
        if(currentRoom.name.equals("ruins") || currentRoom.name.equals("castle") || currentRoom.name.equals("cave") || currentRoom.name.equals("dungeon")) {
            drawForRoom(g2d);
        }
        else if(currentRoom.name.equals("sea") || currentRoom.name.equals("swamp")) {
            drawForWater(g2d);
        }
    }

    public void setCurrentRoom(String roomName) {

        currentRoom = rooms.get(roomName);

        currentMatrix = currentRoom.roomMatrix;
    }

    public void setGameObjects() {
        rooms.get("ruins").addGameObject(new WoodenBox(gp), gp.squareSize * 10, gp.squareSize * 10);
        rooms.get("ruins").addGameObject(new Shop(gp), gp.squareSize * 9, (int)(gp.squareSize * 0.5));
        rooms.get("castle").addGameObject(new WoodenBox(gp), gp.squareSize * 10, gp.squareSize * 10);
        rooms.get("sea").addGameObject(new WoodenBox(gp), gp.squareSize * 10, gp.squareSize * 10);
        rooms.get("swamp").addGameObject(new WoodenBox(gp), gp.squareSize * 10, gp.squareSize * 10);
    }

    private void drawForRoom(Graphics2D g2d) {
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
    }

    private void drawForWater(Graphics2D g2d) {
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
