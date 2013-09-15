#ifndef LANGUAGE
#define LANGUAGE

#include <iostream>
#include <map>

using namespace std;

// Error Hanlding
struct RuntimeError {
  string message;
  RuntimeError(string msg) {
    message = msg;
  }
};

extern ostream& operator<<(ostream& out, struct gbType* x);

// Data Type for Gunbai Data
struct gbType {
  int v;
  map<int, gbType> *a;
  inline gbType(int n = 0) { v = n; a = 0; }

  // Type and Value Checks

  inline void nonZeroCheck(string op) {
    if (v == 0) {
      throw RuntimeError("Zero division with " + op);
    }
  }

  // Array element access
  inline gbType& operator[](gbType k) {
    if (a == 0) {
      a = new map<int, gbType>;
    }
    return (*a)[k.v];
  }

  // Array size
  inline gbType gbArraySize() {
    return gbType(a == 0 ? 0 : a->size());
  }

  // Arithmetics
  inline gbType operator+(gbType y) { return gbType(v + y.v); }
  inline gbType operator-(gbType y) { return gbType(v - y.v); }
  inline gbType operator*(gbType y) { return gbType(v * y.v); }
  inline gbType operator/(gbType y) { y.nonZeroCheck("/"); return gbType(v / y.v); }
  inline gbType operator%(gbType y) { y.nonZeroCheck("%"); return gbType(v % y.v); }
  inline gbType operator-() { return gbType(-v); }

  // Comparison
  inline gbType operator==(gbType y) { return gbType(a == y.a && v == y.v); }
  inline gbType operator!=(gbType y) { return gbType(a != y.a || v != y.v); }
  inline gbType operator<(gbType y) { return gbType(v < y.v); }
  inline gbType operator<=(gbType y) { return gbType(v <= y.v); }
  inline gbType operator>=(gbType y) { return gbType(v >= y.v); }
  inline gbType operator>(gbType y) { return gbType(v > y.v); }
  // Birwise And/Or
  inline gbType operator&(gbType y) { return gbType(v & y.v); }
  inline gbType operator|(gbType y) { return gbType(v | y.v); }
};

typedef gbType gb;

extern ostream& operator<<(ostream& out, gb x);

// Array Creation

inline gbType gbNewArray() {
  gbType a;
  a.a = new map<int, gbType>;
  return a;
}

// Call depth check
inline void gbDepthCheck(int d) {
  if (d > 100) throw RuntimeError("Calls too deep");
}

// Cost Check

extern long long int costLimit;

struct gbScript {
  int gbCost;
  inline void gbCostCheck() {
    if (gbCost > costLimit) {
      throw RuntimeError("Cost limit exceeded");
    }
  }
  virtual void script() {};
  virtual void gbInitGlobals() {};
};
#endif
