package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class UI {

    GamePanel gp;
    Font gameFont;
    String screenText;
    BufferedImage start, loading;
    BufferedImage[] hearts;

    boolean init;

    public int optionNum = 1;
    public boolean loadingScreen;
    public int loadingFrames = 0;

    Shape ring, oval;

    //SCREEN HINTS
    String hint;
    int hintTimer;
    int maxHintLength = 30;

    public UI(GamePanel gp) {
        this.gp = gp;

        try {
            this.start = ImageIO.read(new File("resources/images/screens/startScreen.png"));
            this.loading = ImageIO.read(new File("resources/images/screens/loadingScreen.png"));
            this.gameFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/lunchds.ttf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.setFont(gameFont);

        //TITLE STATE
        if (gp.currentState == gp.titleState) {
            if (loadingScreen) {
                drawLoadingScreen(g2d);
            } else {
                drawStartScreen(g2d);
            }
        }
        //GAME STATE
        if (gp.currentState == gp.gameState) {
            drawDarkScreen(g2d);
            drawPlayerHP(g2d);
            drawScreenHint(g2d);
        }
        //PAUSE STATE
        if (gp.currentState == gp.pauseState) {
            drawDarkScreen(g2d);
            drawPlayerHP(g2d);
            drawPauseScreen(g2d);
        }
    }

    /**
     * draws players HP
     * @param g2d graphics
     */
    public void drawPlayerHP(Graphics2D g2d) {

        if(!init){
            hearts = new BufferedImage[gp.player.maxHP/3];
            Arrays.fill(hearts, gp.player.heart1);
            init = true;
        }
        int currentHeart = (gp.player.HP+2)/3;
        if(gp.player.HP%3==0&&gp.player.HP!=0){
            hearts[currentHeart-1]=gp.player.heart1;
            if(currentHeart!=gp.player.maxHP/3) {
                hearts[currentHeart] = gp.player.heart4;
            }
        }
        if(gp.player.HP%3==2){
            hearts[currentHeart-1]=gp.player.heart2;
        }
        if(gp.player.HP%3==1){
            hearts[currentHeart-1]=gp.player.heart3;
        }
        for(int i = 1; i <=gp.player.maxHP/3; i++) {

            g2d.drawImage(hearts[i-1], i* gp.squareSize, 0, null);
        }

        for(int i = 1; i <= gp.player.armor; i++) {
            g2d.drawImage(gp.player.shield, i * gp.squareSize, gp.squareSize - 5, null);
        }
    }

    private void drawLoadingScreen(Graphics2D g2d) {

        loadingFrames++;
        g2d.drawImage(loading, 0, 0, gp.maxScreenWidth, gp.maxScreenHeight, null);

        //TITLE
        screenText = "Knight adventures";
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 100));

        int y = gp.squareSize * 3;
        int x = getCenterX(screenText, g2d);

        g2d.setColor(Color.BLACK);
        g2d.drawString(screenText, x + 5, y + 5);

        g2d.setColor(Color.WHITE);
        g2d.drawString(screenText, x, y);

        BufferedImage tmp = null;

        if(loadingFrames % (gp.FPS * 2) < gp.FPS / 2) {
            screenText = "Loading";
            tmp = gp.player.down1;
        } else if(loadingFrames % (gp.FPS * 2) >= gp.FPS / 2 && loadingFrames % (gp.FPS * 2) < gp.FPS) {
            screenText = "Loading.";
            tmp = gp.player.right1;
        } else if(loadingFrames % (gp.FPS * 2) >= gp.FPS && loadingFrames % (gp.FPS * 2) < gp.FPS * 1.5) {
            screenText = "Loading..";
            tmp = gp.player.up1;
        } else if(loadingFrames % (gp.FPS * 2) >= gp.FPS * 1.5) {
            screenText = "Loading...";
            tmp = gp.player.left1;
        }

        g2d.drawImage(tmp,
                gp.squareSize/2,
                (int) (gp.maxScreenHeight - gp.squareSize * 1.5),
                null);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 48));

        x = (int) (gp.squareSize * 1.5);
        y = (int) (gp.maxScreenHeight - gp.squareSize * 0.5);

        g2d.setColor(Color.WHITE);
        g2d.drawString(screenText, x, y);

        if (loadingFrames == gp.FPS * 5||true) {
            gp.setupGame();
            gp.currentState = gp.gameState;
            loadingScreen = false;
            loadingFrames = 0;
        }
    }

    private void drawStartScreen(Graphics2D g2d) {

        //TITLE
        screenText = "Knight adventures";
        g2d.drawImage(start, 0, 0, gp.maxScreenWidth, gp.maxScreenHeight, null);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 100));

        int y = gp.squareSize * 3;
        int x = getCenterX(screenText, g2d);

        g2d.setColor(Color.BLACK);
        g2d.drawString(screenText, x + 5, y + 5);

        g2d.setColor(Color.WHITE);
        g2d.drawString(screenText, x, y);

        //NEW GAME
        screenText = "New game";
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 40));

        y = gp.squareSize * 7;
        x = getCenterX(screenText, g2d);

        if (optionNum == 1) {
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 40));

            g2d.setStroke(new BasicStroke(5));
            g2d.drawRoundRect(x - 24,
                    y - (int) g2d.getFontMetrics().getStringBounds(screenText, g2d).getHeight(),
                    (int) g2d.getFontMetrics().getStringBounds(screenText, g2d).getWidth() + 48,
                    (int) (g2d.getFontMetrics().getStringBounds(screenText, g2d).getHeight() * 1.25),
                    25, 25);
        }

        g2d.drawString(screenText, x, y);

        //LOAD GAME
        screenText = "Load game";
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 40));

        y = gp.squareSize * 9;
        x = getCenterX(screenText, g2d);

        if (optionNum == 2) {
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 40));

            g2d.setStroke(new BasicStroke(5));
            g2d.drawRoundRect(x - 24,
                    y - (int) g2d.getFontMetrics().getStringBounds(screenText, g2d).getHeight(),
                    (int) g2d.getFontMetrics().getStringBounds(screenText, g2d).getWidth() + 48,
                    (int) (g2d.getFontMetrics().getStringBounds(screenText, g2d).getHeight() * 1.25),
                    25, 25);
        }

        g2d.drawString(screenText, x, y);


        //EXIT
        screenText = "Exit";
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 40));

        y = gp.squareSize * 11;
        x = getCenterX(screenText, g2d);

        if (optionNum == 3) {
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 40));

            g2d.setStroke(new BasicStroke(5));
            g2d.drawRoundRect(x - 24,
                    y - (int) g2d.getFontMetrics().getStringBounds(screenText, g2d).getHeight(),
                    (int) g2d.getFontMetrics().getStringBounds(screenText, g2d).getWidth() + 48,
                    (int) (g2d.getFontMetrics().getStringBounds(screenText, g2d).getHeight() * 1.25),
                    25, 25);
        }

        g2d.drawString(screenText, x, y);
    }

    private void drawDarkScreen(Graphics2D g2d) {

        //UP
        if(gp.player.screenY < gp.squareSize * 2 - 16) {
            int darkAlpha = (int) ((double)(gp.squareSize * 2 - 16 - gp.player.screenY) / (gp.squareSize*2) * 150);
            g2d.setColor(new Color(0, 0, 0, darkAlpha));
            g2d.fillRect(0, 0, gp.squareSize*(gp.maxCols - 2), gp.squareSize*(gp.maxRows - 2));
        }
        //DOWN
        else if(gp.player.screenY > gp.squareSize * (gp.maxRows - 5)) {
            int darkAlpha = (int) ((double)(gp.player.screenY - gp.squareSize * (gp.maxRows - 5)) / (gp.squareSize*2) * 150);
            g2d.setColor(new Color(0, 0, 0, darkAlpha));
            g2d.fillRect(0, 0, gp.squareSize*(gp.maxCols - 2), gp.squareSize*(gp.maxRows - 2));
        }
        //LEFT
        else if(gp.player.screenX < gp.squareSize * 2 - 8) {
            int darkAlpha = (int) ((double)(gp.squareSize * 2 - 8 - gp.player.screenX) / (gp.squareSize*2) * 150);
            g2d.setColor(new Color(0, 0, 0, darkAlpha));
            g2d.fillRect(0, 0, gp.squareSize*(gp.maxCols - 2), gp.squareSize*(gp.maxRows - 2));
        }
        //RIGHT
        else if(gp.player.screenX > gp.squareSize * (gp.maxCols - 5) + 8) {
            int darkAlpha = (int) ((double)(gp.player.screenX - (gp.squareSize * (gp.maxCols - 5) + 8)) / (gp.squareSize*2) * 150);
            g2d.setColor(new Color(0, 0, 0, darkAlpha));
            g2d.fillRect(0, 0, gp.squareSize*(gp.maxCols - 2), gp.squareSize*(gp.maxRows - 2));
        }

        //ROOM DARKNESS
        if (gp.player.hasTorch) drawLight(g2d);
    }

    public void drawLight(Graphics2D g2d) {
        //PLAYER HAS LIGHT
        double centerX = gp.player.screenX + (double) gp.squareSize / 2;
        double centerY = gp.player.screenY + (double) gp.squareSize / 2;
        double innerRadius = 114;

        Area rect = new Area(new Rectangle2D.Double(0,0,gp.maxScreenWidth, gp.maxScreenHeight));
        Ellipse2D inner = new Ellipse2D.Double(
                centerX - innerRadius,
                centerY - innerRadius,
                innerRadius * 2,
                innerRadius * 2);
        oval = new Area(inner);
        rect.subtract(new Area(inner));
        g2d.setColor(new Color(0, 0, 0, 245));
        g2d.fill(rect);

        //WITH BLUR
//        for(int i = 1; i < innerRadius; i += 1) {
//            ring = createRingShape(centerX, centerY, i + 1, i);
//            g2d.setColor(new Color(0, 0, 0, (int) ((i / (innerRadius - 1)) * 240)));
//            RenderingHints rh = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
//            g2d.setRenderingHints(rh);
//            g2d.fill(ring);
//        }

        //WITH CIRCLE
        for(int i = 1; i < innerRadius - 40; i += 1) {
            ring = createRingShape(centerX, centerY, innerRadius, i + 30);
            g2d.setColor(new Color(0, 0, 0, 8));
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2d.setRenderingHints(rh);
            g2d.fill(ring);
        }
    }

    public Shape createRingShape(double centerX, double centerY, double outerRadius, double innerRadius) {
        Ellipse2D outer = new Ellipse2D.Double(
                centerX - outerRadius,
                centerY - outerRadius,
                outerRadius * 2,
                outerRadius * 2);
        Ellipse2D inner = new Ellipse2D.Double(
                centerX - innerRadius,
                centerY - innerRadius,
                innerRadius * 2,
                innerRadius * 2);

        oval = new Area(inner);
        Area area = new Area(outer);
        area.subtract(new Area(inner));
        return area;
    }

    public void makeScreenHint(String hint, int hintTimer){
        this.hint = hint;
        this.hintTimer = hintTimer;
    }

    private void drawScreenHint(Graphics2D g2d){
        if(hintTimer > 0){
            hintTimer--;
            ArrayList<String> hints = new ArrayList<>(Arrays.asList(hint.split("#")));
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 30));

            String max = "";
            for (int i = 0; i < hints.size(); i++) {
                if (hints.get(i).length() > max.length()) max = hints.get(i);
            }

            int x = getCenterX(max, g2d);
            int y = gp.squareSize * 2;

            g2d.setColor(new Color(0,0,0, 150));
            g2d.fillRoundRect(x - 36,
                    y - (int) g2d.getFontMetrics().getStringBounds(max, g2d).getHeight() - 20,
                    (int) g2d.getFontMetrics().getStringBounds(max, g2d).getWidth() + 72,
                    (int) (g2d.getFontMetrics().getStringBounds(max, g2d).getHeight() * hints.size()) + 48,
                    25, 25);

            g2d.setColor(new Color(255,255,255, 100));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRoundRect(x - 36,
                    y - (int) g2d.getFontMetrics().getStringBounds(max, g2d).getHeight() - 20,
                    (int) g2d.getFontMetrics().getStringBounds(max, g2d).getWidth() + 72,
                    (int) (g2d.getFontMetrics().getStringBounds(max, g2d).getHeight() * hints.size()) + 48,
                    25, 25);

            g2d.setColor(Color.WHITE);
            for (int i = 0; i < hints.size(); i++) {
                x = getCenterX(hints.get(i), g2d);
                y = 2 * gp.squareSize + (int) (g2d.getFontMetrics().getStringBounds(hints.get(i), g2d).getHeight()) * i;
                g2d.drawString(hints.get(i), x, y);
            }
        }
    }

    private void drawPauseScreen(Graphics2D g2d){

        g2d.setColor(new Color(0,0,0,100));
        g2d.fillRect(0, 0, gp.squareSize*(gp.maxCols - 2), gp.squareSize*(gp.maxRows - 2));

        g2d.setColor(Color.WHITE);
        g2d.setFont(gameFont.deriveFont(Font.PLAIN, 70));

        screenText = "Paused";

        int y = gp.maxScreenHeight/2;
        int x = getCenterX(screenText, g2d);

        g2d.drawString(screenText, x, y);
    }

    private int getCenterX (String str, Graphics2D g2d) {
        int length = (int) g2d.getFontMetrics().getStringBounds(str, g2d).getWidth();
        return gp.maxScreenWidth/2 - length/2;
    }
}
