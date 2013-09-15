#include <iostream>
#include <cstdlib>
#include "gunbai.hh"
#include "game.hh"

long long int costLimit;
int numRounds;
int numTurns;
int fieldWidth;
int fieldHeight;
int currentRound;

// Printing out

ostream& operator<<(ostream& out, gb x) {
  if (x.a == 0) {
    out << x.v;
  } else {
    out << "{ ";
    if (!x.a->empty()) {
      map<int, gb>::iterator p = x.a->begin();
      out << p->first << ": " << p->second;
      for (p++; p != x.a->end(); p++) {
	out << ", " << p->first << ": " << p->second;
      }
    }
    out << "}";
  }
}

extern gbScript *gbScript0;

int main(int argc, char* argv[]) {
  if (argc != 6) {
    cerr << "Usage: " << argv[0]
	 << " <# of rounds> <# of turns> <cost limit> <field width> <field height>"
	 << endl;
    exit(1);
  }
  numRounds = atol(argv[1]);
  numTurns = atol(argv[2]);
  costLimit = atol(argv[3]);
  if (numTeams%4 != 0) {
    cerr << "Number of teams = " << numTeams
	 << "; should be a multiple of four" << endl;
    exit(1);
  }
  int width = atol(argv[4]);
  int height = atol(argv[5]);
  if ((width&1) == 0 || (height&1) == 0) {
    cerr << "Field width and height must be odd numbers" << endl;
    exit(1);
  }
  cout << numTeams << endl;
  for (int k = 0; k != numTeams; k++) {
    cout << allTeams[k].teamName << endl;
  }
  playGames(width, height);
  return 0;
}
