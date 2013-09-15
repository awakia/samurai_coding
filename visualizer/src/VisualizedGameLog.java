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
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * Game field state visualizer
 * @author Takashi Chikayama
 */
public final class VisualizedGameLog extends GameLog {

    /**
     * Constructor
     * @param is Input stream for the game log
     */
    VisualizedGameLog(LogStream is, RankingEntry teamRankings[],
            int rno, int gno) throws Exception {
        super(is, teamRankings, rno, gno);
    }
    
    final static Color hexColors[] = {
        new Color(0xC08080),
        new Color(0x80C080),
        new Color(0x80A0C0),
        new Color(0xC0C080),
        new Color(0xA0A0A0)
    };
    final static Color transColors[] = {
        new Color(0x800000),
        new Color(0x008000),
        new Color(0x004080),
        new Color(0x808000)
    };
    final static Color agentColors[] = {
        new Color(0xF00000),
        new Color(0x00F000),
        new Color(0x0080F0),
        new Color(0xF0F000),
        new Color(0xA0A0A0)
    };

    final static Color frozenColors[] = {
        new Color(0x800000),
        new Color(0x008000),
        new Color(0x004080),
        new Color(0x808000)
    };
    final static Color arrowColor = Color.LIGHT_GRAY;
    final static Color bgColor = new Color(0x204020);
    final static Color finishedColor = new Color(0x408080);
    final static Color syzygyColor = new Color(0x80FFFFFF, true);
    /**
     * Drawing game field state
     */    
    static ImagePattern hexImages[] = new ImagePattern[5];
    static ImagePattern siegeImages[] = new ImagePattern[4];
    static ImagePattern transImages[] = new ImagePattern[4];
    static ImagePattern samuraiImages[] = new ImagePattern[4];
    static ImagePattern frozenSamurai[] = new ImagePattern[4];
    static ImagePattern dogImages[] = new ImagePattern[4];
    static ImagePattern frozenDogs[] = new ImagePattern[4];
    static ImagePattern menaceImages[] = new ImagePattern[4];
    static ImagePattern upImage, downImage, leftImage, rightImage;
    static ImagePattern syzygyImage;
    static int centerX[][];
    static int centerY[][];
    static AffineTransform centering[][];
    static Font scoreFont = new Font("Arial", Font.BOLD, 18);

    /**
     * Makes a image of score bar
     * @param w image width
     * @param h image height
     * @param turn the turn number of the game to visuzlie
     * @return Visualized image of scores
     */
    BufferedImage scoreImage(int w, int h, int turn) {
        GameFieldState state = states[turn];
        BufferedImage img =
                new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = img.createGraphics();
        g.setColor(agentColors[4]); // Non-territory
        g.fillRect(0, 0, w, h);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int x = 0;
        for (int t = 0; t != 5; t++) {
            int ww = (int) ((float) w * state.area[t] / state.fieldSize);
            if (t != 4) {
                g.setColor(agentColors[t]);
                g.fillRect(x, 0, ww, h);
            }
            String score = String.valueOf(state.area[t]);
            g.setColor(Color.BLACK);
            GlyphVector scoreVect = scoreFont.createGlyphVector(g.getFontRenderContext(), score);
            Shape scoreShape = 
                    scoreVect.getOutline(0f, (float) -scoreVect.getVisualBounds().getY());
            Rectangle bb = scoreShape.getBounds();
            Graphics2D gg = (Graphics2D) g.create(x + ww / 2 - bb.width / 2, h / 2 - bb.height / 2, 1000, 1000);
            gg.fill(scoreShape);
            x += ww;
        }
        g.dispose();
        return img;
    }

    private static class ImagePattern {
        BufferedImage img;
        int xoff, yoff;
        void draw(Graphics2D g, HexPos pos) {
            Graphics2D gg = (Graphics2D) g.create();
            gg.transform(centering[pos.x][pos.y]);
            gg.drawImage(img, null, xoff, yoff);
            gg.dispose();
        }
    }
    
    private static class OverlayPattern extends ImagePattern {
        OverlayPattern(Shape s, Color c) {
            super();
            Rectangle bb = s.getBounds();
            img = new BufferedImage(bb.width +4, bb.height + 4,
                    BufferedImage.TYPE_4BYTE_ABGR);
            xoff = bb.x - 2;
            yoff = bb.y - 2;
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(-xoff, -yoff);
            g.setColor(c);
            g.fill(s);
            g.dispose();
        }
    }
    
