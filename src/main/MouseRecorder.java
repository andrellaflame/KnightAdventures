package main;

import bullets.Bullet;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseRecorder implements MouseListener {

    GamePanel gp;

    public MouseRecorder(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(gp.currentState == gp.gameState && !gp.player.isReloading && !gp.player.scriptsAreActive) {
            gp.bullets.add(new Bullet(gp, "player_bullet",
                    2, 2,
                    6, 6,
                    (int)gp.player.screenX + 19, (int)gp.player.screenY + 30,
                    e.getX() - 5, e.getY() - 5, "player", false));
            gp.player.isReloading = true;
            gp.playSound(7);
            gp.playSound(1);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
