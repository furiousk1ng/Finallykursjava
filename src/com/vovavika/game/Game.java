package com.vovavika.game;

import com.vovavika.gui.GuiScreen;
import com.vovavika.gui.LeaderboardsPanel;
import com.vovavika.gui.MainMenuPanel;
import com.vovavika.gui.PlayPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class Game extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener{

    public static final int WIDTH = GameBoard.BOARD_WIDTH + 40;// ширина
    public static final int HEIGHT = 630; // высота
    public static final Font main = new Font("Bebas Neue Regular", Font.PLAIN, 28); //шрифт
    private Thread game;
    private boolean running;
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    private GuiScreen screen;

    public Game() {

        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));//отступ от компонентов
        addKeyListener(this);// обрабока событий клавиатуры
        // обработка событий мышки
        addMouseListener(this);
        addMouseMotionListener(this);

        screen = GuiScreen.getInstance();
        screen.add("Menu", new MainMenuPanel());
        screen.add("Play", new PlayPanel());
        screen.add("Leaderboards", new LeaderboardsPanel());
        screen.setCurrentPanel("Menu"); // главный экран (установлена панель меню)
    }
    // метод update
    private void update() {
        screen.update(); //обновление экрана
        Keys.update(); //обновления клавиатуры
    }
    // рендер
    private void render() {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        screen.render(g);
        g.dispose(); //освобождаем ресурсы

        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.drawImage(image, 0, 0, null);// вывод изображения
        g2d.dispose(); //освобождаем ресурсы
    }

    @Override
    public void run() {
        int fps = 0, updates = 0;
        long fpsTimer = System.currentTimeMillis();// текущее время
        double nsPerUpdate = 1000000000.0 / 60;//время между обновлениями

        //время последнего обновления в наносекундах
        double then = System.nanoTime();
        double unprocessed = 0;// необработано

        while (running) {

            boolean shouldRender = false;

            double now = System.nanoTime();
            unprocessed += (now - then) / nsPerUpdate;
            then = now;

            // Очередь обновлений
            while (unprocessed >= 1) {

                // update
                updates++;
                update();
                unprocessed--;
                shouldRender = true;
            }

            // рендер
            if (shouldRender) {
                fps++;
                render();
                shouldRender = false;
            }
            else {
                try {
                    Thread.sleep(1);//приостанавливаем поток
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // FPS timer
            if (System.currentTimeMillis() - fpsTimer > 1000) {
                System.out.printf("%d fps %d updates", fps, updates);
                System.out.println("");
                fps = 0;
                updates = 0;
                fpsTimer += 1000;
            }
        }
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        game = new Thread(this, "game");
        game.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        System.exit(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Keys.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keys.keyReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        screen.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        screen.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        screen.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        screen.mouseMoved(e);
    }
}
