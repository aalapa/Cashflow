package com.cashflow.app.data.dao

import androidx.room.*
import com.cashflow.app.data.entity.EnvelopeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvelopeDao {
    @Query("SELECT * FROM envelopes WHERE isActive = 1 ORDER BY name")
    fun getAllActiveEnvelopes(): Flow<List<EnvelopeEntity>>

    @Query("SELECT * FROM envelopes ORDER BY name")
    fun getAllEnvelopes(): Flow<List<EnvelopeEntity>>

    @Query("SELECT * FROM envelopes WHERE id = :id")
    suspend fun getEnvelopeById(id: Long): EnvelopeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvelope(envelope: EnvelopeEntity): Long

    @Update
    suspend fun updateEnvelope(envelope: EnvelopeEntity)

    @Delete
    suspend fun deleteEnvelope(envelope: EnvelopeEntity)

    @Query("DELETE FROM envelopes")
    suspend fun deleteAllEnvelopes()
}
