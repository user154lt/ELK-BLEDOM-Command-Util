import java.util.Calendar

class CommandUtils {

    /* This function is to turn the lights on or off, when isOn is true the command
    data turns the lights on, when false it will turn the lights off. Please note that
    the boolean value is used twice when forming the command data.
     */

    fun createOnOffCommand(isOn: Boolean): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x04.toByte(),
        0x04.toByte(),
        if (isOn) 1.toByte() else 0.toByte(),
        0x00.toByte(),
        if (isOn) 1.toByte() else 0.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )

    // This function is used to set the colour of the lights, the rgb values are in the range 0..255

    fun createColorCommand(
        redValue: Int,
        greenValue: Int,
        blueValue: Int
    ): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x07.toByte(),
        0x05.toByte(),
        0x03.toByte(),
        redValue.toByte(),
        greenValue.toByte(),
        blueValue.toByte(),
        0x10.toByte(),
        0xEF.toByte()
    )

    /* This function creates command data to set the pattern data to one of the preset patterns.
    There are 29 available patterns which I will document separately, the numbering for the patterns
    starts at zero. This number is then added to 128 to create the data to send to the lights.
     */

    fun createPatternCommand(pattern: Int): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x05.toByte(),
        0x03.toByte(),
        (pattern.coerceIn(0..28) + 128).toByte(),
        0x03,
        0xFF.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )

    /* This function creates the data to change the speed that the pattern will play at. The speed provided
    should be in the range 0..100
     */

    fun createSpeedCommand(speed: Int): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x04.toByte(),
        0x02.toByte(),
        speed.coerceIn(0..100).toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )

    /* This function creates the data to change the brightness of the lights. As with the speed function
    the value provided should be in the range 0..100
     */

    fun createBrightnessCommand(brightness: Int): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x04.toByte(),
        0x01.toByte(),
        brightness.coerceIn(0..100).toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )

    /* This function creates the data to turn the light strip's microphone on or off, 1 is to turn on,
    0 is to turn off. After turning the mic on you must set an eq to make the lights begin to react to
    music/sounds
     */

    fun createMicOnOffCommand(isOn: Boolean): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x04.toByte(),
        0x07.toByte(),
        if (isOn) 1 else 0,
        0xFF.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )

    /* This function creates the data to set the eq of the light strip's mic, you must set this
    after turning the mic on. eqMode is in the range 0..3, 0 = Classic, 1 = Soft, 2 = Dynamic,
    3 = Disco
    */

    fun createMicEqCommand(eqMode: Int): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x05.toByte(),
        0x03.toByte(),
        (eqMode.coerceIn(0..3) + 128).toByte(),
        0x04.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )

    /* This function creates the data to set the sensitivity of the light strip's mic. sensitivity
    can be in the range 0..100, for best results start with a lower sensitivity and turn up as
    required
     */

    fun createMicSensitivityCommand(sensitivity: Int): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x04.toByte(),
        0x06.toByte(),
        sensitivity.coerceIn(0..100).toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )


    /* This function creates the data to set the internal clock of the light strip, in its current state
    it will set the time and day of the week to match the device sending the command. The hour of day is
    in the range 0..23, the minute and second are in the range 0..59. The day of the week is in range 0..6
    with 0 being Monday and 6 being Sunday.
     */

    fun createSyncTimeCommand(): ByteArray {
        val calendar = Calendar.getInstance()
        return byteArrayOf(
            0x7E.toByte(),
            0x07.toByte(),
            0x83.toByte(),
            calendar.get(Calendar.HOUR_OF_DAY).toByte(),
            calendar.get(Calendar.MINUTE).toByte(),
            calendar.get(Calendar.SECOND).toByte(),
            (calendar.get(Calendar.DAY_OF_WEEK) - 1).toByte(),
            0xFF.toByte(),
            0xEF.toByte()
        )
    }

    /* This function is to create data that can control the scheduling of the lights on and off times,
    the hour, minute and second values are all the in the same range as the createSyncTimeCommand function
    above. commandData[6] is the value that controls whether this data will set a time to turn on
    or a time to turn off. This is written below but I will say it here as well, this value is the opposite
    of what you would expect 0 is for an ON time, 1 is for an OFF time. The days of the week you wish to
    schedule for are passed as a list of Booleans where weekdays[0] represents Monday and weekdays[6]
    represents Sunday. Please the function createWeekdaysInt below to see how this List is transformed
    into an Int that is used to create commandData[7], the byte that defines what weekdays are set or cleared.
    We then perform a bitwise comparison on weekdaysInt. If we are setting on/off times we do
    weekdaysInt OR 128, if we are clearing on/off times we do weekdaysInt OR 0.
     */

    fun createTimingCommand(
        hour: Int,
        minute: Int,
        second: Int,
        weekdays: List<Boolean>,
        isOn: Boolean, //True to set an ON time, false to set an OFF time
        isSet: Boolean //True to SET an on/off time, false to CLEAR an on/off time
    ): ByteArray {
        val setOrClearMask = if (isSet) 128 else 0
        val packedWeekdays = packWeekdays(weekdays = weekdays)
        return byteArrayOf(
            0x7E.toByte(),
            0x08.toByte(),
            0x82.toByte(),
            hour.toByte(),
            minute.toByte(),
            second.toByte(),
            if (isOn) 0x00.toByte() else 0x01.toByte(),
            (setOrClearMask or packedWeekdays).toByte(),
            0xEF.toByte()
        )
    }


    private fun packWeekdays(weekdays: List<Boolean>): Int {
        var packed = 0
        for (i in 0..6) {
            if (weekdays[i]) {
                val mask: Int = 1 shl i
                packed = packed or mask
            }
        }
        return packed
    }

    /* This should be the data required to change rgb order of the wires, not really for general use but potentially 
    useful if reusing a controller with a different light strip. The integer passed for each wire represents the new 
    colour you would like the wire to be, and should be either 1,2, or 3. Where 1 = Red, 2 = Green, 3 = Blue.
    
    The wire numbering is as follows, with the 5/12/24v wire on the left:

            |   |   |   |
            |   |   |   |
            |   |   |   |
            v+ 1st 2nd 3rd 

    After using this command you must turn the controller off and back on again for changes to take effect.
    */
    fun createOrderChangeCommand(
        firstWire: Int,
        secondWire: Int,
        thirdWire: Int
    ): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x06.toByte(),
        0x81.toByte(),
        firstWire.toByte(),
        secondWire.toByte(),
        thirdWire.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )
}
