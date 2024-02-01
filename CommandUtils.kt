import java.util.Calendar

class CommandUtils {

    /** Powers lights on/off - Please note that the boolean value is used twice when forming the
     * command data.
     *@param isOn True turns lights on.
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

    /** Sets colour of the lights.
     * @param redValue 0..255
     * @param greenValue 0..255
     * @param blueValue 0..255
     */

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

    /** Sets pattern according to 1 of 29 pre programmed patterns.
     *  @param pattern Number of pattern to select, 0..28
     */

    fun createPatternCommand(pattern: Int): ByteArray = byteArrayOf(
        0x7E.toByte(),
        0x05.toByte(),
        0x03.toByte(),
        (pattern.coerceIn(0..28) + 128).toByte(),
        0x03.toByte(),
        0xFF.toByte(),
        0xFF.toByte(),
        0x00.toByte(),
        0xEF.toByte()
    )

    /** Sets speed pattern will play at.
     *  @param speed Speed value as percentage.
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

    /** Sets brightness of the lights.
     *  @param brightness Brightness value as percentage.
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

    /** Turns light strip internal mic on if present, after using this command you must set an eq
     *  to turn the mic on.
     *  @param isOn True to turn on, False to turn off
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

    /** Sets mic eq, turns mic on when used in conjunction with the value provided by createMicOnOffCommand.
     *  @param eqMode 0 = Classic, 1 = Soft, 2 = Dynamic, 3 = Disco.
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

    /** Sets mic sensitivity, lower values seems to produce better results.
     *  @param sensitivity Sensitivity as a percentage.
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


    /** Sets light strip internal clock, for day of the week 0 = Sunday, 6 = Saturday.
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

    /** Makes the characteristic values to control scheduling of the lights, for each day you can
     * set one time to turn on and one time to turn off.
     * @param hour Hour to set or clear.
     * @param minute Minute to set or clear.
     * @param second Second to set or clear.
     * @param weekdays Weekdays bit packed.
     * @param isOn True = ON, False = OFF.
     * @param isSet True = Set, False = Clear.
     */

    fun createTimingCommand(
        hour: Int,
        minute: Int,
        second: Int,
        weekdays: List<Boolean>,
        isOn: Boolean,
        isSet: Boolean
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
            if (isOn) 0x00.toByte() else 0x01.toByte(), //This looks wrong, but is right!
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

    /** Reorders the RGB traces/wires of the controller, for the ordinal numbering scheme the first
     * wire/trace is the one immediately next to V+. For every parameter 1 = Red, 2 = Green, 3 = Blue.
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
