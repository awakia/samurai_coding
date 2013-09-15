/**
 * This file is a part of software for SamurAI 2013 game visualizer.
 *
 * Takashi Chikayama disclaims to the extent authorized by law any and
 * all warranties, whether express or implied, including, without
 * limitation, any implied warranties of merchantability or fitness for a
 * particular purpose
 * 
 * You assume responsibility for selecting the software to achieve your
 * intended results, and for the results obtained from your use of the
 * software. You shall bear the entire risk as to the quality and the
 * performance of the software.
 */

package samurai2013;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * A procedural class to create appropriate icons.
 * @author Takashi Chikayama
 */
public class IconBuilder {
    private final static Shape circle =
            new Ellipse2D.Float(-500, -500, 1000, 1000);
    private final static Shape forwardShape[] = {
        new Polygon(
            new int[]{-200, -100, -100, -200}, 
            new int[] {-220, -220, 220, 220}, 4),
        new Polygon(
            new int[]{ 0, 300, 0 }, new int[] { -250, 0, 250 }, 3)
    };
    private final static Shape backwardShape[] = {
        new Polygon(
            new int[]{200, 100, 100, 200}, 
            new int[] {-220, -220, 220, 220}, 4),
        new Polygon(
            new int[]{ 0, -300, 0 }, new int[] { -250, 0, 250 }, 3)
    };
    private final static Shape ffShape[] =
    { new Polygon(
            new int[]{ -200, 0, 0, 300, 0, 0, -200},
            new int[]{ -250, -100, -250, 0, 250, 100, 250}, 7)};
    private final static Shape rewindShape[] = 
    { new Polygon(            
            new int[]{  200, 0, 0, -300, 0, 0, 200},
            new int[]{ -250, -100, -250, 0, 250, 100, 250}, 7)};
    private final static Shape stopShape[] =
    { new Polygon(
            new int[]{ -200, 200, 200, -200 },
            new int[] { -200, -200, 200, 200 }, 4) };
    private final static Shape pauseShape[] = 
    { new Polygon(
            new int[]{ -200, -50, -50, -200 },
            new int[]{ -200, -200, 200, 200 }, 4),
        new Polygon(
            new int[]{ 200, 50, 50, 200 },
            new int[]{ -200, -200, 200, 200 }, 4) };
    private final static AffineTransform rotate = new AffineTransform();
    
    static {
        rotate.rotate(Math.PI);
    }
    
    private static Graphics2D tunedGraphics(BufferedImage img, Dimension d) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint
                (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform trans = new AffineTransform();
        trans.translate(d.width/2.0, d.height/2.0);
        trans.scale((d.width-2)/1000.0, (d.height-2)/1000.0);
        g.transform(trans);
        return g;
    }

    /**
     * Makes an image of given shape, dimension, and color.
     * @param d Dimension of the image
     * @param s Shape of the image
     * @param c Color of the image
     * @return An image object as specified
     */
    private static BufferedImage renderImage(Dimension d, Shape[] s, Color c) {
        BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = tunedGraphics(img, d);
        float lineWidth = (float) (1000.0/d.width);
        g.setStroke(new BasicStroke(lineWidth));
        Graphics2D gg = (Graphics2D)g.create();
        gg.translate(-lineWidth, -lineWidth);
        gg.setColor(Color.WHITE);
        gg.draw(circle);
        g.setColor(c);
        g.fill(circle);
        gg.setColor(Color.BLACK);
        for (int i = 0; i != s.length; i++) {
            gg.draw(s[i]);
        }
        gg.dispose();
        g.setColor(Color.WHITE);
        for (int i = 0; i != s.length; i++) {
            g.fill(s[i]);  
        }
        g.dispose();
        return img;
    }
    
    /** Returns a "forward" icon with the specified dimmension and color */
    static ImageIcon forward(Dimension d, Color c) {
        return new ImageIcon(renderImage(d, forwardShape, c));
    }

    /** Returns a "backward" icon with the specified dimmension and color */
    static ImageIcon backward(Dimension d, Color c) {
        return new ImageIcon(renderImage(d, backwardShape, c));
    }

    /** Returns a "fast-forward" icon with the specified dimmension and color */
    static ImageIcon fastForward(Dimension d, Color c) {
        return new ImageIcon(renderImage(d, ffShape, c));
    }
    
    /** Returns a "rewind" icon with the specified dimmension and color */
    static ImageIcon rewind(Dimension d, Color c) {
        return new ImageIcon(renderImage(d, rewindShape, c));
    }
    
    /** Returns a "stop" icon with the specified dimmension and color */
    static ImageIcon stop(Dimension d, Color c) {
        return new ImageIcon(renderImage(d, stopShape, c));
    }

    /** Returns a "pause" icon with the specified dimmension and color */
    static ImageIcon pause(Dimension d, Color c) {
        return new ImageIcon(renderImage(d, pauseShape, c));
    }

    /** Returns a hexagonal icon with the specified height and color */
    static ImageIcon hex(int h, Color c) {
        Shape hex = new Polygon(
                new int[]{0, 2*h - 1, 2*h - 1, 0, -2*h + 1, -2*h + 1},
                new int[]{-2 * h + 1, -h + 1, h - 1, 2 * h - 1, h - 1, -h + 1}, 6);
        BufferedImage img = new BufferedImage(4*h, 4*h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.translate(2*h, 2*h);
        g.setColor(c);
        g.fill(hex);
        return new ImageIcon(img);
    }
}
