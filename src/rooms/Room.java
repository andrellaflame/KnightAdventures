package rooms;

import objects.GameObject;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Room {

    public String name;
    public int darkness;

    RoomManager sq;

    public ArrayList<GameObject> gameObjects;
    public int[][] roomMatrix;

    //NAVIGATION
    public Room upRoom;
    public Room downRoom;
    public Room leftRoom;
    public Room rightRoom;

    public Square[] squares;

    public Room(String name, RoomManager sq, int darkness) {

        this.name = name;
        this.darkness = darkness;
        squares = new Square[30];

        this.sq=sq;

        roomMatrix = new int[sq.gp.maxCols][sq.gp.maxRows];

        gameObjects = new ArrayList<>();
    }

    public void setAllImages(String room) {
        //EMPTY
        setup(room, 0, "tile_1", false);
        setup(room, 1, "tile_1", false);
        setup(room, 2, "tile_1", false);
        setup(room, 3, "tile_1", false);
        setup(room, 4, "tile_1", false);
        setup(room, 5, "tile_1", false);
        setup(room, 6, "tile_1", false);
        setup(room, 7, "tile_1", false);
        setup(room, 8, "tile_1", false);
        setup(room, 9, "tile_1", false);
        //FLOOR
        setup(room, 10, "tile_1", false);
        setup(room, 11, "tile_2", false);
        setup(room, 12, "tile_3", false);
        //WALLS
        setup(room, 13, "wall_up", true);
        setup(room, 14, "wall_down", true);
        setup(room, 15, "wall_left", true);
        setup(room, 16, "wall_right", true);
        setup(room, 17, "wall_up-left-corner", true);
        setup(room, 18, "wall_up-right-corner", true);
        setup(room, 19, "wall_down-left-corner", true);
        setup(room, 20, "wall_down-right-corner", true);
        setup(room, 21, "wall_up-left-door", true);
        setup(room, 22, "wall_up-right-door", true);
        setup(room, 23, "wall_down-left-door", true);
        setup(room, 24, "wall_down-right-door", true);
        setup(room, 25, "wall_standart", true);
    }

    public void setMatrix(String roomName) {

        //READING MATRIX FROM FILE
        try (BufferedReader br = new BufferedReader(new FileReader("resources/maps/" + roomName + ".txt"))) {

            for (int row = 0; row < sq.gp.maxRows; row++) {
                String s = br.readLine();
                String[] array = s.split(" ");

                for (int col = 0; col < array.length; col++) {

                    if (Integer.parseInt(array[col]) > 12) {
                        roomMatrix[col][row] = Integer.parseInt(array[col]);
                    } else {
                        roomMatrix[col][row] = (int) (Math.random() * 3 + 10);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setup(String room, int index, String name, boolean collision) {
        try {
            squares[index] = new Square();
            squares[index].img = ImageIO.read(new File("resources/images/" + room + "/" + name + ".png"));
            squares[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGameObject(GameObject gameObject, int screenX, int screenY){
        gameObjects.add(gameObject);
        gameObjects.get(gameObjects.size() - 1).screenX = screenX;
        gameObjects.get(gameObjects.size() - 1).screenY = screenY;
        gameObjects.get(gameObjects.size() - 1).room = this;
    }
}
