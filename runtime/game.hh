#ifndef GAME
#define GAME

#include "field.hh"
#include "language.hh"

extern int gbRemainingTurns;
extern int gbTurnsToSyzygy;

const int INIT_SYZYGY_BAN = 50;
const int MIN_SYZYGY_INTERVAL = 30;

struct Team {
  int teamID;
  string teamName;
  Agent agents[4];
  int randomSeed;
  struct gbScript* script;
  int totalPoints;
  int ratioSum;
  int areaSum;
  inline Team(int id, string name, gbScript* scr) {
    teamID = id;
    teamName = name;
    script = scr;
    totalPoints = 0;
    ratioSum = 0;
    areaSum = 0;
  }
  gbType storage;
  void cleanUpStorage();
  void pruneStorage();
};

struct Game {
  Field* field;
  Team* teams[4];
  int moves[4][4];
  int turn;
  void dump(ostream& out);
};

extern Team teams[];
extern Team allTeams[];
extern Team* teamRanking[];
extern int numRounds;
extern int numTurns;

extern int numTeams;
extern int currentRound;

extern Game* currentGame;

extern int currentTeam;
extern int finalMoves[];

extern void playGames(int width, int height);

#endif
