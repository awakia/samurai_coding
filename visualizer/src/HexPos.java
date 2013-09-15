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

/**
 * Position of a hexel.
 * @author Takashi Chikayama
 */
class HexPos {
    /**
     * Coordinates of the hexel.
     * Note that for odd y, hexels with the same x-coordinate are shifted
     * towards right by half a hexel width.
     */
    int x, y;
    /** Makes a structure with given parameters */
    HexPos(int x, int y) {
        this.x = x; this.y = y;
    }
    /** Makes a structure by reading data from an input stream */
    HexPos(LogStream is) throws Exception {
        x = is.nextInt();
        y = is.nextInt();
    }
}
