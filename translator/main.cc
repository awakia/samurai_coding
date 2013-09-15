#include <iostream>
#include <fstream>
#include <cstdio>
#include <cstdlib>
#include "translator.hh"
#include "tokens.hh"
#include "line.hh"
#include "error.hh"

extern TokenVec scan(string buf);
extern ostream& operator<<(ostream& out, Token t);
extern void
genScript(vector<Line *>lines, int &lno, int team, ostream &out);
extern void initNames();

string fileName;
bool someErrors;

int main(int argc, char *argv[]) {
  if (argc < 2 || argc > 4) {
    cerr << "usage: " << argv[0] << " scriptFile [teamNumber [c++File]]" << endl;
    exit(1);
  }
  fileName = string(argv[1]);
  int extPos = fileName.rfind('.');
  string coreName;
  if (fileName.substr(fileName.length()-3) == ".gb") {
    coreName = fileName.substr(0, fileName.length()-3);
  } else {
    coreName = fileName;
    fileName += ".gb";
  }
  ifstream file(fileName.c_str());
  if (!file.good()) {
    cerr << "Error in opening input: " << fileName << endl;
    exit(1);
  }
  int team = (argc >= 3 ? atol(argv[2]) : 0);
  string outFileName;
  if (argc >= 3) {
    outFileName = string(argv[3]);
  } else {
    outFileName = coreName + ".cc";
  }
  someErrors = false;
  ofstream ofile(outFileName.c_str());
  if (!ofile.good()) {
    cerr << "Error in opening output: " << outFileName << endl;
    exit(1);
  }
  vector <Line*> lines;
  for (int lno = 1; !file.eof(); lno++) {
    string buf;
    getline(file, buf);
    try {
      TokenVec tokens = scan(buf);
      Line *line = parseLine(tokens.begin());
      line->source = buf;
      lines.push_back(line);
    } catch (SyntaxError err) {
      cerr << fileName << ":" << lno << ": error: "
           << err.message << endl
           << "| " << buf << endl;
      someErrors = true;
    }
  }
  lines.push_back(new Line(eofLine));
  int lno = 0;
  initNames();
  genScript(lines, lno, team, ofile);
  ofile.close();
  if (someErrors) {
    remove(outFileName.c_str());
    exit(1);
  } else {
    exit(0);
  }
}
