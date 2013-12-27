package flight.net.syn;

public interface SyncRegistryHost {
	
	public byte getHostId();
	
	public void syncRegistered(Sync sync);
	
	public void syncUpdated(Sync sync);
	
	public void syncRemoved(Sync sync);
	
}
