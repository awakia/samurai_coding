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
 * Describes conquest of a hexel.
 * @author Takashi Chikayama
 */
class Conquest {
    /** The position of the conquered hexel */
    HexPos at;
    /** The team that conquered the hexel */
    byte team;
    /**
     * Makes a structure representing a conquest reading data from
     * an input stream.
     * @param is The input stream to read from
     * @throws Exception 
     */
    Conquest(LogStream is) throws Exception {
        at = new HexPos(is);
        team = (byte)is.nextInt();
    }
}
