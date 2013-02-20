all :
	javac *.java 

run : all
	java guiPanel

clean :
	-rm *.class



