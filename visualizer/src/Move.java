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
 * Move of an agent.
 * @author Takashi Chikayama
 */
class Move {
    /** The hexel the agent is orignally on */
    HexPos from;
    /** THe hexel the agent moves to */
    HexPos to;
    /**
     * Make a structure representing a move from input stream.
     * @param is The input stream to read from.
     * @throws Exception 
     */
    public Move(LogStream is) throws Exception {
        from = new HexPos(is);
        to = new HexPos(is);
    }
    
}
