.chr Files
==========
Once you get the hang of them, .chr files for nes dev aren't terribly difficult to understand. First thing you should do is open the .chr file in a text editor and see what they look like on the inside. My personal preference is Sublime, but others could work. Just keep trying until one works. If opened properly, you should see a lines (assuming it is a filled .chr file) like this (taken from first line of mario.chr:  
```
030f 1f1f 1c24 2666 0000 0000 1f3f 3f7f
```
What we have here is the information for the first 8x8 pixel square (sprite 0x00) in the .chr file. How we determine what affects each pixel is within two hex numbers. What we want to do is take this hex string and split it into 2 separate hex strings, so something like this:
```
030f 1f1f 1c24 2666
0000 0000 1f3f 3f7f
```
Now we have 2 hex strings of length 16. Because a hex digit is 4 bits, that means we have 2 strings of length 64 in bits, which also happens to be the number of pixels in our 8x8 pixel tile. So how we determine which pixels affect is by going in order. For example, take the first hex digits:
```
0x0 = 0000b
0x0 = 0000b
```
To determine what the first 4 pixels are, we add the bits together. Since both are 0, and 0+0=0, we can see that the first 4 pixels are color 0. So now our first 8x8 tile looks like so:
```
0000????
????????
????????
????????
????????
????????
????????
????????
```
Now lets get a little more advanced, and take the next 2 hex digits:
```
0x3 = 0011b
0x0 = 0000b
```
Since the first 2 digits of each binary string are 0, that means that the next 2 colors are also color 0. However, now we get to the third and 4th digits of the binary string. Because the 1 is in the first place, we simply add 1 to 0 and get 1 like so:
```
 (1*1)
+(0*2)
------
 1
```
The multiplication may not make sense now but it will later when we get to have digits in the second hex number, but for now, we just add 1. So our first row of our 8x8 tile has ended up like so:
```
00000011
```
Not too difficult. Now lets pick things up and skip a few hex digits to get a second hex number with some value, which would be the the 9th digit. If you wish to check your values, here is what your tile should look like so far:
```
00000011
00001111
00011111
00011111
????????
????????
????????
????????
```
Now we have the hex values 1 and 1. Lets translate to binary:
```
1 = 0001b
1 = 0001b
```
Now remember earlier when we multiplied the second value by 2? This is where it comes in. The first 3 pixels are 0, but the last one isn't. It is:
```
 (1*1)
+(1*2)
------
 1
+2
------
 3
```
So this last pixel is color 3. If you have noticed, by making the second binary number 2, we can make a total of four possible combi00000011
00001111
00011111
00011111nations of colors, which happens to be the same number as the nes allows:
```
00 = 0 = color 0
10 = 1 = color 1
01 = 2 = color 2
11 = 3 = color 3
```
Using this knowledge, finishing up the rest of the tile should be pretty simple after you get the hang of it. Here is what it looks like after you finish:
```
00000011
00001111
00011111
00011111
00033322
00322322
00322332
03322332
```
If you did it correct, congrats. If not, keep trying. It should come to you if you keep trying.

###FAQ###
Q. What was the tile we made?

A. Remember that for sprites, color 0 can be transparent. So make all of the 0's transparent, make color 1 a hat color, say red, make color 2 skin color, and make color 3 a hair color. See it yet? It's the upper left sprite of Mario! 


Q. Why is there no color information in the file?

A. Because color in the NES is determined programatically, via attribute tables (which happens to be one of the harder parts of NESdev in my opinion).