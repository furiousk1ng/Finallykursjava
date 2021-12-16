package com.vovavika.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class ScoreManager {

    // CURRENT SCORES
    private int currentScore; // текущий результат
    private int currentTopScore; // лучший результат
    private long time; // текущее время
    private long startingTime; //время начала игры
    private long bestTime; // лучшее время
    private int[] board = new int[16];

    // File
    private String filePath; // путь к файлу
    private String temp; // имя файла
    private GameBoard gBoard;


    private boolean newGame;

    public ScoreManager(GameBoard gBoard) {
        try {
            filePath = new File("").getAbsolutePath(); // абсолютный путь к файлу
        } catch (Exception e) {
            e.printStackTrace();
        }
        temp = "TEMP.txt"; // имя файла

        this.gBoard = gBoard;
    }
    // сброс
    public void reset() {
        File f = new File(filePath, temp);
        if (f.isFile()) {
            f.delete();
        }
        //обновляем переменные
        newGame = true;
        startingTime = 0;
        currentScore = 0;
        time = 0;
    }
    // создание файла
    private void createFile() {
        FileWriter output = null;
        newGame = true; // была начата новая игра

        try {
            File f = new File(filePath, temp); // создаем файл
            output = new FileWriter(f);
            BufferedWriter writer = new BufferedWriter(output);
            writer.write("Current score");
            writer.newLine();
            writer.write("" + 0);
            writer.newLine();
            writer.write("Best result");
            writer.newLine();
            writer.write("" + 0);
            writer.newLine();
            writer.write("Time in milliseconds");
            writer.newLine();
            writer.write("" + 0);
            writer.newLine();
            writer.write("Tiles on the playing field");
            writer.newLine();
            // записываем игровую таблицу
            for (int row = 0; row < GameBoard.ROWS; row++) {
                for (int col = 0; col < GameBoard.COLS; col++) {
                    if(row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1){
                        writer.write("" + 0);
                    }
                    else{
                        writer.write(0 + "-");
                    }
                }
            }
            writer.close(); // закрываем поток
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // сохранение игры
    public void saveGame() {
        FileWriter output = null;
        if (newGame) newGame = false;

        try {
            File f = new File(filePath, temp);
            output = new FileWriter(f);
            BufferedWriter writer = new BufferedWriter(output);
            writer.write("Current score");
            writer.newLine();
            writer.write("" + currentScore); // записываем текущий результат
            writer.newLine();
            writer.write("Best result");
            writer.newLine();
            writer.write("" + currentTopScore); //записываем лучший результат
            writer.newLine();
            writer.write("Time in milliseconds");
            writer.newLine();
            writer.write("" + time);// записваем время
            writer.newLine();
            writer.write("Tiles on the playing field");
            writer.newLine();
            // записываем игровую таблицу
            for (int row = 0; row < GameBoard.ROWS; row++) {
                for (int col = 0; col < GameBoard.COLS; col++) {
                    this.board[row * GameBoard.COLS + col] = gBoard.getBoard()[row][col] != null ? gBoard.getBoard()[row][col].getValue() : 0;
                    if (row == GameBoard.ROWS - 1 && col == GameBoard.COLS - 1)
                        writer.write("" + board[row * GameBoard.COLS + col]);
                    else writer.write(board[row * GameBoard.COLS + col] + "-");
                }
            }
            writer.close(); // закрываем поток
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadGame() {
        try {
            File f = new File(filePath, temp);
            // если нет файла, создаем его
            if (!f.isFile()) {
                createFile();
            }
            // чтение файла
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            reader.readLine();
            currentScore = Integer.parseInt(reader.readLine()); // считываем текущий счет
            reader.readLine();
            currentTopScore = Integer.parseInt(reader.readLine()); // считываем лучший результат
            reader.readLine();
            time = Long.parseLong(reader.readLine()); //считываем время
            reader.readLine();
            startingTime = time;

            // чтение плиток
            String[] board = reader.readLine().split("-");
            for (int i = 0; i < board.length; i++) {
                this.board[i] = Integer.parseInt(board[i]);
            }
            reader.close();// закрываем поток
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // геттер текущего результата
    public int getCurrentScore() {
        return currentScore;
    }
    // сеттер текущего результата
    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }
    // геттер лучшего результата
    public int getCurrentTopScore() {
        return currentTopScore;
    }
    // сеттер лучшего результата
    public void setCurrentTopScore(int currentTopScore) {
        this.currentTopScore = currentTopScore;
    }
    //геттер времени
    public long getTime() {
        return time;
    }
    // сеттер времени
    public void setTime(long time) {
        this.time = time + startingTime;
    }


    public boolean newGame() {
        return newGame;
    }

    public int[] getBoard() {
        return board;
    }
}