import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by Philip on 12/26/16.
 */
public class ApplicationController {
    int width = 200;
    int height = 200;
    Coord center = new Coord(0, 0);
    double zoom = 60;
    int maxIterations = 1000;
    Image mandelbrotImage;

    public ApplicationController() {
        new AppViewer();
    }

    class AppViewer extends JFrame {
        JPanel panel = new JPanel();
        public AppViewer() {
            super("Madelbrot Viewer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 500);
            setResizable(true);

            panel.setBackground(Color.blue);
            BorderLayout layout = new BorderLayout();
            layout.setHgap(10);
            layout.setVgap(10);
            panel.setLayout(layout);

            MandelbrotImageDisplay mandelbrotImageDisplay = new MandelbrotImageDisplay();
            panel.add(mandelbrotImageDisplay, BorderLayout.CENTER);

//            panel.add(new GuiControlsRight(), BorderLayout.EAST);

            add(panel);
            setVisible(true);
        }
    }


    class MandelbrotImageDisplay extends JPanel implements MouseWheelListener {
        MandelbrotImage mandelbrotImage = new MandelbrotImage(width, height, center, zoom, maxIterations);
        final int dstx1 = 0;
        final int dsty1 = 0;
        final int dstx2 = width;
        final int dsty2 = height;
        int srcx1 = 0;
        int srcy1 = 0;
        int srcx2 = 100;
        int srcy2 = 100;
        double imageZoom = 1;
        Coord imageCenter;
        Image mandelbrotImage;


        public MandelbrotImageDisplay() {
            super();
            this.addMouseWheelListener(this);
            setSize(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(mandelbrotImage, dstx1, dsty1, dstx2, dsty2, srcx1, srcy1, srcx2, srcy2, null);
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            double scrollMovement = e.getPreciseWheelRotation();
            if (scrollMovement > 0) {
                zoom *= 1.01;
            } else {
                zoom *= .99;
            }
            repaint();
        }

        private void upadateImage() {
            mandelbrotImage = mandelbrotImagecreator.createMandelbrotImage(width * 2, height * 2, center, zoom / 2, maxIterations);
            imageCenter = new Coord(center.x, center.y);
            imageZoom = 0.5;
        }
    }

//    class GuiControlsRight extends JPanel {
//        public GuiControlsRight() {
//            JTextField maxIterations = new JTextField("1010");
//            maxIterations.getDocument().addDocumentListener(new DocumentListener() {
//                @Override
//                public void insertUpdate(DocumentEvent e) {
//                    System.out.println("ho insert");
//                }
//
//                @Override
//                public void removeUpdate(DocumentEvent e) {
//                    System.out.println("ho remove");
//
//                }
//
//                @Override
//                public void changedUpdate(DocumentEvent e) {
//                    System.out.println("ho change");
//
//                }
//            });
//
//            add(maxIterations);
//            add(new JButton("hohoho"));
//            add(new JButton("hahaha"));
//        }
//    }
}
