package legacy.remote.dao;

import legacy.remote.model.CallRecord

/**
 * An interface for classes that can persist {@link CallRecord}s.
 */
trait CallRecordDao extends BasicDao[CallRecord]