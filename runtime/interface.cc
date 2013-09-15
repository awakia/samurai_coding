#include <cstdlib>
#include "gunbai.hh"
#include "game.hh"

gb gb_remaining_turns(int depth) {
  return gb(gbRemainingTurns);
}

gb gb_turns_before_enabling_syzygies(int depth) {
  return gb(gbTurnsToSyzygy);
}

gb gb_field_size(int depth) {
  gb ret;
  ret[0] = currentGame->field->width;
  ret[1] = currentGame->field->height;
  return ret;
}

gb gb_hexel_owner(gb x, gb y, int depth) {
  if (x.v < 0 || currentGame->field->width <= (x.v - ((y.v&1) == 0 ? 1 : 0)) ||
      y.v < 0 || currentGame->field->height <= y.v) {
    return gb(-2);
  }
  return gb(currentGame->field->hexels[y.v][x.v].owner);
}

gb gb_gate_positions(int depth) {
  gb ret;
  int k = 0;
  for (int k = 0; k != currentGame->field->gates.size(); k++) {
    Hexel* g = currentGame->field->gates[k];
    ret[k][0] = gb(g->x);
    ret[k][1] = gb(g->y);
  }
  return ret;
}

gb gb_agent_positions(int depth) {
  gb ret;
  for (int t = 0; t != 4; t++) {
    for (int a = 0; a != 4; a++) {
      Hexel* pos = currentGame->field->agents[t][a].pos;
      ret[t][a][0] = gb(pos->x);
      ret[t][a][1] = gb(pos->y);
    }
  }
  return ret;
}

gb gb_agent_status(gb t, gb a, int depth) {
  if (t.v < 0 || 4 <= t.v || a.v < 0 || 4 <= a.v) {
    return gb(-1);
  } else {
    return (currentGame->field->agents[t.v][a.v].frozen ? gb(1) : gb(0));
  }
}

gb gb_team_id(int depth) {
  return gb(currentTeam);
}

gb gb_random_value(gb n, int depth) {
  if (n.v <= 0) {
    return gb(0);
  }
  int ret = rand()%n.v;
  return gb(ret);
}

gb gb_move(gb move, int depth) {
  for (int a = 0; a != 4; a++) {
    int m = move[a].v;
    if (m < -1 || 6 <= m) m = -2;
    currentGame->moves[currentTeam][a] = m;
  }
}

gb gb_storage(int depth) {
  return currentGame->teams[currentTeam]->storage;
}
