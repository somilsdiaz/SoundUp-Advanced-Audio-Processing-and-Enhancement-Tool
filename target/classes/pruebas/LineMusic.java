package pruebas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LineMusic extends JPanel implements ActionListener {
    private int currentTime = 0;
    private Timer timer;
    private int songLength = 158; // Longitud de la canción en segundos
    private JLabel timeLabel;
    private int margin = 10; // Margen al principio y al final

    public LineMusic() {
        this.timer = new Timer(1000, this); // Actualiza cada segundo
        this.timer.start();
        this.timeLabel = new JLabel(formatTime(0)); // Inicializa el temporizador en 00:00
        this.timeLabel.setHorizontalAlignment(SwingConstants.CENTER); // Alinea el texto al centro
        this.timeLabel.setForeground(Color.WHITE);
        setLayout(new BorderLayout());
        add(timeLabel, BorderLayout.SOUTH); // Añade la etiqueta al sur para estar en el borde inferior del panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Dibujar la línea
        g.setColor(Color.WHITE);
        g.drawLine(margin, panelHeight / 2, panelWidth - margin, panelHeight / 2);

        // Calcular la posición del círculo
        int circleX = margin + (int) ((panelWidth - 2 * margin) * ((double) currentTime / songLength));
        int circleY = panelHeight / 2;

        // Dibujar el círculo
        g.setColor(Color.WHITE);
        g.fillOval(circleX - 10, circleY - 10, 20, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentTime >= songLength) {
            timer.stop(); // Detén el temporizador cuando la canción termine
        } else {
            currentTime++;
            timeLabel.setText(formatTime(currentTime)); // Actualiza el tiempo con el nuevo formato
            repaint();
        }
    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

 /*   public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Audio Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200);
        LineMusic visualizer = new LineMusic();
        frame.add(visualizer);
        frame.setVisible(true);
    */
}
