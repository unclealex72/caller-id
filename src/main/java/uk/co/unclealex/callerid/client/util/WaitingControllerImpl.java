package uk.co.unclealex.callerid.client.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WaitingControllerImpl implements WaitingController {

	private final List<WaitingListener> i_waitingListeners = Lists.newArrayList();
	private final Map<Integer, String> i_waitingMessages = Maps.newHashMap();
	private int i_nextWaitingHandler = 0;
	
	@Override
	public void addWaitingListener(WaitingListener waitingListener) {
		getWaitingListeners().add(waitingListener);
	}

	@Override
	public void removeWaitingListener(WaitingListener waitingListener) {
		getWaitingListeners().remove(waitingListener);
	}

	@Override
	public int startWaiting(String message, Collection<CanWait> canWaits) {
		int nextWaitingHandler = getNextWaitingHandler();
		setNextWaitingHandler(nextWaitingHandler + 1);
		Map<Integer, String> waitingMessages = getWaitingMessages();
		waitingMessages.put(nextWaitingHandler, message);
		fireStartWaiting(message, nextWaitingHandler, Iterables.concat(getWaitingListeners(), canWaits));
		return nextWaitingHandler;
	}

	@Override
	public void stopWaiting(int waitingHandler, Collection<CanWait> canWaits) {
		fireStopWaiting(waitingHandler, Iterables.concat(getWaitingListeners(), canWaits));
		getWaitingMessages().remove(waitingHandler);
	}

	protected <C extends CanWait> void fireStartWaiting(String message, int waitingHandler, Iterable<C> canWaits) {
		for (C canWait : canWaits) {
			canWait.startWaiting(message, waitingHandler);
		}
	}
	
	protected <C extends CanWait> void fireStopWaiting(int waitingHandler, Iterable<C> canWaits) {
		for (C canWait : canWaits) {
			canWait.stopWaiting(waitingHandler);
		}
	}

	public List<WaitingListener> getWaitingListeners() {
		return i_waitingListeners;
	}

	public Map<Integer, String> getWaitingMessages() {
		return i_waitingMessages;
	}

	public int getNextWaitingHandler() {
		return i_nextWaitingHandler;
	}

	public void setNextWaitingHandler(int nextWaitingHandler) {
		i_nextWaitingHandler = nextWaitingHandler;
	}
}
