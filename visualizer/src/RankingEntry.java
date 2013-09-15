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
 * Represents an item in the ranking.
 * @author Takashi Chikayama
 */
class RankingEntry implements Comparable {
    /** Team ID number */
    int team;
    /** Rank of the team */
    int rank;
    /** Rank of the team in the previous round */
    int points;
    /** Sum of territory ratios (times 10^6) */
    int ratioSum;
    /** Sum of territory areas */
    int areaSum;
    /** Round number of the this entry */
    int roundNumber;
    /** Game number of this entry */
    int gameNumber;
    /** Makes a ranking entry structure */
    RankingEntry(int t, int p, int r, int a, int rno, int gno) {
        team = t;
        points = p;
        ratioSum = r;
        areaSum = a;
        roundNumber = rno;
        gameNumber = gno;
   }
    
    public RankingEntry clone() {
        return new RankingEntry(team, points, ratioSum, areaSum, 
                roundNumber, gameNumber);
    }
    
    /** Updated score info */
    void update(int p, int r, int a, int rno, int gno) {
        points += p;
        ratioSum += r;
        areaSum += a;
        roundNumber = rno;
        gameNumber = gno;
    }

    /**
     * Compares this ranking entry with another entry.
     * @param o Another ranking entry
     * @return An integer value representing the result of the comparison
     */
    @Override
    public int compareTo(Object o) {
        RankingEntry x = (RankingEntry)o;
        if (x.points != points) return x.points - points;
        if (x.ratioSum != ratioSum) return x.ratioSum - ratioSum;
        if (x.areaSum != areaSum) return x.areaSum - areaSum;
        return 0;
    }

    /**
     * Tells whether this ranking entry is a tie with another.
     * @param r Another ranking entry
     * @return Whether this entry and the other entyr are in a tie
     */
    boolean ties(RankingEntry r) {
        return r.points == points &&
                r.ratioSum == ratioSum &&
                r.areaSum == areaSum;
    }

    /**
     * Set the rank and the previous rank of this entry.
     * @param r rank
     */
    void setRank(int r) {
        rank = r;
    }
}
