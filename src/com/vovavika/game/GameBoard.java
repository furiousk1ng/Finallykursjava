package com.vovavika.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.sound.sampled.Clip;

public class GameBoard {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;

    public static final int ROWS = 4;// количество строк
    public static final int COLS = 4;// количество столбцов

    private final int startingTiles = 2; // количество начальных плиток
    private Tile[][] board;
    private boolean dead; // проиграл
    private boolean won; // выиграл
    private BufferedImage gameBoard;
    private int x;
    private int y;

    private static int SPACING = 10; // интервал между плитками
    public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH; // ширина игровой области
    public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT; // высота игровой области

    private long elapsedMS; // прошедшее время
    private long startTime; // время начала игры
    private boolean hasStarted;// начал играть (или нет)

    private ScoreManager scores;
    private Leaderboards lBoard;
    private AudioHandler audio;
    private int saveCount = 0;
    // конструктор игровой области
    public GameBoard(int x, int y) {
        this.x = x;
        this.y = y;
        board = new Tile[ROWS][COLS]; // массив для игровой области
        gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        createBoardImage();

        audio = AudioHandler.getInstance();
        audio.load("click.wav", "click"); // загрузка клика
        audio.load("MainSong.mp3", "BG"); // загрузка песни
        audio.adjustVolume("BG", -10); // настройка громкости
        audio.play("BG", Clip.LOOP_CONTINUOUSLY); // проигрывание песни (бесконечный цикл)

        lBoard = Leaderboards.getInstance();
        lBoard.loadScores(); // загрузка результатов
        scores = new ScoreManager(this);
        scores.loadGame();
        scores.setCurrentTopScore(lBoard.getHighScore()); // лучшии резльтаты
        if(scores.newGame()){
            start();
            scores.saveGame(); // сохраняем результаты
        }
        else{
            for(int i = 0; i < scores.getBoard().length; i++){
                if(scores.getBoard()[i] == 0) continue;
                spawn(i / ROWS, i % COLS, scores.getBoard()[i]);
            }

            dead = checkDead();

            won = checkWon();
        }
    }

    public void reset(){
        board = new Tile[ROWS][COLS];
        start();
        scores.saveGame();
        dead = false;
        won = false;
        hasStarted = false;
        startTime = System.nanoTime();
        elapsedMS = 0;
        saveCount = 0;
    }
    // старт
    private void start() {
        for (int i = 0; i < startingTiles; i++) {
            spawnRandom(); // рандомим две плитки на игровом поле
        }
    }

    /** Debug method */
    private void spawn(int row, int col, int value) {
        board[row][col] = new Tile(value, getTileX(col), getTileY(row));
    }

