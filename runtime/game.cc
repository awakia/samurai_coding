#include "game.hh"
#include "gunbai.hh"
#include <list>
#include <algorithm>

int gbRemainingTurns;
int gbTurnsToSyzygy;

int numGames;
int currentTeam;

Game* currentGame;

void makeGameList(Game gameList[]) {
  // Group closely ranked teams.
  // Games should start from lower-ranked groups
  for (int m = 0; m != numGames; m++) {
    for (int k = 0; k != 4; k++) {
      gameList[m].teams[k] = teamRanking[4*(numGames-m-1)+k];
    }
  }
}  

Field* arrangeField(Game& game, int width, int height) {
  // Make Gates
  vector<int> vg, hg;
  int nvg = min(width/7, 1);
  int nhg = min(height/7, 1);
  for (int k = 0; k != nvg; k++) {
    int p = rand()%(width/2-2) + 1;
    vg.push_back(p);
  }
  for (int k = 0; k != nhg; k++) {
    int p = 2*(rand()%(height/4)) + 1;
    hg.push_back(p);
  }
  // Make Field
  return new Field(width, height, vg, hg);
}

void arrangeAgents(Game* game) {
  Field* f = game->field;
  // Place Agents
  set <int> used;
  for (int k = 0; k != 4; k++) {
  again:
    int r = rand();
    int ry = r%(f->height/2 - 1) + 1;
    int ww = ((ry&1) == 0 ? f->width-1 : f->width);
    int rx = (r&0xFFFF)%(f->width/2-1) + 1;
    int e = (rx << 16) + ry;
    if (used.count(e) != 0) {
      goto again;
    }
    used.insert(e);
    if ((k & 1) != 0) {
      ry = f->height-ry-1;
    }
    if ((k & 2) != 0) {
      rx = ww-rx-1;
    }
    game->field->agents[0][k].init(0, rx,      ry,             k, f);
    game->field->agents[1][k].init(1, ww-rx-1, ry,             k, f);
    game->field->agents[2][k].init(2, rx,      f->height-ry-1, k, f);
    game->field->agents[3][k].init(3, ww-rx-1, f->height-ry-1, k, f);
  }
}

Game* arrangeGame(Game* game, int width, int height) {
  game->field = arrangeField(*game, width, height);
  arrangeAgents(game);
  return game;
}

struct Move {
  Agent* agent;
  Hexel* from;
  Hexel* to;
  inline Move(Agent* a, Hexel* f, Hexel* t):
    agent(a), from(f), to(t) {
  }
};

struct Conquest {
  Hexel* at;
  int newOwner;
  inline Conquest(Hexel* h, int n):
    at(h), newOwner(n) {
  }
};

