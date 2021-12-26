import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * This class represents the JPanel of the game.
 */
public class GamePanel extends JPanel implements ActionListener {

    final int SCREEN_WIDTH = 900;
    final int SCREEN_HEIGHT = 900;
    final int UNIT_SIZE = 25;
    final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    final int DELAY = 75;
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    /**
     * Start point
     */
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Method that draws the contents of the game, as lines, snake and apples
     */
    public void draw(final Graphics graphics) {
        if (!running) {
            gameOver(graphics);
            return;
        }

        IntStream.range(0, SCREEN_HEIGHT / UNIT_SIZE).forEach(i -> {
            graphics.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            graphics.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
        });

        graphics.setColor(Color.RED);
        graphics.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        IntStream.range(0, bodyParts).forEachOrdered(i -> {
            if (i == 0) {
                graphics.setColor(Color.GREEN);
            } else {
                graphics.setColor(Color.LIGHT_GRAY);
            }
            graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        });

        graphics.setColor(Color.red);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        final FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, graphics.getFont().getSize());
    }

    /**
     * Method that generates randomly the coordinates of the apple
     */
    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    /**
     * Moves the snake to the next position using the current direction of its "head"
     */
    public void move() {
        int bound = bodyParts;
        for (int i = bound; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    /**
     * Check if the snake's heda ate an apple
     */
    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    /**
     * Check if the snake's heda collided with the border or its own body
     */
    public void checkCollisions() {
        //Check if the head collides with body
        IntStream.rangeClosed(1, bodyParts).forEachOrdered(i -> {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        });

        //Check if the head touches any of the borders
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }

    }

    /**
     * Display the "Game Over" screen
     */
    public void gameOver(final Graphics graphics) {
        //Game over text
        graphics.setColor(Color.red);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 75));
        final FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        //Score
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
        graphics.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score")) / 2, graphics.getFont().getSize());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
