#include <iostream>
#include <map>
#include <string>
#include "tokens.hh"

static map <int, string> mnems;

static bool mnemonicsInitiated = false;
static void initMnemonics() {
  mnems[numberToken] = "number";
  mnems[stringToken] = "string";
  mnems[localToken] = "local";
  mnems[funcToken] = "func";
  mnems[endFuncToken] = "endFunc";
  mnems[returnToken] = "return";
  mnems[printToken] = "print";
  mnems[ifToken] = "if";
  mnems[elseToken] = "else";
  mnems[endIfToken] = "endIf";
  mnems[whileToken] = "while";
  mnems[endWhileToken] = "endWhile";
  mnems[breakToken] = "break";
  mnems[continueToken] = "continue";
  mnems[idToken] = "id";
  mnems[lparToken] = "lpar";
  mnems[rparToken] = "rpar";
  mnems[braToken] = "bra";
  mnems[cketToken] = "cket";
  mnems[commaToken] = "comma";
  mnems[assignToken] = "assign";
  mnems[eqToken] = "eq";
  mnems[neToken] = "ne";
  mnems[lessToken] = "less";
  mnems[leToken] = "le";
  mnems[greaterToken] = "greater";
  mnems[geToken] = "ge";
  mnems[plusToken] = "plus";
  mnems[minusToken] = "minus";
  mnems[divToken] = "div";
  mnems[multToken] = "mult";
  mnems[modToken] = "mod";
  mnems[dollarToken] = "dollar";
  mnems[eolToken] = "eol";
  mnemonicsInitiated = true;
}

ostream& operator<<(ostream& out, Token t) {
  if (!mnemonicsInitiated) {
    initMnemonics();
  }
  out << mnems[t.kind];
  if (t.kind == numberToken) {
    out << "(" << t.numValue << ")";
  } else if (t.kind == idToken ||
             t.kind == stringToken) {
    out << "('" << t.strValue << "')";
  }
}