void decideMoves(Game* game, 
		 list <Move> &moves,
		 list <Agent*> &frozen,
		 vector <Territory> &territories,
		 list <Syzygy> &syzygies,
		 list <Conquest> &conquests) {
  list <Move> triedMoves;
  multiset <Hexel*> moveTo;
  for (int t = 0; t != 4; t++) {
    for (int a = 0; a != 4; a++) {
      Agent* agent = &game->field->agents[t][a];
      Hexel* fromPos = agent->pos;
      Hexel* newPos = 0;
      int m = game->moves[t][a];
      if (m == -1) {
	// Pass
	if (agent->frozen) {
	  // Issuing a pass will melt the frozen state
	  agent->frozen = false;
	}
      } else if (0 <= m && m < 6 && !agent->frozen) {
	newPos = fromPos->adj[m];
	// Check whether the move direction is OK
	if (newPos == 0) {
	  if (fromPos->gates[m] != 0) {
	    newPos = fromPos->gates[m];
	  } else {
	    // Illegal move will freeze the agent
	    agent->frozen = true;
	    frozen.push_back(agent);
	  } 
	}
	if (newPos != 0) {
	  if (newPos->agent != 0) {
	    // Can't go to an already occupied hexel
	    newPos = 0;
	    agent->frozen = true;
	    frozen.push_back(agent);
	  } else {
	    if (a != 0) {
	      // Samurai can't approach a dog of another team
	      for (int d = 0; d != 6; d++) {
		if (newPos->adj[d] != 0 &&
		    newPos->adj[d]->agent != 0 &&
		    newPos->adj[d]->agent->team != t &&
		    newPos->adj[d]->agent->id == 0) {
		  agent->frozen = true;
		  frozen.push_back(agent);
		  newPos = 0;
		  break;
		}
	      }
	    }
	  }
	}
      } else {
	// Illegal move
	agent->frozen = true;
	frozen.push_back(agent);
      }
      if (newPos != 0) {
	triedMoves.push_back(Move(agent, fromPos, newPos));
	// Register to detect conflicts
	moveTo.insert(newPos);
      }
    }
  }
  // Find move conflicts
  for (list<Move>::iterator m = triedMoves.begin();
       m != triedMoves.end(); m++) {
    Agent* a = m->from->agent;
    if (moveTo.count(m->to) == 1) {
      // No conflict; Make move
      m->to->agent = a;
      m->from->agent = 0;
      a->pos = m->to;
      moves.push_back(*m);
    } else {
      // Move conflict
      a->frozen = true;
      frozen.push_back(a);
    }
  }
  // Find Syzygies
  if (gbTurnsToSyzygy == 0) {
    game->field->syzygies(syzygies);
    for (list <Syzygy>::iterator s = syzygies.begin();
	 s != syzygies.end(); s++) {
      int nsw = s->swappedHexels.size();
      for (int p = 0; p != nsw/2; p++) {
	Hexel *h1 = s->swappedHexels[p];
	Hexel *h2 = s->swappedHexels[nsw-p-1];
	Agent* a1 = h1->agent;
	Agent* a2 = h2->agent;
	a1->pos = h2;
	h2->agent = a1;
	a2->pos = h1;
	h1->agent = a2;
      }
    }
  }
  // Register those conquest made by moves (not sieges)
  for (list <Hexel*>::iterator h = game->field->allHexels.begin();
       h != game->field->allHexels.end(); h++) {
    if ((*h)->agent != 0 &&	// An agent is there
	(*h)->agent->id != 0 &&	// and is not a dog, and
	(*h)->owner != (*h)->agent->team) { // the owner is not the same team
      conquests.push_back(Conquest((*h), (*h)->agent->team));
      (*h)->owner = (*h)->agent->team;
    }
  }
  // Find sieges
  game->field->territories(territories);
  for (vector <Territory>::iterator i = territories.begin();
       i != territories.end(); i++) {
    if (i->guarded) {
      for (set<Hexel*>::iterator s = i->sieges.begin();
	   s != i->sieges.end(); s++) {
	conquests.push_back(Conquest((*s), i->team));
      }
    }
  }
}

