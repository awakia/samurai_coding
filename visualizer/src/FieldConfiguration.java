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
 * Configuration of a game field.
 * @author Takashi Chikayama
 */
class FieldConfiguration {
    /** Width of the field */
    int width;
    /** Height of the field */
    int height;
    /** Positions of gates */
    HexPos gates[];
    /** Number of hexels in the field */
    int fieldSize;

    /**
     * Makes a structure represening a game field configuration
     * by readin data from an input stream.
     * @param is The stream to read from.
     * @throws Exception 
     */
    public FieldConfiguration(LogStream is) throws Exception {
        width = is.nextInt();
        height = is.nextInt();
        fieldSize = width * height - height/2 - 1;
        int nGates = is.nextInt();
        gates = new HexPos[nGates];
        for (int k = 0; k != nGates; k++) gates[k] = new HexPos(is);
    }

    /**
     * A copy constructor.
     * @param prev The object to copy from.
     */
    public FieldConfiguration(FieldConfiguration prev) {
        width = prev.width;
        height = prev.height;
        gates = prev.gates;
        fieldSize = prev.fieldSize;
    }
}
