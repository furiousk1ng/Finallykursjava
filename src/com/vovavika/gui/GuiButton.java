package com.vovavika.gui;


import com.vovavika.game.AudioHandler;
import com.vovavika.game.DrawUtils;
import com.vovavika.game.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GuiButton {

    private State currentState = State.RELEASED; //текущее состояние
    private Rectangle clickBox; //кликбокс
    private ArrayList<ActionListener> actionListeners;
    private String text = ""; //текст кнопки

    private Color main; // цвет
    private Color hover; // цвет при наведении
    private Color pressed; // цвет при нажатии
    private Font font = Game.main.deriveFont(22f); //шрифт
    private AudioHandler audio; //музыка

    public GuiButton(int x, int y, int width, int height){
        clickBox = new Rectangle(x, y, width, height);
        actionListeners = new ArrayList<ActionListener>();
        main = new Color(173, 177, 179);  // задаем основное цвет кнопке
        hover = new Color(31, 31, 33); // задаем цвет при наведении
        pressed = new Color(103, 88, 88); // задаем цвет нажатия

        audio = AudioHandler.getInstance();
        audio.load("select.wav", "select"); //загрузка звука щелчка на кнопку
    }

    public void update(){
    }
    //обработка
    public void render(Graphics2D g){
        // задаем основной цвет
        if(currentState == State.RELEASED){
            g.setColor(main);
            g.fill(clickBox);
        }
        //если кнопка нажата задем ей цвет pressed
        else if(currentState == State.PRESSED){
            g.setColor(pressed);
            g.fill(clickBox);
        }
        // иначе задаем цвет hover
        else{
            g.setColor(hover);
            g.fill(clickBox);
        }
        g.setColor(Color.white);
        g.setFont(font); // устанавливаем щрифт
        // прорисовка надписи кнопки
        g.drawString(text, clickBox.x + clickBox.width / 2  - DrawUtils.getMessageWidth(text, font, g) / 2, clickBox.y + clickBox.height / 2  + DrawUtils.getMessageHeight(text, font, g) / 2);
    }

    public void addActionListener(ActionListener listener){
        actionListeners.add(listener);
    }
    // нажатие мыши
    public void mousePressed(MouseEvent e) {
        if(clickBox.contains(e.getPoint())){
            currentState = State.PRESSED; // текущее состояние
        }
    }
    // отпущена кнопка мыши
    public void mouseReleased(MouseEvent e) {

        if(clickBox.contains(e.getPoint())){
            for(ActionListener al : actionListeners){
                al.actionPerformed(null);
            }
            audio.play("select", 0); //кнопка выбора
        }
        currentState = State.RELEASED;
    }
    //перетаскивание
    public void mouseDragged(MouseEvent e) {
        if(clickBox.contains(e.getPoint())){
            currentState = State.PRESSED;
        }
        else{
            currentState = State.RELEASED;
        }
    }
    // движение мышки
    public void mouseMoved(MouseEvent e) {
        if(clickBox.contains(e.getPoint())){
            currentState = State.HOVER;
        }
        else{
            currentState = State.RELEASED;
        }
    }
    // геттер x
    public int getX(){
        return clickBox.x;
    }
    // геттер y
    public int getY(){
        return clickBox.y;
    }

    public int getWidth(){
        return clickBox.width;
    }

    public int getHeight(){
        return clickBox.height;
    }

    public void setText(String text){
        this.text = text;
    }
    //перечисления
    private enum State{
        HOVER, RELEASED, PRESSED
    }
}
