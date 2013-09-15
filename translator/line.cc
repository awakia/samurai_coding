#include "line.hh"
#include "error.hh"
#include "names.hh"

static void eolCheck(TokenItr tp) {
  if (tp->kind != eolToken) {
    throw SyntaxError("Junk before the end of line");
  }
}


static Line *parseNoExprLine(LineKind k, TokenItr tp) {
  tp++;
  eolCheck(tp);
  return new Line(k);
}

static Line *parseOneExprLine(LineKind k, TokenItr tp) {
  Expression *e = parseExpr(tp);
  eolCheck(tp);
  return new Line(k, e);
}

static Expression* stringOrExpr(TokenItr& tp) {
  if (tp->kind == stringToken) {
    return new Expression((tp++)->strValue);
  } else {
    return parseExpr(tp);
  }
}

static Expression* parseExprList(TokenItr& tp) {
  Expression *e = stringOrExpr(tp);
  if (tp->kind == commaToken) {
    tp++;
    return new Expression(commalist, e, parseExprList(tp));
  } else {
    return new Expression(commalist, e, 0);
  }
}

Line *parseLine(TokenItr tp) {
  switch (tp->kind) {
  case eolToken:
    return new Line(emptyLine);

  case funcToken: {
    tp++;
    if (tp->kind != idToken) {
      throw SyntaxError("Function name expected");
    }
    string *name = &tp->strValue;
    tp++;
    if (tp->kind != lparToken) {
      throw SyntaxError("Open parenthesis expected");
    }
    tp++;
    vector <string> *vl = new vector<string>();
    if (tp->kind != rparToken) {
      while (true) {
	if (tp->kind != idToken) {
	  throw SyntaxError("Function argument name expected");
	}
	vl->push_back(tp->strValue);
	tp++;
	if (tp->kind == rparToken) {
	  break;
	}
	if (tp->kind != commaToken) {
	  throw SyntaxError("Comma or close parenthesis expected");
	}
	tp++;
      }
    }
    tp++;
    eolCheck(tp);
    NameEntry *entry = lookup(*name);
    if (entry != 0) {
      throw SyntaxError("Duplicated definition of " + *name);
    }
    newFunc(*name, vl->size());
    return new Line(funcLine, vl, *name);
  }

  case localToken: {
    vector <string> *vl = new vector<string>();
    tp++;
    while (tp->kind != eolToken) {
      if (tp->kind != idToken) {
	throw SyntaxError("Invalid local variable name");
      }
      vl->push_back(tp->strValue);
      tp++;
    }
    return new Line(localDecl, vl);
  }

  case idToken: {
    Expression *e = parseExpr(tp);
    if (e->kind == variable || e->kind == aelem) {
      if (tp->kind != assignToken) {
	throw SyntaxError("Assignment operator expected");
      }
      tp++;
      Expression *r = parseExpr(tp);
      eolCheck(tp);
      return new Line(e, r);
    } else if (e->kind == fcall) {
      return new Line(functionCall, e);
    } else {
      throw SyntaxError("Invalid line");
    }
  }

  case ifToken:
    tp++;
    return parseOneExprLine(ifLine, tp);
  case whileToken:
    tp++;
    return parseOneExprLine(whileLine, tp);
  case returnToken:
    tp++;
    return parseOneExprLine(returnStmnt, tp);
  case printToken: {
    tp++;
    Expression *e = parseExprList(tp);
    eolCheck(tp);
    return new Line(printStmnt, e);
  }
  case elseToken:
    return parseNoExprLine(elseLine, tp);
  case elifToken:
    tp++;
    return parseOneExprLine(elifLine, tp);
  case endIfToken:
    return parseNoExprLine(endIfLine, tp);
  case endWhileToken:
    return parseNoExprLine(endWhileLine, tp);
  case breakToken:
    return parseNoExprLine(breakStmnt, tp);
  case continueToken:
    return parseNoExprLine(continueStmnt, tp);
  case endFuncToken:
    return parseNoExprLine(endFuncLine, tp);
  default:
    throw SyntaxError("Invalid line");
  }
 
}
