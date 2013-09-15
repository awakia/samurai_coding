SRCS= teams.cc
OBJS=$(patsubst %.cc,%.o,$(SRCS))
DEPENDS=$(patsubst %cc,%d,$(SRCS))

CXXFLAGS= -g -std=c++98 -I..

all: Makefile.scripts $(OBJS)
	make -f $<

Makefile.scripts: makemake teamlist
	./makemake <teamlist

makemake: makemake.cc
	c++ $(CXXFLAGS) $< -o $@

teams.cc: Makefile.scripts

.PHONY: clean depend
depend: $(DEPENDS)
clean:
	rm -f *.o script?.cc teams.* makemake Makefile.scripts
.cc.o:
	g++ $(CXXFLAGS) -c $<
%.d: %.cc
	@set -e; g++ -MM $(CXXFLAGS) $< \
		| sed 's/\($*\)\.o[ :]*/\1.o $@ : /g' > $@; \
		[ -s $@ ] || rm -f $@

-include $(DEPENDS)
