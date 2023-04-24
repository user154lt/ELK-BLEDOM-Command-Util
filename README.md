# ELK-BLEDOM-Command-Util
A simple class written in kotlin to create command data for led strip lights that present themselves as ELK-BLEDOM

This repository contains all the data for issuing commands to
these light strips that I have managed to find so far and may
be updated as and when I find new data.

This is mainly for reference but presented as a simple kotlin class 
that could be used as part of a project to create ByteArrays of command
data.

Each command is 9 bytes in size. In the example given each byte in the array
is assigned as an integer value. Please note as this is kotlin bytes are
signed so if you intend to use the data in a project using unsigned bytes 
you will have to adjust the values accordingly.
