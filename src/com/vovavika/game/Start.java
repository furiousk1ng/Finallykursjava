package com.vovavika.game;

import javax.swing.JFrame;

public class Start {

    public static void main(String[] args){
        Game game = new Game();
        //создаем окно приложения
        JFrame window = new JFrame("2048");
        // закрытие окна по умолчанию
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setResizable(true);
        window.add(game);//добавляет компоненты к фрейму.
        window.pack();	//оптимальный размер окна
        window.setLocationRelativeTo(null);//расположит по центру экрана
        window.setVisible(true);// делает окно видимым
        game.start();//запуск
    }
}