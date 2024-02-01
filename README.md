# ELK-BLEDOM-Command-Util
A simple class written in kotlin to create command data for led strip 
lights that present themselves as ELK-BLEDOM or ELK-BLEDOB. The latter
do not have a mic so those values are not applicable to them.

If your lights work with the app "Lotus Lantern" then this data will
work for your lights.

This repository contains all the data for issuing commands to
these light strips that I have managed to find so far and may
be updated as and when I find new data.

This is mainly for reference but presented as a simple kotlin class 
that could be used as part of a project to create ByteArrays that can be
used to control the lights.

Each command is 9 bytes in size.

So far these are the commands I have working:

Turn On/Off

Set color

Set brightness

Set pattern

Set pattern speed

Set mic on/off

Set mic eq

Set internal time/day of the week

Set scheduled on/off time

Clear scheduled on/off time

Reorder RGB wires/traces


The service used is: 0000fff0-0000-1000-8000-00805f9b34fb

The characteristic used is: 0000fff3-0000-1000-8000-00805f9b34fb
