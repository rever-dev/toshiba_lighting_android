package com.sttptech.toshiba_lighting.Data.Room

import androidx.room.*
import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Group
import com.sttptech.toshiba_lighting.Data.Bean.Scene


@Dao
interface DataDao {
    
    /** 簡易新增所有資料的方法 */
    @Insert(onConflict = OnConflictStrategy.REPLACE) //預設萬一執行出錯怎麼辦，REPLACE為覆蓋
    fun insertCeilingLight(data: CeilingLight)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCeilingLights(data: List<CeilingLight>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(data: Group)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGroups(data: List<Group?>?)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScene(data: Scene?)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllScene(data: List<Scene?>?)
    
    /*=======================================================================================*/
    
    /** 撈取全部資料 */
    @Query("SELECT * FROM " + DataBase.CEILING_LIGHT_TABLE_NAME)
    fun allCeilingLights(): List<CeilingLight>?
    
    @Query("SELECT * FROM " + DataBase.GROUPS_TABLE_NAME)
    fun allGroups(): List<Group>?
    
    @Query("SELECT * FROM " + DataBase.SCENE_TABLE_NAME + " ORDER BY `order`")
    fun allScene(): List<Scene>?
    
    /**撈取某個名字的相關資料 */
    @Query("SELECT * FROM " + DataBase.CEILING_LIGHT_TABLE_NAME + " WHERE uId = :uId")
    fun getCeilingLightByUuid(uId: String): CeilingLight?
    
    @Query("SELECT * FROM " + DataBase.SCENE_TABLE_NAME + " WHERE seq = :seq")
    fun getSceneBySeq(seq: Int): Scene?
    
    @Query("SELECT * FROM " + DataBase.SCENE_TABLE_NAME + " WHERE uId = :uUid")
    fun getSceneByUuid(uUid: String): Scene?
    
    /*=======================================================================================*/
    
    /** 簡易更新資料的方法 */
    @Update
    fun updateCeilingLight(data: CeilingLight)
    
    @Update
    fun updateGroup(data: Group)
    
    @Update
    fun updateScene(data: Scene)
    
    /*======================================================================================= */
    
    /** 簡單刪除資料的方法 */
    @Query("DELETE FROM " + DataBase.CEILING_LIGHT_TABLE_NAME + " WHERE `uId` = :uId")
    fun deleteCeilingLightByUId(uId: String)
    
    @Delete
    fun deleteGroups(data: Group)
    
    @Query("DELETE FROM " + DataBase.SCENE_TABLE_NAME + " WHERE `uId` = :uId")
    fun deleteSceneByUId(uId: String)
    
    @Query("DELETE FROM " + DataBase.SCENE_TABLE_NAME + " WHERE `seq` = :seq")
    fun deleteSceneBySeq(seq: Int): Int
    
    @Query("DELETE FROM " + DataBase.CEILING_LIGHT_TABLE_NAME)
    fun deleteCeilingLightTable()
    
    @Query("DELETE FROM " + DataBase.GROUPS_TABLE_NAME)
    fun deleteGroupsTable()
    
    @Query("DELETE FROM " + DataBase.SCENE_TABLE_NAME)
    fun deleteSceneTable()
}