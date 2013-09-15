#include <vector>
#include <iostream>
#include "names.hh"
#include "error.hh"

vector <NameEntry> locals;
vector <NameEntry> globals;
static vector <NameEntry> funcs;

typedef vector<NameEntry>::iterator Itr;

NameEntry::NameEntry(string n, NameKind k, int d, int na) {
  name = n; kind = k; id = d; numargs = na;
}

NameEntry* lookup(string name) {
  for (int i = 0; i != funcs.size(); i++) {
    if (funcs[i].name == name) {
      return &funcs[i];
    }
  }
  for (int i = 0; i != locals.size(); i++) {
    if (locals[i].name == name) {
      return &locals[i];
    }
  }
  for (int i = 0; i != globals.size(); i++) {
    if (globals[i].name == name) {
      return &globals[i];
    }
  }
  return 0;
}

static void addLocal(string name) {
  for (int i = 0; i != locals.size(); i++) {
    if (locals[i].name == name) {
      throw SyntaxError("No two local variables should have the same name");
    }
  }
  NameEntry newLocal = NameEntry(name, local, locals.size());
  locals.push_back(newLocal);
}

int addLocals(vector <string> *newLocals) {
  int ret = locals.size();
  for (vector <string>::iterator vars = newLocals->begin();
       vars != newLocals->end();
       vars++) {
    NameEntry *entry = lookup(*vars);
    if (entry != 0 && entry->kind == func) {
      throw SyntaxError(*vars + " is defined as a function");
    }
    addLocal(*vars);
  }
  return ret;
}

void popLocals(int upto) {
  while (locals.size() != upto) {
    locals.pop_back();
  }
}

void newGlobal(string name) {
  NameEntry newGlobal = NameEntry(name, global, globals.size());
  globals.push_back(newGlobal);
}

static void enterFunc(string name, int na) {
  NameEntry newFunc = NameEntry(name, func, funcs.size(), na);
  funcs.push_back(newFunc);
}

void newFunc(string name, int na) {
  if (name.substr(0, 3) == "gb_") {
    throw SyntaxError
      ("Defining an entity with name beginning with \"gb_\":"
       + name);
  }
  enterFunc(name, na);
}

void initNames() {
  enterFunc("gb_remaining_turns", 0);
  enterFunc("gb_turns_before_enabling_syzygies", 0);
  enterFunc("gb_field_size", 0);
  enterFunc("gb_hexel_owner", 2);
  enterFunc("gb_gate_positions", 0);
  enterFunc("gb_agent_positions", 0);
  enterFunc("gb_agent_status", 2);
  enterFunc("gb_team_id", 0);
  enterFunc("gb_random_value", 1);
  enterFunc("gb_move", 1);
  enterFunc("gb_storage", 0);
}
