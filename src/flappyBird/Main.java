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
    //konstruktor wywołujący wszystkie polecenia do zainicjowania gry
    public FlappyBird() {
        JFrame jframe = new JFrame();
        //czas odswiezania
        Timer timer = new Timer(20, this);
        //dodajemy Keylistener do okna
        jframe.addKeyListener(this);
        //towrzymy obiekt "renderer"
        renderer = new Renderer();
        //inicjujemy randomowe liczby
        rand = new Random();
        //dodajemy obiekt do okna
        jframe.add(renderer);
        //dodawanie MouseListenera do okna
        jframe.addMouseListener(this);
        //ustawienia parametrow rozdzielczosci okna
        jframe.setSize(WIDTH, HEIGHT);
        //komenda, ktora zabrania zmian wielkosci okna
        jframe.setResizable(false);
        //ustawienie tytulu
        jframe.setTitle("Flappy!");
        //ustawienie ikony okna
        jframe.setIconImage(Toolkit.getDefaultToolkit().getImage("ptaszek.png"));
        //ustawienie zakonczenia programu wraz z zamknieciem okna
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        //ustawiamy okno na widoczne
        jframe.setVisible(true);
        //ustawia okno na srodku ekranu
        jframe.setLocationRelativeTo(null);

        //tworzy obiekt "bird" typu Rectangle
        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
        //tworzy obiekt "pimps" typu Arraylist
        pimps = new ArrayList<Rectangle>();
        //zapelnianie Arraylist przez 4 kolumny
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
        timer.start();

    }
    //metoda dodajaca kolumny
    public void addColumn(boolean start) {
        int harder = 600;
        int gap = 300;
        int width = 100;
        //randomowe wysokosci
        int height = 50 + rand.nextInt(300);
        //pierwsze kolumny
        if (start) {
            pimps.add(new Rectangle(WIDTH + width + pimps.size() * 300, HEIGHT - height - 120, width, height));
            pimps.add(new Rectangle(WIDTH + width + (pimps.size() - 1) * 300, 0, width, HEIGHT - height - gap));
        } else {
            //zwiekszanie poziomu trudnosci, czestrze pojawianie sie kolumn
            for (int i = 0; i < score; i++) {
                harder = harder - 10;
            }
            pimps.add(new Rectangle(pimps.get(pimps.size() - 1).x + harder, HEIGHT - height - 120, width, height));
            pimps.add(new Rectangle(pimps.get(pimps.size() - 1).x, 0, width, HEIGHT - height - gap));

        }
    }
    //kolorowanie kolumn
    public void paintColumn(Graphics g, Rectangle column) {

        g.setColor(Color.GREEN.darker().darker().darker().darker());
        g.fillRect(column.x, column.y, column.width, column.height);


    }
    //metoda okreslajaca co sie dzieje po nacisnieciu spacji badz przycisku myszy
    public void jump() {
        //jak ma reagowac skok kiedy gra sie zakonczy
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
        //rozpoczecie gry po kliknieciu myszy badz spacji
        if (!started) {

            started = true;
        } else {
            //wyzerowanie grawitacji
            if (gravity > 0) {
                gravity = 0;
            }//wzlatywanie do gory
            gravity -= 10;
        }
    }
    //metoda okreslajaca co sie dzieje w danych sytuacjach
    @Override
    public void actionPerformed(ActionEvent e) {
        //zainicjowana predkosc poruszania sie kolumn
        int speed = 10;
        //ustawianie najwyzszego wyniku
        if (score >= highscore) {
            highscore = score;
        }//inkrementacja ticku
        ticks++;
        //co ma sie dziac kiedy gra wystartuje
        if (started) {
            //tworzenie kolumn i ustawienie z jaka predkoscia maja sie przesuwac
            for (int i = 0; i < pimps.size(); i++) {
                Rectangle column = pimps.get(i);

                column.x -= speed;
            }
            //ustawianie tick rate
            if (ticks % 2 == 0 && gravity < 15) {
                gravity += 2;
            }
            //dodawanie kolumn
            for (int i = 0; i < pimps.size(); i++) {
                Rectangle column = pimps.get(i);
                //warunek aby usuwac kolumny ktore juz przelecialy
                if (column.x + column.width < 0) {
                    pimps.remove(column);
                    //usuwanie kolumn ktore nie beda wyswietlac sie na ekranie
                    if (column.y == 0) {
                        addColumn(false);
                    }
                }
            }
            //ustawienie ptaka aby dzialala na niego grawitacja
            bird.y += gravity;
            //petla dla kolekcji
            for (Rectangle column : pimps) {
                //warunek, ktory pozwala na zliczanie punktow kiedy przeskakujemy przez rure
                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
                    score++;

                }//co sie stanie kiedy ptak uderzy w obiekt
                if (column.intersects(bird)) {
                    //zakonczenie gry
                    gameOver = true;
                    //swobodne spadanie przed celem po uderzeniu
                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;
                    } else {
                        //co sie stanie kiedy uderzymy w rure od gory, efekt zeslizgniecia sie
                        if (column.y != 0) {
                            bird.y = column.y - bird.height;
                            //co sie stanie kiedy uderzymy od dolu, zostaniemy na poziomie tej rury
                        } else if (bird.y < column.height) {
                            bird.y = column.height;
                        }
                    }
                }
            }//ograniczenie do miejsca przeznaczonego do gry od gory
            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;

            }
            //ograniczenie do miejsca przeznaczonego do gry od dolu
            if (bird.y + gravity >= HEIGHT - 130) {
                //zatrzymanie ptaka na podlodze kiedy spadnie
                bird.y = HEIGHT - 120 - bird.height;
                gameOver = true;
            }

        }
        //przemalowanie
        renderer.repaint();
    }
    //metoda odpowiadajaca za malowanie
    public void repaint(Graphics g) {
        //ustawiam co ma znajdowac sie na tle
        ImageIcon background = new ImageIcon("swiat.jpg");
        //ustawienie gdzie ma sie znajdowac tlo
        background.paintIcon(this, g, 0, 0);
        //ustawiam jak ma wyglodac podloga
        ImageIcon floor = new ImageIcon("podloga.png");
        //ustawienie gdzie ma sie znajdowac podloga
        floor.paintIcon(this, g, 0, 680);
        //blok try catch
        try {
            //zaladowanie wygladu ptaka
            BufferedImage img = ImageIO.read(new File("ptaszek.png"));
            //rysowanie wygladu ptaka i okreslonym miejscu
            g.drawImage(img, bird.x, bird.y, 40, 40, null);
        } catch (IOException e) {
            //wylapuje ewentualny blad
            e.printStackTrace();
        }
        //petla dla kolekcji malujaca kolumny
        for (Rectangle column : pimps) {

            paintColumn(g, column);
        }
        //ustawienie koloru
        g.setColor(Color.white);
        //ustawienie czcionki
        g.setFont(new Font("Serif", 1, 50));
        //wyswietlanie napisu kiedy gra sie nie rozpoczela
        if (!started) {
            g.drawString("Click to start!", WIDTH / 2 - 120, HEIGHT / 2 - 50);
        }
        // wyswietlanie napisow kiedy gra sie zakonczyla
        if (gameOver) {
            //wyswietla napis gameover
            g.drawString("GameOver!", WIDTH / 2 - 120, HEIGHT / 2 - 50);
            //zmiana czcionki
            g.setFont(new Font("Serif italic ", Font.ITALIC, 30));
            //wyswietla napis click to start again
            g.drawString("Click to start again!", WIDTH / 2 - 120, HEIGHT / 2);
            //zmiana czcionki
            g.setFont(new Font("SansSerif bold", Font.BOLD, 20));
            //wyswietla wynik
            g.drawString("score: " + score, WIDTH / 2 - 350, HEIGHT / 2 - 350);
        }
        //co ma sie dziac kiedy gra jest wystartowana badz niewystartowana
        if (!gameOver && started) {
            //pokazuje wynik podczas rozgrywki
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }//zmiana czcionki
        g.setFont(new Font("SansSerif bold", Font.BOLD, 20));
        //pokazuje najlepszy uzyskany wynik
        g.drawString("Highscore: " + highscore, WIDTH - 160, HEIGHT / 2 - 350);
    }

    //tworzenie obiektu flappybirda
    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }


    @Override//kiedy klikniemy myszke wykonuje metode jump()
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

    @Override//kiedy klawisz SPACJA zostanie zwolniony wykonuje metode jump()
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
    }
}