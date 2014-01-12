all: nesPaint.java
	javac nesPaint.java

run: nesPaint.java
	rm -f *.class
	javac nesPaint.java
	java nesPaint

jar: nesPaint.java
	rm -f *.class manifest.txt nesPaint.jar
	javac nesPaint.java
	echo Main-Class: nesPaint > manifest.txt
	jar cvfm nesPaint.jar manifest.txt *.class
	rm -f *.class manifest.txt

clean:
	rm -f *.class manifest.txt