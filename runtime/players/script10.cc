#include "gunbai.hh"
struct gbScript10: gbScript {
#line 1 "greedy.gb"
gb outOfField(gb p, int gbDepth) { gbCostCheck(); gbDepthCheck(gbDepth);
#line 2 "greedy.gb"
gb x, y;
#line 3 "greedy.gb"
x = p[gbType(0)];
#line 4 "greedy.gb"
y = p[gbType(1)];
#line 5 "greedy.gb"
gbCost += 30;
if ((((x<gbType(0))|(x>=width))|((x>=(width-gbType(1)))&((y&gbType(1))==gbType(0)))).v) {
#line 6 "greedy.gb"
gbCost += 1;
  return gbType(1);
#line 7 "greedy.gb"
}
#line 8 "greedy.gb"
gbCost += 8;
if (((y<gbType(0))|(y>=height)).v) {
#line 9 "greedy.gb"
gbCost += 1;
  return gbType(1);
#line 10 "greedy.gb"
}
#line 11 "greedy.gb"
gbCost += 1;
  return gbType(0);
#line 12 "greedy.gb"
return gbType(); }
#line 14 "greedy.gb"
gb randomMove(int gbDepth) { gbCostCheck(); gbDepthCheck(gbDepth);
#line 15 "greedy.gb"
if (gb_agent_status(team, agnt, (gbCost += 3, gbDepth+1)).v) {
#line 16 "greedy.gb"
gbCost += 2;
  return -gbType(1);
#line 17 "greedy.gb"
} else {
#line 18 "greedy.gb"
  return gb_random_value(gbType(6), (gbCost += 1, gbDepth+1));
#line 19 "greedy.gb"
}
#line 20 "greedy.gb"
return gbType(); }
#line 22 "greedy.gb"
gb neighbor(gb p, gb k, int gbDepth) { gbCostCheck(); gbDepthCheck(gbDepth);
#line 23 "greedy.gb"
gb x, y, ret, offset;
#line 24 "greedy.gb"
x = p[gbType(0)];
#line 25 "greedy.gb"
y = p[gbType(1)];
#line 26 "greedy.gb"
gbCost += 16;
if (((y&gbType(1))==gbType(0)).v) {
#line 27 "greedy.gb"
offset = gbType(0);
gbCost += 3;
#line 28 "greedy.gb"
} else {
#line 29 "greedy.gb"
offset = -gbType(1);
gbCost += 4;
#line 30 "greedy.gb"
}
#line 31 "greedy.gb"
gbCost += 4;
if ((k==gbType(0)).v) {
#line 32 "greedy.gb"
ret[gbType(0)] = (x+offset);
#line 33 "greedy.gb"
ret[gbType(1)] = (y-gbType(1));
gbCost += 14;
#line 34 "greedy.gb"
} else if ((k==gbType(1)).v) {
#line 35 "greedy.gb"
ret[gbType(0)] = ((x+gbType(1))+offset);
#line 36 "greedy.gb"
ret[gbType(1)] = (y-gbType(1));
gbCost += 19;
#line 37 "greedy.gb"
} else if ((k==gbType(2)).v) {
#line 38 "greedy.gb"
ret[gbType(0)] = (x+gbType(1));
#line 39 "greedy.gb"
ret[gbType(1)] = y;
gbCost += 15;
#line 40 "greedy.gb"
} else if ((k==gbType(3)).v) {
#line 41 "greedy.gb"
ret[gbType(0)] = ((x+gbType(1))+offset);
#line 42 "greedy.gb"
ret[gbType(1)] = (y+gbType(1));
gbCost += 19;
#line 43 "greedy.gb"
} else if ((k==gbType(4)).v) {
#line 44 "greedy.gb"
ret[gbType(0)] = (x+offset);
#line 45 "greedy.gb"
ret[gbType(1)] = (y+gbType(1));
gbCost += 17;
#line 46 "greedy.gb"
} else if ((k==gbType(5)).v) {
#line 47 "greedy.gb"
ret[gbType(0)] = (x-gbType(1));
#line 48 "greedy.gb"
ret[gbType(1)] = y;
gbCost += 15;
#line 49 "greedy.gb"
}
#line 50 "greedy.gb"
gbCost += 1;
  return ret;
#line 51 "greedy.gb"
return gbType(); }
#line 53 "greedy.gb"
gb isTaboo(gb agnt, gb dest, int gbDepth) { gbCostCheck(); gbDepthCheck(gbDepth);
#line 54 "greedy.gb"
gb k;
#line 55 "greedy.gb"
k = gbType(0);
#line 56 "greedy.gb"
gbCost += 6;
while ((k<numTaboos).v) {
#line 57 "greedy.gb"
gbCost += 20;
if (((taboos[k][gbType(0)]==dest[gbType(0)])&(taboos[k][gbType(1)]==dest[gbType(1)])).v) {
#line 58 "greedy.gb"
gbCost += 1;
  return gbType(1);
#line 59 "greedy.gb"
}
#line 60 "greedy.gb"
k = (k+gbType(1));
#line 61 "greedy.gb"
gbCost += 4;
gbCostCheck();
}
#line 62 "greedy.gb"
gbCost += 1;
  return gbType(0);
#line 63 "greedy.gb"
return gbType(); }
#line 65 "greedy.gb"
gb decideMove(gb agnt, int gbDepth) { gbCostCheck(); gbDepthCheck(gbDepth);
#line 66 "greedy.gb"
gb current, k, cont, dest, gates, atgate;
#line 67 "greedy.gb"
if (gb_agent_status(team, agnt, (gbCost += 3, gbDepth+1)).v) {
#line 68 "greedy.gb"
gbCost += 2;
  return -gbType(1);
#line 69 "greedy.gb"
}
#line 70 "greedy.gb"
current = pos[team][agnt];
#line 71 "greedy.gb"
gates = gb_gate_positions((gbCost += 8, gbDepth+1));
#line 72 "greedy.gb"
atgate = gbType(0);
#line 73 "greedy.gb"
k = gbType(0);
#line 74 "greedy.gb"
gbCost += 11;
while ((k!=gates.gbArraySize()).v) {
#line 75 "greedy.gb"
gbCost += 20;
if (((gates[k][gbType(0)]==current[gbType(0)])&(gates[k][gbType(1)]==current[gbType(1)])).v) {
#line 76 "greedy.gb"
atgate = gbType(1);
gbCost += 3;
#line 77 "greedy.gb"
}
#line 78 "greedy.gb"
k = (k+gbType(1));
#line 79 "greedy.gb"
gbCost += 4;
gbCostCheck();
}
#line 80 "greedy.gb"
rand = gb_random_value(gbType(6), (gbCost += 2, gbDepth+1));
#line 81 "greedy.gb"
k = gbType(0);
#line 82 "greedy.gb"
cont = gbType(1);
#line 83 "greedy.gb"
gbCost += 8;
while (cont.v) {
#line 84 "greedy.gb"
dest = neighbor(current, ((rand+k)%gbType(6)), (gbCost += 7, gbDepth+1));
#line 85 "greedy.gb"
gbCost += 3;
if ((((atgate|(outOfField(dest, (gbCost += 4, gbDepth+1))==gbType(0)))&(gb_hexel_owner(dest[gbType(0)], dest[gbType(1)], (gbCost += 9, gbDepth+1))!=team))&(isTaboo(agnt, dest, (gbCost += 5, gbDepth+1))==gbType(0))).v) {
#line 86 "greedy.gb"
cont = gbType(0);
#line 87 "greedy.gb"
taboos[numTaboos] = dest;
#line 88 "greedy.gb"
numTaboos = (numTaboos+gbType(1));
#line 89 "greedy.gb"
gbCost += 18;
  return ((rand+k)%gbType(6));
#line 90 "greedy.gb"
} else {
#line 91 "greedy.gb"
k = (k+gbType(1));
#line 92 "greedy.gb"
gbCost += 9;
if ((k==gbType(6)).v) {
#line 93 "greedy.gb"
cont = gbType(0);
gbCost += 3;
#line 94 "greedy.gb"
}
#line 95 "greedy.gb"
}
#line 96 "greedy.gb"
gbCost += -1;
gbCostCheck();
}
#line 97 "greedy.gb"
  return randomMove((gbCost += 0, gbDepth+1));
#line 98 "greedy.gb"
return gbType(); }
void script() { int gbDepth = 0; gbCost = 0;
#line 100 "greedy.gb"
team = gb_team_id((gbCost += 1, gbDepth+1));
#line 101 "greedy.gb"
size = gb_field_size((gbCost += 2, gbDepth+1));
#line 102 "greedy.gb"
width = size[gbType(0)];
#line 103 "greedy.gb"
height = size[gbType(1)];
#line 104 "greedy.gb"
pos = gb_agent_positions((gbCost += 12, gbDepth+1));
#line 105 "greedy.gb"
numTaboos = gbType(0);
#line 106 "greedy.gb"
moves[gbType(0)] = randomMove((gbCost += 7, gbDepth+1));
#line 107 "greedy.gb"
moves[gbType(1)] = decideMove(gbType(1), (gbCost += 5, gbDepth+1));
#line 108 "greedy.gb"
moves[gbType(2)] = decideMove(gbType(2), (gbCost += 5, gbDepth+1));
#line 109 "greedy.gb"
moves[gbType(3)] = decideMove(gbType(3), (gbCost += 5, gbDepth+1));
#line 110 "greedy.gb"
gb_move(moves, (gbCost += 2, gbDepth+1));
#line 111 "greedy.gb"
}
gb width, height, team, agnt, numTaboos, taboos, pos, rand, size, moves;void gbInitGlobals() {
width = gbType(0);
height = gbType(0);
team = gbType(0);
agnt = gbType(0);
numTaboos = gbType(0);
taboos = gbType(0);
pos = gbType(0);
rand = gbType(0);
size = gbType(0);
moves = gbType(0);
}

} gbScriptObj10;
