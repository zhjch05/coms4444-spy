all:
	java spy.sim.Simulator -t 10000 --player random random random random random -m default_map --gui --fps 5

compile:
	javac spy/sim/*.java

clean:
	rm spy/*/*.class