void playOneGame(Game* game, ostream &out) {
  for (int k = 0; k != 4; k++) {
    out << game->teams[k]->teamID << " ";
  }
  out << "// Teams in this game" << endl;
  out << numTurns << " // Max # of turns" << endl;
  out << "// Field layout" << endl;
  game->field->outputLayout(out);
  out << "// Initial agent positions" << endl;
  game->field->outputAgentPositions(out);
  gbTurnsToSyzygy = INIT_SYZYGY_BAN;
  for (int k = 0; k != 4; k++) {
    Team *t = game->teams[k];
    t->storage.a = new map<int, gbType>();
  }
  for (game->turn = 0; game->turn != numTurns; game->turn++) {
    gbRemainingTurns = numTurns - game->turn;
    bool termination = false;
    for (int t = 0; t != 4; t++) {
      for (int a = 0; a != 4; a++) {
	game->moves[t][a] = -1;	// Initialize with "stay"
      }
    }
    for (int k = 0; k != 4; k++) {
      Team *t = game->teams[k];
      currentTeam = k;
      t->script->gbInitGlobals();
      t->script->gbCost = 0;
      t->cleanUpStorage();
      try {
	t->script->script();
      } catch (RuntimeError err) {
	cerr << "Team " << k << " turn " << game->turn << ": "
	     << err.message << endl;
      }
      t->pruneStorage();
    }
    list <Move> moves;
    list <Agent*> frozen;
    vector <Territory> territories;
    list <Syzygy> syzygies;
    list <Conquest> conquests;
    out << "1 // Game continues; Turn = " << game->turn << endl;
    decideMoves(game, moves, frozen, territories, syzygies, conquests);
    // Output moves
    out << moves.size() << " // # of valid moves" << endl;
    for (list <Move>::iterator m = moves.begin(); m != moves.end(); m++) {
      out << *m->from << " " << *m->to << endl;
    }
    // Output syzygies
    out << syzygies.size() << " // # of syzygies" << endl;
    if (syzygies.size() != 0) {
      for (list <Syzygy>:: iterator s = syzygies.begin();
	   s != syzygies.end(); s++) {
	vector <Hexel*> *swapped = &s->swappedHexels;
	out << swapped->size();
	for (vector <Hexel*>:: iterator h = swapped->begin();
	     h != swapped->end(); h++) {
	  out << " " << (*h)->x << " " << (*h)->y;
	}
	out << endl;
      }
      gbTurnsToSyzygy = MIN_SYZYGY_INTERVAL;
    }
    // Output conquests
    out << conquests.size() << " // # Owner changes" << endl;
    for (list <Conquest>::iterator o = conquests.begin();
	 o != conquests.end(); o++) {
      out << *o->at << " " << o->newOwner << endl;
      o->at->owner = o->newOwner;
    }
    // Output frozen agents
    out << frozen.size() << " // Frozen" << endl;
    for (list <Agent*>::iterator f = frozen.begin(); f != frozen.end(); f++) {
      out << (*f)->team << " " << (*f)->id << endl;
    }
    if (gbTurnsToSyzygy != 0) gbTurnsToSyzygy -= 1;
    // Output sieges 
    int siegeCount = 0;
    for (vector <Territory>::iterator c = territories.begin();
	 c != territories.end(); c++) {
      if (c->sieges.size() != 0) siegeCount += 1;
    }
    out << siegeCount << " // # of sieges" << endl;
    vector <Territory*> transContinental;
    for (vector <Territory>::iterator c = territories.begin();
	 c != territories.end(); c++) {
      if (c->sieges.size() != 0) {
	out << c->team << " " << c->sieges.size()
	    << " // Sieged team and # of obtained hexels" << endl;
	for (set <Hexel*>::iterator s = c->sieges.begin();
	     s != c->sieges.end(); s++) {
	  out << **s << endl;
	}
      }
      if (c->transContinental) {
	transContinental.push_back(&*c);
      }
    }
    out << transContinental.size() << " // # of transcontinentals" << endl;
    if (!transContinental.empty()) {
      int territorySizes[5] = {};
      for (list <Hexel*>::iterator h = game->field->allHexels.begin();
	   h != game->field->allHexels.end(); h++) {
	territorySizes[(*h)->owner] += 1;
      }
      for (vector <Territory*>::iterator trans = transContinental.begin();
	   trans != transContinental.end(); trans++) {
	// Test whether the team accomplished transcontinetal
	// occupation is the top team.
	out << (*trans)->team << " " << (*trans)->size << " // Team and size" << endl;
	for (set <Hexel*>::iterator h = (*trans)->hexels.begin();
	     h != (*trans)->hexels.end(); h++) {
	  out << " " << **h;
	}
	out << " // Hexels" << endl;
	int terr = territorySizes[(*trans)->team];
	for (int t = 0; t != 4; t++) {
	  if (territorySizes[t] > terr) goto NOT_TOP;
	}
	termination = true;
      NOT_TOP:;
      }
    }
    int areas[4] = {0};
    for (list <Hexel*>::iterator i = game->field->allHexels.begin();
	 i != game->field->allHexels.end(); i++) {
      int owner = (*i)->owner;
      if (owner != 4) areas[owner] += 1;
    }
    out << "// Current Area Sizes:";
    for (int t = 0; t != 4; t++) out << " " << areas[t];
    out << endl;
    if (termination) break;
  }
  out << "0 // Game terminates" << endl;
}


struct TeamAreaPair {
  int team;
  int area;
  int score;
};

bool operator<(TeamAreaPair p1, TeamAreaPair p2) {
  return p1.area > p2.area;
}

bool compareScores(Team *x, Team *y) {
  if (x->totalPoints != y->totalPoints)
    return x->totalPoints > y->totalPoints;
  if (x->ratioSum != y->ratioSum)
    return x->ratioSum > y->ratioSum;
  if (x->areaSum != y->areaSum)
    return x->areaSum > y->areaSum;
  else return true;
}


