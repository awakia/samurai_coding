enum ExprKind {
  numeric, strliteral, variable, aelem,
  fcall, commalist,
  uminus, uplus, asize,
  product, quotient, remainder,
  sum, difference,
  lt, le, gt, ge, eq, neq,
  bitwiseand, bitwiseor,
};

struct Expression {
  ExprKind kind;
  int numValue;
  string strValue;
  Expression *left, *right;
  vector <Expression*> *arglist;
  int cost;
  Expression(ExprKind k, Expression *l, Expression *r = 0);
  Expression(ExprKind k, int v = 0);
  Expression(ExprKind k, string str, vector<Expression*> *al);
  Expression(string s);
};

Expression *parseExpr(TokenItr &tp);
