package uk.co.unclealex.callerid.remote.dao;

import uk.co.unclealex.callerid.remote.model.CallRecord

/**
 * An interface for classes that can persist {@link CallRecord}s.
 */
trait CallRecordDao extends BasicDao[CallRecord]