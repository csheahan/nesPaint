NES Painter
===========
Version 1.0

###What is NES Painter?###
Nes painter is a GUI app designed to improve the experience of making NES backgrounds. If you look at an example NES game, say my [High Low Game](https://github.com/csheahan/HighLow-NES/blob/master/data.asm#L224), where the background data is, you may notice how long and annoying it looks to enter. The goals of this program is as so:

1. Provide a GUI for the background drawing. Having to have a reference up of the hex address of everybackground tile and visualizing the background in ones head is both annoying and tedious.
2. Speed up the process of NES development. Creating backgrounds is a big time sink because there is a lot of data entry and no real time confirmation that you have it just right.
3. Document how .chr files work. I looked for quite a while on how a .chr file works and just couldn't find it, so I plan to document how they work quite thoroughly to assist with any future endeavors in using .chr files, not just by me but anyone interested in making tools for NESdev. Said documentation can be found [Here](/Documentation/).

###How to run###
<u>Windows</u>  
There are 3 ways to run in windows:
1. Run the attached "run.bat" file.
2. Run the "newPaint.jar" file. If there is no jar file, simply run the "makeJar.bat" file to create one.
3. Run the appropriate commands in cmd prompt
```
javac nesPaint.java
java nesPaint
```
assuming you have your paths properly set up.

<u>Linux</u>  
There are 4 ways to run in linux:
1. Run this command in your terminal:
```
make run
```
2. Run these commands in your terminal:
```
make
java nesPaint
```
3. Run these command in your terminal:
```
make jar
java -jar nesPaint.jar
```
4. Run these commands in your terminal:
```
javac nesPaint.java
java nesPaint
```

###How to use###   
It is quite simple to use nesPaint, so long as you have a valid .chr file. At the beginning you are prompted to locate said .chr file. Once selected, the file is drawn, and you have 4 parts to the app. You have your canvas on the top left, which is what you will paint on. You have your paint (background sprites) on the top right which you use to paint on the canvas. You have a visual aid to tell you what is currently selected under your paint area. And finally you have an output to textfile button, which when pressed will output the current canvas into proper .db format to a file named output.txt in the current directory.

###Future Plans###
- Fix some inconsitancies between Linux and Windows, the main being text. Setting the font is more of a quick fix than a real fix currently.
- Implement tools such as paint fill, background fill (fills all unfilled tiles with current selection), and more.
- Improve visuals. This is my first GUI project but still, can always improve.
- Make it scaleable? Don't know if I want this, but something to consider (or alternatively, hardcode scaling such as 1x, 2x, 4x, and 8x modes from a dropdown).
- Implement color selection from the user.

###Version History###
- 1.0 Initial push. Basic working project.