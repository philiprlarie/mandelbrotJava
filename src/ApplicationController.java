import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;

/**
 * Created by Philip on 12/26/16.
 */
public class ApplicationController {
    int width = 200;
    int height = 200;
    Coord center = new Coord(0, 0);
    double zoom = 60;
    int maxIterations = 100;
    MandelbrotImageFilter mandelbrotImageFilter = MandelbrotImageFilter.BLACK_WHITE;

    public ApplicationController() {
        new AppViewer();
    }

    class AppViewer extends JFrame {
        JPanel panel = new JPanel();
        public AppViewer() {
            super("Madelbrot Viewer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(true);
            setSize(400, 500);

            panel.setBackground(Color.blue);
            BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
            panel.setLayout(layout);

            JTextArea textArea = new JTextArea("Scroll over image to zoom in and out. For more or less granularity, enter an int in \"Max Iterations\" (note, increasing max iterations will increase computational complexity and may cause noticeable performance delays). Try different filters. Generate a PNG image file by clicking \"Generate Image\".");
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JPanel imageHolder = new JPanel();
            imageHolder.setLayout(new BoxLayout(imageHolder, BoxLayout.PAGE_AXIS));
            MandelbrotImageDisplay mandelbrotImageDisplay = new MandelbrotImageDisplay();
            imageHolder.add(mandelbrotImageDisplay);
            imageHolder.setMaximumSize(new Dimension(width, height));
            imageHolder.setPreferredSize(new Dimension(width, height));


            JPanel guiControlsRight = new GuiControlsRight(mandelbrotImageDisplay);
            guiControlsRight.setLayout(new BoxLayout(guiControlsRight, BoxLayout.PAGE_AXIS));

            panel.add(textArea);
            panel.add(imageHolder, BorderLayout.CENTER);
            panel.add(guiControlsRight, BorderLayout.SOUTH);
            add(panel);

            setVisible(true);
        }
    }

    class MandelbrotImageDisplay extends JPanel implements MouseWheelListener {
        MandelbrotImage mandelbrotImage = new MandelbrotImage(width, height, center, zoom, maxIterations);
        boolean currentlyGeneratingImage = false;

