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
 * Describes plays made in one turn of the game.
 * @author Takashi Chikayama
 */
class Play {
    /** Moves made in this turn */
    Move moves[];
    /** Agents frozen after this turn */
    Agent frozen[];
    /** Syzygies caused in this turn */
    Syzygy syzygies[];
    /** Newly conquered territories in this turn */
    Conquest conquests[];
    /** Sieges made in this turn */
    Siege sieges[];
    /** Transcontinental territories after this turn */
    Transcontinental trans[];
    /** Number of sieged hexels in this turn */
    int siegesAtTurn;
    /**
     * Makes a structure representing plays made in one turn by
     * reading the data from an input stream.
     * @param is Log data stream.
     * @throws Exception 
     */
    Play(LogStream is) throws Exception {
        int nMoves = is.nextInt();
        moves = new Move[nMoves];
        for (int k = 0; k != nMoves; k++) moves[k] = new Move(is);
        int nSyzygies = is.nextInt();
        syzygies = new Syzygy[nSyzygies];
        for (int k = 0; k != nSyzygies; k++) syzygies[k] = new Syzygy(is);
        int nConquests = is.nextInt();
        conquests = new Conquest[nConquests];
        for (int k = 0; k != nConquests; k++) conquests[k] = new Conquest(is);
        int nFrozen = is.nextInt();
        frozen = new Agent[nFrozen];
        for (int k = 0; k != nFrozen; k++) frozen[k] = new Agent(is);
        int nSieges = is.nextInt();
        sieges = new Siege[nSieges];
        siegesAtTurn = 0;
        for (int k = 0; k != nSieges; k++) {
            sieges[k] = new Siege(is);
            siegesAtTurn += sieges[k].sieged.length;
        }
        int nTrans = is.nextInt();
        trans = new Transcontinental[nTrans];
        for (int k = 0; k != nTrans; k++) trans[k] = new Transcontinental(is);
    }
}
