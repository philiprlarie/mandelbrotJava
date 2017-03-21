import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Created by Philip on 12/26/16.
 */
public class ApplicationController {
    // create a model class and puth these fields in there
    int width = 200;
    int height = 200;
    Coord center = new Coord(0, 0);
    double zoom = 60;
    int maxIterations = 100;
    MandelbrotImageFilter mandelbrotImageFilter = MandelbrotImageFilter.BLACK_WHITE;

    public ApplicationController() {
        new AppViewer();
    }

    // this should be application view class
    class AppViewer extends JFrame {
        JPanel panel = new JPanel();
        public AppViewer() {
            super("Mandelbrot Viewer");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(true);
            setSize(400, 500);

            panel.setBackground(Color.blue);
            BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
            panel.setLayout(layout);

            JTextArea textArea = new JTextArea("Place your cursor over an area you’d like to explore and scroll up to zoom in, or down to zoom out. Increase or decrease the \"Max Iterations\" for finer or coarser granularity, respectively (note: max iterations must be a positive integer, max 3000. Increasing max iterations will increase computational time). Test out different color filters, and save your fractal as a PNG.");
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
            panel.add(imageHolder);
            panel.add(guiControlsRight);
            add(panel);

            setVisible(true);
        }
    }

    // much of this should be mandelbrot image display view class. other parts should be in the controller
    class MandelbrotImageDisplay extends JPanel implements MouseWheelListener {
        // this should live in the model
        MandelbrotImage mandelbrotImage = new MandelbrotImage(width, height, center, zoom, maxIterations);
        // this should live in the controller
        boolean currentlyGeneratingImage = false;

        public MandelbrotImageDisplay() {
            super();
            this.addMouseWheelListener(this);
            setSize(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            // the logic for generating the parameters of painting the image should go in the controller
            // maybe all of this logic should live in the controller
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

        // pass this event to the controller
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

        // this logic should live in the controller
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
            int h = height;
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

        // this logic should live in the controller
        class WorkerImageCreator extends SwingWorker<MandelbrotImage, Void> {
            @Override
            public MandelbrotImage doInBackground() {
                currentlyGeneratingImage = true;
                double ratio = Math.sqrt(Math.sqrt(Math.E)); // this ratio minimizes calculations amortized over zoom
                int newWidth = (int) Math.ceil((double) width * ratio * ratio); // pixels are ratio times more dense so we can zoom in. image is ratio times more wide so we can zoom out.
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
            this.mandelbrotImageDisplay = mandelbrotImageDisplay; // we shouldn't need this reference. that should live in the controller

            JLabel myLabel = new JLabel("Max Iterations:");
            JTextField maxIterationsTextBox = new JTextField(8);
            maxIterationsTextBox.setText("" + maxIterations);
            JPanel maxIterationsTextBoxHolder = new JPanel();
            maxIterationsTextBoxHolder.add(myLabel);
            maxIterationsTextBoxHolder.add(maxIterationsTextBox);
            maxIterationsTextBox.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    // this event handling should be in GUI section controller
                    if (validateMaxIterationsInput(maxIterationsTextBox)) {
                        maxIterations = Integer.parseInt(maxIterationsTextBox.getText());
                        mandelbrotImageDisplay.repaint();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    // this event handling should be in GUI section controller
                    if (validateMaxIterationsInput(maxIterationsTextBox)) {
                        maxIterations = Integer.parseInt(maxIterationsTextBox.getText());
                        mandelbrotImageDisplay.repaint();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    // this event handling should be in GUI section controller
                    if (validateMaxIterationsInput(maxIterationsTextBox)) {
                        maxIterations = Integer.parseInt(maxIterationsTextBox.getText());
                        mandelbrotImageDisplay.repaint();
                    }
                }
            });

            JButton blackAndWhiteBtn = new JButton("Black and White");
            blackAndWhiteBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    // this event handling should be in GUI section controller
                    mandelbrotImageFilter = MandelbrotImageFilter.BLACK_WHITE;
                    mandelbrotImageDisplay.repaint();
                }
            });
            JButton orangeAndBlackBtn = new JButton("Orange and Black");
            orangeAndBlackBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    // this event handling should be in GUI section controller
                    mandelbrotImageFilter = MandelbrotImageFilter.ORANGE_BLACK;
                    mandelbrotImageDisplay.repaint();
                }
            });
            JButton colorBandBtn = new JButton("Color Bands");
            colorBandBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    // this event handling should be in GUI section controller
                    mandelbrotImageFilter = MandelbrotImageFilter.COLOR_BANDS;
                    mandelbrotImageDisplay.repaint();
                }
            });

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            JButton generateImgBtn = new JButton("Generate Image");
            generateImgBtn.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent e) {
                    // this event handling should be in GUI section controller. pass progress bar to controller
                    generateImgBtn.setEnabled(false);
                    new WorkerImageSaver(progressBar, generateImgBtn).execute();
                }
            });

            add(maxIterationsTextBoxHolder);
            add(blackAndWhiteBtn);
            add(orangeAndBlackBtn);
            add(colorBandBtn);
            add(generateImgBtn);
            add(progressBar);
        }

        // put this in a GUI section controller
        private boolean validateMaxIterationsInput (JTextField maxIterationsTextBox) {
            String text = maxIterationsTextBox.getText();
            int value;
            try {
                value = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                maxIterationsTextBox.setBackground(Color.PINK);
                return false;
            }
            if (value > 0 && value <= 3000) {
                maxIterationsTextBox.setBackground(Color.WHITE);
                return true;
            } else {
                maxIterationsTextBox.setBackground(Color.PINK);
                return false;
            }
        }

        // put this in a GUI section controller
        class WorkerImageSaver extends SwingWorker<MandelbrotImage, Void> {
            JProgressBar progressBar;
            JButton generateImgBtn;
            public WorkerImageSaver (JProgressBar progressBar, JButton generateImgBtn) {
                super();
                this.progressBar = progressBar;
                this.generateImgBtn = generateImgBtn;
            }

            @Override
            public MandelbrotImage doInBackground() {
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
                mandelbrotImage.saveImage(progressBar);
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
                System.out.printf("Image generated after %d milliseconds.\n", duration);

                return mandelbrotImage;
            }

            @Override
            public void done() {
                try {
                    this.progressBar.setValue(0);
                    this.generateImgBtn.setEnabled(true);
                    MandelbrotImage mandelbrotImage = get();
                } catch (InterruptedException ignore) {
                } catch (java.util.concurrent.ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

enum MandelbrotImageFilter {
    BLACK_WHITE, ORANGE_BLACK, COLOR_BANDS
}