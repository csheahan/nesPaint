@ECHO OFF
ECHO Making jar file 'nesPaint.jar'
ECHO ----------------------------------------------------
ECHO If there are any java files other than nesPaint.java
ECHO Please close this window, remove them, and restart.
ECHO ----------------------------------------------------
pause
del nesPaint.jar manifest.txt *.class
javac *.java
echo Main-Class: nesPaint >manifest.txt
jar cvfm nesPaint.jar manifest.txt *.class
del manifest.txt *.class