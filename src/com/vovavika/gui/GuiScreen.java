package com.vovavika.gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class GuiScreen {

    private static GuiScreen screen;
    private HashMap<String, GuiPanel> panels;
    private String currentPanel = ""; //текущая
    //конструктор
    private GuiScreen() {
        panels = new HashMap<String, GuiPanel>();
    }

    public static GuiScreen getInstance() {
        if (screen == null) {
            screen = new GuiScreen();
        }
        return screen;
    }
    // обновление
    public void update() {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).update();
        }
    }
    // рендер
    public void render(Graphics2D g) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).render(g);
        }
    }
    // добавление
    public void add(String panelName, GuiPanel panel) {
        panels.put(panelName, panel);
    }

    public void setCurrentPanel(String panelName) {
        currentPanel = panelName;
    }
    // нажатие мыши
    public void mousePressed(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mousePressed(e);
        }
    }
    // отпущена кнопка мыши
    public void mouseReleased(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mouseReleased(e);
        }
    }
    //перетаскивание
    public void mouseDragged(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mouseDragged(e);
        }
    }
    // перемещение мыши
    public void mouseMoved(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mouseMoved(e);
        }
    }
}
