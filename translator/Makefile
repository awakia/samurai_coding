PROG= gb2c++
SRCS= main.cc debug.cc line.cc expr.cc  scan.cc  names.cc generate.cc
HDRS= error.hh  expr.hh  line.hh  names.hh  tokens.hh
OBJS=$(patsubst %.cc,%.o,$(SRCS))
DEPENDS=$(patsubst %cc,%d,$(SRCS))

CFLAGS= -g

$(PROG): $(OBJS)
	g++ $(CFLAGS) -o $@ $(OBJS)

TAGS: $(SRCS) $(HDRS)
	etags $(SRCS) $(HDRS)

.PHONY: clean depend
depend: $(DEPENDS)
clean:
	rm -f $(PROG) $(OBJS) $(DEPENDS) TAGS ?*~
.cc.o:
	g++ $(CFLAGS) -c $<
%.d: %.cc
	@set -e; g++ -MM $(CFLAGS) $< \
		| sed 's/\($*\)\.o[ :]*/\1.o $@ : /g' > $@; \
		[ -s $@ ] || rm -f $@
-include $(DEPENDS)
