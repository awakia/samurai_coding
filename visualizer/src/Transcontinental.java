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
 * Desctibes a transcontinental territory.
 * @author Takashi Chikayama
 */
class Transcontinental {
    /** Owner team of the territory */
    int team;
    /** Hexel positions that constitute the territory */
    HexPos hexels[];
    /** Creates a structure reading data from an input stream. */
    public Transcontinental(LogStream is) throws Exception {
        team = is.nextInt();
        int size = is.nextInt();
        hexels = new HexPos[size];
        for (int h = 0; h != size; h++) {
            hexels[h] = new HexPos(is);
        }
    }
    
}
