
import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class SingleColorBox extends JComponent {

    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    static final float COLOR_OFFSET = .02f;
    static final int UNIT_SIZE = 10;
    static final float BITE_SIZE = 256;
    static final int ONE = 1;

    static final Color firstColor = Color.magenta;
    static final Color secondColor = Color.cyan;
    static final String[] COLOR_ELEMENTS = new String[]{"red", "green", "blue"};

    static final float[] DMC = new float[3];

    private Color curColor;
    private static JFrame window;

    private byte[] direction = new byte[]{1, 1, 1};

    private static final boolean verbose = true;

    {
        curColor = firstColor;
        int dx = firstColor.getRed() - secondColor.getRed();
        int dy = firstColor.getGreen() - secondColor.getGreen();
        int dz = firstColor.getBlue() - secondColor.getBlue();
        double deltaColorMagnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
        DMC[0] = (float) (dx / deltaColorMagnitude) * COLOR_OFFSET;
        DMC[1] = (float) (dy / deltaColorMagnitude) * COLOR_OFFSET;
        DMC[2] = (float) (dz / deltaColorMagnitude) * COLOR_OFFSET;
        System.out.println("Color vector    : ");
        for (int i = 0; i < 3; i++)
            System.out.println("\tdelta " + COLOR_ELEMENTS[i] + " : " + DMC[0]);
        System.out.println();
    }

    public static void main(String[] a) {
        window = new JFrame();
        window.setSize(WIDTH, HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(new SingleColorBox());
        window.setVisible(true);

        Thread paintThread = new Thread(() -> {
            synchronized (window) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        while (true) {
                            window.wait(50);
                            window.paint(window.getGraphics());
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        paintThread.start();
    }

    public void paint(Graphics g) {
        g.setColor(curColor);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());
        curColor = iterateColor(curColor);
//        g.setColor(Color.black);
//        for (int i = UNIT_SIZE; i < 82 * UNIT_SIZE; i += UNIT_SIZE)
//            g.drawLine(i, UNIT_SIZE, i, 51 * UNIT_SIZE);
//
//        for (int i = UNIT_SIZE; i < 52 * UNIT_SIZE; i += UNIT_SIZE)
//            g.drawLine(UNIT_SIZE, i, 81 * UNIT_SIZE, i);
    }

    private Color iterateColor(Color color) {
        float[] newColor = new float[3];
        newColor[0] = (color.getRed() / BITE_SIZE + direction[0] * DMC[0]);
        newColor[1] = (color.getGreen() / BITE_SIZE + direction[1] * DMC[1]);
        newColor[2] = (color.getBlue() / BITE_SIZE + direction[2] * DMC[2]);
        for (int i = 0; i < DMC.length; i++) {
            if (newColor[i] < 0) {
                direction[i] *= -1;
                do {
                    newColor[i] += direction[i] * DMC[i];
                } while (newColor[i] < 0);
            }
            if (newColor[i] > 1) {
                direction[i] *= -1;
                do {
                    newColor[i] += direction[i] * DMC[i];
                } while (newColor[i] > 1);
            }

            if (verbose) {
                System.out.println("\t" + COLOR_ELEMENTS[i] + " : " + newColor[i]);
            }
        }
        return new Color(newColor[0], newColor[1], newColor[2]);
    }
}
