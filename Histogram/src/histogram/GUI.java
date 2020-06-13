package histogram;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GUI extends JFrame {
    private JButton TOFILE;
    private JButton TOOUTPUT;
    private JButton LOAD;
    private JButton LOADFILE;
    private JTextArea OUTPUT;
    private JTextArea INPUT;
    private JPanel rootPanel;
    private JPanel panel;
    private ArrayList<String> lista;
    private Map<String, Integer> list;


    public GUI() {
        add(rootPanel);
        setTitle("HISTOGRAMY");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage("histogram.png"));
        setLocationRelativeTo(null);
        //metoda która wyszukuje pliku w systemie
        LOADFILE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // jak zwykłe napisy ale mogą być zmieniane przez metody
                StringBuilder build = new StringBuilder();
                // Inicjujemy wyszukiwarke na domyślny folder
                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
                //szukacz ma wyswietlic okno
                int returnValue = chooser.showOpenDialog(null);
                //jeśli zatwierdzimy
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    // szukacz bierze wyswietlony plik
                    File selectedFile = chooser.getSelectedFile();
                    //blok try catch
                    try {
                        //ustawiamy skaner na plik
                        Scanner InputFromFile = new Scanner(selectedFile);
                        //jeśli ma jeszcze jakieś linie
                        while (InputFromFile.hasNextLine()) {
                            //pobierz linie do builda
                            build.append(InputFromFile.nextLine());
                        }
                        //wypisz w polu IMPUT pobrany tekst
                        INPUT.setText(String.valueOf(build));
                        System.out.print(build);
                        //zamknij plik
                        InputFromFile.close();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        // listener do pola tekstowego
        INPUT.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
        //listener zapisujący do pliku
        TOFILE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = INPUT.getText();
                try {
                    ToFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        //listener do pola tekstowego z wynikiem
        OUTPUT.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
        //listener do histogramowania tekstu znajdujacego sie w polu INPUT do pola OUTPUT
        TOOUTPUT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OUTPUT.setText(making());
            }
        });
    }//metoda służąca za zapis do pliku

    public void ToFile() throws IOException {
        //inicjalizacja pliku
        File file = new File("file.txt");
        //otwieranie pliku
        PrintWriter output = new PrintWriter(file);
        output.print(making());
        //zamykanie pliku
        output.close();
    }

    //tworzenie histogramu
    public String making() {
        //ustawienie skanera na napis w polu tekstowym
        Scanner scanner = new Scanner(INPUT.getText());
        //inicjalizacja Arraylist
        ArrayList<String> lista = new ArrayList<String>();
        //Inicjalizacja TreeMap
        TreeMap<String, Integer> list = new TreeMap<String, Integer>();
        //zapełnianie listy
        while (scanner.hasNext()) {
            lista.add(scanner.next());
        }
        //zapełnianie mapy przy użyciu listy
        for (String key : lista) {
            list.put(key, Collections.frequency(lista, key));
        }
        //wyswietlanie mapy przez podział kluczy jako słowo i wartości jako ilość
        for (Map.Entry<String, Integer> entry : list.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(key + ": " + value);
        }// zwraca mape jako tekst
        return (list.toString());
    }
}
