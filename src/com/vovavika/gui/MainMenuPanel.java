package com.vovavika.gui;

import com.vovavika.game.DrawUtils;
import com.vovavika.game.Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends GuiPanel {

    private Font titleFont = Game.main.deriveFont(100f);	//шрифт заголовка
    private Font creatorFont = Game.main.deriveFont(28f);   //шрифт
    private String title = "2048"; 							// заголовок
    private String creator = "Курсовая работа Вовы и Вики";
    private int buttonWidth = 220; // ширина кнопки

    public MainMenuPanel() {
        super();
        // создаем кнопку Play
        GuiButton playButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 220, buttonWidth, 60);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Play");
            }
        });
        playButton.setText("Play"); // задаем текст кнопки
        add(playButton); //добавляем кнопку

        // создаем кнопку Scores
        GuiButton scoresButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 310, buttonWidth, 60);
        scoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Leaderboards");
            }
        });
        scoresButton.setText("Scores"); // задаем текст кнопки
        add(scoresButton); //добавляем кнопку

        // создаем кнопку Quit
        GuiButton quitButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 400, buttonWidth, 60);
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        quitButton.setText("Quit"); // задаем текст кнопки
        add(quitButton); //добавляем кнопку
    }
    // рендеринг
    @Override
    public void render(Graphics2D g){
        super.render(g);
        g.setFont(titleFont); // задаем шрифт
        g.setColor(Color.black);  // задаем цвет
        g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, 150);
        g.setFont(creatorFont); // задаем шрифт
        g.drawString(creator, Game.WIDTH / 2 - DrawUtils.getMessageWidth(creator, creatorFont, g) / 2, 570); // отображаем строку
    }
}