    private void createBoardImage() {	// метод, с помощью которого рисуется фон игровой доски
        Graphics2D g = (Graphics2D) gameBoard.getGraphics();
        g.setColor(Color.darkGray);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g.setColor(Color.lightGray);
        // создается сетка между плитками
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = SPACING + SPACING * col + Tile.WIDTH * col;
                int y = SPACING + SPACING * row + Tile.HEIGHT * row;
                g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
            }
        }
    }
    // обновления
    public void update() {
        saveCount++;
        if (saveCount >= 120) {
            saveCount = 0;
            scores.saveGame();
        }
        // если не выиграл и не проиграл
        if (!won && !dead) {
            if (hasStarted) {
                elapsedMS = (System.nanoTime() - startTime) / 1000000; // прошедшее время
                scores.setTime(elapsedMS);
            }
            else {
                startTime = System.nanoTime(); // текущее время
            }
        }
        // нажатие клавиш
        checkKeys();
        //если текущий результат лучше наилучшего, устанавливаем текущий
        if (scores.getCurrentScore() > scores.getCurrentTopScore()) {
            scores.setCurrentTopScore(scores.getCurrentScore());
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) continue;
                current.update();
                resetPosition(current, row, col);
                if (current.getValue() == 2048) {
                    setWon(true);
                }
            }
        }
    }
    // метод рендеринга
    public void render(Graphics2D g) {
        BufferedImage finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g2d.drawImage(gameBoard, 0, 0, null);
        // рендеринг текущей плитки
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) continue;
                current.render(g2d);
            }
        }

        g.drawImage(finalBoard, x, y, null);// рисование изображения
        g2d.dispose();	//освобождаем ресурсы


    }
    // метод сброса позиции
    private void resetPosition(Tile tile, int row, int col) {
        if (tile == null) return;

        int x = getTileX(col);
        int y = getTileY(row);
        //расстояние до плитки
        int distX = tile.getX() - x;
        int distY = tile.getY() - y;

        if (Math.abs(distX) < Tile.SLIDE_SPEED) {
            tile.setX(tile.getX() - distX);
        }

        if (Math.abs(distY) < Tile.SLIDE_SPEED) {
            tile.setY(tile.getY() - distY);
        }

        if (distX < 0) {
            tile.setX(tile.getX() + Tile.SLIDE_SPEED);
        }
        if (distY < 0) {
            tile.setY(tile.getY() + Tile.SLIDE_SPEED);
        }
        if (distX > 0) {
            tile.setX(tile.getX() - Tile.SLIDE_SPEED);
        }
        if (distY > 0) {
            tile.setY(tile.getY() - Tile.SLIDE_SPEED);
        }
    }
    // геттер плитки x
    public int getTileX(int col) {
        return SPACING + col * Tile.WIDTH + col * SPACING;
    }
    // геттер плитки y
    public int getTileY(int row) {
        return SPACING + row * Tile.HEIGHT + row * SPACING;
    }

    // границы поля
    private boolean checkOutOfBounds(int direction, int row, int col) {
        if (direction == LEFT) {
            return col < 0;
        }
        else if (direction == RIGHT) {
            return col > COLS - 1;
        }
        else if (direction == UP) {
            return row < 0;
        }
        else if (direction == DOWN) {
            return row > ROWS - 1;
        }
        return false;
    }
    // движение
    private boolean move(int row, int col, int horizontalDirection, int verticalDirection, int direction) {
        boolean canMove = false;
        Tile current = board[row][col];
        if (current == null) return false;
        boolean move = true;
        int newCol = col;
        int newRow = row;
        // пока плитка способна двигаться
        while (move) {
            newCol += horizontalDirection;
            newRow += verticalDirection;
            //проверяем достигла ли плитка границ поля
            if (checkOutOfBounds(direction, newRow, newCol)) break;
            // проверяем есть ли другая плитка на место куда необходимо сдвинуть
            if (board[newRow][newCol] == null) {
                board[newRow][newCol] = current;
                canMove = true; // можем передвинуть
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;// старое место плитки = null
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
            }
            // если можем обьеденить плитки (имеют одинаковые значения)
            else if (board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()) {
                board[newRow][newCol].setCanCombine(false);
                board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);// удваиваем значение плитки
                canMove = true;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null; // старое место плитки = null
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                board[newRow][newCol].setCombineAnimation(true); // анимация обьединения плиток
                scores.setCurrentScore(scores.getCurrentScore() + board[newRow][newCol].getValue());
            }
            else {
                move = false;
            }
        }
        return canMove;
    }
    // движение плитки
    public void moveTiles(int direction) {
        boolean canMove = false;
        int horizontalDirection = 0;
        int verticalDirection = 0;
        // движение влево
        if (direction == LEFT) {
            horizontalDirection = -1; 	// меняем горизонтальное направление
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove)
                        canMove = move(row, col, horizontalDirection, verticalDirection, direction);
                    else move(row, col, horizontalDirection, verticalDirection, direction);
                }
            }
        }
        // движение вправо
        else if (direction == RIGHT) {
            horizontalDirection = 1;	// меняем горизонтальное направление
            for (int row = 0; row < ROWS; row++) {
                for (int col = COLS - 1; col >= 0; col--) {
                    if (!canMove)
                        canMove = move(row, col, horizontalDirection, verticalDirection, direction);
                    else move(row, col, horizontalDirection, verticalDirection, direction);
                }
            }
        }
        // движение вверх
        else if (direction == UP) {
            verticalDirection = -1;	// меняем вертикальное направление
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove)
                        canMove = move(row, col, horizontalDirection, verticalDirection, direction);
                    else move(row, col, horizontalDirection, verticalDirection, direction);
                }
            }
        }
        // движение вниз
        else if (direction == DOWN) {
            verticalDirection = 1; // меняем вертикальное направление
            for (int row = ROWS - 1; row >= 0; row--) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove)
                        canMove = move(row, col, horizontalDirection, verticalDirection, direction);
                    else move(row, col, horizontalDirection, verticalDirection, direction);
                }
            }
        }
        else {
            System.out.println(direction + " is not a valid direction."); // неправильное направление
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) continue;
                current.setCanCombine(true);
            }
        }
        // проверяем можем ли двигать плитку
        if (canMove) {
            audio.play("click", 0); // звук клика
            spawnRandom(); // спавн плиток
            setDead(checkDead());
        }
    }

    // метод отвечающий за завершение игры (проиграл или нет)
    private boolean checkDead() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == null) return false;
                boolean canCombine = checkSurroundingTiles(row, col, board[row][col]);
                // проверка можешь ли обьеденить плитки
                if (canCombine) {
                    return false;
                }
            }
        }
        return true;
    }
    // метод проверки на выигрыш
    private boolean checkWon() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if(board[row][col] == null) continue;
                // если набирается 2048, то выиграл
                if(board[row][col].getValue() >= 2048) return true;
            }
        }
        return false;
    }
    // проверка на соседние плитки
    private boolean checkSurroundingTiles(int row, int col, Tile tile) {
        if (row > 0) {
            Tile check = board[row - 1][col];
            if (check == null) return true;
            // проверяем число соседней плитки и текущей
            if (tile.getValue() == check.getValue()) return true;
        }
        if (row < ROWS - 1) {
            Tile check = board[row + 1][col];
            // проверяем число соседней плитки и текущей
            if (check == null) return true;
            if (tile.getValue() == check.getValue()) return true;
        }
        if (col > 0) {
            Tile check = board[row][col - 1];
            if (check == null) return true;
            // проверяем число соседней плитки и текущей
            if (tile.getValue() == check.getValue()) return true;
        }
        if (col < COLS - 1) {
            Tile check = board[row][col + 1];
            if (check == null) return true;
            // проверяем число соседней плитки и текущей
            if (tile.getValue() == check.getValue()) return true;
        }
        return false;
    }
    // метод отвечающий за появление плиток на игровом поле
    private void spawnRandom() {
        Random random = new Random(); //генератор рандомных чисел
        boolean notValid = true;

        while (notValid) {
            int location = random.nextInt(16);
            int row = location / ROWS;
            int col = location % COLS;
            Tile current = board[row][col];
            // допустимое ли место для спауна плитки
            if (current == null) {
                int value = random.nextInt(10) < 9 ? 2 : 4;
                Tile tile = new Tile(value, getTileX(col), getTileY(row));
                board[row][col] = tile;
                notValid = false;
            }
        }
    }
    // проверка нажатие клавиш
    private void checkKeys() {
        if (!Keys.pressed[KeyEvent.VK_LEFT] && Keys.prev[KeyEvent.VK_LEFT]) {
            moveTiles(LEFT);	//двигаем плитку влево
            if (!hasStarted) hasStarted = !dead;
        }
        if (!Keys.pressed[KeyEvent.VK_RIGHT] && Keys.prev[KeyEvent.VK_RIGHT]) {
            moveTiles(RIGHT);	//двигаем плитку вправо
            if (!hasStarted) hasStarted = !dead;
        }
        if (!Keys.pressed[KeyEvent.VK_UP] && Keys.prev[KeyEvent.VK_UP]) {
            moveTiles(UP); 		//двигаем плитку вверх
            if (!hasStarted) hasStarted = !dead;
        }
        if (!Keys.pressed[KeyEvent.VK_DOWN] && Keys.prev[KeyEvent.VK_DOWN]) {
            moveTiles(DOWN);	//двигаем плитку вниз
            if (!hasStarted) hasStarted = !dead;
        }
    }
    // получение плитки с наибольшим значением
    public int getHighestTileValue(){
        int value = 2;
        for(int row = 0; row < ROWS; row++){
            for(int col = 0; col < COLS; col++){
                if(board[row][col] == null) continue;
                if(board[row][col].getValue() > value) value = board[row][col].getValue();
            }
        }
        return value;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        if(!this.dead && dead){
            //добавляем плитку с наибольшим значением
            lBoard.addTile(getHighestTileValue());
            // добавляем результат
            lBoard.addScore(scores.getCurrentScore());
            // сохраняем результат
            lBoard.saveScores();
        }
        this.dead = dead;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public void setBoard(Tile[][] board) {
        this.board = board;
    }

    public int getX() {
        return x;
    } // возвращает значение x плитки

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }	// возвращает значение y плитки

    public void setY(int y) {
        this.y = y;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        if(!this.won && won && !dead){
            // сохранем результат
            lBoard.saveScores();
        }
        this.won = won;
    }

    public ScoreManager getScores(){
        return scores;
    }
}
