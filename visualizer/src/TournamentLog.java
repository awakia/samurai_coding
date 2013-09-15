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
 * Log of one whole tournament.
 * @author Takashi Chikayama
 */
public class TournamentLog {
    /**
     * The games in the tournament.
     * {@code games[r][g]} is the game g of the r-th round.
     */
    VisualizedGameLog games[][];
    /** Number of teams participated in the tournament */
    private int nTeams;
    /** Names of the teams */
    String teamList[];
    /** Initial ranking */
    RankingEntry initialRanking[];
    /** Number of rounds in the tournmanet */
    int nRounds;
    /** Number of games per one round */
    int nGames;
    /**
     * Makes a tournment log structure reading log data from
     * an input stream.
     * @param is The input stream to read from.
     * @throws Exception 
     */
    TournamentLog(LogStream is) throws Exception {
        nTeams = is.nextInt();
        if (nTeams%4 != 0) {
            throw new Exception("Number of teams is not a multiple of four");
        }
        teamList = new String[nTeams];
        for (int k = 0; k != nTeams; k++) {
            teamList[k] = is.nextLine();
        }
        nRounds = is.nextInt();
        nGames = nTeams/4;
        RankingEntry teamRankingEntries[] = new RankingEntry[nTeams];
        initialRanking = new RankingEntry[nTeams];
        games = new VisualizedGameLog[nRounds][nGames];
        for (int t = 0; t != nTeams; t++) {
            initialRanking[t] = new RankingEntry(t, 0, 0, 0, -1, -1);
            teamRankingEntries[t] = initialRanking[t];
        }
        for (int r = 0; r != nRounds; r++) {
            for (int g = 0; g != nGames; g++) {
                games[r][g] =
                        new VisualizedGameLog(is, teamRankingEntries, r, g);
            }
        }
    }
}
