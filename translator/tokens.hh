#ifndef TOKENS
#define TOKENS

#include <string>
#include <vector>

using namespace std;

enum TokenKind {
  numberToken, stringToken,
  localToken,
  funcToken, endFuncToken, returnToken,
  printToken,
  ifToken, elseToken, elifToken, endIfToken,
  whileToken, endWhileToken,
  breakToken, continueToken,
  idToken,
  lparToken, rparToken,
  braToken, cketToken,
  commaToken,
  assignToken,
  andToken, orToken,
  eqToken, neToken, lessToken, leToken, greaterToken, geToken,
  plusToken, minusToken,
  divToken, multToken, modToken,
  dollarToken,
  eolToken,
};

struct Token {
  TokenKind kind;
  int numValue;
  string strValue;
  Token(TokenKind k, int nv = 0, string sv = "") {
    kind = k;
    numValue = nv;
    strValue = sv;
  }
};

typedef vector <Token> TokenVec;
typedef vector <Token>::iterator TokenItr;
#endif
