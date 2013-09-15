#include "tokens.hh"
#include "expr.hh"
#include "names.hh"
#include "error.hh"

Expression::Expression(ExprKind k, Expression *l, Expression *r) {
  kind = k;
  left = l;
  right = r;
  cost = 1;
  if (l != 0) cost += l->cost;
  if (r != 0) cost += r->cost;
}

Expression::Expression(string s) {
  kind = strliteral;
  strValue = s;
}

Expression::Expression(ExprKind k,  int v) {
  kind = k;
  numValue = v;
  cost = 1;
}

Expression::Expression(ExprKind k, string str, vector<Expression*> *al = 0) {
  kind = k;
  strValue = str;
  arglist = al;
  if (al != 0) {
    cost = 2 * al->size();
    for (vector<Expression*>::iterator i = al->begin();
         i != al->end();
         i++) {
      cost += (*i)->cost;
    }
  }
}

Expression *parseFunCall(TokenItr &tp, string name) {
  vector <Expression*> *al = new vector<Expression*>();
  if (tp->kind == rparToken) {
    tp++;
    return new Expression(fcall, name, al);
  } else {
    while (true) {
      al->push_back(parseExpr(tp));
      if (tp->kind == rparToken) {
        tp++;
        break;
      } else if (tp->kind != commaToken) {
        throw SyntaxError("Comma or close parenthesis expected");
      }
      tp++;
    }
    return new Expression(fcall, name, al);
  }
}

Expression *parsePrimary(TokenItr &tp) {
  switch (tp->kind) {
  case idToken: {
    string name = tp->strValue;
    tp++;
    if (tp->kind == lparToken) {
      tp++;
      return parseFunCall(tp, name);
    } else {
      Expression *var = new Expression(variable, name);
      while (tp->kind == braToken) {
        tp++;
        Expression *key = parseExpr(tp);
        if (tp->kind != cketToken) {
          throw SyntaxError("Close bracket expected");
        }
        tp++;
        var = new Expression(aelem, var, key);
      }
      return var;
    }
  }
  case lparToken: {
    tp++;
    Expression *expr = parseExpr(tp);
    if (tp->kind != rparToken) {
      throw SyntaxError("Close parenthesis expected");
    }
    tp++;
    return expr;
  }
  case numberToken: {
    int nv = tp->numValue;
    tp++;
    return new Expression(numeric, nv);
  }
  case eolToken:
    throw SyntaxError("Expression expected");
  default:
    throw SyntaxError("Invalid token in an expression");
  }
}

Expression *parseFactor(TokenItr &tp) {
  ExprKind kind;
  switch (tp->kind) {
  case plusToken:
    kind = uplus; break;
  case minusToken:
    kind = uminus; break;
  case dollarToken:
    kind = asize; break;
  default:
    return parsePrimary(tp);
  }
  return new Expression(kind, parsePrimary(++tp));
}

Expression *parseTerm(TokenItr &tp) {
  Expression *expr = parseFactor(tp);
  while (true) {
    ExprKind kind;
    switch (tp->kind) {
    case multToken:
      kind = product; break;
    case divToken:
      kind = quotient; break;
    case modToken:
      kind = remainder; break;
    default:
      return expr;
    }
    tp++;
    Expression *right = parseFactor(tp);
    expr = new Expression(kind, expr, right);
  }
}

Expression *parseSexpr(TokenItr &tp) {
  Expression *expr = parseTerm(tp);
  while (true) {
    ExprKind kind;
    switch (tp->kind) {
    case plusToken:
      kind = sum; break;
    case minusToken:
      kind = difference; break;
    default:
      return expr;
    }
    tp++;
    Expression *right = parseTerm(tp);
    expr = new Expression(kind, expr, right);
  }
}


Expression *parseComparison(TokenItr &tp) {
  Expression *expr = parseSexpr(tp);
  ExprKind kind;
  switch (tp->kind) {
  case eqToken:
    kind = eq; break;
  case neToken:
    kind = neq; break;
  case lessToken:
    kind = lt; break;
  case leToken:
    kind = le; break;
  case greaterToken:
    kind = gt; break;
  case geToken:
    kind = ge; break;
  default:
    return expr;
  }
  tp++;
  Expression *right = parseSexpr(tp);
  return new Expression(kind, expr, right);
}

Expression *parseAndExpr(TokenItr &tp) {
  Expression *expr = parseComparison(tp);
  while (tp->kind == andToken) {
    tp++;
    expr = new Expression(bitwiseand, expr, parseComparison(tp));
  }
  return expr;
}

Expression *parseOrExpr(TokenItr &tp) {
  Expression *expr = parseAndExpr(tp);
  while (tp->kind == orToken) {
    tp++;
    expr = new Expression(bitwiseor, expr, parseAndExpr(tp));
  }
  return expr;
}

Expression *parseExpr(TokenItr &tp) {
  return parseOrExpr(tp);
}
