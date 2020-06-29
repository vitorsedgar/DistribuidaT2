
all:	NodoInterface.class Nodo.class  Main.class

Main.class: Main.java Nodo.class NodoInterface.class
			@javac Main.java

Nodo.class: Nodo.java NodoInterface.class
			@javac Nodo.java

NodoInterface.class: NodoInterface.java
			@javac NodoInterface.java

RMIStart.class: RMIStart.java
			@javac RMIStart.java

clean:
			@rm -rf *.class *~