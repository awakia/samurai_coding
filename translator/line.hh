#include "tokens.hh"
#include "expr.hh"

enum LineKind {
  eofLine,
  emptyLine,
  localDecl,
  assignment,
  functionCall,
  printStmnt,
  ifLine,
  elseLine,
  elifLine,
  endIfLine,
  whileLine,
  endWhileLine,
  breakStmnt,
  continueStmnt,
  returnStmnt,
  funcLine,
  endFuncLine,
};  

struct Line {
  LineKind kind;
  // For assignments
  Expression *lhs;
  Expression *rhs;
  // For local variable declaration and function definition
  vector <string> *varList;
  // For function header and print
  string str;
  // Source line
  string source;

  Line(LineKind k) {
    kind = k;
  }
  Line(Expression* l, Expression *r = 0) {
    kind = assignment;
    lhs = l;
    rhs = r;
  }
  Line(LineKind k, Expression *e) {
    // if, while, move, return, print, and function call lines
    kind = k;
    lhs = e;
  }
  Line(LineKind k, vector <string> *vl, string name = "") {
    // function definition and local variable declaration
    kind = k;
    varList = vl;
    str = name;
  }
};

extern Line *parseLine(TokenItr tp);
