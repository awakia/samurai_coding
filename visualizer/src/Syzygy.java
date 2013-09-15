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
 * Describes a syzygy swap.
 * @author Takashi Chikayama
 */
class Syzygy {
    /** Hexels on which swapped agents are */
    HexPos swapped[];

    /**
     * Creates a syzygy structure reading data from an input stream.
     * @param is The input stream to read the data.
     * @throws Exception 
     */
    Syzygy(LogStream is) throws Exception {
        int size = is.nextInt();
        swapped = new HexPos[size];
        for (int k = 0; k != size; k++) swapped[k] = new HexPos(is);
    }
}
