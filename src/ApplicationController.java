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

        public MandelbrotImageDisplay() {
            super();
            this.addMouseWheelListener(this);
            setSize(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(newImageNeeded()) {
                generateNewImage();
            }

            g.drawImage(mandelbrotImage.getImage(), dstx1, dsty1, dstx2, dsty2, 0, 0, mandelbrotImage.getWidth(), mandelbrotImage.getHeight(), null);
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

        private boolean newImageNeeded() {
            int w = width;
            int h = width;
            double x = center.x;
            double y = center.y;
            double z = zoom;
            int w0 = mandelbrotImage.getWidth();
            int h0 = mandelbrotImage.getHeight();
            double x0 = mandelbrotImage.getCenter().x;
            double y0 = mandelbrotImage.getCenter().y;
            double z0 = mandelbrotImage.getZoom();

            // viewport zoom exceeds given image zoom
            if (z > z0) {
                return true;
            }

            // viewport is trying to display outside image bounds
            if (x - (double)w / (2 * z) < x0 - (double)w0 / (2 * z0)) { // trying to view too far left
                return true;
            } else if (x + (double)w / (2 * z) < x0 + (double)w0 / (2 * z0)) { // too far right
                return true;
            } else if (y - (double)h / (2 * z) < y0 - (double)h0 / (2 * z0)) { // too far down
                return true;
            } else if (y + (double)h / (2 * z) < y0 + (double)h0 / (2 * z0)) { // too far up
                return true;
            }

            return false;
        }

        private void generateNewImage() {
            double ratio = Math.sqrt(Math.E); // this ratio minimizes calculations amortized over zoom
            int newWidth = (int) Math.ceil((double)width * ratio * ratio);
            int newHeight = (int) Math.ceil((double)height * ratio * ratio);

            System.out.printf("Generating new mandelbrot image with parameters: width = %d; height = %d; center = (%f, %f); zoom = %f; maxIterations = %d\n", newWidth, newHeight, center.x, center.y, zoom * ratio, maxIterations);
            long startTime = System.nanoTime();
            mandelbrotImage = new MandelbrotImage(newWidth, newHeight, center, zoom * ratio, maxIterations);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
            System.out.printf("Image generated after %d milliseconds.\n", duration);
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