        public MandelbrotImageDisplay() {
            super();
            this.addMouseWheelListener(this);
            setSize(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (newImageNeeded() && !currentlyGeneratingImage) {
                new WorkerImageCreator().execute();
            }
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

            final int dstx1 = 0;
            final int dsty1 = 0;
            final int dstx2 = width;
            final int dsty2 = height;
            int srcx1 = (int) Math.ceil((x - x0) * z0 + w0 / 2 - w / 2 * z0 / z);
            int srcy1 = (int) Math.ceil((y - y0) * z0 + h0 / 2 - h / 2 * z0 / z);
            int srcx2 = (int) Math.floor((x - x0) * z0 + w0 / 2 + w / 2 * z0 / z);
            int srcy2 = (int) Math.floor((y - y0) * z0 + h0 / 2 + h / 2 * z0 / z);

            g.drawImage(mandelbrotImage.getImage(), dstx1, dsty1, dstx2, dsty2, srcx1, srcy1, srcx2, srcy2, null);
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            double scrollMovement = e.getPreciseWheelRotation();
            int scrollPosX = e.getX(); // pixel position of scroll
            int scrollPosY = e.getY();
            int w = width;
            int h = height;
            // double zoom = zoom;
            double x0 = center.x;
            double y0 = center.y;
            double x = ((double) scrollPosX - (double) w / 2) / zoom + x0; // cartesian position of scroll
            double y = ((double) scrollPosY - (double) h / 2) / zoom + y0; // cartesian position of scroll

            double zoomRatio; // (oldZoom / newZoom)
            if (scrollMovement > 0) {
                zoomRatio = 0.99;
                System.out.println("zooming out");
            } else {
                zoomRatio = 1.01;
                System.out.println("zooming in");
            }
            double newCenterX = x0 / zoomRatio + x * (zoomRatio - 1) / zoomRatio;
            double newCenterY = y0 / zoomRatio + y * (zoomRatio - 1) / zoomRatio;

            center = new Coord(newCenterX, newCenterY);
            zoom = zoom * zoomRatio;
            repaint();
        }

        private boolean newImageNeeded() {
            // user changes some parameters
            if (mandelbrotImage.getMaxIterations() != maxIterations) {
                return true;
            }
            String filterName = mandelbrotImage.getClass().getSimpleName();
            if (filterName.equals("MandelbrotImage") && mandelbrotImageFilter != MandelbrotImageFilter.BLACK_WHITE) {
                return true;
            } else if (filterName.equals("MandelbrotImageOrangeBlack") && mandelbrotImageFilter != MandelbrotImageFilter.ORANGE_BLACK) {
                return true;
            } else if (filterName.equals("MandelbrotImageColorBands") && mandelbrotImageFilter != MandelbrotImageFilter.COLOR_BANDS) {
                return true;
            }

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
            if (x - (double) w / (2 * z) < x0 - (double) w0 / (2 * z0)) { // trying to view too far left
                return true;
            } else if (x + (double) w / (2 * z) > x0 + (double) w0 / (2 * z0)) { // too far right
                return true;
            } else if (y - (double) h / (2 * z) < y0 - (double) h0 / (2 * z0)) { // too far down
                return true;
            } else if (y + (double) h / (2 * z) > y0 + (double) h0 / (2 * z0)) { // too far up
                return true;
            }
            return false;
        }

        class WorkerImageCreator extends SwingWorker<MandelbrotImage, Void> {
            @Override
            public MandelbrotImage doInBackground() {
                currentlyGeneratingImage = true;
                double ratio = Math.sqrt(Math.E); // this ratio minimizes calculations amortized over zoom
                int newWidth = (int) Math.ceil((double) width * ratio * ratio);
                int newHeight = (int) Math.ceil((double) height * ratio * ratio);

                System.out.printf("Generating new mandelbrot image with parameters: width = %d; height = %d; center = (%f, %f); zoom = %f; maxIterations = %d\n", newWidth, newHeight, center.x, center.y, zoom * ratio, maxIterations);
                long startTime = System.nanoTime();
                MandelbrotImage mandelbrotImage;
                if (mandelbrotImageFilter == MandelbrotImageFilter.ORANGE_BLACK) {
                    mandelbrotImage = new MandelbrotImageOrangeBlack(newWidth, newHeight, center, zoom * ratio, maxIterations);
                } else if (mandelbrotImageFilter == MandelbrotImageFilter.COLOR_BANDS) {
                    mandelbrotImage = new MandelbrotImageColorBands(newWidth, newHeight, center, zoom * ratio, maxIterations);
                } else {
                    mandelbrotImage = new MandelbrotImage(newWidth, newHeight, center, zoom * ratio, maxIterations);
                }

                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
                System.out.printf("Image generated after %d milliseconds.\n", duration);

                return mandelbrotImage;
            }

            @Override
            public void done() {
                currentlyGeneratingImage = false;
                try {
                    mandelbrotImage = get();
                    MandelbrotImageDisplay.this.repaint();
                } catch (InterruptedException ignore) {
                } catch (java.util.concurrent.ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GuiControlsRight extends JPanel {
        MandelbrotImageDisplay mandelbrotImageDisplay;
        public GuiControlsRight(MandelbrotImageDisplay mandelbrotImageDisplay) {
            this.mandelbrotImageDisplay = mandelbrotImageDisplay;

            JLabel myLabel = new JLabel("Max Iterations:");
            JTextField maxIterationsTextBox = new JTextField(8);
            maxIterationsTextBox.setText("" + maxIterations);
            JPanel maxIterationsTextBoxHolder = new JPanel();
            maxIterationsTextBoxHolder.add(myLabel);
            maxIterationsTextBoxHolder.add(maxIterationsTextBox);
            maxIterationsTextBox.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    maxIterations = Integer.parseInt(maxIterationsTextBox.getText());
                    mandelbrotImageDisplay.repaint();
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    maxIterations = Integer.parseInt(maxIterationsTextBox.getText());
                    mandelbrotImageDisplay.repaint();
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    maxIterations = Integer.parseInt(maxIterationsTextBox.getText());
                    mandelbrotImageDisplay.repaint();
                }
            });

            JButton blackAndWhiteBtn = new JButton("Black and White");
            blackAndWhiteBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    mandelbrotImageFilter = MandelbrotImageFilter.BLACK_WHITE;
                    mandelbrotImageDisplay.repaint();
                }
            });
            JButton orangeAndBlackBtn = new JButton("Orange and Black");
            orangeAndBlackBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    mandelbrotImageFilter = MandelbrotImageFilter.ORANGE_BLACK;
                    mandelbrotImageDisplay.repaint();
                }
            });
            JButton colorBandBtn = new JButton("Color Bands");
            colorBandBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    mandelbrotImageFilter = MandelbrotImageFilter.COLOR_BANDS;
                    mandelbrotImageDisplay.repaint();
                }
            });

            JButton generateImgBtn = new JButton("Generate Image");
            generateImgBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    System.out.printf("Saving mandelbrot image with parameters: center = (%f, %f); zoom = %f; maxIterations = %d\n", center.x, center.y, zoom, maxIterations);
                    long startTime = System.nanoTime();
                    MandelbrotImage mandelbrotImage;
                    if (mandelbrotImageFilter == MandelbrotImageFilter.ORANGE_BLACK) {
                        mandelbrotImage = new MandelbrotImageOrangeBlack(width, height, center, zoom, maxIterations);
                    } else if (mandelbrotImageFilter == MandelbrotImageFilter.COLOR_BANDS) {
                        mandelbrotImage = new MandelbrotImageColorBands(width, height, center, zoom, maxIterations);
                    } else {
                        mandelbrotImage = new MandelbrotImage(width, height, center, zoom, maxIterations);
                    }
                    mandelbrotImage.saveImage();
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
                    System.out.printf("Image generated after %d milliseconds.\n", duration);
                }
            });

            add(maxIterationsTextBoxHolder);
            add(blackAndWhiteBtn);
            add(orangeAndBlackBtn);
            add(colorBandBtn);
            add(generateImgBtn);
        }
    }
}

enum MandelbrotImageFilter {
    BLACK_WHITE, ORANGE_BLACK, COLOR_BANDS
}