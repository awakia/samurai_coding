#include <string>

using namespace std;

enum NameKind { local, global, func };

struct NameEntry {
  string name;
  NameKind kind;
  int id;
  int numargs;
  NameEntry(string n, NameKind k, int d, int na = 0);
};

extern NameEntry* lookup(string name);
extern int addLocals(vector <string> *newLocals);
extern void popLocals(int upto);
extern void newGlobal(string name);
extern void newFunc(string name, int na);
extern vector <NameEntry> globals;
extern vector <NameEntry> locals;
