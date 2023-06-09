import java.util.*

class CommandUtils {

    /* This function is to turn the lights on or off, when isOn is true the command
    data turns the lights on, when false it will turn the lights off. Please note that
    the boolean value is used twice when forming the command data.
     */

    fun createOnOffCommand(isOn: Boolean): ByteArray {
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 4
        commandData[2] = 4
        commandData[3] = if (isOn) 1.toByte() else 0.toByte()
        commandData[4] = 0
        commandData[5] = if (isOn) 1.toByte() else 0.toByte()
        commandData[6] = -1
        commandData[7] = 0
        commandData[8] = -17
        return commandData
    }

    // This function is used to set the colour of the lights, the rgb values are in the range 0..255

    fun createColorCommand(
        redValue: Int,
        greenValue: Int,
        blueValue: Int
    ): ByteArray {
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 7
        commandData[2] = 5
        commandData[3] = 3
        commandData[4] = redValue.toByte()
        commandData[5] = greenValue.toByte()
        commandData[6] = blueValue.toByte()
        commandData[7] = 16
        commandData[8] = -17
        return commandData
    }

    /* This function creates command data to set the pattern data to one of the preset patterns.
    There are 29 available patterns which I will document separately, the numbering for the patterns
    starts at zero. This number is then added to 128 to create the data to send to the lights.
     */

    fun createPatternCommand(pattern: Int): ByteArray {
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 5
        commandData[2] = 3
        commandData[3] = (pattern + 128).toByte()
        commandData[4] = 3
        commandData[5] = -1
        commandData[6] = -1
        commandData[7] = 0
        commandData[8] = -17
        return commandData
    }

    /* This function creates the data to change the speed that the pattern will play at. The speed provided
    should be in the range 0..100
     */

    fun createSpeedCommand(speed: Int): ByteArray {
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 4
        commandData[2] = 2
        commandData[3] = speed.toByte()
        commandData[4] = -1
        commandData[5] = -1
        commandData[6] = -1
        commandData[7] = 0
        commandData[8] = -17
        return commandData
    }

    /* This function creates the data to change the brightness of the lights. As with the speed function
    the value provided should be in the range 0..100
     */

    fun createBrightnessCommand(brightness: Int): ByteArray {
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 4
        commandData[2] = 1
        commandData[3] = brightness.toByte()
        commandData[4] = -1
        commandData[5] = -1
        commandData[6] = -1
        commandData[7] = 0
        commandData[8] = -17
        return commandData
    }

    /* This function creates the data to turn the light strip's microphone on or off, 1 is to turn on,
    0 is to turn off. After turning the mic on you must set an eq to make the lights begin to react to
    music/sounds
     */

    fun createMicOnOffCommand(isOn: Boolean): ByteArray{
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 4
        commandData[2] = 7
        commandData[3] = if (isOn) 1 else 0
        commandData[4] = -1
        commandData[5] = -1
        commandData[6] = -1
        commandData[7] = 0
        commandData[8] = -17
        return commandData
    }

    /* This function creates the data to set the eq of the light strip's mic, you must set this
    after turning the mic on. eqMode is in the range 0..3, 0 = Classic, 1 = Soft, 2 = Dynamic,
    3 = Disco
    */

    fun createMicEqCommand(eqMode: Int): ByteArray{
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 5
        commandData[2] = 3
        commandData[3] = (eqMode + 128).toByte()
        commandData[4] = 4
        commandData[5] = -1
        commandData[6] = -1
        commandData[7] = 0
        commandData[8] = -17
        return commandData
    }

    /* This function creates the data to set the sensitivity of the light strip's mic. sensitivity
    can be in the range 0..100, for best results start with a lower sensitivity and turn up as
    required
     */

    fun createMicSensitivityCommand(sensitivity: Int): ByteArray{
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 4
        commandData[2] = 6
        commandData[3] = sensitivity.toByte()
        commandData[4] = -1
        commandData[5] = -1
        commandData[6] = -1
        commandData[7] = 0
        commandData[8] = -17
        return commandData
    }


    /* This function creates the data to set the internal clock of the light strip, in its current state
    it will set the time and day of the week to match the device sending the command. The hour of day is
    in the range 0..23, the minute and second are in the range 0..59. The day of the week is in range 0..6
    with 0 being Monday and 6 being Sunday.
     */

    fun createSyncTimeCommand(): ByteArray {
        val commandData = ByteArray(9)
        val calendar = Calendar.getInstance()
        commandData[0] = 126
        commandData[1] = 7
        commandData[2] = -125
        commandData[3] = calendar.get(Calendar.HOUR_OF_DAY).toByte()
        commandData[4] = calendar.get(Calendar.MINUTE).toByte()
        commandData[5] = calendar.get(Calendar.SECOND).toByte()
        commandData[6] = (calendar.get(Calendar.DAY_OF_WEEK) - 1).toByte()
        commandData[7] = -1
        commandData[8] = -17
        return commandData
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
        val setOrClearFlag = if (isSet) 128 else 0
        val weekdaysInt = createWeekdaysInt(weekdays = weekdays)
        val commandData = ByteArray(9)
        commandData[0] = 126
        commandData[1] = 8
        commandData[2] = -126
        commandData[3] = hour.toByte()
        commandData[4] = minute.toByte()
        commandData[5] = second.toByte()
        commandData[6] = if (isOn) 0 else 1 //Timing set bit, schedule on = 0 schedule off = 1 (Yes really!)
        commandData[7] = (setOrClearFlag or weekdaysInt).toByte() // 128 or weekDays int to set, 0 or weekDays int to clear
        commandData[8] = -17
        return commandData
    }


    private fun createWeekdaysInt(weekdays: List<Boolean>): Int{
        var weekdaysInt = 0
        for (i in 0..6){
            if (weekdays[i]){
                val flag: Int = 1 shl i
                weekdaysInt = weekdaysInt or flag
            }
        }
        return weekdaysInt
    }


}