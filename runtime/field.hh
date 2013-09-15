#ifndef FIELD
#define FIELD

#include <vector>
#include <list>
#include <set>
#include <iostream>

using namespace std;

// Game Field Representation

// Agents

struct Agent {
  struct Hexel* pos;                // Hexel currently on
  int id;                            // Agent ID: 0 means a dog
  bool frozen;                        // Whether or not in the forzen state
  int team;
  void init(int team, int x, int y, int i, struct Field* field);
};

// Syzygy: Syzygy swap description

struct Syzygy {
  vector <Hexel*> swappedHexels;
  Syzygy(vector <Hexel*> sh): swappedHexels(sh) {}
};

bool operator==(const Syzygy &s1, const Syzygy &s2);

// Territory: A group of contiguous hexels belonging to one team

struct Territory {
  int team;
  int size;
  set <Hexel*> hexels;                // The whole terriotory
  set <Hexel*> sieges;                // Newly obtained ones
  bool guarded;
  bool north, south, east, west;
  bool transContinental;
  void addHexel(Hexel* h, Field* f);
  Territory(Hexel* h, set <Hexel*> &recorded, Field* f);
};

bool operator<(const Territory &c1, const Territory &c2);

// Rational

struct Rational {
  const int numerator;
  const int denominator;
  Rational(int n, int d): numerator(n), denominator(d) {};
};

// Equality comparison of two rationals
//   Note that infinity, i.e., denominator being 0, is allowed.
//   Also note that 0/0 == x/y holds for any x and y here.

inline bool operator==(const Rational &r1, const Rational &r2) {
  return r1.numerator*r2.denominator == r2.numerator*r1.denominator;
}

// Field

struct Field {
  int width, height;
  struct Hexel** hexels;        // Hexel at (x, y) is hexels[y][x]
  vector <Hexel*> gates;        // Gate positions
  list <Hexel*> edges;                // Hexels on edges
  list <Hexel*> allHexels;        // All hexels
  struct Agent agents[4][4];        // Agents
  list <Agent*> allAgents;        // All agents
  Field(int w, int h, vector<int>& vg, vector<int>& hg);
  void outputLayout(ostream& out);
  void outputAgentPositions(ostream& out);
  void dump(ostream& out);
  void syzygies(list <Syzygy>& syzygies);
  Rational slope(Hexel *x, Hexel *y);
  void territories(vector <Territory> &territories);
};

ostream& operator<<(ostream& out, Field& f);

// Hexels

struct Hexel {
  int x, y;                        // Coordinates (only for debugging);
  int id;                        // Used for identifying a hexel
  struct Agent* agent;                // agent on the hexel; 0 if none
  int owner;                        // territory owner team; 4 if none
  inline Hexel() {
    owner = 4;
    agent = 0;
  }
  void place(struct Agent* agt);
  Hexel* adj[6];                // Six adjacent hexels/null at edges
  Hexel* gates[6];                // Adjacent through the gate/null if not
};

ostream & operator<<(ostream& out, Hexel& h);

inline bool operator<(Hexel &h1, Hexel &h2) {
  if (h1.x < h2.x) return true;
  if (h1.x == h2.x) return (h1.y < h1.y);
  return false;
}

inline bool operator==(Hexel &h1, Hexel &h2) {
  return h1.x == h2.x && h1.y == h2.y;
}

#endif
