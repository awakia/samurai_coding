#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <map>
#include "tokens.hh"
#include "error.hh"

using namespace std;

static map <string, TokenKind> reservedWords;
static bool scannerInitiated = false;

void initScanner() {
  reservedWords["local"] = localToken;
  reservedWords["func"] = funcToken;
  reservedWords["endfunc"] = endFuncToken;
  reservedWords["return"] = returnToken;
  reservedWords["print"] = printToken;
  reservedWords["if"] = ifToken;
  reservedWords["else"] = elseToken;
  reservedWords["elif"] = elifToken;
  reservedWords["endif"] = endIfToken;
  reservedWords["while"] = whileToken;
  reservedWords["endwhile"] = endWhileToken;
  reservedWords["break"] = breakToken;
  reservedWords["continue"] = continueToken;
  scannerInitiated = true;
}

TokenVec scan(string buf) {
  if (!scannerInitiated) {
    initScanner();
  }
  istringstream str(buf + '\n');
  TokenVec tokens;
  char ch;
  str.get(ch);
  while (true) {
    while (ch == ' ' || ch == '\t' || ch == '\r') {
      str.get(ch);
    }
    switch (ch) {
    case '#':
      do {
	str.get(ch);
      } while (ch != '\n');
      tokens.push_back(Token(eolToken));
      return tokens;
    case '\n':
      tokens.push_back(Token(eolToken));
      return tokens;
    case '=':
      str.get(ch);
      if (ch == '=') {
	tokens.push_back(Token(eqToken));
	str.get(ch);
      } else {
	tokens.push_back(Token(assignToken));
      }
      break;
    case '|':
      tokens.push_back(Token(orToken));
      str.get(ch);
      break;
    case '&':
      tokens.push_back(Token(andToken));
      str.get(ch);
      break;
    case '!':
      str.get(ch);
      if (ch != '=') {
	throw SyntaxError("Equal sign expected after '!'");
      }
      tokens.push_back(Token(neToken));
      str.get(ch);
      break;
    case '<':
      str.get(ch);
      if (ch == '=') {
	tokens.push_back(Token(leToken));
	str.get(ch);
      } else {
	tokens.push_back(Token(lessToken));
      }
      break;
    case '>':
      str.get(ch);
      if (ch == '=') {
	tokens.push_back(Token(geToken));
	str.get(ch);
      } else {
	tokens.push_back(Token(greaterToken));
      }
      break;
    case '+':
      tokens.push_back(Token(plusToken));
      str.get(ch);
      break;
    case '-':
      tokens.push_back(Token(minusToken));
      str.get(ch);
      break;
    case '/':
      tokens.push_back(Token(divToken));
      str.get(ch);
      break;
    case '*':
      tokens.push_back(Token(multToken));
      str.get(ch);
      break;
    case '%':
      tokens.push_back(Token(modToken));
      str.get(ch);
      break;
    case '$':
      tokens.push_back(Token(dollarToken));
      str.get(ch);
      break;
    case '(':
      tokens.push_back(Token(lparToken));
      str.get(ch);
      break;
    case ')':
      tokens.push_back(Token(rparToken));
      str.get(ch);
      break;
    case '[':
      tokens.push_back(Token(braToken));
      str.get(ch);
      break;
    case ']':
      tokens.push_back(Token(cketToken));
      str.get(ch);
      break;
    case ',':
      tokens.push_back(Token(commaToken));
      str.get(ch);
      break;
    case '\'':
      {
	string chars = "";
	while (true) {
	  str.get(ch);
	NEXTCH:
	  if (ch == '\'') {
	    str.get(ch);
	    break;
	  } else if (ch == '\\') {
	    str.get(ch);
	    switch (ch) {
	    case 'n': ch = '\n'; break;
	    case 't': ch = '\t'; break;
	    case '0': case '1': case '2': case '3':
	    case '4': case '5': case '6': case '7': {
	      int v = 0;
	      do {
		v += 8 * v + ch - '0';
		str.get(ch);
	      } while ('0' <= ch && ch <= '7');
	      str.unget();
	      goto NEXTCH;
	    }
	    default:;
	    }
	  }
	  chars += ch;
	}
	tokens.push_back(Token(stringToken, 0, chars));
      }
      break;
    default:
      if ('0' <= ch && ch <= '9') {
	str.unget();
	int nv;
	str >> nv;
	tokens.push_back(Token(numberToken, nv));
	str.get(ch);
	break;
      } else if (ch == '_' ||
		 'A' <= ch && ch <= 'Z' ||
		 'a' <= ch && ch <= 'z') {
	string name;
	do {
	  name += ch;
	  str.get(ch);
	} while (ch == '_' ||
		 'A' <= ch && ch <= 'Z' ||
		 'a' <= ch && ch <= 'z' ||
		 '0' <= ch && ch <= '9');
	if (reservedWords.count(name) != 0) {
	  tokens.push_back(reservedWords[name]);
	} else {
	  tokens.push_back(Token(idToken, 0, name));
	}
	break;
      } else {
	string msg = "Invalid character '";
	msg.push_back(ch);
	msg += "' found";
	throw SyntaxError(msg);
      }
    }
  }
}
