package com.blockchain.encryption

import java.security.Timestamp

/**
 * Created by Ziwei on 6/10/2017.
 */

data class BlockHeader(val SignedBy: String, val TimeStamp: Timestamp, val BlockHash: String, val PreBlockHash: String, val RecordKey: ByteArray) {

    @Override
    fun equals(block: BlockHeader): Boolean {

        if (SignedBy != block.SignedBy)
            return false

        if (TimeStamp != block.TimeStamp)
            return false

        if (BlockHash != block.BlockHash)
            return false

        if (PreBlockHash != block.PreBlockHash)
            return false

        if (RecordKey.size != block.RecordKey.size)
            return false

        for (i in 0..RecordKey.size) {
            if (RecordKey[i] != block.RecordKey[i])
                return false
        }

        return true
    }

}
