package com.vovavika.gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GuiPanel {

    private ArrayList<GuiButton> buttons; // список кнопок

    public GuiPanel(){
        buttons = new ArrayList<GuiButton>();
    }
    // обновления
    public void update(){
        for(GuiButton b : buttons){
            b.update();
        }
    }
    // рендер
    public void render(Graphics2D g){
        for(GuiButton b : buttons){
            b.render(g);
        }
    }
    // добавление кноки
    public void add(GuiButton button){
        buttons.add(button);
    }
    // удаление
    public void remove(GuiButton button){
        buttons.remove(button);
    }
    // нажатие мыши
    public void mousePressed(MouseEvent e){
        for(GuiButton b : buttons){
            b.mousePressed(e);
        }
    }
    // отпущена кнопка мыши
    public void mouseReleased(MouseEvent e){
        for(GuiButton b : buttons){
            b.mouseReleased(e);
        }
    }
    //перетаскивание
    public void mouseDragged(MouseEvent e){
        for(GuiButton b : buttons){
            b.mouseDragged(e);
        }
    }
    // перемещение мыши
    public void mouseMoved(MouseEvent e){
        for(GuiButton b : buttons){
            b.mouseMoved(e);
        }
    }
}
