@ECHO OFF
ECHO Building java files
javac nesPaint.java
pause
java nesPaint
pause
ECHO Deleting class files
del *.class
pause