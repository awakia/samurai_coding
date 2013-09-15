#include <cstdlib>
#include <iomanip>
#include <stack>
#include <algorithm>
#include "field.hh"

Field::Field(int w, int h, vector<int>& vg, vector<int>& hg) {
  // Set sizes
  width = w;
  height = h;
  // Allocate hexels
  hexels = new Hexel*[h];
  for (int y = 0; y != h; y++) {
    bool evenRow = ((y & 1) == 0);
    int ww = (evenRow ? w-1 : w);
    hexels[y] = new Hexel[ww];
    for (int x = 0; x != ww; x++) {
      Hexel& hx = hexels[y][x];
      hx.x = x;
      hx.y = y;
      hx.id = y * w + x;
      allHexels.push_back(&hx);
    }
  }
  // Register edge hexels
  for (int y = 1; y != h-1; y++) {
    bool evenRow = ((y & 1) == 0);
    int ww1 = (evenRow ? width-2 : width-1);
    edges.push_back(&hexels[y][0]);
    edges.push_back(&hexels[y][ww1]);
  }
  for (int x = 1; x != width-1; x++) {
    edges.push_back(&hexels[0][x]);
    edges.push_back(&hexels[height-1][x]);
  }
  // Set hexel adjacency
  int bottom = h - 1;
  for (int y = 0; y != h; y++) {
    bool evenRow = ((y & 1) == 0);
    int ww = (evenRow ? w - 1 : w);
    for (int x = 0; x != ww; x++) {
      Hexel& hx = hexels[y][x];
      if (evenRow) {
	hx.adj[0] = ((y == 0) ?      0 : &hexels[y-1][x]);
	hx.adj[1] = ((y == 0) ?      0 : &hexels[y-1][x+1]);
	hx.adj[2] = ((x == ww-1) ?   0 : &hexels[y  ][x+1]);
	hx.adj[3] = ((y == bottom) ? 0 : &hexels[y+1][x+1]);
	hx.adj[4] = ((y == bottom) ? 0 : &hexels[y+1][x]);
	hx.adj[5] = ((x == 0) ?      0 : &hexels[y  ][x-1]);
      } else {
	hx.adj[0] = (x == 0 ?      0 : &hexels[y-1][x-1]);
	hx.adj[1] = ((x == ww-1) ? 0 : &hexels[y-1][x]);
	hx.adj[2] = ((x == ww-1) ? 0 : &hexels[y  ][x+1]);
	hx.adj[3] = ((x == ww-1) ? 0 : &hexels[y+1][x]);
	hx.adj[4] = (x == 0 ?      0 : &hexels[y+1][x-1]);
	hx.adj[5] = (x == 0 ?      0 : &hexels[y  ][x-1]);
      }
      // Clear gate adjacency
      hx.gates[0] = hx.gates[1] = hx.gates[2] =
	hx.gates[3] = hx.gates[4] = hx.gates[5] = 0;
    }
  }

  // Set gates
  for (vector<int>::const_iterator i = vg.begin(); i != vg.end(); i++) {
    int x = *i;
    Hexel* tl = &hexels[0][x];
    Hexel* bl = &hexels[bottom][x];
    gates.push_back(tl);
    gates.push_back(bl);
    tl->gates[0] = tl->gates[1] = bl;
    bl->gates[3] = bl->gates[4] = tl;
    Hexel* tr = &hexels[0][width-2-x];
    Hexel* br = &hexels[bottom][width-2-x];
    gates.push_back(tr);
    gates.push_back(br);
    tr->gates[0] = tr->gates[1] = br;
    br->gates[3] = br->gates[4] = tr;
  }
  for (vector<int>::const_iterator i = hg.begin(); i != hg.end(); i++) {
    int y = *i;
    if ((y & 1) == 0) {
      cerr << "No horizontal gates allowed on even rows: " << y << endl;
      exit(1);
    }
    Hexel* lu = &hexels[y][0];
    Hexel* ru = &hexels[y][width-1];
    gates.push_back(lu);
    gates.push_back(ru);
    lu->gates[5] = ru;
    ru->gates[2] = lu;
    Hexel* ll = &hexels[bottom-y][0];
    Hexel* rl = &hexels[bottom-y][width-1];
    gates.push_back(ll);
    gates.push_back(rl);
    ll->gates[5] = rl;
    rl->gates[2] = ll;
  }

  // List up agents
  for (int t = 0; t != 4; t++) {
    for (int a = 0; a != 4; a++) {
      allAgents.push_back(&agents[t][a]);
    }
  }
}

void Field::outputLayout(ostream& out) {
  out << width << " " << height << " // Field width and height" << endl;
  out << gates.size() << " // Number of gate hexels" << endl;
  for (vector <Hexel*>::iterator g = gates.begin();
       g != gates.end(); g++) {
    out << **g << " ";
  }
  out << "// Gate positions" << endl;
}

