package VisualComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LineMusic extends JPanel implements ActionListener, MouseListener {

    private int currentTime = 0;
    private Timer timer;
    private int songLength = 0; // Longitud de la canción en segundos
    private JLabel timeLabel;
    private int margin = 10; // Margen al principio y al final
    private boolean isPaused = false;
    private String route;
    private int estado = 1;

    public LineMusic(int duracion, String ruta) {
        route = ruta;
        songLength = duracion;
        this.timer = new Timer(1000, this); // Actualiza cada segundo
        this.timer.start();
        this.timeLabel = new JLabel(formatTime(0)); // Inicializa el temporizador en 00:00
        this.timeLabel.setHorizontalAlignment(SwingConstants.CENTER); // Alinea el texto al centro
        this.timeLabel.setForeground(Color.WHITE);
        setLayout(new BorderLayout());
        add(timeLabel, BorderLayout.SOUTH); // Añade la etiqueta al sur para estar en el borde inferior del panel
        addMouseListener(this); // Añadir el MouseListener para capturar los clics
    }

    public void status(int status) {
        estado = status;
    }

    /* public static void main(String[] args) {
        // Crear un JFrame para contener el LineMusic
        JFrame frame = new JFrame("Line Music Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 200);

        // Crear una instancia de LineMusic con una duración de 300 segundos (5 minutos)
        LineMusic lineMusic = new LineMusic(300);

        // Añadir la instancia de LineMusic al frame
        frame.add(lineMusic);

        // Hacer visible el frame
        frame.setVisible(true);
    }*/
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
        } else if (!isPaused) {
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

    public void stop() {
        isPaused = true;
        timer.stop();
    }

    public int start() {
        isPaused = false;
        timer.start();
        return currentTime;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int panelWidth = getWidth();
        int mouseX = e.getX();

        // Calcular el nuevo tiempo basado en la posición del clic
        currentTime = (int) (((double) (mouseX - margin) / (panelWidth - 2 * margin)) * songLength);
        if (currentTime < 0) {
            currentTime = 0;
        } else if (currentTime > songLength) {
            currentTime = songLength;
        }

        timeLabel.setText(formatTime(currentTime)); // Actualizar la etiqueta del tiempo
        repaint(); // Redibujar el panel
        if (estado == 1) {
            AudioNormalizer.reproducirCancionDesde(route, currentTime);
        }
        System.out.println("Tiempo actual: " + formatTime(currentTime)); // Retornar el tiempo correspondiente al punto
    }

    // Métodos no utilizados del MouseListener
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
}
