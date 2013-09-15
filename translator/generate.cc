#include <iostream>
#include <sstream>
#include <stdlib.h>
#include "translator.hh"
#include "line.hh"
#include "error.hh"
#include "names.hh"

static void
genLines(vector<Line *>lines, int &lno,
             int terminatorSet,
             bool withinWhile, bool withinFuncDef,
             ostream &out);
static void
genExpr(Expression *e, ostream &out);

static int cost;

static void emitCost(ostream &out) {
  if (cost != 0) {
    out << endl << "gbCost += " << cost << ";";
  }
  cost = 0;
}

static void printLno(vector <Line*> lines, int &lno, ostream &out) {
  lno++;
  out << endl << "#line " << lno << " \"" << fileName << "\"";
}

static void genVariable(Expression *e, ostream &out) {
  NameEntry *entry = lookup(e->strValue);
  if (entry == 0) {
    newGlobal(e->strValue);
    entry = lookup(e->strValue);
  }
  if (entry->kind == func) {
    throw SyntaxError("Function name used as a variable");
  }
  out << entry->name;
}

static void genBinary(Expression *e, string op, ostream &out) {
  out << "(";
  genExpr(e->left, out);
  out << op;
  genExpr(e->right, out);
  out << ")";
  cost++;
}

static void genFunctionCall(Expression *e, ostream &out) {
  NameEntry *entry = lookup(e->strValue);
  if (entry == 0 || entry->kind != func) {
    throw SyntaxError("Undefined function " + e->strValue);
  }
  if (entry->numargs != e->arglist->size()) {
    throw SyntaxError("Argument number mismatch: " + e->strValue);
  }
  out << entry->name << "(";
  for (vector <Expression*>::iterator i = e->arglist->begin();
       i != e->arglist->end();
       i++) {
    genExpr(*i, out);
    out << ", ";
  }
  out << "(gbCost += " << cost << ", gbDepth+1))";
  cost = 0;
}

static string encodeString(string s) {
  string out = "\"";
  for (string::iterator p = s.begin();
       p != s.end();
       p++) {
    switch (*p) {
    case '\'': out += "\\'"; break;
    case '\"': out += "\\\""; break;
    case '\n': out += "\\n"; break;
    case '\t': out += "\\t"; break;
    default: out += *p;
    }
  }
  out += "\"";
  return out;
}

static void genExpr(Expression *e, ostream &out) {
  switch (e->kind) {
  case numeric:
    out << "gbType(" << e->numValue << ")";
    cost++;
    break;
  case variable:
    genVariable(e, out);
    cost++;
    break;
  case aelem:
    genExpr(e->left, out);
    out << "[";
    genExpr(e->right, out);
    out << "]";
    cost++;
    break;
  case fcall:
    genFunctionCall(e, out);
    break;
  case uminus:
    out << "-";
    genExpr(e->left, out);
    cost++;
    break;
  case uplus:
    genExpr(e->left, out);
    cost++;
    break;
  case asize:
    genExpr(e->left, out);
    out << ".gbArraySize()";
    cost++;
    break;
  case product:
    genBinary(e, "*", out);
    break;
  case quotient:
    genBinary(e, "/", out);
    break;
  case remainder:
    genBinary(e, "%", out);
    break;
  case sum:
    genBinary(e, "+", out);
    break;
  case difference:
    genBinary(e, "-", out);
    break;
  case lt:
    genBinary(e, "<", out);
    break;
  case le:
    genBinary(e, "<=", out);
    break;
  case gt:
    genBinary(e, ">", out);
    break;
  case ge:
    genBinary(e, ">=", out);
    break;
  case eq:
    genBinary(e, "==", out);
    break;
  case neq:
    genBinary(e, "!=", out);
    break;
  case bitwiseand:
    genBinary(e, "&", out);
    break;
  case bitwiseor:
    genBinary(e, "|", out);
    break;
  case strliteral:
    out << encodeString(e->strValue);
    break;
  default:
    cerr << "System Error: Unexpected Expression Encountered" << endl;
    exit(1);
  }
}

