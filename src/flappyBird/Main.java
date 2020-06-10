package flappyBird;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class FlappyBird extends Component implements ActionListener, MouseListener, KeyListener {

    public static FlappyBird flappyBird;
    public final int WIDTH = 800, HEIGHT = 800;
    public Renderer renderer;
    public int score, highscore;
    public int ticks, gravity;
    public boolean gameOver, started;
    public Rectangle bird;
    public ArrayList<Rectangle> pimps;
    public Random rand;

    public FlappyBird() {
        JFrame jframe = new JFrame();

        Timer timer = new Timer(20, this);
        jframe.addKeyListener(this);
        renderer = new Renderer();
        rand = new Random();
        jframe.add(renderer);
        jframe.addMouseListener(this);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.setResizable(false);
        jframe.setTitle("Flappy!");
        jframe.setIconImage(Toolkit.getDefaultToolkit().getImage("ptaszek.png"));
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        jframe.setVisible(true);
        jframe.setLocationRelativeTo(null);


        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);

        pimps = new ArrayList<Rectangle>();
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();

    }

    public void addColumn(boolean start) {
        int harder = 600;
        int gap = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);
        if (start) {
            pimps.add(new Rectangle(WIDTH + width + pimps.size() * 300, HEIGHT - height - 120, width, height));
            pimps.add(new Rectangle(WIDTH + width + (pimps.size() - 1) * 300, 0, width, HEIGHT - height - gap));
        } else {
            for (int i = 0; i < score; i++) {
                harder = harder - 10;
            }
            pimps.add(new Rectangle(pimps.get(pimps.size() - 1).x + harder, HEIGHT - height - 120, width, height));
            pimps.add(new Rectangle(pimps.get(pimps.size() - 1).x, 0, width, HEIGHT - height - gap));

        }
    }

    public void paintColumn(Graphics g, Rectangle column) {

        g.setColor(Color.GREEN.darker().darker().darker().darker());
        g.fillRect(column.x, column.y, column.width, column.height);


    }

    public void jump() {

        if (gameOver) {

            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
            pimps.clear();
            gravity = 0;
            score = 0;
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }
        if (!started) {

            started = true;
        } else {
            if (gravity > 0) {
                gravity = 0;
            }
            gravity -= 10;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int speed = 10;
        if (score >= highscore) {
            highscore = score;
        }
        ticks++;
        if (started) {
            for (int i = 0; i < pimps.size(); i++) {
                Rectangle column = pimps.get(i);

                column.x -= speed;
            }

            if (ticks % 2 == 0 && gravity < 15) {
                gravity += 2;
            }

            for (int i = 0; i < pimps.size(); i++) {
                Rectangle column = pimps.get(i);

                if (column.x + column.width < 0) {
                    pimps.remove(column);

                    if (column.y == 0) {
                        addColumn(false);
                    }
                }
            }
            bird.y += gravity;

            for (Rectangle column : pimps) {

                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
                    score++;

                }
                if (column.intersects(bird)) {

                    gameOver = true;
                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;
                    } else {
                        if (column.y != 0) {
                            bird.y = column.y - bird.height;
                        } else if (bird.y < column.height) {
                            bird.y = column.height;
                        }
                    }
                }
            }
            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;

            }
            if (bird.y + gravity >= HEIGHT - 130) {

                bird.y = HEIGHT - 120 - bird.height;
                gameOver = true;
            }

        }

        renderer.repaint();
    }

    public void repaint(Graphics g) {

        ImageIcon background = new ImageIcon("swiat.jpg");
        background.paintIcon(this, g, 0, 0);

        ImageIcon floor = new ImageIcon("podloga.png");
        floor.paintIcon(this, g, 0, 680);

        try {
            BufferedImage img = ImageIO.read(new File("ptaszek.png"));

            g.drawImage(img, bird.x, bird.y, 40, 40, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Rectangle column : pimps) {

            paintColumn(g, column);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Serif", 1, 50));
        if (!started) {
            g.drawString("Click to start!", WIDTH / 2 - 120, HEIGHT / 2 - 50);
        }

        if (gameOver) {
            g.drawString("GameOver!", WIDTH / 2 - 120, HEIGHT / 2 - 50);
            g.setFont(new Font("Serif italic ", Font.ITALIC, 30));
            g.drawString("Click to start again!", WIDTH / 2 - 120, HEIGHT / 2);
            g.setFont(new Font("SansSerif bold", Font.BOLD, 20));
            g.drawString("score: " + score, WIDTH / 2 - 350, HEIGHT / 2 - 350);
        }
        if (!gameOver && started) {
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }
        g.setFont(new Font("SansSerif bold", Font.BOLD, 20));
        g.drawString("Highscore: " + highscore, WIDTH - 160, HEIGHT / 2 - 350);
    }


    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        jump();
    }

    @Override
    public void mousePressed(MouseEvent e) {

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

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
    }
}