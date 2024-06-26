package pruebas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleAudioVisualizer extends JPanel implements ActionListener {
    private int currentTime = 0;
    private Timer timer;
    private int songLength = 300; // Longitud de la canción en segundos
    private JLabel timeLabel;
    private int margin = 10; // Margen al principio y al final

    public SimpleAudioVisualizer() {
        this.timer = new Timer(1000, this); // Actualiza cada segundo
        this.timer.start();
        this.timeLabel = new JLabel("Time: 0s");
        setLayout(new BorderLayout());
        add(timeLabel, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Dibujar la línea
        g.setColor(Color.BLACK);
        g.drawLine(margin, panelHeight / 2, panelWidth - margin, panelHeight / 2);

        // Calcular la posición del círculo
        int circleX = margin + (int) ((panelWidth - 2 * margin) * ((double) currentTime / songLength));
        int circleY = panelHeight / 2;

        // Dibujar el círculo
        g.setColor(Color.RED);
        g.fillOval(circleX - 10, circleY - 10, 20, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentTime >= songLength) {
            timer.stop(); // Detén el temporizador cuando la canción termine
        } else {
            currentTime++;
            timeLabel.setText("Time: " + currentTime + "s");
            repaint();
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Audio Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 20);
        SimpleAudioVisualizer visualizer = new SimpleAudioVisualizer();
        frame.add(visualizer);
        frame.setVisible(true);
    }
}