    private static class OffsetImage extends ImagePattern{
        OffsetImage(Shape s, Color c, boolean fill, boolean sunken) {
            super();
            Rectangle bb = s.getBounds();
            img = new BufferedImage(bb.width + 4, bb.height + 4,
                    BufferedImage.TYPE_4BYTE_ABGR);
            xoff = bb.x - 2;
            yoff = bb.y - 2;
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(-xoff, -yoff);
            Color upper = (sunken ? Color.BLACK : Color.WHITE);
            Color lower = (!sunken ? Color.BLACK : Color.WHITE);
            g.translate(-1, -1);
            g.setColor(upper);
            g.draw(s);
            g.translate(2, 2);
            g.setColor(lower);
            g.draw(s);
            g.translate(-1, -1);
            g.setColor(c);
            if (fill) {
                g.fill(s);
            } else {
                g.draw(s);
            }
            g.dispose();
        }
    }
    static int prevXu = -1; // Size units of previous setting
    static int prevYu = -1;
    static GameFieldState prevState;
    static BufferedImage prevImage;
    static int xu, yu;         // Unit lengths

    private static boolean prepareImages(int w, int h, GameFieldState state) {
        xu = (w - 2) / 2 / state.width;
        yu = (h - 2) / (3 * state.height + 1);
        if (xu == prevXu && yu == prevYu) return false;
        prevXu = xu;
        prevYu = yu;
        Shape hexShape = new Polygon(
                new int[]{0, xu - 1, xu - 1, 0, -xu + 1, -xu + 1},
                new int[]{-2 * yu + 1, -yu + 1, yu - 1, 2 * yu - 1, yu - 1, -yu + 1}, 6);
        Shape samuraiShape =
                new Ellipse2D.Float(-xu / 2, -yu, xu, 2 * yu);
        Shape dogShape = new Polygon(
                new int[]{0, xu / 4, 2 * xu / 3, xu / 4, 0, -xu / 4, -2 * xu / 3, -xu / 4},
                new int[]{-yu, -yu / 3, 0, yu / 3, yu, yu / 3, 0, -yu / 3}, 8);
        Shape menaceShape =
                new Ellipse2D.Float(-2 * xu, -3 * yu, 4 * xu, 6 * yu);
        Shape swappedShape =
                new Ellipse2D.Float(-2 * xu, -3 * yu, 4 * xu, 6 * yu);
        for (int t = 0; t != 4; t++) {
            hexImages[t] = new OffsetImage(hexShape, hexColors[t], true, false);
            siegeImages[t] = new OffsetImage(hexShape, agentColors[t], true, false);
            transImages[t] = new OffsetImage(hexShape, transColors[t], false, true);
            samuraiImages[t] = new OffsetImage(samuraiShape, agentColors[t], true, false);
            frozenSamurai[t] = new OffsetImage(samuraiShape, frozenColors[t], true, false);
            dogImages[t] = new OffsetImage(dogShape, agentColors[t], true, false);
            frozenDogs[t] = new OffsetImage(dogShape, frozenColors[t], true, false);
            menaceImages[t] = new OffsetImage(menaceShape, agentColors[t], false, false);
        }
        syzygyImage = new OverlayPattern(swappedShape, syzygyColor);
        hexImages[4] = new OffsetImage(hexShape, hexColors[4], true, false);
        upImage = new OffsetImage(
                new Polygon(
                new int[]{0, xu / 2, xu / 4, xu / 4, -xu / 4, -xu / 4, -xu / 2},
                new int[]{-yu, 0, 0, yu, yu, 0, 0}, 7),
                arrowColor, true, true);
        downImage = new OffsetImage(
                new Polygon(
                new int[]{0, xu / 2, xu / 4, xu / 4, -xu / 4, -xu / 4, -xu / 2},
                new int[]{yu, 0, 0, -yu, -yu, 0, 0}, 7),
                arrowColor, true, true);
        leftImage = new OffsetImage(
                new Polygon(
                new int[]{-2 * xu / 3, 0, 0, 2 * xu / 3, 2 * xu / 3, 0, 0},
                new int[]{0, -yu, -yu / 2, -yu / 2, yu / 2, yu / 2, yu}, 7),
                arrowColor, true, true);
        rightImage = new OffsetImage(
                new Polygon(
                new int[]{2 * xu / 3, 0, 0, -2 * xu / 3, -2 * xu / 3, 0, 0},
                new int[]{0, -yu, -yu / 2, -yu / 2, yu / 2, yu / 2, yu}, 7),
                arrowColor, true, true);
        centerX = new int[w][h];
        centerY = new int[w][h];
        centering = new AffineTransform[w][h];
        int xoff = (w - 2 * state.width * xu - 2) / 2;
        int yoff = (h - (3 * state.height + 2) * yu - 2) / 2;
        for (int y = 0; y != h; y++) {
            int yy = (3 * y + 2) * yu;
            for (int x = 0; x != w; x++) {
                int xx = (2 * x + 1) * xu;
                if ((y & 1) == 0) {
                    xx += xu;
                }
                centerX[x][y] = xx + xoff;
                centerY[x][y] = yy + yoff;
                centering[x][y] = new AffineTransform();
                centering[x][y].translate(xx + xoff, yy + yoff);
            }
        }
        return true;
    }

