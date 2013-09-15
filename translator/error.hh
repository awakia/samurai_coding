#include <exception>
#include <string>

struct SyntaxError {
  string message;
  SyntaxError(string msg) {
    message = msg;
  }
};
