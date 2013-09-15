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

import java.io.File;
import java.io.FileReader;

/**
 * Input stream from which a tournament log is read in.
 * @author Takashi Chikayama
 */
public class LogStream {
    private FileReader fr;
    private int nextch;
    /**
     * Reads an integer value from the stream.
     * @return The value read in.
     * @throws Exception 
     */
    int nextInt() throws Exception {
        while (nextch < '0' || '9' < nextch) {
            if (nextch == '/') {
                do {
                    nextch = fr.read();
                } while (nextch != 10);
            }
            nextch = fr.read();
            if (nextch == -1) throw new Exception("End of file reached");
        }
        int v = 0;
        do {
            v = 10*v + nextch - '0';
            nextch = fr.read();
        } while ('0' <= nextch && nextch <= '9');
        return v;
    }
    /**
     * Reads one whole line from the stream.
     * @return The string of characters in whatever remaining on the current line
     * @throws Exception 
     */
    String nextLine() throws Exception {
        while (nextch != 10) nextch = fr.read();
        if (nextch == -1) throw new Exception("End of file reached");
        nextch = fr.read();
        String v = "";
        while (nextch != 10) {
            v += (char)nextch;
            nextch = fr.read();
        }
        return v;
    }
    /**
     * Creates a stream to read in from a specified file.
     * @param file The file from which to read the data.
     * @throws Exception 
     */
    LogStream(File file) throws Exception {
        fr = new FileReader(file);
        nextch = fr.read();
    }
}
