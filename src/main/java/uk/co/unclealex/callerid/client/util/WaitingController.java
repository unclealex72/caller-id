package uk.co.unclealex.callerid.client.util;

import java.util.Collection;

public interface WaitingController {

	public void addWaitingListener(WaitingListener waitingListener);
	public void removeWaitingListener(WaitingListener waitingListener);

	public int startWaiting(String message, Collection<CanWait> canWaits);
	public void stopWaiting(int handler, Collection<CanWait> canWaits);
}