    private void drawSiege(BufferedImage image, Siege conq) {
        Graphics2D g = image.createGraphics();
        for (HexPos h: conq.sieged) siegeImages[conq.team].draw(g, h);
        g.dispose();
    }
    
    private void drawSyzygy(BufferedImage image, Syzygy syz) {
        Graphics2D g = image.createGraphics();
        for (HexPos h: syz.swapped) syzygyImage.draw(g, h);
        g.setColor(syzygyColor);
        g.setStroke(new BasicStroke(xu/2.0f));
        int last = syz.swapped.length-1;
        int x1 = syz.swapped[0].x;
        int y1 = syz.swapped[0].y;
        int x2 = syz.swapped[last].x;
        int y2 = syz.swapped[last].y;
        g.drawLine(centerX[x1][y1], centerY[x1][y1], 
                centerX[x2][y2], centerY[x2][y2]);
        g.dispose();
    }
    
    private void drawTrans(BufferedImage image, Transcontinental trans) {
        Graphics2D g = image.createGraphics();
        for (HexPos h: trans.hexels) transImages[trans.team].draw(g, h);
        g.dispose();
    }

    /**
     * Makes visualized image of a game field state
     * @param w image width
     * @param h image height
     * @param turn turn number of the state to visualize
     * @return Visual image of the game field state
     */
    public BufferedImage fieldImage(int w, int h, int turn) {
        BufferedImage image;
        GameFieldState state = states[turn];
        boolean sizeChanged = prepareImages(w, h, state);
        if (state == prevState && prevImage != null && !sizeChanged) {
            image = prevImage;
        } else {
            image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor((turn  != numTurns ? bgColor : finishedColor));
            g.fillRect(0, 0, w, h);
            /*
             * Hexels
             */
            for (int y = 0; y != state.height; y++) {
                int right = ((y & 1) == 0 ? state.width - 1 : state.width);
                for (int x = 0; x != right; x++) {
                    HexPos p = new HexPos(x, y);
                    hexImages[state.ownerOf(p)].draw(g, p);
                }
            }
            for (int k = 0; k != state.gates.length; k++) {
                HexPos pos = state.gates[k];
                ImagePattern arrow;
                if (pos.y == 0) {
                    arrow = upImage;
                } else if (pos.y == state.height - 1) {
                    arrow = downImage;
                } else if (pos.x == 0) {
                    arrow = leftImage;
                } else {
                    arrow = rightImage;
                }
                arrow.draw(g, pos);
            }
            /*
             * Syzygy
             */
            for (Syzygy s: state.syzygies) drawSyzygy(image, s);
            /*
             * Transcontinental
             */
            for (Transcontinental t: state.trans) drawTrans(image, t);
            /*
             * Sieges
             */
            for (Siege s: state.sieges) drawSiege(image, s);
             /*
             * Agents
             */
            for (int t = 0; t != 4; t++) {
                for (int a = 0; a != 4; a++) {
                    HexPos pos = state.agentPos[t][a];
                    if (a == 0) {
                        if (!state.frozen[t][a]) {
                            dogImages[t].draw(g, pos);
                        } else {
                            frozenDogs[t].draw(g, pos);
                        }
                        menaceImages[t].draw(g, pos);
                    } else if (!state.frozen[t][a]) {
                        samuraiImages[t].draw(g, pos);
                    } else {
                        frozenSamurai[t].draw(g, pos);
                    }
                }
            }
            prevState = state;
            prevImage = image;
            g.dispose();
        }
        return image;
    }
}
