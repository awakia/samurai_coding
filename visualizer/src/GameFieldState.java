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
 * Representation of a game field state
 * @author Takashi Chikayama
 */
class GameFieldState extends FieldConfiguration {
    /**
     * Position of agents.  agentPos[team][id] indicates the position of 
     * the agent of team and id. 
     */
    HexPos agentPos[][];
    /**
     * State of the hexel.
     * The state is encoded in a byte as {@code (owner<<5)+(team<<2)+agentId}
     */
    private byte hexelStates[][];
    /**
     * Territory area sizes of teams.
     * area[team] tells the number of hexels of the territories of team.
     * area[4] tells the number of neutral hexels.
     */
    int area[];
    /** Set of syzigies happened during the play that made this state. */
    Syzygy syzygies[];
    /** frozen[t][i] tells whether the agent of team with is id i is frozen. */
    boolean frozen[][];
    /** Set of sieges happened during the play that made this state. */
    Siege sieges[];
    /** Number of hexels sieged during the play that madethis state. */
    int siegesAtTurn;
    /** Set of transcontinental occupations. */
    Transcontinental trans[];
    
    /**
     * Returns the owner team of the specified hexel.
     * @param h Position of the hexel to be checked
     * @return The owner team of the hexel, or 4, if neutral
     */
    int ownerOf(HexPos h) {
        return (hexelStates[h.x][h.y] >> 5) & 0x7;
    }
    
    private byte encodeState(int owner, int team, int id) {
        return (byte)((owner<<5) + (team<<2) + id);
    }
    
    private int teamAt(HexPos h) {
        return (hexelStates[h.x][h.y] >> 2) & 0x7;
    }
    
    private int idAt(HexPos h) {
        return hexelStates[h.x][h.y] & 0x3;
    }
    
    private void placeAgentAt(HexPos h, int t, int a) {
        hexelStates[h.x][h.y] = encodeState(ownerOf(h), t, a);
    }
    
    private void removeAgentAt(HexPos h) {
        hexelStates[h.x][h.y] = encodeState(ownerOf(h), 4, 0);
    }
    
    private void changeOwnerOf(HexPos h, int t) {
        int prevOwner = ownerOf(h);
        area[prevOwner] -= 1;
        hexelStates[h.x][h.y] = encodeState(t, teamAt(h), idAt(h));
        area[t] += 1;
    }
    
    /**
     * Reads in and makes a data structure for a game field state
     * in its initial state.
     * @param is Input stream to read the log of the game.
     * @throws Exception 
     */
    GameFieldState(LogStream is) throws Exception {
        super(is);
        hexelStates = new byte[width][height];
        area = new int[5];
        area[4] = fieldSize;
        for (int y = 0; y != height; y++) {
            int ww = ((y&1) == 0 ? width-1 : width);
            for (int x = 0; x != ww; x++) {
                hexelStates[x][y] = encodeState(4, 4, 0);
            }
        }
        agentPos = new HexPos[4][4];
        for (int t = 0; t != 4; t++) {
            for (int a = 0; a != 4; a++) {
                HexPos p = new HexPos(is);
                agentPos[t][a]  = p;
                placeAgentAt(p, t, a);
                if (a != 0) changeOwnerOf(p, t);
            }
        }
        frozen = new boolean[4][4];
        syzygies = new Syzygy[0];
        sieges = new Siege[0];
        trans = new Transcontinental[0];
    }
    
    /**
     * Update the field state by moving an agent from one hexel to another.
     * @param from The hexel from which the the agent is moved.
     * @param to The hexel to which the agent is moved.
     */
    final void moveAgent(HexPos from, HexPos to) {
        int team = teamAt(from);
        int id = idAt(from);
        placeAgentAt(to, team, id);
        removeAgentAt(from);
        agentPos[team][id] = to;
    }
    
    /**
     * Swap positions of two agents.
     * @param h1 One hexel on which the agent originally is.
     * @param h2 The other hexel on which the agent originally is.
     */
    final void swapAgents(HexPos h1, HexPos h2) {
        int t1 = teamAt(h1);
        int t2 = teamAt(h2);
        int a1 = idAt(h1);
        int a2 = idAt(h2);
        placeAgentAt(h1, t2, a2);
        placeAgentAt(h2, t1, a1);
        agentPos[t1][a1] = h2;
        agentPos[t2][a2] = h1;
    }
    
    /**
     * Makes a new game field state from the original state and
     * plays made on that state.
     * @param prev The original field state.
     * @param play The play that changes the state.
     */
    GameFieldState(GameFieldState prev, Play play) {
        super(prev);
        hexelStates = new byte[width][height];
        for (int x = 0; x != width; x++) {
            hexelStates[x] = prev.hexelStates[x].clone();
        }
        agentPos = new HexPos[4][4];
        for (int t = 0; t != 4; t++) {
            agentPos[t] = prev.agentPos[t].clone();
        }
        area = prev.area.clone();
        // Process moves
        for (Move m: play.moves) {
            moveAgent(m.from, m.to);
        }
        // Processs syzygies
        syzygies = play.syzygies;
        for (Syzygy s: syzygies) {
            int l = s.swapped.length;
            for (int k = 0; k != l/2; k++) {
                swapAgents(s.swapped[k], s.swapped[l-k-1]);
            }
        }
        // Process conquests
        for (Conquest c: play.conquests) {
            changeOwnerOf(c.at, c.team);
        }
        frozen = new boolean[4][4];
        for (Agent f: play.frozen) {
            frozen[f.team][f.agnt] = true;
        }
        sieges = play.sieges;
        siegesAtTurn = play.siegesAtTurn;
        trans = play.trans;
    }
}