static void genAssignment(Line *l, ostream &out) {
  Expression *left = l->lhs;
  out << endl;
  if (left->kind == variable || left->kind == aelem) {
    genExpr(left, out);
  } else {
    throw SyntaxError("Left value expected to assign to");
  }
  out << " = ";
  genExpr(l->rhs, out);
  out << ";";
  cost += 1;
}

static void
genBlock(vector<Line *> lines, int &lno,
             int terminatorSet,
             bool withinWhile,
             bool withinFuncDef,
             ostream &out) {
  Line *l = lines[lno];
  if (l->kind == localDecl) {
    printLno(lines, lno, out);
    vector <string>::iterator lp = l->varList->begin();
    out << endl << "gb " << *lp;
    for (lp++; lp != l->varList->end(); lp++) {
      out << ", " << *lp;
    }
    out << ";";
    int revert = addLocals(l->varList);
    genLines(lines, lno, terminatorSet,
                 withinWhile, withinFuncDef, out);
    popLocals(revert);
  } else {
    genLines(lines, lno, terminatorSet,
                 withinWhile, withinFuncDef, out);
  }
}

static void
genIf(Line *l, vector <Line*> lines, int &lno,
          bool withinWhile, bool withinFuncdef, ostream &out) {
  cost++;
  stringstream buf;
  genExpr(l->lhs, buf);
  emitCost(out);
  out << endl << "if (" << buf.rdbuf() << ".v) {";
  genBlock(lines, lno,
           (1 << elseLine) | (1 << elifLine) | (1 << endIfLine),
           withinWhile, withinFuncdef, out);
  emitCost(out);
  l = lines[lno];
  while (l->kind == elifLine) {
    printLno(lines, lno, out);
    buf.clear();
    genExpr(l->lhs, buf);
    out << endl << "} else if (" << buf.rdbuf() << ".v) {";
    genBlock(lines, lno,
             (1 << elseLine) | (1 << elifLine) | (1 << endIfLine),
             withinWhile, withinFuncdef, out);
    emitCost(out);
    l = lines[lno];
  }
  if (l->kind == elseLine) {
    printLno(lines, lno, out);
    out  << endl << "} else {";
    genBlock(lines, lno,
                 1 << endIfLine,
                 withinWhile, withinFuncdef, out);
    emitCost(out);
  }
  if (lines[lno]->kind != endIfLine) {
    throw SyntaxError("endif expected");
  }
  printLno(lines, lno, out);
  out << endl << "}";
}

static void
genWhile(Line *l, vector <Line*> lines, int &lno,
             bool withinFuncdef, ostream &out) {
  int costBeforeCondition;
  stringstream buf;
  genExpr(l->lhs, buf);
  emitCost(out);
  int conditionCost = cost - costBeforeCondition;
  out << endl << "while (" << buf.rdbuf() << ".v) {";
  genBlock(lines, lno, (1 << endWhileLine), true, withinFuncdef, out);
  if (lines[lno]->kind != endWhileLine) {
    throw SyntaxError("endwhile expected");
  }
  printLno(lines, lno, out);
  cost += conditionCost;
  emitCost(out);
  out << endl << "gbCostCheck();" << endl << "}";
}

