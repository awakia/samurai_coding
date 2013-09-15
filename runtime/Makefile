PROG= samuraiPlay
PLAYERSDIR= players
SRCS= field.cc interface.cc game.cc main.cc
HDRS= field.hh gunbai.hh interface.hh language.hh game.hh
OBJS=$(patsubst %.cc,%.o,$(SRCS))
DEPENDS=$(patsubst %cc,%d,$(SRCS))

CXXFLAGS= -g -std=c++98

$(PROG): $(OBJS) samplePlayers
	g++ $(CXXFLAGS) -o $@ $(OBJS) $(PLAYERSDIR)/*.o
samplePlayers:
	cd $(PLAYERSDIR); make

TAGS: $(SRCS) $(HDRS)
	etags $(SRCS) $(HDRS)

.PHONY: clean depend
depend: $(DEPENDS)
clean:
	rm -f $(PROG) $(OBJS) $(DEPENDS) TAGS ?*~
	cd $(PLAYERSDIR); make clean
.cc.o:
	g++ $(CXXFLAGS) -c $<
%.d: %.cc
	@set -e; g++ -MM $(CXXFLAGS) $< \
		| sed 's/\($*\)\.o[ :]*/\1.o $@ : /g' > $@; \
		[ -s $@ ] || rm -f $@
-include $(DEPENDS)
