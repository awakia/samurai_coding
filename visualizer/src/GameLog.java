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

import java.util.Arrays;

/**
 * Log of a game of SamurAI 2013
 * @author Takashi Chikayama
 */
public class GameLog {
    /** Teams participating in the game */
    int teams[] = new int[4];
    /** Maximum allowed number of turns in this game */
    int maxTurns;
    /** Actual number of turns in this game */
    int numTurns;
    /**
     * States of the game field.
     * states[n]: field state *before* the turn n and *fater* turn n-1.
     */
    GameFieldState states[];

    /**
     * Rankings after this game.
     * It is sorted in the ascending order of rankings.
     */
    RankingEntry ranking[];

    /**
     * Makes a game log structure.
     * @param is Input stream to read game log data
     * @param teamRankings Ranking entries before this game for participating teams
     * @param rno Round number
     * @param gno Game number
     * @throws Exception 
     */
    GameLog(LogStream is, RankingEntry teamRankings[], int rno, int gno) throws Exception {
        for (int k = 0; k != 4; k++) {
            teams[k] = is.nextInt();
        }
        maxTurns = is.nextInt();
        states = new GameFieldState[maxTurns+1];
        states[0] = new GameFieldState(is);
        int turn = 1;
        while (is.nextInt() != 0) {
            Play play = new Play(is);
            states[turn] = new GameFieldState(states[turn-1], play);
            turn += 1;
        }
        numTurns = turn - 1;
        decidePoints(teamRankings, rno, gno);
    }
    
    private void decidePoints(RankingEntry teamRankings[], int rno, int gno) {
        RankingEntry thisGameRanking[] = new RankingEntry[4];
        for (int k = 0; k != 4; k++) {
            thisGameRanking[k] = 
                    new RankingEntry(k, states[numTurns].area[k], 0, 0, rno, gno);
        }
        Arrays.sort(thisGameRanking);
        if (thisGameRanking[0].points != thisGameRanking[1].points) {
            thisGameRanking[0].points = 6;
            if (thisGameRanking[1].points != thisGameRanking[2].points) {
                thisGameRanking[1].points = 4;
                if (thisGameRanking[2].points != thisGameRanking[3].points) {
                    thisGameRanking[2].points = 2;
                    thisGameRanking[3].points = 0;
                } else {
                    thisGameRanking[2].points = 1;
                    thisGameRanking[3].points = 1;
                }
            } else {
                if (thisGameRanking[2].points != thisGameRanking[3].points) {
                    thisGameRanking[1].points = 3;
                    thisGameRanking[2].points = 3;
                    thisGameRanking[3].points = 0;
                } else {
                    thisGameRanking[1].points = 2;
                    thisGameRanking[2].points = 2;
                    thisGameRanking[3].points = 2;                    
                }
            }
        } else if (thisGameRanking[1].points != thisGameRanking[2].points) {
            thisGameRanking[0].points = 5;
            thisGameRanking[1].points = 5;
            if (thisGameRanking[2].points != thisGameRanking[3].points) {
                thisGameRanking[2].points = 2;
                thisGameRanking[3].points = 0;
            } else {
                thisGameRanking[2].points = 1;
                thisGameRanking[3].points = 1;
            }
        } else if (thisGameRanking[2].points != thisGameRanking[3].points) {
            thisGameRanking[0].points = 4;
            thisGameRanking[1].points = 4;
            thisGameRanking[2].points = 4;
        } else {
            thisGameRanking[0].points = 3;
            thisGameRanking[1].points = 3;
            thisGameRanking[2].points = 3;
            thisGameRanking[2].points = 3;
        }
        ranking = new RankingEntry[teamRankings.length];
        for (RankingEntry r: teamRankings) {
            ranking[r.team] = r.clone();
        }
        for (RankingEntry r: thisGameRanking) {
            GameFieldState s = states[numTurns];
            int ratio = (int)(1000000.0*s.area[r.team]/(s.fieldSize-s.area[4]));
            ranking[teams[r.team]].update(r.points, ratio, s.area[r.team], rno, gno);
        }
        Arrays.sort(ranking);
        // Also, set the "rank" field taking ties into account
        for (int k = 0; k != ranking.length; k++) {
            RankingEntry e = ranking[k];
            int rank = k;
            while (rank != 0 && e.ties(ranking[rank-1])) {
                rank -= 1;
            }
            e.setRank(rank);
            teamRankings[e.team] = e;
        }
    }
}