static void
genLines(vector<Line *>lines, int &lno,
             int terminatorSet,
             bool withinWhile,
             bool withinFuncdef,
             ostream &out) {
  while (true) {
    Line* l = lines[lno];
    if (((1<<l->kind) & terminatorSet) != 0) {
      return;
    }
    printLno(lines, lno, out);
    try {
      switch (l->kind) {
      case emptyLine:
        break;
      case assignment:
        genAssignment(l, out);
        break;
      case functionCall:
        out << endl;
        genFunctionCall(l->lhs, out);
        out << ";";
        break;
      case printStmnt: {
        int costBefore = cost;
        out << endl << "cerr";
        for (Expression *e = l->lhs; e != 0; e = e->right) {
          out << " << ";
          genExpr(e->left, out);
        }
        out << "<< endl;";
        cost = costBefore;
        break;
      }
      case ifLine:
        genIf(l, lines, lno, withinWhile, withinFuncdef, out);
        break;
      case whileLine:
        genWhile(l, lines, lno, withinFuncdef, out);
        break;
      case breakStmnt:
        if (!withinWhile) {
          throw SyntaxError("Break outside of while block");
        }
        cost++;
        emitCost(out);
        out << endl << "break;";
        break;
      case continueStmnt:
        if (!withinWhile) {
          throw SyntaxError("Continue outside of while block");
        }
        cost++;
        emitCost(out);
        out << endl << "continue;";
        break;
      case returnStmnt: {
        if (!withinFuncdef) {
          throw SyntaxError("Return outside of function definition");
        }
        stringstream buf;
        genExpr(l->lhs, buf);
        emitCost(out);
        out << endl << "  return " << buf.rdbuf() << ";";
        break;
      }
      default:
        throw SyntaxError("Misplaced line");
      }
    } catch (SyntaxError err) {
      cerr << fileName << ":" << lno << ": error: "
           << err.message << endl
           << "| " << l->source << endl;
    }
  }
}

static void
genFuncDef(vector<Line *>lines, int &lno, ostream &out) {
  Line *l = lines[lno];
  printLno(lines, lno, out);
  string name = l->str;
  NameEntry *entry = lookup(name);
  if (entry == 0 || entry->kind != func) {
    throw SyntaxError("Doubly defined function: " + name);
  }
  // Generate function header
  out << endl << "gb " << entry->name << "(";
  int revert = addLocals(l->varList);
  for (vector <string>::iterator lp = l->varList->begin();
       lp != l->varList->end();
       lp++) {
    out << "gb " << *lp << ", ";
  }
  out << "int gbDepth) { gbCostCheck(); gbDepthCheck(gbDepth);";
  cost = 0;
  genBlock(lines, lno, (1<<endFuncLine), false, true, out);
  if (lines[lno]->kind != endFuncLine) {
    throw SyntaxError("endfunc expected");
  }
  printLno(lines, lno, out);
  emitCost(out);
  out << endl << "return gbType(); }";
  popLocals(revert);
}

void
genScript(vector<Line*> lines, int &lno, int team, ostream &out) {
  // Prolog
  out << "#include \"gunbai.hh\"" << endl
      << "struct gbScript" << team << ": gbScript {";
  // Process function definitions
  try {
    while (lines[lno]->kind == funcLine) {
      genFuncDef(lines, lno, out);
      while (lines[lno]->kind == emptyLine) {
        lno++;
      }
    }
  } catch (SyntaxError err) {
    cerr << fileName << ":" << lno << ": error: "
         << err.message << endl
         << "| " << lines[lno]->source << endl;
  }
  // Process main script
  out << endl << "void script() { int gbDepth = 0; gbCost = 0;";
  cost = 0;
  genLines(lines, lno, (1<<eofLine), false, false, out);
  out << endl << "}";

  // Declarations of global variables
  if (!globals.empty()) {
    vector <NameEntry>::iterator gp = globals.begin();
    out << endl << "gb " << gp->name;
    for (gp++; gp != globals.end(); gp++) {
      out << ", " << gp->name;
    }
    out << ";";
  }

  // Initialization of global variables
  out << "void gbInitGlobals() {" << endl;
  for (vector <NameEntry>::iterator gp = globals.begin();
       gp != globals.end(); gp++) {
    out << gp->name << " = gbType(0);" << endl;;
  }
  out << "}" << endl;

  // Epilog
  out << endl << "} gbScriptObj"  << team << ";" << endl;
}