void Field::outputAgentPositions(ostream& out) {
  for (int t = 0; t != 4; t++) {
    for (int a = 0; a != 4; a++) {
      out << *agents[t][a].pos << " ";
    }
  }
  out << endl;
}

ostream& operator<<(ostream& out, Hexel& h) {
  out << h.x << " " << h.y;
  return out;
}

ostream& operator<<(ostream& out, Field& f) {
  const char dogChar[] = "abcd";
  const char samuraiChar[] = "ABCD";
  out << "Field: " << f.width << " x " << f.height << endl;
  out << "  ";
  for (int x = 0; x != f.width; x++) {
    out << setw(6) << x;
  }
  out << endl;
  for (int y = 0; y != f.height; y++) {
    out << setw(2) << y << ": ";
    bool evenRow = ((y & 1) == 0);
    if (evenRow) out << "   ";
    int w = (evenRow ? f.width-1 : f.width);
    for (int x = 0; x != w; x++) {
      Hexel& hx = f.hexels[y][x];
      // westward or upward gate
      out << (x == 0 && hx.gates[5] != 0 ? '<' :
	      (y == 0 && hx.gates[0] != 0 ? '^' :'.'));
      if (hx.owner < 0) {
	out << ".";
      } else {
	out << samuraiChar[hx.owner];
      }
      if (hx.agent == 0) {
	out << "..";
      } else if (hx.agent->id == 0) {
	out << dogChar[hx.agent->team] << ".";
      } else {
	out << samuraiChar[hx.agent->team];
	if (hx.agent->frozen) {
	  out << "p";
	} else {
	  out << ".";
	}
      }
      // eastward or downward gate
      out << (x == w-1 && hx.gates[2] != 0 ? '>' :
	      (y == f.height-1 && hx.gates[3] != 0 ? 'V' :'.'));
      if (x != w-1) {
	out << " ";
      }
    }
    out << endl;
  }
}

bool connected(Hexel* h1, Hexel* h2, Hexel* h) {
  // Test whether hexels h1 and h2 are connected before
  // the team obtained the hexel h.
  set<Hexel*> visited;
  vector<Hexel*> toVisit;
  int team = h->owner;
  toVisit.push_back(h1);
  visited.insert(h1);
  do {
    Hexel* x = toVisit.back();
    toVisit.pop_back();
    for (int j = 0; j != 6; j++) {
      Hexel* a = x->adj[j];
      if (a == h2) {
	return true;
      }
      if (a != 0 && a != h && a->owner == team &&
	  visited.count(a) == 0) {
	visited.insert(a);
	toVisit.push_back(a);
      }
    }
  } while (!toVisit.empty());
  return false;
}

// Territory structure

void Territory::addHexel(Hexel* h, Field* f) {
  hexels.insert(h);
  if (h->y == 0) north = true;
  if (h->y == f->height-1) south = true;
  if (h->x == 0) west = true;
  if (h->x == (((h->y&1) == 0) ? f->width-2 : f->width-1)) east = true;
}

Territory::Territory(Hexel* h, set <Hexel*> &recorded, Field* f) {
  team = h->owner;
  guarded = false;
  north = south = east = west = false;
  // Flood out from the initial hexel
  stack <Hexel*> toVisit;
  toVisit.push(h);
  addHexel(h, f);
  recorded.insert(h);
  do {
    Hexel* pos = toVisit.top();
    toVisit.pop();
    if (pos->agent != 0 && pos->agent->team == team) {
      guarded = true;
    }
    for (int d = 0; d != 6; d++) {
      Hexel* a = pos->adj[d];
      if (a != 0 && a->owner == team && hexels.count(a) == 0) {
	toVisit.push(a);
	addHexel(a, f);
	recorded.insert(a);
      }
    }
  } while (!toVisit.empty());
  if (guarded && hexels.size() >= 6) {
    // Find siege for guarded territories with 6 or more hexels.
    // First, find all the hexels lying outside of this territory.
    // We will start from hexels on a field edge and flood out.
    set <Hexel*> outside;
    for (list <Hexel*>::iterator i = f->edges.begin();
	 i != f->edges.end(); i++) {
      Hexel* e = *i;
      if (hexels.count(e) == 0 && outside.count(e) == 0) {
	outside.insert(e);
	toVisit.push(e);
	do {
	  Hexel* v = toVisit.top();
	  toVisit.pop();
	  for (int d = 0; d != 6; d++) {
	    Hexel* a = v->adj[d];
	    if (a != 0 && hexels.count(a) == 0 && outside.count(a) == 0) {
	      outside.insert(a);
	      toVisit.push(a);
	    }
	  }
	} while (!toVisit.empty());
      }
    }
    // Any cells not in the "outside" sets belong to the territory,
    // except those guarded by a samurai of another team.
    for (int y = 0; y != f->height; y++) {
      bool evenRow = ((y & 1) == 0);
      int ww = (evenRow ? f->width - 1 : f->width);
      for (int x = 0; x != ww; x++) {
	Hexel* hh = &f->hexels[y][x];
	if (outside.count(hh) == 0) {
	  size++;
	  if (hh->owner != team && (hh->agent == 0 || hh->agent->id == 0)) {
	    sieges.insert(hh);
	  }
	}
      }
    }
  }
  size = hexels.size();
  transContinental = ((north & south) | (east & west));
}

