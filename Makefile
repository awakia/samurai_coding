all:
	cd translator; make
	cd runtime; make

tar:	clean
	tar cvzf ../samurai2013.tgz *

exampleTeamList:
	cp runtime/players/teamlist.example runtime/players/teamlist

example: exampleTeamList all


.PHONY: clean
clean:
	cd translator; make clean
	cd runtime; make clean
	rm -r -f ?*~ *.stackdump
