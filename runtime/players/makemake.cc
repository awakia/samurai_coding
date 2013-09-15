#include <iostream>
#include <fstream>
#include <sstream>
#include <cstdlib>
#include <vector>

using namespace std;

const char* makefileName = "Makefile.scripts";
const char* teamsFileName  = "teams.cc";

void abortExecution() {
  remove(makefileName);
  remove(teamsFileName);
  cerr << "Failed to compile team-dependent files" << endl;
  exit(1);
}

void writeQuotedString(string str, ostream& out) {
  for (int i = 0; i != str.size(); i++) {
    if (str[i] == '"') {
      out << '\\';
    }
    out << str[i];
  }
}

int main(int argc, char* argv[]) {
  bool errors = false;
  if (argc != 1) {
    cerr << argv[0] << ": No command line arguments expected" << endl;
    exit(1);
  }

  // Open output files
  ofstream makefile(makefileName);
  if (!makefile.good()) {
    cerr << "Failed to open output: " << makefileName << endl;
    abortExecution();
  }
  ofstream teamsfile("teams.cc");
  if (!teamsfile.good()) {
    cerr << "Failed to open output: " << teamsFileName << endl;
    abortExecution();
  }

  // Read team names and script file names
  vector <string> teams;
  vector <string> scripts;
  string line;
  while (getline(cin, line)) {
    stringstream lineStream(line);
    string teamName, scriptFileName;
    lineStream >> teamName >> scriptFileName;
    teams.push_back(teamName);
    scripts.push_back(scriptFileName);
  }    
  if (teams.size()%4 != 0) {
    cerr << "Number of teams is not a multiple of four" << endl;
    abortExecution();
  }

  // Output team table C++ file
  teamsfile << "#include \"game.hh\"" << endl << endl;
  for (int t = 0;t != teams.size(); t++) {
    teamsfile << "extern gbScript gbScriptObj" << t << ";" << endl;
  }
  teamsfile
    << endl
    << "int numTeams = " << teams.size() << ";" << endl
    << "Team* teamRanking[" << teams.size() << "];" << endl
    << "Team allTeams[] = {" << endl;
  for (int t = 0;t != teams.size(); t++) {
    teamsfile << "Team(" << t << ", \"";
    writeQuotedString(teams[t], teamsfile);
    teamsfile << "\", &gbScriptObj" << t << ")," << endl;
  }
  teamsfile << "};" << endl;

  // Output makefil for the scripts
  makefile
    << "TARGETS=";
  for (int s = 0; s != scripts.size(); s++) {
    makefile << " script" << s << ".o";
  }
  makefile
    << endl
    << "CXXFLAGS= -I.. -g" << endl
    << "TRANSLATOR= ../../translator/gb2c++" << endl << endl
    << "all: $(TARGETS)" << endl
    << ".cc.o:" << endl
    << "\tc++ $(CXXFLAGS) -c $<" << endl;
  for (int s = 0; s != scripts.size(); s++) {
    makefile
      << "script" << s << ".cc: "
      << scripts[s] << ".gb $(TRANSLATOR)" << endl
      << "\t$(TRANSLATOR) " << scripts[s] << ".gb "
      << s << " script" << s << ".cc" << endl;
  }

  exit(0);
}
