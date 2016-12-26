//import javax.swing.*;
//import java.awt.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

/**
 * Created by Philip on 12/25/16.
 */
public class Application {
    public static void main(String[] args) {
        MandelbrotGridCreator mandelbrotGridCreator = new MandelbrotGridCreator();
        double[][] mandelbrotGrid = mandelbrotGridCreator.createGrid(200, 200, 0, 0, 60, 1000);
//        for (double[] row : mandelbrotGrid) {
//            for (double val : row) {
//                System.out.printf("%5.0f", val);
//            }
//            System.out.println("");
//        }
        MandelbrotImageCreator mandelbrotImageCreator = new MandelbrotImageCreator();
        mandelbrotImageCreator.createImage(mandelbrotGrid);


        new AppViewer(mandelbrotImageCreator.getImg());

    }
}

class AppViewer extends JFrame {
    JPanel panel = new JPanel();
    JButton button = new JButton("It's a button");
    BufferedImage img;

    public AppViewer(BufferedImage img) {
        super("Madelbrot Viewer");
        this.img = img;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setResizable(true);

        MandelbrotImageDisplay mandelbrotImageDisplay = new MandelbrotImageDisplay();
        mandelbrotImageDisplay.setImg(img);
        add(mandelbrotImageDisplay);

//        panel.add(button);
//        add(panel);
        setVisible(true);
    }

}

class MandelbrotImageDisplay extends JPanel implements MouseWheelListener {
    Image img;

    int dstx1 = 0;
    int dsty1 = 0;
    int dstx2 = 200;
    int dsty2 = 200;
    int srcx1 = 0;
    int srcy1 = 0;
    int srcx2 = 100;
    int srcy2 = 100;

    public MandelbrotImageDisplay() {
        super();
        this.addMouseWheelListener(this);
    }

    public void setImg(Image img) {
        this.img = img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, dstx1, dsty1, dstx2, dsty2, srcx1, srcy1, srcx2, srcy2, null);
        g.drawImage(img, 2,2,2,2,2,2,2,2, null);
    }

    public void mouseWheelMoved (MouseWheelEvent e) {
        double scrollMovement = e.getPreciseWheelRotation();
        if (scrollMovement > 0) {
            srcx1++;
            System.out.println("up");
        } else {
            srcx1--;
            System.out.println("down");
        }
        repaint();
    }
}
