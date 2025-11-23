package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.CategorizationRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategorizationRuleDao {
    @Query("SELECT * FROM categorization_rules WHERE isActive = 1 ORDER BY keyword")
    fun getAllActiveRules(): Flow<List<CategorizationRuleEntity>>

    @Query("SELECT * FROM categorization_rules WHERE envelopeId = :envelopeId AND isActive = 1")
    fun getRulesForEnvelope(envelopeId: Long): Flow<List<CategorizationRuleEntity>>

    @Query("SELECT * FROM categorization_rules WHERE id = :id")
    suspend fun getRuleById(id: Long): CategorizationRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: CategorizationRuleEntity): Long

    @Update
    suspend fun updateRule(rule: CategorizationRuleEntity)

    @Delete
    suspend fun deleteRule(rule: CategorizationRuleEntity)
    
    @Query("DELETE FROM categorization_rules")
    suspend fun deleteAllRules()
}