bool operator<(const Territory &c1, const Territory &c2) {
  return c1.size < c2.size;
}

// Field::territories:
//   List up territories to its argument:
//     vector <Territory>& territories
// Note that the hexel owners of the field should have already been updated.

void Field::territories(vector <Territory>& terrs) {
  set <Hexel*> recorded;
  for (int y = 0; y != height; y++) {
    bool evenRow = ((y & 1) == 0);
    int ww = (evenRow ? width - 1 : width);
    for (int x = 0; x != ww; x++) {
      Hexel* h = &hexels[y][x];
      if (h->owner != 4 && recorded.count(h) == 0) {
	// Start from a hexel owned by some team and not recorded yet
	terrs.push_back(Territory(h, recorded, this));
      }
    }
  }
  sort(terrs.begin(), terrs.end());
}

// Field::slope
//   Returns the slope of the line connecting two hexels, h1 and h2.

Rational Field::slope(Hexel *h1, Hexel *h2) {
  int h1x = 2*h1->x + ((h1->y & 1) ? 0 : 1);
  int h2x = 2*h2->x + ((h2->y & 1) ? 0 : 1);
  return Rational(h2x - h1x, h2->y - h1->y);
}

void Field::syzygies(list <Syzygy> &syzygies) {
  // Find four or more agents (including at least one dog)
  // aligned on a straight line.
  // If any agents are included in more than one such alignment,
  // all the alignments including that agent are removed.
  list <Syzygy> temporary;
  set <Hexel*> already;
  set <Hexel*> duplicated;
  for (list <Agent*>::iterator ia = allAgents.begin();
       ia != allAgents.end(); ia++) {
    Agent* a = *ia;
    Hexel* apos = a->pos;
    list <Agent*>::iterator ib = ia;
    while (++ib != allAgents.end()) {
      Agent* b = *ib;
      Hexel* bpos = b->pos;
      Rational slopeAB = slope(apos, bpos);
      vector <Hexel*> candidates;
      bool dogIncluded = (a->id == 0 || b->id == 0);
      list <Agent*>::iterator ic = ib;
      while (++ic != allAgents.end()) {
	Agent* c = *ic;
	Hexel* cpos = c->pos;
	Rational slopeAC = slope(apos, cpos);
	if (slopeAB == slopeAC) {
	  candidates.push_back(cpos);
	  dogIncluded |= (c->id == 0);
	}
      }
      if (candidates.size() >= 2 && dogIncluded) {
	// Four or more agents with at least one dog in a line
	candidates.push_back(apos);
	candidates.push_back(bpos);
	sort(candidates.begin(), candidates.end());
	Syzygy newSyzygy = Syzygy(candidates);
	for (vector <Hexel*>::iterator ih = candidates.begin();
	     ih != candidates.end(); ih++) {
	  Hexel* h = *ih;
	  if (already.count(h) != 0) {
	    duplicated.insert(h);
	    goto DUP_FOUND;
	  } else {
	    already.insert(h);
	  }
	}
	temporary.push_back(newSyzygy);
      }
    DUP_FOUND:;
    }
  }
  // Register only those syzygies without any common hexels
  for (list <Syzygy>::iterator s = temporary.begin();
       s != temporary.end(); s++) {
    for (vector <Hexel*>:: iterator h = s->swappedHexels.begin();
	 h != s->swappedHexels.end(); h++) {
      if (duplicated.count(*h) != 0)
	goto DUPLICATED;
    }
    syzygies.push_back(*s);
  DUPLICATED:;
  }
}

/*
bool operator==(const Syzygy &s1, const Syzygy &s2) {
  const vector <Hexel*>* sh1 = &s1.swappedHexels;
  const vector <Hexel*>* sh2 = &s2.swappedHexels;
  if (sh1->size() != sh2->size()) {
    return false;
  }
  for (int k = 0; k != sh1->size(); k++) {
    if ((*sh1)[k] != (*sh2)[k]) return false;
  }
  return true;
}
*/

void Hexel::place(Agent* agt) {
  agent = agt;
  if (agt->id != 0) {
    owner = agt->team;
  }
  agt->pos = this;
}

void Agent::init(int tm, int x, int y, int i, Field* field) {
  frozen = false;
  team = tm;
  id = i;
  pos = &field->hexels[y][x];
  pos->place(this);
}
