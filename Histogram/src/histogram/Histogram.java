package histogram;


import javax.swing.*;

public class Histogram {

    public static void main(String[] args) {
        //używane do inicjacji metody main przy użyciu okienek
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //tworzenie okienka
                GUI myGui = new GUI();
                //ustawienie na widoczne
                myGui.setVisible(true);
            }
        });
    }
}

