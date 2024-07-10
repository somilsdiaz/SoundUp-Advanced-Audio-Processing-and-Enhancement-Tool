/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VisualComponent;

/**
 *
 * @author Somils
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadingCircle extends JPanel implements ActionListener {

    private double angle = 0;
    private Timer timer;
    private final int numberOfCircles = 12; // Número de círculos pequeños
    private final int circleRadius = 12; // Radio del círculo grande
    private final int smallCircleDiameter = 9; // Diámetro de los círculos pequeños

    public LoadingCircle() {
        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(75, 94, 212)); // Color azul claro
        g2d.setStroke(new BasicStroke(3)); // Grosor de las líneas

        // Dibuja múltiples círculos pequeños alrededor del centro
        for (int i = 0; i < numberOfCircles; i++) {
            double currentAngle = angle + (2 * Math.PI / numberOfCircles) * i;
            int x = centerX + (int) (circleRadius * Math.cos(currentAngle)) - smallCircleDiameter / 2;
            int y = centerY + (int) (circleRadius * Math.sin(currentAngle)) - smallCircleDiameter / 2;
            g2d.drawOval(x, y, smallCircleDiameter, smallCircleDiameter);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        angle += 0.05; // Incrementa el ángulo para rotar los círculos
        if (angle >= 2 * Math.PI) {
            angle = 0;
        }
        repaint();
    }
}
