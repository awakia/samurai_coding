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
 * Describes a siege
 * @author Takashi Chikayama
 */
class Siege {
    /** Team number of the team that achieved the siege */
    int team;
    /** Set of hexcels obtained by the siege */
    HexPos sieged[];
    /**
     * Makes a siege structure reading log data from an input stream.
     * @param is input stream to read in a part of game log
     * @throws Exception 
     */
    Siege(LogStream is) throws Exception {
        team = is.nextInt();
        int numSieged = is.nextInt();
        sieged = new HexPos[numSieged];
        for (int c = 0; c != numSieged; c++) {
            sieged[c] = new HexPos(is);
        }
    }
}