void playGames(int width, int height) {
  numGames = numTeams/4;
  for (int r = 0; r != numTeams; r++) {
    teamRanking[r] = &allTeams[r];
  }
  cout << numRounds << endl;
  Game gameList[numGames];
  for (currentRound = 0; currentRound != numRounds; currentRound++) {
    makeGameList(gameList);
    for (int m = 0; m != numGames; m++) {
      currentGame = arrangeGame(&gameList[m], width, height);
      playOneGame(currentGame, cout);
      Field* field = currentGame->field;
      struct TeamAreaPair gameScore[4];
      for (int t = 0; t != 4; t++) {
	gameScore[t].team = t;
	gameScore[t].area = 0;
      }
      int nonNeutral = 0;
      for (list <Hexel*>::iterator i = field->allHexels.begin();
	   i != field->allHexels.end(); i++) {
	int owner = (*i)->owner;
	if (owner != 4) {
	  gameScore[owner].area += 1;
	  nonNeutral += 1;
	}
      }
      sort(&gameScore[0], &gameScore[4]);
      if (gameScore[0].area != gameScore[1].area) {
	gameScore[0].score = 6;
	if (gameScore[1].area != gameScore[2].area) {
	  gameScore[1].score = 4;
	  if (gameScore[2].area != gameScore[3].area) {
	    gameScore[2].score = 2;
	    gameScore[3].score = 0;
	  } else {
	    gameScore[2].score = 1;
	    gameScore[3].score = 1;
	  }
	} else if (gameScore[2].area != gameScore[3].area) {
	  gameScore[1].score = 3;
	  gameScore[2].score = 3;
	  gameScore[3].score = 0;
	} else {
	  gameScore[1].score = 2;
	  gameScore[2].score = 2;
	  gameScore[3].score = 2;
	}
      } else if (gameScore[1].area != gameScore[2].area) {
	gameScore[0].score = 5;
	gameScore[1].score = 5;
	if (gameScore[2].area != gameScore[3].area) {
	  gameScore[2].score = 2;
	  gameScore[3].score = 0;
	} else {
	  gameScore[2].score = 1;
	  gameScore[3].score = 1;
	}
      } else if (gameScore[2].area != gameScore[3].area) {
	gameScore[0].score = 4;
	gameScore[1].score = 4;
	gameScore[2].score = 4;
	gameScore[3].score = 0;
      } else {
	gameScore[0].score = 3;
	gameScore[1].score = 3;
	gameScore[2].score = 3;
	gameScore[4].score = 3;
      }
      cout << "// Game scores" << endl;
      for (int r = 0; r != 4; r++) {
	Team* team = currentGame->teams[gameScore[r].team];
	team->totalPoints += gameScore[r].score;
	team->ratioSum +=
	  (int)(1000000.0*gameScore[r].area/nonNeutral);
	team->areaSum += gameScore[r].area;
	cout << "// " << r
	     << ": team " << currentGame->teams[gameScore[r].team]->teamID
	     << "; area = " << gameScore[r].area
	     << "; points = " << gameScore[r].score
	     << "; total points = " << team->totalPoints
	     << "; ratio sum = " << team->ratioSum
	     << "; area sum = " << team->areaSum
	     << endl;
      }
    }
    sort(&teamRanking[0], &teamRanking[numTeams], compareScores);
    cout << "// Ranking after round " << currentRound << endl;
    for (int r = 0; r != numTeams; r++) {
      Team *team = teamRanking[r];
      cout << "// " << r << ": team " << team->teamID
	   << "; total points = " << team->totalPoints
	   << "; ratio sum = " << team->ratioSum
	   << "; area sum = " << team->areaSum
	   << endl;
    }
  }
}

void Team::cleanUpStorage() {
  storage.v = 0;
  map<int, gbType> tempMap;
  if (storage.a != 0) {
    tempMap = *storage.a;
    storage.a->clear();
    for (map<int, gbType>::iterator i = tempMap.begin();
	 i != tempMap.end(); i++) {
      int index = i->first;
      if (0 <= index && index < 1024) {
	gbType value = gbType(i->second.v);
	storage.a->insert(pair<int, gbType>(index, value));
      }
    }
  }
}

void Team::pruneStorage() {
  gbType newStorage;
  if (storage.a != 0) {
    for (map<int, gbType>::iterator i = storage.a->begin();
	 i != storage.a->end(); i++) {
      int key = i->first;
      if (0 <= key && key < 1024) {
	newStorage[key] = gbType(i->second.v);
      }
    }
  }
  storage = newStorage;
}
