package com.sttptech.toshiba_lighting.Data.Bean

data class SceneSchedule(
    var taskId: String?,
    var taskCode: String?,
    var taskSeq: Int?,
    var cycleTaskType: String?,
    var cycleTaskMode: String?,
    var statusDB: String?,
    var payload: List<PayloadDTO>?,
    var sceneName: String?
) : Comparable<SceneSchedule> {
    
    class PayloadDTO(
        var cycleTaskId: String?,
        var dayOfWeek: Int?,
        var minuteOfDay: Int?,
        var act: String?,
        var grsituationUuid: String?
    )
    
    override fun compareTo(other: SceneSchedule): Int {
        return (payload!![0].minuteOfDay!!).compareTo(other.payload!![0].minuteOfDay!!)
    }
}
